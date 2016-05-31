package net.deusaquilus.tableprinter;

import net.deusaquilus.tableprinter.results.Row;

/**
 * Created by aioffe on 5/29/16.
 */
public interface Sink<T> {
    void push(Row<T> row);
    boolean canPull();
    Row<T> pull();
}
