package com.lotaris.junitee.dao;

import com.lotaris.junitee.utils.InflectorHelper;
import java.lang.reflect.Field;
import javax.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class to inject DAO correctly into an object
 * 
 * @author Laurent Prevost, laurent.prevost@lotaris.com
 */
public class DaoInjector {
	private static final Logger LOG = LoggerFactory.getLogger(DaoInjector.class);

	private DaoInjector() {}

	/**
	 * Analyzes the object to get the fields annotated for injections
	 * and inject to those fields the entity manager given.
	 * <p>
	 * During the injection, new instances of DAO are created
	 * 
	 * @param obj The object to get all the fields that must be injected
	 * @param em 
	 */
	public static void inject(Object obj, EntityManager em) {
		inject(obj, obj.getClass(), em);
	}
	
	/**
	 * Recursively apply the injection of DAO
	 * 
	 * @param obj The object to get the fields
	 * @param cl The current stage of inheritance class
	 * @param em The entity manager to inject
	 */
	private static void inject(Object obj, Class cl, EntityManager em) {
		// Inject in the super class fields if any
		if (cl.getSuperclass() != Object.class) {
			inject(cl.getSuperclass().cast(obj), cl.getSuperclass(), em);
		}
		
		// Get all the fields of the object to check if the annotation is present.
		for (Field field : cl.getDeclaredFields()) {
			DAO daoAnnotation = field.getAnnotation(DAO.class);
			if (daoAnnotation != null) {
				// Instantiate a DAO based on the implementation class discovered just before.
				Object dao;
				try {
					dao = DaoInjector.class.getClassLoader().loadClass(InflectorHelper.retrieveInstantiableClassName(field.getType())).newInstance();
				}
				catch (ClassNotFoundException | IllegalAccessException | InstantiationException ex) {
					LOG.warn("Unable to instantiate the DAO from the type {}", field.getType().getCanonicalName(), ex);
					continue;
				}

				// Set the DAO instance to the object.
				try {
					field.setAccessible(true);
					field.set(obj, dao);
					field.setAccessible(false);
				}
				catch (IllegalAccessException | IllegalArgumentException ex) {
					LOG.warn("Unable to set the DAO instance to the current test class.", ex);
					continue;
				}
				
				// Prepare working variables for the entity manager injection.
				Field emField = null;
				String emFieldName = daoAnnotation.fieldName().isEmpty() ? "em" : daoAnnotation.fieldName();
				Class daoClass = dao.getClass();

				// Try to find the first field that correspond to the name.
				do {
					try {
						emField = daoClass.getDeclaredField(emFieldName);
					}
					catch (NoSuchFieldException | SecurityException ex) {
						daoClass = daoClass.getSuperclass();
					}
				} while (daoClass != null && emField == null);
					
				// Be sure a field is found, otherwise continue to the remaining injections to do.
				if (emField == null) {
					LOG.warn("Unable to retrieve the field {} on the DAO {}", emFieldName, dao.getClass().getSimpleName());
					continue;
				}
				
				// Try to inject the entity manager to the DAO.
				try {
					emField.setAccessible(true);
					emField.set(dao, em);
					emField.setAccessible(false);
				}
				catch (IllegalAccessException | IllegalArgumentException | SecurityException ex) {
					LOG.warn("Unable to set the entity manager on the DAO {}", dao.getClass().getSimpleName(), ex);
					continue;
				}
			}
		}
	}
}
