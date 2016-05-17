package net.deusaquilus.tableprinter;

import java.io.PrintWriter;
import java.util.Collection;

import net.deusaquilus.tableprinter.results.Row;
import net.deusaquilus.tableprinter.results.impl.streaming.ConcurrentResultGenerator;
import net.deusaquilus.tableprinter.results.impl.streaming.IncrementalRowSet;
import net.deusaquilus.tableprinter.results.impl.streaming.LinkedBlockingQueueResultGenerator;

public class ConcurrentTablePrinter<T> {

	private ConcurrentResultGenerator<T> resultGenerator;
	private PrintWriter pw;
	private Collection<String> resultVars;

	public ConcurrentTablePrinter(PrintWriter pw, Collection<String> resultVars) {
		this.resultGenerator = new LinkedBlockingQueueResultGenerator<T>();
		this.pw = pw;
		this.resultVars = resultVars;
	}

	public ConcurrentTablePrinter(PrintWriter pw, Collection<String> resultVars, ConcurrentResultGenerator<T> resultGenerator) {
		this.resultGenerator = resultGenerator;
		this.pw = pw;
		this.resultVars = resultVars;
	}

	public void start() {
		TablePrintingThread<T> thread = new TablePrintingThread<T>(this.resultGenerator, this.pw, this.resultVars);
		new Thread(thread).start();
	}

	public void stop() {
		resultGenerator.finish();
	}

	public void add(Row<T> row) {
		resultGenerator.addRow(row);
	}

	public static class TablePrintingThread<T> implements Runnable {
		private ConcurrentResultGenerator<T> resultGenerator;
		private PrintWriter pw;
		private Collection<String> resultVars;

		public TablePrintingThread(ConcurrentResultGenerator<T> resultGenerator, PrintWriter pw, Collection<String> resultVars) {
			this.resultGenerator = resultGenerator;
			this.pw = pw;
			this.resultVars = resultVars;
		}

		public void run() {
			TablePrinter<T> printer = new TablePrinter<T>();
			// flush the results, the ConcurrentResultGenerator will block unless there are more results
			printer.format(pw, new IncrementalRowSet<T>(resultGenerator, resultVars));
			pw.flush();
		}

	}


}
