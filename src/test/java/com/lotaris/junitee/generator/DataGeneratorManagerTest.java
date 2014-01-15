package com.lotaris.junitee.generator;

import com.lotaris.junitee.dummy.BeforeCrashGenerator;
import com.lotaris.junitee.dummy.DataGeneratorWithDao;
import com.lotaris.junitee.dummy.DataGeneratorWithInheritanceAndDaos;
import com.lotaris.junitee.dummy.DoNotCrashGenerator;
import com.lotaris.junitee.dummy.DummyDataStateGenerator;
import com.lotaris.rox.annotations.RoxableTest;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import javax.persistence.EntityManager;
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
 * @author Laurent Prevost, laurent.prevost@lotaris.com
 */
public class DataGeneratorManagerTest {
	@Mock
	private Statement statement;
	
	@Mock
	private EntityManager em;
	
	@Mock
	private EntityTransaction et;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
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
			public boolean executeAfter() {
				return true;
			}
		};
		
		Description description = Description.createSuiteDescription("Some description", annotation);
		
		when(em.getTransaction()).thenReturn(et);
		
		doThrow(Exception.class).when(statement).evaluate();
		
		DataGeneratorManager gm = new DataGeneratorManager(em);
		
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
	@RoxableTest(key = "3e82d8f2891e")
	public void failingEvaluateMethodOnTestMethodShouldRunAfterWhenExecuteAfterIsTrue() throws Throwable {
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
			public boolean executeAfter() {
				return true;
			}
		};
		
		Description description = Description.createSuiteDescription("Some description", annotation);
		
		when(em.getTransaction()).thenReturn(et);
		
		doThrow(Exception.class).when(statement).evaluate();
		
		DataGeneratorManager gm = new DataGeneratorManager(em);
		
		try {
			gm.apply(statement, description).evaluate();
		}
		catch (Throwable t) { /* Do nothing with the exception to let the test do its job */ }
		
		// After should not be called when a test fails
		assertEquals(2, gm.getDataGenerator(DoNotCrashGenerator.class).count);
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
			public boolean executeAfter() {
				return false;
			}
		};
		
		Description description = Description.createSuiteDescription("Some description", annotation);
		
		when(em.getTransaction()).thenReturn(et);
		
		doThrow(Exception.class).when(statement).evaluate();
		
		DataGeneratorManager gm = new DataGeneratorManager(em);
		
		try {
			gm.apply(statement, description).evaluate();
		}
		catch (Throwable t) { /* Do nothing with the exception to let the test do its job */ }
		
		// After should not be called when a test fails
		assertEquals(1, gm.getDataGenerator(DoNotCrashGenerator.class).count);
	}

	@Test
	@RoxableTest(key = "a4ed12539e67")
	public void afterMethodOnGeneratorMethodShouldBeRunWhenNoFaliuresOccurInBeforeOrEvaluate() throws Throwable {
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
			public boolean executeAfter() {
				return true;
			}
		};
		
		Description description = Description.createSuiteDescription("Some description", annotation);
		
		when(em.getTransaction()).thenReturn(et);
		
		DataGeneratorManager gm = new DataGeneratorManager(em);
		gm.apply(statement, description).evaluate();
		
		// After should be called when everything goes well
		assertEquals(2, gm.getDataGenerator(DoNotCrashGenerator.class).count);
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
			public boolean executeAfter() {
				return true;
			}
		};
		
		Description description = Description.createSuiteDescription("Some description", annotation);
		
		when(em.getTransaction()).thenReturn(et);
		DataGeneratorManager gm = new DataGeneratorManager(em);
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
			public boolean executeAfter() {
				return true;
			}
		};
		
		when(em.getTransaction()).thenReturn(et);

		Description description = Description.createSuiteDescription("Some description", annotation);
		DataGeneratorManager gm = new DataGeneratorManager(em);
		
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
			public boolean executeAfter() {
				return true;
			}
		};
		
		when(em.getTransaction()).thenReturn(et);

		Description description = Description.createSuiteDescription("Some description", annotation);
		DataGeneratorManager gm = new DataGeneratorManager(em);
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
			public boolean executeAfter() {
				return true;
			}
		};
		
		when(em.getTransaction()).thenReturn(et);

		Description description = Description.createSuiteDescription("Some description", annotation);
		DataGeneratorManager gm = new DataGeneratorManager(em);
		gm.apply(statement, description).evaluate();

		assertNotNull(gm.getDataGenerator(DataGeneratorWithInheritanceAndDaos.class).firstDao);
		assertNotNull(gm.getDataGenerator(DataGeneratorWithInheritanceAndDaos.class).secondDao);
	}

	@Test
	@RoxableTest(key = "9bada00f878e")
	public void runTenTestMethodsShouldSeeOnlyOneConfigurationOfDataStateGeneratorAndTenCreateStateAndTenRestoreState() throws Throwable {
		// Be sure that the dummy state generator is ready to be tested
		try {
			Field f = DataStateGeneratorHelper.class.getDeclaredField("stateGenerator");
			f.setAccessible(true);
			try {
				f.set(null, null);
			}
			catch (IllegalAccessException | IllegalArgumentException e) {
				throw new RuntimeException("Unable to reset the field stateGenerator on DataStateGeneratorHelper", e);
			}
		}
		catch (NoSuchFieldException | SecurityException e) {
			throw new RuntimeException("Unable to get the stateGenerator field on DataStateGeneratorHelper to do the tricky tests.", e);
		}		
		
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
			public boolean executeAfter() {
				return true;
			}
		};
		
		when(em.getTransaction()).thenReturn(et);

		for (int i = 0; i < 10; i++) {
			Description description = Description.createSuiteDescription("Some description", annotation);
			DataGeneratorManager gm = new DataGeneratorManager(em);
			gm.apply(statement, description).evaluate();
		}
		
		assertEquals("Only one configure call should happens", 1, DummyDataStateGenerator.configureCnt);
		assertEquals("Ten create state calls should happen", 10, DummyDataStateGenerator.createStateCnt);
		assertEquals("Ten resore state calls should happen", 10, DummyDataStateGenerator.restoreStateCnt);
	}
}
