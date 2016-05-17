package net.deusaquilus.tableprinter.results.impl.streaming;

import java.util.Collection;

import net.deusaquilus.tableprinter.TablePrinterError;
import net.deusaquilus.tableprinter.results.Row;
import net.deusaquilus.tableprinter.results.RowSet;

import org.apache.commons.lang.NotImplementedException;

public class IncrementalRowSet<T> implements RowSet<T> {

	public static interface ResultGenerator<T> {
		boolean hasNext() throws TablePrinterError;
		Row<T> generateNext() throws TablePrinterError;
	}

	protected ResultGenerator<T> resultGenerator;
	protected Collection<String> resultVars;
	protected int currentRowNumber = 0;

	public IncrementalRowSet() {
	}

	public IncrementalRowSet(ResultGenerator<T> resultGenerator, Collection<String> resultVars) {
		this.resultGenerator = resultGenerator;
		this.resultVars = resultVars;
	}

	public void remove() {
		throw new NotImplementedException();
	}

	public boolean hasNext() throws TablePrinterError {
		return resultGenerator.hasNext();
	}

	public Row<T> next() throws TablePrinterError {
		currentRowNumber++;
		return resultGenerator.generateNext();
	}

	public int getRowNumber() {
		return currentRowNumber;
	}

	public Collection<String> getResultVars() {
		return resultVars;
	}

	public ResultGenerator<T> getResultGenerator() {
		return resultGenerator;
	}

	public void setResultGenerator(ResultGenerator<T> resultGenerator) {
		this.resultGenerator = resultGenerator;
	}
}
