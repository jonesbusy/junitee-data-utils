package com.lotaris.junitee.dummy;

import com.lotaris.junitee.dao.DAO;
import com.lotaris.junitee.generator.IDataGenerator;

/**
 * Data generator to check if the DAO is correctly instantiated
 * 
 * @author Laurent Prevost, laurent.prevost@lotaris.com
 */
public class DataGeneratorWithDao implements IDataGenerator {
	@DAO
	public IImplementationDao firstDao;
	
	@Override
	public void before() {
	}

	@Override
	public void after() {
	}
}
