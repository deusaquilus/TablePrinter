package net.deusaquilus.tableprinter.results.impl.streaming;

import java.util.concurrent.ConcurrentLinkedQueue;

import net.deusaquilus.tableprinter.TablePrinterError;
import net.deusaquilus.tableprinter.results.Row;

public class ConcurrentLinkedQueueResultGenerator<T> implements ConcurrentResultGenerator<T> {

	public static final long DEFAULT_SLEEP_TIME = 200;

	private ConcurrentLinkedQueue<Row<T>> queue = new ConcurrentLinkedQueue<Row<T>>();

	private volatile boolean done = false;
	long sleepTime = 200;

	public void addRow(Row<T> row) {
		queue.add(row);
	}

	public void finish() {
		done = true;
	}

	public boolean hasNext() {
		return !done;
	}

	private void sleep() {
		try {
			Thread.sleep(DEFAULT_SLEEP_TIME);
		} catch (InterruptedException e) {
			throw new TablePrinterError("Error waiting for next result to generate.", e);
		}
	}

	public Row<T> generateNext() throws TablePrinterError {
		// block until you get a result
		Row<T> nextResult = queue.poll();
		while (nextResult == null && !done) {
			sleep();
			nextResult = queue.poll();
		}

		// then return it
		return nextResult;
	}

}
