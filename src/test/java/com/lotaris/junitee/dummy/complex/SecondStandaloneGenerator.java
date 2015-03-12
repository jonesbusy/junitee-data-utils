package com.lotaris.junitee.dummy.complex;

import com.lotaris.junitee.generator.CompositeDataGenerator;

/**
 *
 * @author Laurent Prevost <laurent.prevost@lotaris.com>
 */
public class SecondStandaloneGenerator extends CompositeDataGenerator {
	
	@Override
	protected void generateData() {
		Object generatedData = getDataGenerator(FirstStandaloneGenerator.class).getGeneratedData();
	}

	@Override
	protected void cleanData() {
	}
}
