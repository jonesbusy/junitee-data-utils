package com.lotaris.junitee.generator;

/**
 * Define what can be done by a data generator
 * 
 * @author Laurent Prevost <laurent.prevost@lotaris.com>
 */
public interface IDataGenerator {
	/**
	 * Generate data before the test starts.
	 */
	void generate();
	
	/**
	 * Cleanup data after the test ends.
	 */
	void cleanup();
}
