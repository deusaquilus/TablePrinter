package net.deusaquilus.tableprinter.results.impl;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.NotImplementedException;

import net.deusaquilus.tableprinter.results.Row;
import net.deusaquilus.tableprinter.results.RowSet;
import net.deusaquilus.tableprinter.results.impl.ListRow;

public class ListRowSet<T> implements RowSet<T> {



	private List<Row<T>> results;
	private Collection<String> resultVars;
	private Iterator<Row<T>> iterator;

	public ListRowSet(List<Row<T>> results, Collection<String> resultVars) {
		this.results = results;
		this.iterator = results.iterator();
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
	public static <T, R extends Row<T>> ListRowSet<T> construct(List<R> results, Collection<String> resultVars) {
		return new ListRowSet<T>((List<Row<T>>) results, resultVars);
	}


	public List<Row<T>> getResults() {
		return results;
	}

	public void remove() {
		throw new NotImplementedException();
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
