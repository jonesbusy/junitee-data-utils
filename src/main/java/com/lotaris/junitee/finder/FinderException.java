package com.lotaris.junitee.finder;

/**
 * Dedicated exception for the finders
 * 
 * @author Laurent Prevost <laurent.prevost@lotaris.com>
 */
public class FinderException extends Exception {
	public FinderException() {}

	public FinderException(String message) {
		super(message);
	}

	public FinderException(Throwable cause) {
		super(cause);
	}

	public FinderException(String message, Throwable cause) {
		super(message, cause);
	}
}
