package net.deusaquilus.tableprinter.results.impl;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.NotImplementedException;

import net.deusaquilus.tableprinter.results.Row;
import net.deusaquilus.tableprinter.results.RowSet;
import net.deusaquilus.tableprinter.results.impl.ListRow;

public class ListRowSet<T> implements RowSet<T> {

	private List<List<T>> results;
	private Collection<String> resultVars;
	private Iterator<List<T>> iterator;
	private int rowNumber;

	public ListRowSet() {
	}

	public ListRowSet(List<List<T>> results, Collection<String> resultVars) {
		setResults(results);
		setResultVars(resultVars);
	}


	public List<List<T>> getResults() {
		return results;
	}
	public void setResults(List<List<T>> results) {
		this.results = results;
		this.iterator = results.iterator();
		this.rowNumber = 0;
	}

	public void remove() {
		throw new NotImplementedException();
	}

	public boolean hasNext() {
		return iterator.hasNext();
	}
	public Row<T> next() {
		List<T> next = iterator.next();
		this.rowNumber++;
		return new ListRow<T>(next, resultVars);
	}
	public int getRowNumber() {
		return this.rowNumber;
	}

	public Collection<String> getResultVars() {
		return resultVars;
	}
	public void setResultVars(Collection<String> resultVars) {
		this.resultVars = resultVars;
	}



}
