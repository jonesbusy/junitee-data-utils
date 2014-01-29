package com.lotaris.junitee.dummy;

import javax.ejb.EJB;

/**
 * Custom data generator to have DAO injected
 * 
 * @author Laurent Prevost, laurent.prevost@lotaris.com
 */
public class DummyGeneratorWithDaos {
	@EJB
	public IImplementationDao iCustomDao;
	
	@EJB
	public ImplementationDao customDao;
}