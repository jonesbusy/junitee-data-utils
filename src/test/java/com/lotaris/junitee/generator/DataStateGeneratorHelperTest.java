package com.lotaris.junitee.generator;

import com.lotaris.rox.annotations.RoxableTest;
import java.lang.reflect.Field;
import java.util.Properties;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Laurent Prevost, laurent.prevost@lotaris.com
 */
public class DataStateGeneratorHelperTest {
	@Before
	public void setup() {
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
	}
	
	@Test
	@RoxableTest(key = "7e991aef7754")
	public void stateGeneratorShouldBeNullWhenConfigureMethodIsNotCalledOnHelper() {
		assertNull("A data state generator was found where none should be found", DataStateGeneratorHelper.getStateGenerator());
	}
	
	@Test
	@RoxableTest(key = "3993b394446d")
	public void onlyOneInstanceOfDataStateGeneratorMustBeInstantiatedAcrossCompleteTestRunAndHelperStateShouldBeCoherent() {
		Properties properties = new Properties();
		
		properties.setProperty("junitee.stategenerator.class", "com.lotaris.junitee.dummy.DummyDataStateGenerator");
		
		DataStateGeneratorHelper.configure(properties);
		
		Object o1 = DataStateGeneratorHelper.getStateGenerator();
		assertNotNull("First state generator must not be null", o1);

		Object o2 = DataStateGeneratorHelper.getStateGenerator();
		assertNotNull("Second state generator must not be null", o2);

		assertEquals("The data state generator should be the same two consecutive calls to the getStateGenerator method.", o1, o2);
	}
	
	@Test
	@RoxableTest(key = "d14e453c76cf")
	public void noStateGeneratorShouldBeAvailableBeforeGetStateGeneratorIsCalled() {
		assertFalse("A state generator is available where it should not be the case.", DataStateGeneratorHelper.hasStateGenerator());
	}
	
	@Test
	@RoxableTest(key = "dd60a0a6f0e8")
	public void stateGeneratorShouldBeAvailableAfterGetStateGeneratorIsCalled() {
		Properties properties = new Properties();
		
		properties.setProperty("junitee.stategenerator.class", "com.lotaris.junitee.dummy.DummyDataStateGenerator");
		
		DataStateGeneratorHelper.configure(properties);
		
		DataStateGeneratorHelper.getStateGenerator();
		
		assertTrue("No state generator is available.", DataStateGeneratorHelper.hasStateGenerator());
	}
}
