package net.deusaquilus.tableprinter.results;

import java.util.Iterator;

/**
 * A single Row of data that can be published by the table printer.
 * @author aioffe
 *
 * @param <T>
 */
public interface Row<T>
{
	public boolean isDense();

    public T get(String varName);

    public boolean contains(String varName);

    /**
     * The most performant API, clients should prefer using this
     * @return
     */
    public Iterator<T> values();

}
