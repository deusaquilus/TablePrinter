package net.deusaquilus.tableprinter.results.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.deusaquilus.tableprinter.results.Row;


public class ListRow<T> implements Row<T> {

	private List<T> data;
	private Collection<String> vars;

	public ListRow(Row<T> result, Collection<String> allVars) {
		if (!result.isDense()) {
			throw new IllegalArgumentException("Dense Array Result interface requires a dense result set, " +
					"use the SparseResultAdapter if needed");

		}

		data = new ArrayList<T>(allVars.size());
		for (Iterator<T> t = result.values(); t.hasNext();) {
			data.add(t.next());
		}
	}


	public ListRow(List<T> dataRow, Collection<String> allVars) {
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
		return data.get(index);
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
		return data.iterator();
	}

}
