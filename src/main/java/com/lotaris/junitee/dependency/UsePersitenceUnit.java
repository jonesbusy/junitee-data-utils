package com.lotaris.junitee.dependency;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Allows to override the default
 * 
 * @author Valentin Delaye <valentin.delaye@novaccess.ch>
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface UsePersitenceUnit {
	/**
	 * @return List class that can be used to mock other classes
	 */
	String name();
}
