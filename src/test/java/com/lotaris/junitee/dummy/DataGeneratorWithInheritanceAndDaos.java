package com.lotaris.junitee.dummy;

import javax.ejb.EJB;

/**
 * Data generator to check if the DAO is correctly instantiated
 * 
 * @author Laurent Prevost, laurent.prevost@lotaris.com
 */
public class DataGeneratorWithInheritanceAndDaos extends DataGeneratorWithDao {
	@EJB
	public IImplementationDao secondDao;
}
