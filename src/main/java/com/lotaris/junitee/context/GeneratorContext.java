package com.lotaris.junitee.context;

import java.util.HashMap;
import java.util.Map;

/**
 * Extends the statement of Junit to add contextual data to be shared
 * across different generators.
 * 
 * @author Laurent Prevost, laurent.prevost@lotaris.com
 */
public class GeneratorContext {
	private Map<Class, Object> dataStore = new HashMap<>();

	/**
	 * Put an object linked to its class
	 * 
	 * @param <T> The class type
	 * @param cl The class of the type
	 * @param object The object the type
	 */
	public <T> void put(Class<? extends T> cl, T object) {
		dataStore.put(cl, object);
	}
	
	/**
	 * Retrieve an object by its class
	 * 
	 * @param <T> The type of the object
	 * @param cl The class of the object
	 * @return The object of the type
	 */
	public <T> T get(Class<? extends T> cl) {
		return (T) dataStore.get(cl);
	}
	
	/**
	 * Check if an object is already present in the store
	 * 
	 * @param <T> The type of the object
	 * @param cl The class of the object
	 * @return True if object is present
	 */
	public <T> boolean contains(Class<? extends T> cl) {
		return dataStore.containsKey(cl);
	}
}
