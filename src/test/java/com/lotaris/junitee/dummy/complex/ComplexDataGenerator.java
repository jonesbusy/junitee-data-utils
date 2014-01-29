package com.lotaris.junitee.dummy.complex;

import com.lotaris.junitee.generator.CompositeDataGenerator;
import com.lotaris.junitee.generator.InjectDataGenerator;

/**
 *
 * @author Laurent Prevost <laurent.prevost@lotaris.com>
 */
public class ComplexDataGenerator extends CompositeDataGenerator {

	@InjectDataGenerator
	private FirstStandaloneGenerator firstGenerator;
	
	@InjectDataGenerator
	private SecondStandaloneGenerator secondGenerator;

	@Override
	protected void setup() {
		register(firstGenerator);
		register(secondGenerator.dependsOn(firstGenerator));
	}
	
	@Override
	protected void generate() {
		
	}
	
}
