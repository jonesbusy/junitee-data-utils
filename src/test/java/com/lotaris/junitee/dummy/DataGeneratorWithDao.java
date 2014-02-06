package com.lotaris.junitee.dummy;

import com.lotaris.junitee.generator.IDataGenerator;
import javax.ejb.EJB;

/**
 * Data generator to check if the DAO is correctly instantiated
 * 
 * @author Laurent Prevost <laurent.prevost@lotaris.com>
 */
public class DataGeneratorWithDao implements IDataGenerator {
	@EJB
	public IImplementationDao firstDao;
	
	@Override
	public void generate() {
	}
	
	@Override
	public void cleanup() {
	}
}
