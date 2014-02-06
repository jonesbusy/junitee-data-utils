package com.lotaris.junitee.dummy;

import javax.ejb.EJB;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * A first DAO to build a chain of DAO
 * 
 * @author Laurent Prevost <laurent.prevost@lotaris.com>
 */
public class FirstDao implements IFirstDao {
	@PersistenceContext
	public EntityManager firstEm;
	
	@EJB
	public ThirdDao thirdDao;
}
