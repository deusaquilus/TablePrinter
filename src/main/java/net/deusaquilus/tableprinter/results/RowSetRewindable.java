package net.deusaquilus.tableprinter.results;

/**
 * Simple interface that defines a rewindable row set
 * @author aioffe
 *
 * @param <T>
 */
public interface RowSetRewindable<T> extends RowSet<T> {
	public void rewind();
}
