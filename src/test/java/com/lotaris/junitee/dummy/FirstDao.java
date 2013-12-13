package com.lotaris.junitee.dummy;

import javax.ejb.EJB;
import javax.persistence.EntityManager;

/**
 * A first DAO to build a chain of DAO
 * 
 * @author Laurent Prevost, laurent.prevost@lotaris.com
 */
public class FirstDao {
	public EntityManager firstEm;
	
	@EJB
	public ThirdDao thirdDao;
}
