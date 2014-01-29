package com.lotaris.junitee.generator;

/**
 * The data generator runner is a data structure to help to 
 * fix the order of data generators to run into a composite 
 * data generator structure.
 * 
 * @author Laurent Prevost <laurent.prevost@lotaris.com>
 */
class DataGeneratorRunner {
	/**
	 * The data generator to run by the runner
	 */
	private IDataGenerator dataGenerator;
	
	/**
	 * Next runner, should be null when the end of the chain is reached
	 */
	private DataGeneratorRunner next;

	/**
	 * Constructor
	 * 
	 * @param dataGenerator The data generator to run
	 */
	public DataGeneratorRunner(IDataGenerator dataGenerator) {
		this.dataGenerator = dataGenerator;
	}

	/**
	 * Add a new data generator to the runner
	 * 
	 * @param nextDataGenerator The next data generator to run
	 * @return The data generator runner created for the next data generator
	 */
	public DataGeneratorRunner setNext(IDataGenerator nextDataGenerator) {
		next = new DataGeneratorRunner(nextDataGenerator);
		return next;
	}
	
	/**
	 * @return The next data generator runner
	 */
	DataGeneratorRunner getNext() {
		return next;
	}

	/**
	 * @return The data generator to be run
	 */
	IDataGenerator getDataGenerator() {
		return dataGenerator;
	}
	
	/**
	 * Execute the data generator runner and call
	 * the whole composite chain
	 */
	public void execute() {
		dataGenerator.run();

		if (next != null) {
			next.execute();
		}
	}
}
