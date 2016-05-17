package net.deusaquilus.tableprinter.results.impl;

import java.util.Collection;
import java.util.Iterator;


import net.deusaquilus.tableprinter.results.Row;

import org.apache.commons.lang.NotImplementedException;


public class SparseRowAdapter<T> implements Row<T>, Iterator<T> {

	private Row<T> sparseResult;
	private Collection<String> vars;
	private Iterator<String> varsIterator;

	public SparseRowAdapter(Row<T> sparseResult, Collection<String> vars) {
		this.sparseResult = sparseResult;
		this.vars = vars;
	}

	public boolean isDense() {
		return true;
	}

	public T get(String varName) {
		return sparseResult.get(varName);
	}

	public boolean contains(String varName) {
		return vars.contains(varName);
	}

	/**
	 * Since this is the main method that we need to support, for the sake of performance,
	 * we implement this on the present object directly
	 */
	public Iterator<T> values() {
		// reset the varaibles iterator
		varsIterator = vars.iterator();

		// return this object
		return this;
	}


	public boolean hasNext() {
		return varsIterator.hasNext();
	}

	public T next() {
		String nextVar = varsIterator.next();
		return get(nextVar);
	}

	public void remove() {
		throw new NotImplementedException();
	}

}
