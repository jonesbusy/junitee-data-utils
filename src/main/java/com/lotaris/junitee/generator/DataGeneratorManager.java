package com.lotaris.junitee.generator;

import com.lotaris.junitee.dependency.DependencyInjector;
import com.lotaris.junitee.dependency.UsePersitenceUnit;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The data generator manager keep track of factories to ensure only one data generator type 
 * is instantiated during a class test execution.
 * 
 * This data generator manager should be used associated with a JUnit Rule mechanism.
 * 
 * @author Laurent Prevost <laurent.prevost@lotaris.com>
 */
public class DataGeneratorManager implements TestRule {
	private static final Logger LOG = LoggerFactory.getLogger(DataGeneratorManager.class);
	
	/**
	 * Entity manager factory to generate new entity manager to share between generators for a same test
	 */
	private EntityManagerFactory entityManagerFactory;
	
	/**
	 * Keep track of factories to be able to retrieve a data generator directly in a test
	 */
	private Map<Class, IDataGenerator> dataGenerators = new HashMap<>();
	
	/**
	 * Keep track of entity manager used in the test
	 */
	private Map<String, EntityManager> entityManagers = new HashMap<>();
	
	/**
	 * Determine if a test is running or not. This is required to enable/disable
	 * the behavior of method interceptions during the test method run.
	 */
	private static Boolean testRunning = false;
	
	/**
	 * Force the construction of the data generator with an entity manager
	 * 
	 * @param entityManagerFactory Entity manager factory to create new entity managers
	 */
	public DataGeneratorManager(EntityManagerFactory entityManagerFactory) {
		this.entityManagerFactory = entityManagerFactory;
	}

	@Override
	public Statement apply(final Statement base, final Description description) {
		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				
				try {	
					generate(description);
					testRunning = true;
					base.evaluate();
				}
				finally {
					testRunning = false;
					cleanup(description);
				}
			}
		};
	}	
	
	/**
	 * Be able to retrieve a data generator
	 * 
	 * @param <T> Data generator type
	 * @param dataGeneratorClass The data generator class to lookup
	 * @return The data generator found, null otherwise
	 */
	@SuppressWarnings("unchecked")
	public <T extends IDataGenerator> T getDataGenerator(Class<T> dataGeneratorClass) {
		// Check if the data generator exists
		if (dataGenerators.containsKey(dataGeneratorClass)) {
			return (T) dataGenerators.get(dataGeneratorClass);
		}
		// Unknown data generator
		else {
			throw new RuntimeException(new DataGeneratorException("The data generator " + dataGeneratorClass.getCanonicalName() + " is not present in the annotation."));
		}
	}
	
	/**
	 * Actions to generate data
	 * 
	 * @param description The description to get test data
	 * @param entityManager The entity manager
	 * @throws Throwable Any errors 
	 */
	private void generate(Description description) throws DataGeneratorException {
		// Clear the generators used in a previous test. Clear must be there because 
		// there is no warranty to reach the after if a test fails.
		dataGenerators.clear();

		DataGenerator dgAnnotation = description.getAnnotation(DataGenerator.class);
		
		if (dgAnnotation == null) {
			return;
		}
		
		// Retrieve all the data generators defined for the test method.
		for (Class<? extends IDataGenerator> dataGeneratorClass : dgAnnotation.value()) {
			// Check if the data generator is already instantiated.
			if (!dataGenerators.containsKey(dataGeneratorClass)) {
				
				// Override default entity manager
				EntityManager em;
				if(dataGeneratorClass.getAnnotation(UsePersitenceUnit.class) == null) {
					System.out.println("Create default EM");
					em = entityManagerFactory.createEntityManager();
				}
				else {
					System.out.println("Override EM");
					em = Persistence.createEntityManagerFactory(dataGeneratorClass.getAnnotation(UsePersitenceUnit.class).name()).createEntityManager();
				}
				
				// TODO : Don't know if there's on other way to check for the persistence unit name
				String entityManagerKey = (String)em.getProperties().get("com.lotaris.junite-data-utils.unit.name");
				if(!entityManagers.containsKey(entityManagerKey)) {
					entityManagers.put(entityManagerKey, em);
				}
				
				try {
					// Instantiate a new data generator proxy, inject the EJB and keep track of it.
					IDataGenerator dataGenerator = (IDataGenerator) Enhancer.create(
						dataGeneratorClass, 
						new Class[] {IDataGenerator.class}, 
						new GeneratorCallback(em)
					);
					
					DependencyInjector.inject(dataGenerator, em, true);
					dataGenerators.put(dataGeneratorClass, dataGenerator);
				}
				catch (Exception ex) {
					LOG.error("Injection failed during the creation of the data generator: " + dataGeneratorClass.getCanonicalName(), ex);
					throw new DataGeneratorException("Unable to instantiate the data generator " + dataGeneratorClass.getCanonicalName(), ex);
				}
			}
			else {
				LOG.error("The data generator [" + dataGeneratorClass.getCanonicalName() + "] is already instantiated. One instance of each data generator is allowed.");
				throw new DataGeneratorException("The data generator " + dataGeneratorClass.getCanonicalName() + " is already registered. "
					+ "Only one instance of each generator can be specified in the annotation.");
			}
		}
		
		try {			
			for(EntityManager em : entityManagers.values()) {
				em.getTransaction().begin();
			}
			Class<? extends IDataGenerator>[] dataGeneratorClass = dgAnnotation.value();
			for (int i = 0; i < dataGeneratorClass.length; i++) {
				getDataGenerator(dataGeneratorClass[i]).generate();
			}
			for(EntityManager em : entityManagers.values()) {
				em.getTransaction().commit();
			}
		}
		catch (Exception e) {
			LOG.error("Unkown error", e);
			for(EntityManager em : entityManagers.values()) {
				em.getTransaction().rollback();
			}
			throw new DataGeneratorException("An unexpected error occured during the data generation.", e);
		}
		finally {
			for(EntityManager em : entityManagers.values()) {
				em.clear();
			}
		}
	}

	/**
	 * Actions to clean the data
	 * 
	 * @param description The description to get test data
	 * @param entityManager The entity manager
	 * @throws Throwable Any errors 
	 */
	private void cleanup(Description description) throws DataGeneratorException {
		DataGenerator dgAnnotation = description.getAnnotation(DataGenerator.class);
		
		if (dgAnnotation != null && dgAnnotation.executeCleanup()) {
			try {
				for(EntityManager em : entityManagers.values()) {
					em.getTransaction().begin();
				}
				
				Class<? extends IDataGenerator>[] dataGeneratorClass = dgAnnotation.value();
				for (int i = dataGeneratorClass.length - 1; i >= 0; i--) {
					getDataGenerator(dataGeneratorClass[i]).cleanup();
				}
			for(EntityManager em : entityManagers.values()) {
				em.getTransaction().commit();
			}
			}
			catch (Exception e) {
				LOG.error("Unknow error", e);
			for(EntityManager em : entityManagers.values()) {
				em.getTransaction().rollback();
			}
				throw new DataGeneratorException("An unexpected error occured during cleanup phase.", e);
			}
			finally {
				for(EntityManager em : entityManagers.values()) {
					em.clear();
				}
			}
		}
	}
	
	/**
	 * Callback class to allow lazy instantiation of annotated fields
	 */
	private static class GeneratorCallback implements MethodInterceptor {
		/**
		 * Entity manager to manage the transactions
		 */
		private EntityManager entityManager;
		
		/**
		 * Constructor
		 * 
		 * @param entityManager Entity manager
		 * @param proxiedGenerator The generator to proxy
		 */
		public GeneratorCallback(EntityManager entityManager) {
			this.entityManager = entityManager;
		}
		
		@Override
		public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
			// Invoke create/update/delete methods encapsulated into a transaction
			if (testRunning && method.getName().startsWith("create") || method.getName().startsWith("update") || method.getName().startsWith("delete")) {
				try {
					entityManager.getTransaction().begin();
					Object result = proxy.invokeSuper(obj, args);
					entityManager.getTransaction().commit();
					return result;
				}
				catch (Throwable t) {
					if (entityManager.getTransaction().isActive()) {
						entityManager.getTransaction().rollback();
					}
					throw t;
				}
			}

			// Invoke any other method directly
			else {
				return proxy.invokeSuper(obj, args);
			}
		}
	}
}
