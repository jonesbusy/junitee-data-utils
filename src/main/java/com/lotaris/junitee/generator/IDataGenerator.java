package com.lotaris.junitee.generator;

/**
 * Define what can be done by a data generator
 * 
 * @author Laurent Prevost, laurent.prevost@lotaris.com
 */
public interface IDataGenerator {
	/**
	 * Do a processing before a test method starts.
	 */
	void run();
}
