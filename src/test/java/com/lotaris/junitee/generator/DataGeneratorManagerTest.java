package com.lotaris.junitee.generator;

import com.lotaris.junitee.dummy.BeforeCrashGenerator;
import com.lotaris.junitee.dummy.DataGeneratorWithDao;
import com.lotaris.junitee.dummy.DataGeneratorWithInheritanceAndDaos;
import com.lotaris.junitee.dummy.DoNotCrashGenerator;
import com.lotaris.rox.annotations.RoxableTest;
import java.lang.annotation.Annotation;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author Laurent Prevost <laurent.prevost@lotaris.com>
 */
public class DataGeneratorManagerTest {
	@Mock
	private Statement statement;
	
	@Mock
	private EntityManagerFactory entityManagerFactory;
	
	@Mock
	private EntityManager entityManager;
	
	@Mock
	private EntityTransaction entityTransaction;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		when(entityManagerFactory.createEntityManager()).thenReturn(entityManager);
		when(entityManager.getTransaction()).thenReturn(entityTransaction);
	}

	@Test
	@RoxableTest(key = "731192f53198")
	public void failingBeforeMethodOnGeneratorShouldAvoidStatementToBeEvaluated() throws Throwable {
		DataGenerator annotation = new DataGenerator() {
			@Override
			public Class<? extends IDataGenerator>[] value() {
				return new Class[] { BeforeCrashGenerator.class };
			}

			@Override
			public Class<? extends Annotation> annotationType() {
				return DataGenerator.class;
			}

			@Override
			public boolean executeCleanup() {
				return true;
			}
		};
		
		Description description = Description.createSuiteDescription("Some description", annotation);
		
		doThrow(Exception.class).when(statement).evaluate();
		
		DataGeneratorManager gm = new DataGeneratorManager(entityManagerFactory);
		
		try {
			gm.apply(statement, description).evaluate();
		}
		catch (Throwable t) { /* Do nothing with the exception to let the test do its job */ }

		// The test evaluate method should never be called when before failed
		verify(statement, never()).evaluate();
		
		// Save for the after method on the generator
		assertEquals(1, gm.getDataGenerator(BeforeCrashGenerator.class).count);
	}
	
	@Test
	@RoxableTest(key = "5baec6dfc545")
	public void failingEvaluateMethodOnTestMethodShouldAvoidAfterToBeEvaluatedWhenExecuteAfterIsFalse() throws Throwable {
		DataGenerator annotation = new DataGenerator() {
			@Override
			public Class<? extends IDataGenerator>[] value() {
				return new Class[] { DoNotCrashGenerator.class };
			}

			@Override
			public Class<? extends Annotation> annotationType() {
				return DataGenerator.class;
			}

			@Override
			public boolean executeCleanup() {
				return false;
			}
		};
		
		Description description = Description.createSuiteDescription("Some description", annotation);
		
		doThrow(Exception.class).when(statement).evaluate();
		
		DataGeneratorManager gm = new DataGeneratorManager(entityManagerFactory);
		
		try {
			gm.apply(statement, description).evaluate();
		}
		catch (Throwable t) { /* Do nothing with the exception to let the test do its job */ }
		
		// After should not be called when a test fails
		assertEquals(1, gm.getDataGenerator(DoNotCrashGenerator.class).count);
	}

	@Test
	@RoxableTest(key = "4d88eb036a72")
	public void generatorForGivenDescrptionShouldBeAvailableIntoTheTest() throws Throwable {
		DataGenerator annotation = new DataGenerator() {
			@Override
			public Class<? extends IDataGenerator>[] value() {
				return new Class[] { DoNotCrashGenerator.class };
			}

			@Override
			public Class<? extends Annotation> annotationType() {
				return DataGenerator.class;
			}
			
			@Override
			public boolean executeCleanup() {
				return true;
			}
		};
		
		Description description = Description.createSuiteDescription("Some description", annotation);
		
		DataGeneratorManager gm = new DataGeneratorManager(entityManagerFactory);
		gm.apply(statement, description).evaluate();

		assertNotNull(gm.getDataGenerator(DoNotCrashGenerator.class));
	}

	@Test
	@RoxableTest(key = "f7fd1e684d9a")
	public void registeringTwoGeneratorOfTheSameTypeShouldBeForbiden() throws Throwable {
		DataGenerator annotation = new DataGenerator() {
			@Override
			public Class<? extends IDataGenerator>[] value() {
				return new Class[] { DoNotCrashGenerator.class, DoNotCrashGenerator.class };
			}

			@Override
			public Class<? extends Annotation> annotationType() {
				return DataGenerator.class;
			}

			@Override
			public boolean executeCleanup() {
				return true;
			}
		};
		
		Description description = Description.createSuiteDescription("Some description", annotation);
		DataGeneratorManager gm = new DataGeneratorManager(entityManagerFactory);
		
		try {
			gm.apply(statement, description).evaluate();
			fail("The data generator exception should be raised to prevent registering two generators of the same type.");
		}
		catch (DataGeneratorException dge) { /* Do nothing */ }
	}

	@Test
	@RoxableTest(key = "97515a548141")
	public void generatorWithDaoShouldHaveTheDaoNotNull() throws Throwable {
		DataGenerator annotation = new DataGenerator() {
			@Override
			public Class<? extends IDataGenerator>[] value() {
				return new Class[] { DataGeneratorWithDao.class };
			}

			@Override
			public Class<? extends Annotation> annotationType() {
				return DataGenerator.class;
			}

			@Override
			public boolean executeCleanup() {
				return true;
			}
		};
		
		Description description = Description.createSuiteDescription("Some description", annotation);
		DataGeneratorManager gm = new DataGeneratorManager(entityManagerFactory);
		gm.apply(statement, description).evaluate();

		assertNotNull(gm.getDataGenerator(DataGeneratorWithDao.class).firstDao);
	}

	@Test
	@RoxableTest(key = "9b956721084f")
	public void inheritedGeneratorWithDaoShouldHaveAllInheritedDaoNotNull() throws Throwable {
		DataGenerator annotation = new DataGenerator() {
			@Override
			public Class<? extends IDataGenerator>[] value() {
				return new Class[] { DataGeneratorWithInheritanceAndDaos.class };
			}

			@Override
			public Class<? extends Annotation> annotationType() {
				return DataGenerator.class;
			}

			@Override
			public boolean executeCleanup() {
				return true;
			}
		};
		
		Description description = Description.createSuiteDescription("Some description", annotation);
		DataGeneratorManager gm = new DataGeneratorManager(entityManagerFactory);
		gm.apply(statement, description).evaluate();

		assertNotNull(gm.getDataGenerator(DataGeneratorWithInheritanceAndDaos.class).firstDao);
		assertNotNull(gm.getDataGenerator(DataGeneratorWithInheritanceAndDaos.class).secondDao);
	}
}
