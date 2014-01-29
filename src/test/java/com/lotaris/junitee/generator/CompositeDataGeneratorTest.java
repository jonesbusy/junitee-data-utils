package com.lotaris.junitee.generator;

import com.lotaris.junitee.dummy.complex.ComplexDataGenerator;
import com.lotaris.rox.annotations.RoxableTest;
import com.lotaris.rox.annotations.RoxableTestClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test the composite data generator
 *
 * @author Laurent Prevost <laurent.prevost@lotaris.com>
 */
@RoxableTestClass(tags = "composite-data-generator")
public class CompositeDataGeneratorTest {
	@Test
	@RoxableTest(key = "fefe06a3725d")
	public void runningCompositeDataGeneratorWithOnlyOneDataGeneratorRegisteredShouldRunCorrectly() {
		final ObjectChecker oc = new ObjectChecker();
		
		final IDataGenerator dg = new IDataGenerator() { @Override public void run() { oc.runCalled++; oc.orderChecker += "+run"; } };
		
		CompositeDataGenerator cdg = new CompositeDataGenerator() {
			@Override protected void setup() { register(dg); oc.setupCalled = true; oc.orderChecker += "+setup"; }
			@Override protected void generate() { oc.generateCalled = true; oc.orderChecker += "+generate"; }
		};
		
		cdg.run();
		
		assertEquals("Data generator run method should be called", 1, oc.runCalled);
		assertTrue("Composite data generator setup method should be called", oc.setupCalled);
		assertTrue("Composite data generator generate method should be called", oc.generateCalled);
		
		assertEquals("Methods should be called in that order: setup - run - generate", "+setup+run+generate", oc.orderChecker);
	}

	@Test
	@RoxableTest(key = "470f04b03932")
	public void runningCompositeDataGeneratorWithoutDataGeneratorRegisteredShouldRunCorrectly() {
		final ObjectChecker oc = new ObjectChecker();
		
		CompositeDataGenerator cdg = new CompositeDataGenerator() {
			@Override protected void setup() { oc.setupCalled = true; oc.orderChecker += "+setup"; }
			@Override protected void generate() { oc.generateCalled = true; oc.orderChecker += "+generate"; }
		};
		
		cdg.run();
		
		assertEquals("Data generator run method should be called", 0, oc.runCalled);
		assertTrue("Composite data generator setup method should be called", oc.setupCalled);
		assertTrue("Composite data generator generate method should be called", oc.generateCalled);
		
		assertEquals("Methods should be called in that order: setup - generate", "+setup+generate", oc.orderChecker);
	}

	@Test
	@RoxableTest(key = "8fff70694587")
	public void runningCompositeDataGeneratorWithThreeDataGeneratorsRegisteredShouldRunCorrectly() {
		final ObjectChecker oc = new ObjectChecker();
		
		CompositeDataGenerator cdg = new CompositeDataGenerator() {
			@Override protected void setup() { 
				oc.setupCalled = true; 
				oc.orderChecker += "+setup(beforeRegister)"; 

				register(new IDataGenerator() { @Override public void run() { oc.runCalled++; oc.orderChecker += "+dg1"; } });
				register(new IDataGenerator() { @Override public void run() { oc.runCalled++; oc.orderChecker += "+dg2"; } });
				register(new IDataGenerator() { @Override public void run() { oc.runCalled++; oc.orderChecker += "+dg3"; } });

				oc.orderChecker += "+setup(afterRegister)"; 
			}
			
			@Override protected void generate() { oc.generateCalled = true; oc.orderChecker += "+generate"; }
		};
		
		cdg.run();
		
		assertEquals("Data generators run method should be called", 3, oc.runCalled);
		assertTrue("Composite data generator setup method should be called", oc.setupCalled);
		assertTrue("Composite data generator generate method should be called", oc.generateCalled);
		
		assertEquals("Methods should be called in that order: setup - run(dg1) - run(dg2) - run(dg3) - generate", 
			"+setup(beforeRegister)+setup(afterRegister)+dg1+dg2+dg3+generate", oc.orderChecker);
	}
	
	@Test
	@RoxableTest(key = "2a7f978a8783")
	public void overidingSetupMethodShouldNotBeMandatoryIfNoDataGeneratorIsRequired() {
		final ObjectChecker oc = new ObjectChecker();
		
		CompositeDataGenerator cdg = new CompositeDataGenerator() {
			@Override protected void generate() { oc.generateCalled = true; oc.orderChecker += "+generate"; }
		};
		
		cdg.run();
		
		assertEquals("Data generator run method should be called", 0, oc.runCalled);
		assertFalse("Composite data generator setup method should not be called", oc.setupCalled);
		assertTrue("Composite data generator generate method should be called", oc.generateCalled);
		
		assertEquals("Methods should be called in that order: setup - run(dg1) - run(dg2) - run(dg3) - generate", "+generate", oc.orderChecker);
	}

	@Test
	@RoxableTest(key = "8f67e877c063")
	public void itShouldNotBePossibleToDependsCompositeDataGeneratorOnItself() {
		CompositeDataGenerator cdg = new CompositeDataGenerator() {
			private CompositeDataGenerator internalComposite = new CompositeDataGenerator() { @Override protected void generate() {} };

			@Override protected void setup() { internalComposite.dependsOn(internalComposite); }
			
			@Override protected void generate() {}
		};
		
		try {
			cdg.run();
			fail("It should not be possible to depend a data generator on itself");
		}
		catch (IllegalArgumentException iae) {
			assertEquals("The data generator cannot depends on itself.", iae.getMessage());
		}
	}
	
	@Test
	@RoxableTest(key = "eb5b6abaafe6")
	public void itShouldNotBePossibleToDependsTwiceOnTheSameDataGenerator() {
		CompositeDataGenerator cdg = new CompositeDataGenerator() {
			CompositeDataGenerator internalComposite = new CompositeDataGenerator() { @Override protected void generate() {} };

			@Override protected void setup() { 
				dependsOn("dg1", internalComposite);
				dependsOn("dg1", internalComposite);
			}
			
			@Override protected void generate() {}
		};
		
		try {
			cdg.run();
			fail("It should not be possible to depend on the same data generator twice");
		}
		catch (IllegalArgumentException iae) {
			assertEquals("Data generator dg1 already registered.", iae.getMessage());
		}
	}
	
	@Test
	@RoxableTest(key = "9867a0e096c2")
	public void itShouldNotBePossibleToDependsWhenNullNameIsProvided() {
		CompositeDataGenerator cdg = new CompositeDataGenerator() {
			CompositeDataGenerator internalComposite = new CompositeDataGenerator() { @Override protected void generate() {} };

			@Override protected void setup() { 
				dependsOn(null, internalComposite);
			}
			
			@Override protected void generate() {}
		};
		
		try {
			cdg.run();
			fail("It should not be possible to depend on a data generator when no name is provided");
		}
		catch (IllegalArgumentException iae) {
			assertEquals("The data generator name should be provided.", iae.getMessage());
		}
	}
	
	@Test
	@RoxableTest(key = "7c31c81e9215")
	public void settingValuesToTheCompositeDataGeneratorConfigurationShouldAllowUsingThemIntoTheGenerateMethod() {
		CompositeDataGenerator cdg = new CompositeDataGenerator() {

			@Override
			protected void setup() {
				numberToGenerate(10).usePrefix("test");
			}

			
			
			@Override
			protected void generate() {
				assertEquals("Number to generate should be correct", 10, getNumberToGenerate());
				assertEquals("Use prefix should be correct", "test", getPrefix());
			}
		};
		
		cdg.run();
	}

	private static class ObjectChecker {
		private boolean setupCalled = false;
		private boolean generateCalled = false;
		private int runCalled = 0;
		
		private String orderChecker = "";
	}
}
