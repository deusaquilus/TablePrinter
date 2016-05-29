package net.deusaquilus.tableprinter.results;

import java.util.Collection;
import java.util.Iterator;

/**
 * A single Row of data that can be published by the table printer.
 * @author aioffe
 *
 * @param <T>
 */
public interface Row<T>
{
	boolean isDense();

    T get(String varName);

    boolean contains(String varName);

    /**
     * The most performant API, clients should prefer using this
     * @return
     */
    Iterator<T> values();

    Collection<String> getResultVars();
}
