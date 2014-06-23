package com.lotaris.junitee.dependency;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Allows to inject Mocks in place of real implementation of classes
 * 
 * @author Laurent Prevost <laurent.prevost@lotaris.com>
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UseMock {
	/**
	 * @return List class that can be used to mock other classes
	 */
	Class<?>[] value();
}
