package com.lotaris.junitee.dummy;

import javax.ejb.EJB;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Inherits from abstract DAO to test hierarchical injections
 * 
 * @author Laurent Prevost, laurent.prevost@lotaris.com
 */
public class ThirdDao extends AbstractDao {
	@PersistenceContext
	public EntityManager thirdEm;
	
	@EJB
	public SecondDao secondDao;
}
