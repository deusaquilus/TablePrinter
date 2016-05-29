package net.deusaquilus.tableprinter.results.impl;

import net.deusaquilus.tableprinter.results.Row;
import net.deusaquilus.tableprinter.results.RowSet;
import org.apache.commons.lang.NotImplementedException;

import java.util.*;

public class SingletonRowSet<T> implements RowSet<T> {

	private Row<T> result;
	private Collection<String> resultVars;
	private boolean done = false;

	public SingletonRowSet(Row<T> result, Collection<String> resultVars) {
		this.result = result;
		this.resultVars = resultVars;
	}

	/**
	 * Convenience constructor method that allows covariance i.e. can allow subtypes of Row to be passed in
	 * directly without casting.
	 * @param result
	 * @param resultVars
	 * @param <T>
	 * @param <R>
     * @return
     */
	public static <T, R extends Row<T>> SingletonRowSet<T> construct(R result, Collection<String> resultVars) {
		return new SingletonRowSet<T>((Row<T>) result, resultVars);
	}

	public static <T, R extends Row<T>> SingletonRowSet<T> construct(Collection<String> resultVars, T... results) {
		return SingletonRowSet.construct(new ListRow<T>(resultVars, results), resultVars);
	}

	public void remove() {
		throw new NotImplementedException();
	}

	public boolean hasNext() {
		return !done;
	}
	public Row<T> next() {
		done = true;
		return result;
	}

	public Collection<String> getResultVars() {
		return resultVars;
	}
}
