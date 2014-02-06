package com.lotaris.junitee.generator;

import com.lotaris.junitee.dependency.DependencyInjector;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManager;
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

	@Override
	public Statement apply(Statement base, Description description) {
		return internalApply(base, description);
	}

	/**
	 * Method to avoid problems with anonymous class and final variables otherwise
	 * the content of this method can be put on the method apply
	 */
	public Statement internalApply(final Statement base, final Description description) {
		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				try {
					generate(description);
					base.evaluate();
				}
				finally {
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
	public <T extends IDataGenerator> T getDataGenerator(Class<T> dataGeneratorClass) {
		return (T) dataGenerators.get(dataGeneratorClass);
	}
	
	/**
	 * Actions to generate data
	 * 
	 * @param description The description to get test data
	 * @param context The generator context
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
				try {
					// Instantiate a new data generator, inject the DAO and keep track of it.
					IDataGenerator dataGenerator = dataGeneratorClass.newInstance();
					DependencyInjector.inject(dataGenerator, entityManager, true);
					dataGenerators.put(dataGeneratorClass, dataGenerator);
				}
				catch (IllegalAccessException | InstantiationException ex) {
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
			entityManager.getTransaction().begin();
			for (IDataGenerator dataGenerator : dataGenerators.values()) {
				dataGenerator.generate();
			}
			entityManager.getTransaction().commit();
		}
		catch (Exception e) {
			LOG.error("Unkown error", e);
			throw new DataGeneratorException("An unexpected error occured during the data generation.", e);
		}
		finally {
			entityManager.clear();
		}
	}

	/**
	 * Actions to clean the data
	 * 
	 * @param description The description to get test data
	 * @param context The generator context
	 * @throws Throwable Any errors 
	 */
	private void cleanup(Description description) throws DataGeneratorException {
		DataGenerator dgAnnotation = description.getAnnotation(DataGenerator.class);
		
		if (dgAnnotation != null && dgAnnotation.executeCleanup()) {
			try {
				entityManager.getTransaction().begin();
				for (IDataGenerator dataGenerator : dataGenerators.values()) {
					dataGenerator.cleanup();
				}
				entityManager.getTransaction().commit();
			}
			catch (Exception e) {
				LOG.error("Unknow error", e);
				throw new DataGeneratorException("An unexpected error occured during cleanup phase.", e);
			}
			finally {
				entityManager.clear();
			}
		}
	}
}
