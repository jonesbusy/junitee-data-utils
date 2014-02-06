package com.lotaris.junitee.generator;

/**
 * Dedicated exception for the generators
 * 
 * @author Laurent Prevost <laurent.prevost@lotaris.com>
 */
public class DataGeneratorException extends Exception {
	public DataGeneratorException() {}

	public DataGeneratorException(String message) {
		super(message);
	}

	public DataGeneratorException(Throwable cause) {
		super(cause);
	}

	public DataGeneratorException(String message, Throwable cause) {
		super(message, cause);
	}
}
