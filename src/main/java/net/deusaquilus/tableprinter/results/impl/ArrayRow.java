package net.deusaquilus.tableprinter.results.impl;

import java.util.Collection;
import java.util.Iterator;


import net.deusaquilus.tableprinter.results.Row;

public class ArrayRow<T> implements Row<T>, Iterator<T> {

	private T[] data;
	private Collection<String> vars;
	private int currentIndex = 0;

	public ArrayRow(Row<T> result, Collection<String> allVars) {
		if (!result.isDense()) {
			throw new IllegalArgumentException("Dense Array Result interface requires a dense result set, " +
					"use the SparseResultAdapter if needed");

		}

		data = (T[]) new Object[allVars.size()];
		int i = 0;
		for (Iterator<T> t = result.values(); t.hasNext();) {
			data[i] = t.next();
			i++;
		}
	}


	public ArrayRow(T[] dataRow, Collection<String> allVars) {
		this.data = dataRow;
		this.vars = allVars;
	}

	public boolean isDense() {
		return true;
	}

	public T get(String varName) {
		if (!vars.contains(varName)) {
			return null;
		}
		int index = getIndexOfValue(varName);
		if (index == -1) {
			return null;
		}
		return data[index];
	}

	private int getIndexOfValue(String varName) {
		int i = 0;
		for (String var : vars) {
			if (var.equals(varName)) {
				return i;
			}
			i++;
		}
		return -1;
	}

	public boolean contains(String varName) {
		return vars.contains(varName);
	}

	public Iterator<T> values() {
		this.currentIndex = 0;
		return this;
	}

	public boolean hasNext() {
		return (currentIndex < data.length);
	}

	public T next() {
		T value = data[currentIndex];
		currentIndex++;
		return value;
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}

	public Collection<String> getResultVars() {
		return vars;
	}
}
