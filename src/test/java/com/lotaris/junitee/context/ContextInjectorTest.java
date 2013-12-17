package com.lotaris.junitee.context;

import com.lotaris.junitee.dummy.DummyGeneratorWithContexts;
import static org.junit.Assert.*;

import com.lotaris.junitee.dummy.GeneratorWithContextWithInheritance;
import com.lotaris.rox.annotations.RoxableTest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * @author Laurent Prevost, laurent.prevost@lotaris.com
 */
public class ContextInjectorTest {
	@Mock
	private GeneratorContext context;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	@RoxableTest(key = "26a641c6d724")
	public void classWithImplementationContextFieldAnnotatedShouldHaveContextInjected() {
		DummyGeneratorWithContexts injected = new DummyGeneratorWithContexts();
		
		ContextInjector.inject(injected, context);
		
		assertNotNull(injected.generatorContextOne);
		assertNotNull(injected.generatorContextTwo);
		assertEquals(injected.generatorContextOne, context);
		assertEquals(injected.generatorContextTwo, context);
	}

	@Test
	@RoxableTest(key = "b876e64689b8")
	public void generatorThatInheritsFromAnotherGeneratorShouldHaveAllTheAnnotatedFieldsInjectedAcrossInheritanceChain() {
		GeneratorWithContextWithInheritance injected = new GeneratorWithContextWithInheritance();
		
		ContextInjector.inject(injected, context);
		
		assertNotNull(injected.generatorContextOne);
		assertNotNull(injected.generatorContextTwo);
		assertNotNull(injected.generatorContextThree);
		assertEquals(injected.generatorContextOne, context);
		assertEquals(injected.generatorContextTwo, context);
		assertEquals(injected.generatorContextThree, context);
	}
}
