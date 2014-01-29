package com.lotaris.junitee.dependency;

import com.lotaris.junitee.utils.InflectorHelper;
import com.lotaris.junitee.utils.NoValidClassException;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

/**
 * Dependency injector helper offers method to facilitate the injections
 * of fields into objects and such things.
 * 
 * @author Laurent Prevost <laurent.prevost@lotaris.com>
 */
public class DependencyInjectorHelper {
	/**
	 * Empty constructor
	 */
	private DependencyInjectorHelper() {}
	
	/**
	 * Inject something into a field of the holder with the value provided
	 * 
	 * @param field The field to inject
	 * @param holder The object where the field is
	 * @param value The value to set to the field on the object
	 * @throws DependencyInjectionException  Any error during the injection
	 */
	static void injectField(Field field, Object holder, Object value) throws DependencyInjectionException {
		// Try to inject the entity manager to the DAO.
		boolean fieldMustBeChanged = field.isAccessible();
		if (!fieldMustBeChanged) {
			field.setAccessible(true);
		}
			
		try {
			field.set(holder, value);
		}
		catch (IllegalAccessException | IllegalArgumentException | SecurityException ex) {
			throw new DependencyInjectionException("Unable to set the value [" + value + "] on field ["
				+ field.getName() + "] of the object [" + holder.getClass().getSimpleName() + "].", ex);
		}
		finally {
			if (!fieldMustBeChanged) {
				field.setAccessible(false);
			}
		}
	}

	/**
	 * Find an implementation class from a class that is provided
	 * 
	 * @param cl The class to get the real implementation class
	 * @return The real implementation class that can be instantiated
	 * @throws DependencyInjectionException Unable to find a valid class that can be instantiated
	 */
	static Class findImplementationClass(Class cl) throws DependencyInjectionException {
		// Instantiate a new object based on class name (check if it is an interface or a concrete class).
		try {
			return InflectorHelper.retrieveInstantiableClassName(cl);
		}
		catch (NoValidClassException ex) {
			throw new  DependencyInjectionException("Unable to instantiate a new object from type [" + cl.getCanonicalName() + "]. Maybe there is no empty constructor.", ex);
		}		
	}
	
	/**
	 * Instantiate a new EJB instance of a class
	 * 
	 * @param ejbField The field that is marked to be injected with an EJB class instance
	 * @param ejbRegistry The EJB registry to reuse existing instance of EJB
	 * @return The new/reused instance of the EJB ready to be injected
	 * @throws DependencyInjectionException Error during the instantiation of the new EJB
	 */
	static Object instantiateEjb(Field ejbField, Map<String, Object> ejbRegistry) throws DependencyInjectionException {
		Class implementationClass = findImplementationClass(ejbField.getType());
		
		if (ejbRegistry.containsKey(implementationClass.getCanonicalName())) {
			return ejbRegistry.get(implementationClass.getCanonicalName());
		}
		else {
			try {
				Object instanceOfImplementationClass = implementationClass.newInstance();
				ejbRegistry.put(implementationClass.getCanonicalName(), instanceOfImplementationClass);
				return instanceOfImplementationClass;
			}
			catch (IllegalAccessException | InstantiationException e) {
				throw new DependencyInjectionException("Unable to instantiate the EJB.", e);
			}
		}
	}
	
	/**
	 * Instantiate a new Data Generator instance of a class
	 * 
	 * @param generatorField The field that is marked to be injected with a InjectDataGenerator instance
	 * @param path The current path to detect circular dependency injection
	 * @param generatorRegistry The data generator registry to check the circular dependency injection
	 * @return The new instance of the data generator ready to be injected
	 * @throws DependencyInjectionException Error during the instantiation of the new Data Generator
	 */
	static Object instantiateDataGenerator(Field generatorField, String path, Set<String> generatorRegistry) throws DependencyInjectionException {
		String currentPath = path + "." + generatorField.getName();
		Class implementationClass = findImplementationClass(generatorField.getType());

		// Check if the path has already been reached
		if (generatorRegistry.contains(currentPath)) {
			throw new DependencyInjectionException("The data generator " + implementationClass.getSimpleName() + " already exists for the path: " 
				+ currentPath + ". It seems that you have a loop in your data generator configuration that is not allowed.");
		}
		
		// Create new instance of the data generator
		else {
			try {
				Object instanceOfImplementationClass = implementationClass.newInstance();
				generatorRegistry.add(currentPath);
				return instanceOfImplementationClass;
			}
			catch (IllegalAccessException | InstantiationException e) {
				throw new DependencyInjectionException("Unable to instantiate the data generator.", e);
			}
		}
	}

	/**
	 * Retrieve the value of a field on the holder
	 * 
	 * @param field The field from which a value must be retrieved
	 * @param holder The object where the field is
	 * @return The value of the field from the holder
	 * @throws DependencyInjectionException When it is not possible to get the value of the field from the holder
	 */
	static Object getObject(Field field, Object holder) throws DependencyInjectionException{
		// Try to inject the entity manager to the DAO.
		boolean fieldMustBeChanged = field.isAccessible();
		if (!fieldMustBeChanged) {
			field.setAccessible(true);
		}
		
		try {
			return field.get(holder);
		}
		catch (IllegalAccessException | IllegalArgumentException e) {
			throw new DependencyInjectionException(
				"Unable to get the value on field [" + field.getName() + "] of the object [" + holder.getClass().getSimpleName() + "].", e);
		}
		finally {
			if (!fieldMustBeChanged) {
				field.setAccessible(false);
			}
		}
	}
	
	/**
	 * Check if a field is null on the holder
	 * 
	 * @param field The field to check on the holder
	 * @param holder The holder where the field is
	 * @return True if the field has no value (null value), false otherwise
	 * @throws DependencyInjectionException When it is not possible to retrieve the state of the field
	 */
	static boolean isNull(Field field, Object holder) throws DependencyInjectionException {
		return getObject(field, holder) == null;
	}
}
