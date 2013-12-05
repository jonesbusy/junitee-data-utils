package com.lotaris.junitee.dummy;

import com.lotaris.junitee.dao.DAO;

/**
 * A DAO that inherits to test if all the DAO annotated fields
 * are injected across the inheritance chain
 * 
 * @author Laurent Prevost, laurent.prevost@lotaris.com
 */
public class GeneratorWithInheritance extends DummyGeneratorWithDaos {
	@DAO
	public ImplementationDao daoInChildClass;
}
