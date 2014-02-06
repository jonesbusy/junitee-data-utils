package com.lotaris.junitee.dummy;

import javax.ejb.EJB;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Abstract DAO to test injection accros all the levels
 * 
 * @author Laurent Prevost <laurent.prevost@lotaris.com>
 */
public class AbstractDao {
	@PersistenceContext
	public EntityManager abstractEm;
	
	@EJB
	public FirstDao firstDao;
}
