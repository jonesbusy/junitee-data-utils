package com.lotaris.junitee.generator;

import java.util.Properties;

/**
 * The data state generator helper offers the utility methods
 * to configure and get a state generator.
 * 
 * @author Laurent Prevost <laurent.prevost@lotaris.com>
 */
public class DataStateGeneratorHelper {
	/**
	 * Configuration constants
	 */
	private static final String JUNITEE_STATEGENERATOR_CLASS = "junitee.stategenerator.class";
	
	/**
	 * The state generator manage the state of the data across a complete test run. The singleton
	 * is there to simplify the usage of the state generator to handle creating a state and restoring
	 * the state before all the tests and after each tests.
	 * 
	 * The idea is to create a data state before ALL the tests and then restoring this state
	 * after each test has run.
	 */
	private static IDataStateGenerator stateGenerator;
	
	/**
	 * Private constructor to use the helper as a Singleton
	 */
	private DataStateGeneratorHelper() {}
	
	/**
	 * Configure the helper
	 * 
	 * @param configuration The configuration to apply
	 */
	static void configure(Properties configuration) {
		// Check if the configuration is valid and the state generator not already created
		if (stateGenerator == null && configuration.containsKey(JUNITEE_STATEGENERATOR_CLASS)) {
			try {
				// Create the state generator
				stateGenerator = (IDataStateGenerator) 
					DataStateGeneratorHelper.class.getClassLoader().loadClass(configuration.getProperty(JUNITEE_STATEGENERATOR_CLASS)).newInstance();
				stateGenerator.configure(configuration);
			}
			catch (ClassNotFoundException cnfe) {
				throw new IllegalArgumentException("Unable to find the class " + configuration.getProperty(JUNITEE_STATEGENERATOR_CLASS));
			}
			catch (IllegalAccessException | InstantiationException e) {
				throw new IllegalArgumentException("Unable to create an instance of state generator. The empty constructor is mandatory.");
			}
		}
	}
	
	/**
	 * Create a state generator based on configuration file
	 */
	static IDataStateGenerator getStateGenerator() {
		return stateGenerator;
	}
	
	/**
	 * Be sure a state generator is available
	 * 
	 * @return True if a state generator is ready
	 */
	static boolean hasStateGenerator() {
		return stateGenerator != null;
	}
}
