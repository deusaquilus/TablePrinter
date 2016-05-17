package net.deusaquilus.tableprinter.results.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;


import net.deusaquilus.tableprinter.results.Row;
import net.deusaquilus.tableprinter.results.RowSet;
import net.deusaquilus.tableprinter.results.RowSetRewindable;

import org.apache.commons.lang.NotImplementedException;


public class RowSetMem<T> implements RowSetRewindable<T> {

	private List<Row<T>> results;

	private Iterator<Row<T>> iterator;

	private Collection<String> allVars;

	private int rowNumber = 0;

	public RowSetMem() {
	}

	public RowSetMem(RowSet<T> resultSet) {
		this(resultSet, -1);
	}

	/**
	 *
	 * @param resultSet
	 * @param numResultsToConsume Number of results int he ResultSet to consume, -1 to consume all
	 */
	public RowSetMem(RowSet<T> resultSet, int numResultsToConsume) {

		// if the user has given us an initial capacity, initialize the result set to that
		// so we don't have to un-necessarily resize the array
		if (numResultsToConsume != -1) {
			results = new ArrayList<Row<T>>(numResultsToConsume);
		} else {
			results = new ArrayList<Row<T>>();
		}

		allVars = resultSet.getResultVars();

		// for the number of results the user asks us to consume (and while we have results to consume)
		for (int i = 0; resultSet.hasNext(); i++) {
			if (numResultsToConsume != -1) {
				if (i >= numResultsToConsume) {
					break;
				}
			}

			Row<T> result = resultSet.next();
			results.add(new ArrayRow<T>(result, allVars));
		}

		rowNumber = 0;
		iterator = results.iterator();
	}

	public int size() {
		return results.size();
	}

	public boolean hasNext() {
		return iterator.hasNext();
	}

	public Row<T> next() {
		rowNumber++;
		return iterator.next();
	}

	public int getRowNumber() {
		return rowNumber;
	}

	public Collection<String> getResultVars() {
		return allVars;
	}

	public void remove() {
		throw new NotImplementedException();
	}

	public void rewind() {
		rowNumber = 0;
		iterator = results.iterator();
	}

	public List<Row<T>> getResults() {
		return results;
	}

	public void setResults(List<Row<T>> results) {
		this.results = results;
	}

}
