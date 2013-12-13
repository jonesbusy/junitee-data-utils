package com.lotaris.junitee.dummy;

import javax.ejb.EJB;
import javax.persistence.EntityManager;

/**
 * Inherits from abstract DAO to test hierarchical injections
 * 
 * @author Laurent Prevost, laurent.prevost@lotaris.com
 */
public class ThirdDao extends AbstractDao {
	public EntityManager thirdEm;
	
	@EJB
	public SecondDao secondDao;
}
