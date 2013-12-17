package com.lotaris.junitee.dummy;

import com.lotaris.junitee.context.DataContext;
import com.lotaris.junitee.context.GeneratorContext;

/**
 * Custom data generator to have Contexts injected
 * 
 * @author Laurent Prevost, laurent.prevost@lotaris.com
 */
public class DummyGeneratorWithContexts {
	@DataContext
	public GeneratorContext generatorContextOne;
	
	@DataContext
	public GeneratorContext generatorContextTwo;
}