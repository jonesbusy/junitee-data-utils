package com.lotaris.junitee.dummy;

import javax.ejb.EJB;
import javax.persistence.EntityManager;

/**
 * A second DAO to build a chain of DAO
 * 
 * @author Laurent Prevost, laurent.prevost@lotaris.com
 */
public class SecondDao {
	public EntityManager secondEm;
	
	@EJB
	public IFirstDao firstDao;
	
	@EJB
	public SecondDao secondDao;
	
	@EJB
	public FirstDao secondInstanceOfFirstDao;
}
