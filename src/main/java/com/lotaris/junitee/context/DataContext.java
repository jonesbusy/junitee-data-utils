package com.lotaris.junitee.context;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * When context should be injected into a data generator
 * 
 * @author Laurent Prevost, laurent.prevost@lotaris.com
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DataContext {
}
