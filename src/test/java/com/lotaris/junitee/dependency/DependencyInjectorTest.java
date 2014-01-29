package com.lotaris.junitee.dependency;

import com.lotaris.junitee.dummy.ImplementationDao;
import com.lotaris.junitee.dummy.DummyGeneratorWithDaos;
import com.lotaris.junitee.dummy.FirstDao;
import com.lotaris.junitee.dummy.GeneratorWithComplexDao;
import com.lotaris.junitee.dummy.GeneratorWithInheritance;
import com.lotaris.rox.annotations.RoxableTest;
import com.lotaris.rox.annotations.RoxableTestClass;
import javax.persistence.EntityManager;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.*;
import org.mockito.MockitoAnnotations;

/**
 * @author Laurent Prevost, laurent.prevost@lotaris.com
 */
@RoxableTestClass(tags = "dependency-injector")
public class DependencyInjectorTest {
	@Mock
	private EntityManager em;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	@RoxableTest(key = "3358d773c570")
	public void classWithImplementationDaoShouldHaveDaoInjected() {
		DummyGeneratorWithDaos injected = new DummyGeneratorWithDaos();
		
		DependencyInjector.inject(injected, em);
		
		assertNotNull(injected.customDao);
	}
	
	@Test
	@RoxableTest(key = "2ad8ec021f96")
	public void classWithInterfaceDaoShouldHaveDaoInjected() {
		DummyGeneratorWithDaos injected = new DummyGeneratorWithDaos();
		
		DependencyInjector.inject(injected, em);
		
		assertNotNull(injected.iCustomDao);
	}
	
	@Test
	@RoxableTest(key = "14787a13a5e5")
	public void anyDaoInjectedWithEmAsEntityManagerFieldShouldHaveEmNotNull() {
		DummyGeneratorWithDaos injected = new DummyGeneratorWithDaos();
		
		DependencyInjector.inject(injected, em);
		
		assertNotNull(injected.customDao.em);
		assertNotNull(((ImplementationDao) injected.iCustomDao).em);
	}	

	@Test
	@RoxableTest(key = "60cf490b470d")
	public void generatorThatInheritsFromAnotherGeneratorShouldHaveAllTheAnnotatedFieldsInjectedAcrossInheritanceChain() {
		GeneratorWithInheritance injected = new GeneratorWithInheritance();
		
		DependencyInjector.inject(injected, em);
		
		assertNotNull(injected.daoInChildClass);
		assertNotNull(injected.daoInChildClass.em);
		assertNotNull(injected.iCustomDao);
		assertNotNull(((ImplementationDao) injected.iCustomDao).em);
		assertNotNull(injected.customDao);
		assertNotNull(injected.customDao.em);
	}
	
	@Test
	@RoxableTest(key = "92021e674e90")
	public void generatorWithComplexDaoShouldHaveTheEntityManagerAndNestedEjbInjectedEveryWhere() {
		GeneratorWithComplexDao generator = new GeneratorWithComplexDao();
		
		DependencyInjector.inject(generator, em);
		
		assertNotNull(generator.thirdDao.abstractEm);
		assertNotNull(generator.thirdDao.thirdEm);
		assertNotNull(generator.thirdDao.firstDao.firstEm);
		assertNotNull(generator.thirdDao.secondDao.secondEm);
		assertNotNull(((FirstDao) generator.thirdDao.secondDao.firstDao).firstEm);
		assertNotNull(((FirstDao) generator.thirdDao.secondDao.firstDao).thirdDao.thirdEm);
		assertNotNull(generator.thirdDao.secondDao.secondDao.secondEm);
		assertNotNull(generator.thirdDao.secondDao.secondInstanceOfFirstDao.firstEm);
	}
	
	@Test
	@RoxableTest(key = "e9cef1d10b28")
	public void generatorWithComplexDaoThatHasCyclyReferenceShouldUseTheSameInjectedObjects() {
		GeneratorWithComplexDao generator = new GeneratorWithComplexDao();
		
		DependencyInjector.inject(generator, em);
		
		assertEquals(generator.thirdDao, generator.thirdDao.firstDao.thirdDao);
	}

	@Test
	@RoxableTest(key = "703196f776dd")
	public void onlyOneInstanceOfEjbShouldBeInstantiatedInComplexObjectGraph() {
		GeneratorWithComplexDao generator = new GeneratorWithComplexDao();
		
		DependencyInjector.inject(generator, em);
		
		assertEquals(generator.thirdDao, generator.thirdDao.firstDao.thirdDao);
		assertEquals(generator.thirdDao.firstDao, generator.thirdDao.secondDao.firstDao);
		assertEquals(generator.thirdDao.secondDao.firstDao, generator.thirdDao.secondDao.secondInstanceOfFirstDao);
		assertEquals(generator.thirdDao.secondDao, generator.thirdDao.secondDao.secondDao);
	}
}
