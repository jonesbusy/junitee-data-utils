package com.lotaris.junitee.utils;

/**
 * Exception when a class is not valid to be instantiated within junitee
 * 
 * @author Laurent Prevost <laurent.prevost@lotaris.com>
 */
public class NoValidClassException extends Exception {
	public NoValidClassException(String message) {
		super(message);
	}
}
