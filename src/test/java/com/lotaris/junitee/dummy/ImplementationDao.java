package com.lotaris.junitee.dummy;

import javax.persistence.EntityManager;

/**
 * Custom DAO to have the entity manager injected
 * 
 * @author Laurent Prevost
 */
public class ImplementationDao implements IImplementationDao {
	public EntityManager em;
}
