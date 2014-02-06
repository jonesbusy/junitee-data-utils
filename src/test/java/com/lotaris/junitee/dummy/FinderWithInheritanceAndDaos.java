package com.lotaris.junitee.dummy;

import javax.ejb.EJB;

/**
 * Data generator to check that the before and after are called
 * 
 * @author Laurent Prevost <laurent.prevost@lotaris.com>
 */
public class FinderWithInheritanceAndDaos extends FinderWithDao {
	@EJB
	public IImplementationDao childICustomDao;
	
	@EJB
	public ImplementationDao childCustomDao;
}
