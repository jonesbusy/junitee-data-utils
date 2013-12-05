package com.lotaris.junitee.utils;

/**
 * Helper class to extract class names
 * 
 * @author Laurent Prevost, laurent.prevost@lotaris.com
 */
public final class InflectorHelper {
	private InflectorHelper() {}
	
	public static String retrieveInstantiableClassName(Class cl) {
		String simpleName = cl.getSimpleName();
		String packageName = cl.getCanonicalName().replace(simpleName, "");
		
		// By convention, if we have an interface, there is a prefix of one character
		if (cl.isInterface()) {
			simpleName = simpleName.substring(1);
		}
		
		return packageName + simpleName;
	}
}
