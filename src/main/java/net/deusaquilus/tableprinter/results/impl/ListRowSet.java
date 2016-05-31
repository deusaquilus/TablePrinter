package net.deusaquilus.tableprinter.results.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.deusaquilus.tableprinter.results.Row;
import net.deusaquilus.tableprinter.results.RowSet;
import net.deusaquilus.tableprinter.results.impl.ListRow;

public class ListRowSet<T> implements RowSet<T> {



	private List<Row<T>> results;
	private Collection<String> resultVars;
	private Iterator<Row<T>> iterator;

	/**
	 * Construct a list-based row set. In the case that individual rows do not contain
	 * all of the required variables, this constructor can be used to manually pass in a superset
	 * of all the variables contained in all the rows.
	 * @param results
	 * @param resultVars
     */
	public ListRowSet(List<Row<T>> results, Collection<String> resultVars) {
		this.results = results;
		this.iterator = results.iterator();
		this.resultVars = resultVars;
	}

	public ListRowSet(List<Row<T>> results) {
		this.results = results;
		this.iterator = results.iterator();
		this.resultVars = results.get(0).getResultVars();
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
	public static <T, R extends Row<T>> ListRowSet<T> construct(List<R> results, Collection<String> resultVars) {
		return new ListRowSet<T>((List<Row<T>>) results, resultVars);
	}

	public static <T, R extends Row<T>> ListRowSet<T> construct(Collection<String> resultVars, R... results) {
		return ListRowSet.construct(Arrays.asList(results), resultVars);
	}

	public static <T, R extends Row<T>> ListRowSet<T> construct(R... results) {
		return new ListRowSet(Arrays.asList(results));
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}

	public boolean hasNext() {
		return iterator.hasNext();
	}
	public Row<T> next() {
		return iterator.next();
	}

	public Collection<String> getResultVars() {
		return resultVars;
	}
}
