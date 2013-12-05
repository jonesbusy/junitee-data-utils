package com.lotaris.junitee.dummy;

import com.lotaris.junitee.generator.IDataGenerator;

/**
 * Generator to check that evaluate and after are not called.
 * 
 * @author Laurent Prevost, laurent.prevost@lotaris.com
 */
public class BeforeCrashGenerator implements IDataGenerator {

	public int count = 0;
	
	@Override
	public void before() {
		count++;
		throw new RuntimeException("Exception to validate that the after is not called.");
	}

	@Override
	public void after() {
		count++;
	}
}
