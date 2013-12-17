package com.lotaris.junitee.generator;

import com.lotaris.junitee.context.ContextInjector;
import com.lotaris.junitee.context.IContextRule;
import com.lotaris.junitee.context.GeneratorContext;
import com.lotaris.junitee.dao.DaoInjector;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManager;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * The data generator manager keep track of factories to ensure only one data generator type 
 * is instantiated during a class test execution.
 * 
 * This data generator manager should be used associated with a JUnit Rule mechanism.
 * 
 * @author Laurent Prevost, laurent.prevost@lotaris.com
 */
public class DataGeneratorManager implements IContextRule {
	/**
	 * Entity manager to share across all the data generators and DAOs in the factories.
	 */
	private EntityManager entityManager;

	/**
	 * Keep track of factories to be able to retrieve a data generator directly in a test
	 */
	private Map<Class, IDataGenerator> dataGenerators = new HashMap<>();
	
	/**
	 * Force the construction of the data generator with an entity manager
	 * 
	 * @param entityManager Entity manager to use
	 */
	public DataGeneratorManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	@Override
	public Statement apply(Statement base, Description description) {
		return apply(new GeneratorContext(), base, description);
	}

	/**
	 * Method to avoid problems with anonymous class and final variables otherwise
	 * the content of this method can be put on the method apply
	 */
	@Override
	public Statement apply(final GeneratorContext context, final Statement base, final Description description) {
		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				before(context, description);
				try {
					base.evaluate();
				}
				finally {
					after(context, description);
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
	public <T extends IDataGenerator> T getDataGenerator(Class<T> dataGeneratorClass) {
		return (T) dataGenerators.get(dataGeneratorClass);
	}
	
	/**
	 * Actions to be done before a test is run
	 * 
	 * @param description The description to get test data
	 * @param context The generator context
	 * @throws Throwable Any errors 
	 */
	private void before(GeneratorContext context, Description description) throws DataGeneratorException {
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
				try {
					// Instantiate a new data generator, inject the DAO and keep track of it.
					IDataGenerator dataGenerator = dataGeneratorClass.newInstance();
					DaoInjector.inject(dataGenerator, entityManager);
					ContextInjector.inject(dataGenerator, context);
					dataGenerators.put(dataGeneratorClass, dataGenerator);
				}
				catch (IllegalAccessException | InstantiationException ex) {
					throw new DataGeneratorException("Unable to instantiate the data generator " + dataGeneratorClass.getCanonicalName(), ex);
				}
			}
			else {
				throw new DataGeneratorException("The data generator " + dataGeneratorClass.getCanonicalName() + " is already registered. "
					+ "Only one instance of each generator can be specified in the annotation.");
			}
		}

		// Manage a transaction and call the operation to be done before the test starts.
		try {
			entityManager.getTransaction().begin();
			for (IDataGenerator dataGenerator : dataGenerators.values()) {
				dataGenerator.before();
			}
			entityManager.getTransaction().commit();
			entityManager.clear();
		}
		catch (Exception e) {
			throw new DataGeneratorException("An unexpected error occured during before phase of generators.", e);
		}
	}

	/**
	 * Actions to be done after a test is run
	 * 
	 * @param description The description to get test data
	 * @param context The generator context
	 * @throws Throwable Any errors 
	 */
	private void after(GeneratorContext context, Description description) throws DataGeneratorException {
		DataGenerator dgAnnotation = description.getAnnotation(DataGenerator.class);
		
		if (dgAnnotation != null && dgAnnotation.executeAfter()) {
			try {
				// Manage a transaction and call the operation to be done after the test ends.
				entityManager.getTransaction().begin();
				for (IDataGenerator dataGenerator : dataGenerators.values()) {
					dataGenerator.after();
				}
				entityManager.getTransaction().commit();
			}
			catch (Exception e) {
				throw new DataGeneratorException("An unexpected error occured during after phase of generators.", e);
			}
		}
	}
}
