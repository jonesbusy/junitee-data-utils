package com.lotaris.junitee.dependency;

import com.lotaris.rox.annotations.RoxableTest;
import com.lotaris.rox.annotations.RoxableTestClass;
import java.lang.reflect.Field;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author Laurent Prevost <laurent.prevost@lotaris.com>
 */
@RoxableTestClass(tags = "dependency-inject-helper")
public class DependencyInjectorHelperTest {
	@Test
	@RoxableTest(key = "9a4eb1dd9021")
	public void fieldInjectionShouldBePossibleInAllVisibilityCasses() throws Throwable {
		Object test = new Object() {
			private Object privateField;
			protected Object protectedField;
			public Object publicField;
			Object packageField;
		};
		
		Field privateField = test.getClass().getDeclaredField("privateField");
		injectFieldAsserter("Private", privateField, test);
		
		Field protectedField = test.getClass().getDeclaredField("protectedField");
		injectFieldAsserter("Protected", protectedField, test);
		
		Field publicField = test.getClass().getDeclaredField("publicField");
		injectFieldAsserter("Public", publicField, test);
		
		Field packageField = test.getClass().getDeclaredField("packageField");
		injectFieldAsserter("Package", packageField, test);
	}
	
	private void injectFieldAsserter(String fieldType, Field field, Object holder) {
		try {
			DependencyInjectorHelper.injectField(field, holder, new Object());
		}
		catch (DependencyInjectionException die) {
			fail(fieldType + " field should be injected but was not due to: " + die.getMessage());
		}
	}
	
	@Test
	@RoxableTest(key = "cc0e9d700c99")
	public void simpleClassShouldeBeCreatedWhenCallingInstiatorHelperMethod() {
		assertNotNull("A simple class should be instantiated by the helper method.", 
			instantiationTester(SimpleClass.class));
	}
	
	@Test
	@RoxableTest(key = "20522dc30e5d")
	public void withInterfaceClassShouldBeFoundAndInstantiatedByHelperMethod() {
		assertNotNull("A simple class should be instantiated by the helper method when its interface is given.", 
			instantiationTester(ISimpleClass.class));
	}	

	@Test
	@RoxableTest(key = "0fa3e110966b")
	public void classWithoutEmptyConstructorCannotBeInstantiatedByHelperMethod() {
		assertNull("A simple class should not be instantiated by the helper method when there is no empty constructor.", 
			instantiationTester(NoEmptyConstructorClass.class));
	}
	
	@Test
	@RoxableTest(key = "618a55318822")
	public void classWithoutEmptyConstructorFromInterfaceCannotBeInstantiatedByHelperMethod() {
		assertNull("A simple class should not be instantiated by the helper method when there is no empty constructor when its interface is given.", 
			instantiationTester(INoEmptyConstructorClass.class));
	}

	private Object instantiationTester(Class cl) {
		try {
			Object obj = DependencyInjectorHelper.findImplementationClass(cl);
			System.out.println(obj);
			return obj;
		}
		catch (DependencyInjectionException die) {
			return null;
		}
	}
	
	public interface ISimpleClass {}
	public static class SimpleClass implements ISimpleClass {}

	public interface INoEmptyConstructorClass {}
	public static class NoEmptyConstructorClass implements INoEmptyConstructorClass {
		public NoEmptyConstructorClass(Object voidObject) {}
	}
}
