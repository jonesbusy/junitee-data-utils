package com.lotaris.junitee.generator;

import java.util.Properties;
import javax.persistence.EntityManager;

/**
 * The data state generator has in charge to manage the data before and after
 * every other data manipulations are done.
 * 
 * The idea is to be able to get the state of the data at the begining of a test run
 * and then, to be able to restore the state of the data after each test.
 * 
 * @author Laurent Prevost, laurent.prevost@lotaris.com
 */
public interface IDataStateGenerator {
	/**
	 * Configure the state generator to change its behavior.
	 * 
	 * @param configuration The configuration
	 */
	void configure(Properties configuration);
	
	/**
	 * Create a state to keep data before doing anything else
	 * 
	 * @param entityManager Entity manager to manage the data
	 */
	void createState(EntityManager entityManager);
	
	/**
	 * Restore a state of data after transforming data
	 * 
	 * @param entityManager Entity manager to manage the data
	 */
	void restoreState(EntityManager entityManager);
}
