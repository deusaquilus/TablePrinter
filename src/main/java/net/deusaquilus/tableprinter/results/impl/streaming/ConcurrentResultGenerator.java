package net.deusaquilus.tableprinter.results.impl.streaming;

import net.deusaquilus.tableprinter.TablePrinterError;
import net.deusaquilus.tableprinter.results.Row;
import net.deusaquilus.tableprinter.results.impl.streaming.IncrementalRowSet.ResultGenerator;

public interface ConcurrentResultGenerator<T> extends ResultGenerator<T> {
	void addRow(Row<T> row);
	void finish();
	boolean hasNext();
	Row<T> generateNext() throws TablePrinterError;
}
