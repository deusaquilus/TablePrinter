package net.deusaquilus.tableprinter;

public class StringValuePrinter<T> implements ValuePrinter<T>{
	public String printValue(T value) {
		return value.toString();
	}
}
