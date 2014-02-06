package com.lotaris.junitee.dummy;

import com.lotaris.junitee.generator.IDataGenerator;

/**
 * Data generator to check that the before and after are called
 * 
 * @author Laurent Prevost <laurent.prevost@lotaris.com>
 */
public class DoNotCrashGenerator implements IDataGenerator {

	public int count = 0;
	
	@Override
	public void generate() {
		count++;
	}
	
	@Override
	public void cleanup() {
		
	}
}
