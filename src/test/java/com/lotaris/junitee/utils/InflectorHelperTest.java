package com.lotaris.junitee.utils;

import com.lotaris.junitee.utils.InflectorHelper;
import com.lotaris.rox.annotations.RoxableTest;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Laurent Prevost, laurent.prevost@lotaris.com
 */
public class InflectorHelperTest {
	public interface ICustomDao {}
	
	public class CustomDao implements ICustomDao {}
	
	@Test
	@RoxableTest(key = "4cad7922ddfe")
	public void interfaceNameShouldBeTransformedToImplementationName() {
		assertEquals(CustomDao.class.getCanonicalName(), InflectorHelper.retrieveInstantiableClassName(ICustomDao.class));
	}
	
	@Test
	@RoxableTest(key = "827e479e1ce7")
	public void classNameShouldBeTransformedToItsProperName() {
		assertEquals(CustomDao.class.getCanonicalName(), InflectorHelper.retrieveInstantiableClassName(CustomDao.class));
	}
}
