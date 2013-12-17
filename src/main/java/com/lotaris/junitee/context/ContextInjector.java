package com.lotaris.junitee.context;

import com.lotaris.junitee.dao.*;
import java.lang.reflect.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class to inject context into the generators
 * 
 * @author Laurent Prevost, laurent.prevost@lotaris.com
 */
public class ContextInjector {
	private static final Logger LOG = LoggerFactory.getLogger(DaoInjector.class);

	private ContextInjector() {}

	/**
	 * Analyze the object to inject generator context on every field
	 * annotated with the {@link DataContext}
	 * 
	 * @param obj The object to get all the fields that must be injected
	 * @param context The context to inject
	 */
	public static void inject(Object obj, GeneratorContext context) {
		inject(obj, obj.getClass(), context);
	}
	
	/**
	 * Do the real injection recursively on the class hierarchy
	 * 
	 * @param obj The object to get the fields
	 * @param cl The current stage of inheritance class
	 * @param context The context to inject
	 */
	private static void inject(Object obj, Class cl, GeneratorContext context) {
		// Inject in the super class fields if any
		if (cl.getSuperclass() != Object.class) {
			inject(cl.getSuperclass().cast(obj), cl.getSuperclass(), context);
		}

		// Get all the fields of the object to check if the annotation is present.
		for (Field field : cl.getDeclaredFields()) {
			DataContext dataContextAnnotation = field.getAnnotation(DataContext.class);
			if (dataContextAnnotation != null) {
				if (field.getType() != GeneratorContext.class) {
					LOG.warn("Unable to set the context to the generator: {}", cl);
					continue;
				}
				
				// Set the context instance to the object.
				try {
					field.setAccessible(true);
					field.set(obj, context);
					field.setAccessible(false);
				}
				catch (IllegalAccessException | IllegalArgumentException ex) {
					LOG.warn("Unable to set the context to the generator: {}", cl, ex);
					continue;
				}
			}
		}
	}
}
