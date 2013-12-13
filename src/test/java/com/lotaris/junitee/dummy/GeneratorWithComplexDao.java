package com.lotaris.junitee.dummy;

import com.lotaris.junitee.dao.DAO;
import com.lotaris.junitee.generator.IDataGenerator;

/**
 * Generator with complex DAO to test injections
 * 
 * @author Laurent Prevost, laurent.prevost
 */
public class GeneratorWithComplexDao implements IDataGenerator {

	@DAO
	public ThirdDao thirdDao;
	
	@Override
	public void after() {
	}

	@Override
	public void before() {
	}
}
