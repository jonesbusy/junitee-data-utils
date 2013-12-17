package com.lotaris.junitee.context;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * Define what should be a context rule that can share data across multiple rules that are also context rules.
 *
 * @author Laurent Prevost, laurent.prevost@lotaris.com
 */
public interface IContextRule extends TestRule {
	/**
	 * Modifies the method-running {@link Statement} to implement this test-running rule.
	 *
	 * @param context The generator context
	 * @param base The {@link Statement} to be modified
	 * @param description A {@link Description} of the test implemented in {@code base}
	 * @return a new statement that wraps around {@code base}.
	 */
	Statement apply(GeneratorContext context, Statement statement, Description description);
}
