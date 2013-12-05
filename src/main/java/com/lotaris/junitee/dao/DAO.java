package com.lotaris.junitee.dao;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation should be use to inject an entity manager to
 * the fields annotated
 * 
 * @author Laurent Prevost, laurent.prevost@lotaris.com
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DAO {
	/**
	 * Define the name of the field to set the entity manager
	 * @return The field name, empty name if not filled
	 */
	String fieldName() default "";
}
