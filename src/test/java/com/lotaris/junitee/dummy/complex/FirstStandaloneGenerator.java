package com.lotaris.junitee.dummy.complex;

import com.lotaris.junitee.generator.IDataGenerator;

/**
 *
 * @author Laurent Prevost <laurent.prevost@lotaris.com>
 */
public class FirstStandaloneGenerator implements IDataGenerator {

	@Override
	public void generate() {
	
	}

	@Override
	public void cleanup() {
	}
	
	public Object getGeneratedData() {
		return new Object();
	}
}
