package com.lotaris.junitee.dummy;

import com.lotaris.junitee.generator.IDataGenerator;
import javax.ejb.EJB;

/**
 * Generator with complex DAO to test injections
 * 
 * @author Laurent Prevost, laurent.prevost
 */
public class GeneratorWithComplexDao implements IDataGenerator {

	@EJB
	public ThirdDao thirdDao;
	
	@Override
	public void run() {
	}
}
