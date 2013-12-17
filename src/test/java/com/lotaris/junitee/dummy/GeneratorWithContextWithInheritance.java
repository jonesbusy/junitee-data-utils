package com.lotaris.junitee.dummy;

import com.lotaris.junitee.context.DataContext;
import com.lotaris.junitee.context.GeneratorContext;

/**
 * A data generator that inherits from another must have all the
 * contexts injected.
 * 
 * @author Laurent Prevost, laurent.prevost@lotaris.com
 */
public class GeneratorWithContextWithInheritance extends DummyGeneratorWithContexts {
	@DataContext
	public GeneratorContext generatorContextThree;
}
