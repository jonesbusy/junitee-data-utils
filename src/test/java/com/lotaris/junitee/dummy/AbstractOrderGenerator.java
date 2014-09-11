package com.lotaris.junitee.dummy;

import com.lotaris.junitee.generator.IDataGenerator;

/**
 * Generator to enforce the order of the generate/cleanup run
 * 
 * @author Laurent Prevost <laurent.prevost@lotaris.com>
 */
public abstract class AbstractOrderGenerator implements IDataGenerator {
	public static String order = "";
}
