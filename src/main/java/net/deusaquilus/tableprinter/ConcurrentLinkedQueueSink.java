package net.deusaquilus.tableprinter;

import net.deusaquilus.tableprinter.results.Row;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by aioffe on 5/29/16.
 */
public class ConcurrentLinkedQueueSink<T> implements Sink<T> {

    private ConcurrentLinkedQueue<Row<T>> queue = new ConcurrentLinkedQueue<Row<T>>();
    private boolean isOpen = true;

    public boolean isOpen() {
        return isOpen;
    }

    public void push(Row<T> row) {
        queue.offer(row);
    }

    public boolean canPull() {
        return !queue.isEmpty();
    }

    public Row<T> pull() {
        return queue.remove();
    }

    public void close() {
        this.isOpen = false;
    }
}
