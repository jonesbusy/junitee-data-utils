package com.lotaris.junitee.generator;

import com.lotaris.rox.annotations.RoxableTest;
import com.lotaris.rox.annotations.RoxableTestClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test class for the complext data generator
 * 
 * @author Laurent Prevost <laurent.prevost@lotaris.com>
 */
@RoxableTestClass(tags = "data-generator-runner")
public class DataGeneratorRunnerTest {
	@Test
	@RoxableTest(key = "da0a866d3b98")
	public void creatingAndRunningDataGeneratorRunnerWithOneDataGeneratorShouldWork() {
		final ObjectChecker oc = new ObjectChecker();
		
		IDataGenerator dg = new IDataGenerator() { @Override public void run() { oc.count++; } };

		DataGeneratorRunner dgr = new DataGeneratorRunner(dg);

		assertNotNull("Data generator should be registered in the runner", dgr.getDataGenerator());

		dgr.execute();
		
		assertEquals("Only one runs should be done on data generator runner", 1, oc.count);
	}
	
	@Test
	@RoxableTest(key = "c6be936166d1")
	public void nextDataGeneratorRunnerShouldBeNullWhenCreatingDataGeneratorRunnerWithOneDataGenerator() {
		IDataGenerator dg = new IDataGenerator() { @Override public void run() { } };

		DataGeneratorRunner dgr = new DataGeneratorRunner(dg);
		
		assertNull("No next runner should be available", dgr.getNext());
	}
	
	@Test
	@RoxableTest(key = "3cde9a9a97ca")
	public void creatingAndRunningDataGeneratorRunnerWithTwoDataGeneratorsShouldWork() {
		final ObjectChecker oc = new ObjectChecker();
		
		IDataGenerator dg1 = new IDataGenerator() { @Override public void run() { oc.count++; } };

		DataGeneratorRunner dgr1 = new DataGeneratorRunner(dg1);

		assertNotNull("First data generator should be registered in the runner", dgr1.getDataGenerator());
		
		IDataGenerator dg2 = new IDataGenerator() { @Override public void run() { oc.count++; } };
		
		DataGeneratorRunner dgr2 = dgr1.setNext(dg2);
		
		assertNotNull("Second data generator should be registered in the runner", dgr2);
		assertNotEquals("Second data generator runner should not be the same as the first one", dgr1, dgr2);

		dgr2.execute();
		
		assertEquals("Running the second generator runner should only run the second generator", 1, oc.count);
		
		dgr1.execute();
		
		assertEquals("Running the first generator runner should run all the generators", 3, oc.count);
	}
	
	@Test
	@RoxableTest(key = "97a3ef75e710")
	public void creatingDataGeneratorRunnerChainShouldKeepTheCorrectOrderInDataStructureAndRun() {
		final ObjectChecker oc = new ObjectChecker();
		
		IDataGenerator dg1 = new IDataGenerator() { @Override public void run() { oc.stringChecker += "+dg1"; } };
		IDataGenerator dg2 = new IDataGenerator() { @Override public void run() { oc.stringChecker += "+dg2"; } };
		IDataGenerator dg3 = new IDataGenerator() { @Override public void run() { oc.stringChecker += "+dg3"; } };

		DataGeneratorRunner dgr = new DataGeneratorRunner(dg1);

		dgr.setNext(dg2).setNext(dg3);

		assertEquals("First data generator should be dg1", dg1, dgr.getDataGenerator());
		assertEquals("Second data generator should be dg2", dg2, dgr.getNext().getDataGenerator());
		assertEquals("Third data generator should be dg3", dg3, dgr.getNext().getNext().getDataGenerator());
		assertNull("No forth data generator should be present", dgr.getNext().getNext().getNext());
		
		dgr.execute();
		
		assertEquals("Executing data generator runner should respect the order", "+dg1+dg2+dg3", oc.stringChecker);
	}
	
	private static class ObjectChecker {
		private int count = 0;
		private String stringChecker = "";
	}
}
