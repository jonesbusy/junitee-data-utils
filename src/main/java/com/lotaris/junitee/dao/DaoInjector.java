package com.lotaris.junitee.dao;

import com.lotaris.junitee.utils.InflectorHelper;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import javax.ejb.EJB;
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
				Class daoClass = dao.getClass();

				// Try to set the entity manager in the class hierarchy to the object
				do {
					for (Field emField : daoClass.getDeclaredFields()) {
						if (emField.getType() == EntityManager.class) {
							injectEntityManager(dao, emField, em);
						}
					}
					daoClass = daoClass.getSuperclass();
				} while (daoClass != null);
				
				// Inject the EJBs
				Map<Class, Object> registry = new HashMap<>();
				registry.put(dao.getClass(), dao);
				injectEjb(dao, dao.getClass(), em, registry);
			}
		}
	}
	
	/**
	 * Inject entity manager into an object
	 * 
	 * @param obj The object for which the entity manager should be injected
	 * @param field The field to inject
	 * @param em The entity manager to inject
	 */
	private static void injectEntityManager(Object obj, Field field, EntityManager em) {
		// Try to inject the entity manager to the DAO.
		try {
			field.setAccessible(true);
			field.set(obj, em);
			field.setAccessible(false);
		}
		catch (IllegalAccessException | IllegalArgumentException | SecurityException ex) {
			LOG.warn("Unable to set the entity manager on field {} the object {}", field.getName(), obj.getClass().getSimpleName(), ex);
		}
	}

	/**
	 * Utility method to inject the EJB to a field of an object
	 * 
	 * @param obj The object to inject the EJB instance
	 * @param field The field of the object to inject
	 * @param ejb The EJB to inject
	 */
	private static void injectEjbField(Object obj, Field field, Object ejb) {
		// Try to inject the entity manager to the DAO.
		try {
			field.setAccessible(true);
			field.set(obj, ejb);
			field.setAccessible(false);
		}
		catch (IllegalAccessException | IllegalArgumentException | SecurityException ex) {
			LOG.warn("Unable to set the EJB on field {} of the object {}", field.getName(), obj.getClass().getSimpleName(), ex);
		}
	}

	/**
	 * Inject recursively any EJB detected in an object graph through 
	 * Java Reflection. Store a cache of object to avoid infinite loops.
	 * 
	 * @param obj The object where to get the EJB fields
	 * @param cl The class of the object to check inheritance
	 * @param em The entity manager to inject when a field of that type is detected
	 * @param registry The cache of EJB
	 */
	private static void injectEjb(Object obj, Class cl, EntityManager em, Map<Class, Object> registry) {
		// Inject in the super class fields if any
		if (cl.getSuperclass() != Object.class) {
			injectEjb(cl.getSuperclass().cast(obj), cl.getSuperclass(), em, registry);
		}

		// Iterate on all the fields present on the class
		for (Field field : cl.getDeclaredFields()) {
			// Check if the field is an entity manager
			if (field.getType() == EntityManager.class) {
				injectEntityManager(obj, field, em);
			}
			
			// Check if the field is annotated
			else if (field.getAnnotation(EJB.class) != null) {
				// Try to retrieve an EJB already created
				Object ejb = registry.get(field.getType());
				
				// Create new ejb and store the reference to that ejb
				if (ejb == null) {
					try {
						ejb = field.getType().newInstance();
						registry.put(field.getType(), ejb);
						injectEjb(ejb, field.getType(), em, registry);
					}
					catch (IllegalAccessException | InstantiationException ex) {
						LOG.warn("Unable to create EJB {}", field.getType().getCanonicalName(), ex);
						continue;
					}
				}

				// Inject the ejb to the object field
				injectEjbField(obj, field, ejb);
			}
		}
	}
}
