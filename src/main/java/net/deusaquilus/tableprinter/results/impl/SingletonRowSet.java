package net.deusaquilus.tableprinter.results.impl;

import net.deusaquilus.tableprinter.results.Row;
import net.deusaquilus.tableprinter.results.RowSet;
import org.apache.commons.lang.NotImplementedException;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class SingletonRowSet<T> implements RowSet<T> {

	private Row<T> result;
	private Collection<String> resultVars;

	public SingletonRowSet(Row<T> result, Collection<String> resultVars) {
		this.result = result;
		this.resultVars = resultVars;
	}

	/**
	 * Convenience constructor method that allows covariance i.e. can allow subtypes of Row to be passed in
	 * directly without casting.
	 * @param results
	 * @param resultVars
	 * @param <T>
	 * @param <R>
     * @return
     */
	public static <T, R extends Row<T>> SingletonRowSet<T> construct(List<R> results, Collection<String> resultVars) {
		return new SingletonRowSet<T>((Row<T>) results, resultVars);
	}

	public void remove() {
		throw new NotImplementedException();
	}

	public boolean hasNext() {
		return false;
	}
	public Row<T> next() {
		return result;
	}

	public Collection<String> getResultVars() {
		return resultVars;
	}
}
