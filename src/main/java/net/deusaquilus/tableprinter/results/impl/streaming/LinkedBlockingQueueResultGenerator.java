package net.deusaquilus.tableprinter.results.impl.streaming;

import java.util.Iterator;
import java.util.concurrent.LinkedBlockingQueue;

import net.deusaquilus.tableprinter.TablePrinterError;
import net.deusaquilus.tableprinter.results.Row;

public class LinkedBlockingQueueResultGenerator<T> implements ConcurrentResultGenerator<T> {

	public static final long DEFAULT_SLEEP_TIME = 200;

	private LinkedBlockingQueue<Row<T>> queue = new LinkedBlockingQueue<Row<T>>();

	private volatile boolean done = false;
	long sleepTime = 200;

	public void addRow(Row<T> row) {
		queue.add(row);
	}

	public void finish() {
		done = true;
	}

	private static class DoneRow<T> implements Row<T> {

		public boolean isDense() {
			return false;
		}

		public T get(String varName) {
			return null;
		}

		public boolean contains(String varName) {
			return false;
		}

		public Iterator<T> values() {
			return null;
		}

	}

	public boolean hasNext() {
		if (done) {
			// need to pass a row if we are on the last row
			queue.add(new DoneRow<T>());
		}
		return !done;
	}

	public Row<T> generateNext() throws TablePrinterError {
		// block until you get a result
		try {
			return queue.take();
		} catch (InterruptedException e) {
			throw new TablePrinterError("Error waiting for next result to generate.", e);
		}
	}

}
