package com.lotaris.junitee.dummy;

import static com.lotaris.junitee.dummy.AbstractOrderGenerator.order;

/**
 * Generator TWO to test execution order
 * 
 * @author Laurent Prevost <laurent.prevost@lotaris.com>
 */
public class OrderTwoGenerator extends AbstractOrderGenerator {
	@Override
	public void generate() {
		order += "2";
	}
	
	@Override
	public void cleanup() {
		order += "3";
	}
}
