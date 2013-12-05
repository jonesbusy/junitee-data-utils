package com.lotaris.junitee.generator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to configure Data Generators for
 * a test method or a class.
 * 
 * @author Laurent Prevost, laurent.prevost@lotaris.com
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DataGenerator {
	Class<? extends IDataGenerator>[] value();
}
