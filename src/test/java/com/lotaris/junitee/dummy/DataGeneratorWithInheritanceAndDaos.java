package com.lotaris.junitee.dummy;

import com.lotaris.junitee.dao.DAO;

/**
 * Data generator to check if the DAO is correctly instantiated
 * 
 * @author Laurent Prevost, laurent.prevost@lotaris.com
 */
public class DataGeneratorWithInheritanceAndDaos extends DataGeneratorWithDao {
	@DAO
	public IImplementationDao secondDao;
}
