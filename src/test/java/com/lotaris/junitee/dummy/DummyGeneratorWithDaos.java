package com.lotaris.junitee.dummy;

import com.lotaris.junitee.dao.DAO;

/**
 * Custom data generator to have DAO injected
 * 
 * @author Laurent Prevost, laurent.prevost@lotaris.com
 */
public class DummyGeneratorWithDaos {
	@DAO
	public IImplementationDao iCustomDao;
	
	@DAO
	public ImplementationDao customDao;
	
	@DAO(fieldName = "entityManager")
	public DaoWithCustomEntityManagerField secondDao;
}
