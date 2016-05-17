package net.deusaquilus.tableprinter.results.impl;

import java.util.Iterator;
import java.util.Map;

import net.deusaquilus.tableprinter.results.Row;


public class MapRow<T> implements Row<T> {

	private Map<String, T> results;

	public MapRow(Map<String, T> results) {
		this.results = results;
	}

	public boolean isDense() {
		return false;
	}

	public T get(String varName) {
		return results.get(varName);
	}

	public boolean contains(String varName) {
		return results.containsKey(varName);
	}

	public Iterator<T> values() {
		return results.values().iterator();
	}

}
