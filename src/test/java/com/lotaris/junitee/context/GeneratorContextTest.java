package com.lotaris.junitee.context;

import static org.junit.Assert.*;

import com.lotaris.rox.annotations.RoxableTest;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Laurent Prevost, laurent.prevost@lotaris.com
 */
public class GeneratorContextTest {
	private GeneratorContext context;
	
	@Before
	public void setup() {
		context = new GeneratorContext();
	}
	
	@Test
	@RoxableTest(key = "1ed6b06ef9d8")
	public void retrievingTheSameObjectPutInTheGeneratorContextShouldBePossible() {
		Object obj = new Object();
		
		context.put(Object.class, obj);
		
		assertNotNull(context.get(Object.class));
		assertEquals(obj, context.get(Object.class));
	}

	@Test
	@RoxableTest(key = "9a9340d4bbf6")
	public void puttingAnObjectOfTheSameTypeShouldOverrideTheFirstPut() {
		Object obj1 = new Object();
		Object obj2 = new Object();
		
		context.put(Object.class, obj1);
		context.put(Object.class, obj2);
		
		assertNotNull(context.get(Object.class));
		assertNotEquals(obj1, context.get(Object.class));
		assertEquals(obj2, context.get(Object.class));
	}

	@Test
	@RoxableTest(key = "0bdad1733d7a")
	public void usingHasMethodOnTheGeneratorContextShouldAnswerTrueForAnObjectTypePresentInTheContext() {
		Object obj = new Object();
		
		context.put(Object.class, obj);
		
		assertTrue(context.contains(Object.class));
	}
}
