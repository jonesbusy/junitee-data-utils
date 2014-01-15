package com.lotaris.junitee.dummy;

import com.lotaris.junitee.generator.IDataStateGenerator;
import java.util.Properties;
import javax.persistence.EntityManager;

/**
 * Dummy state generator to assert that the generator is
 * called at the right time.
 * 
 * @author Laurent Prevost, laurent.prevost@lotaris.com
 */
public class DummyDataStateGenerator implements IDataStateGenerator {

	public static int configureCnt;
	public static int createStateCnt;
	public static int restoreStateCnt;

	public DummyDataStateGenerator() {
		configureCnt = 0;
		createStateCnt = 0;
		restoreStateCnt = 0;
	}
	
	@Override
	public void configure(Properties configuration) {
		configureCnt++;
	}
	
	@Override
	public void createState(EntityManager entityManager) {
		createStateCnt++;
	}

	@Override
	public void restoreState(EntityManager entityManager) {
		restoreStateCnt++;
	}
}
