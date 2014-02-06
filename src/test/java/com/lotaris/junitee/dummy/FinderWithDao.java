package com.lotaris.junitee.dummy;

import com.lotaris.junitee.finder.IFinder;
import javax.ejb.EJB;

/**
 * Data generator to check that the before and after are called
 * 
 * @author Laurent Prevost <laurent.prevost@lotaris.com>
 */
public class FinderWithDao implements IFinder {
	@EJB
	public IImplementationDao iCustomDao;
	
	@EJB
	public ImplementationDao customDao;
	
	public void find() {}
}
