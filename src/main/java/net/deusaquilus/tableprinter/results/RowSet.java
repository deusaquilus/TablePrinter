package net.deusaquilus.tableprinter.results;

import java.util.Collection;
import java.util.Iterator;

import net.deusaquilus.tableprinter.TablePrinter;

/**
 * An interface that defines a result set that is consumable by the {@link TablePrinter}
 * @author aioffe
 *
 * @param <T>
 */
public interface RowSet<T> extends Iterator<Row<T>> {

	/** Is there a next result?. */
    public boolean hasNext();

    /** Moves onto the next result. */
    public Row<T> next();

    /** Return the "row" number for the current iterator item */
    public int getRowNumber() ;

    /**
     * Get all of the variables that will be returned in the result set because
     * each {@link Row} may not have all of the variables (i.e. when it is
     * 'sparse'). Note that the result
     * variables can be stored in any collection so long as the collection returns
     * a consistent ordering of elements so long as it is not modified.
     *
     * One important thing to note is that when a Result object is not dense,
     * the fields cannot be returned via a simple iteration and the
     * <code>SparseResultAdapter</code> needs to be used. This adapter
     * iterates through the solution variables for each wrapped element
     * and the calls a <code>Result.get<code> on every variable. Therefore
     * when implementing a efficient sparse result set, make sure that
     * the collection object you use supports a fast iteration
     * of this collection.
     *
     * @see Row
     */
    public Collection<String> getResultVars();
}
