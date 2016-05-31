package net.deusaquilus.tableprinter;


import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import net.deusaquilus.tableprinter.results.Row;
import net.deusaquilus.tableprinter.results.RowSet;
import net.deusaquilus.tableprinter.results.RowSetRewindable;
import net.deusaquilus.tableprinter.results.impl.ListRowSet;
import net.deusaquilus.tableprinter.results.impl.RowSetMem;
import net.deusaquilus.tableprinter.results.impl.SingletonRowSet;

public class TablePrinter<T> {

    private Thread sinkThread;

    private int[] colWidths;

    private TablePrinterConfig config = new TablePrinterConfig();

    private ValuePrinter<T> valuePrinter = new StringValuePrinter<T>();

    private int lineWidth;

    private AtomicBoolean isOpen = new AtomicBoolean(true);

    public TablePrinter() {
    }

    public TablePrinter(TablePrinterConfig config) {
        this.config = config;
    }

    private RowSetRewindable<T> computeWidthsAndConsumeResults(RowSetRewindable<T> rewindableSlice) {
    	this.colWidths = TablePrintingUtils.measureColWidths(rewindableSlice, config, valuePrinter);
    	rewindableSlice.rewind();
    	return rewindableSlice;
    }

    public void startWriting(PrintWriter pw, RowSet<T> resultSet) {
        startWritingInternal(pw, resultSet, -1);
    }

    public void startWritingInternal(PrintWriter pw, RowSet<T> resultSet, int measuringRows) {
        if ( resultSet.getResultVars().size() == 0 ) {
            pw.println("==== No Data ====") ;
            return;
        }

        // use a section of the result set to compute the column widths
        RowSetRewindable<T> rewindableSlice = new RowSetMem<T>(resultSet, measuringRows);
        RowSetRewindable<T> widthComputedStoredResultSet = this.computeWidthsAndConsumeResults(rewindableSlice);

        // create the heading row
        String headingRow[] = TablePrintingUtils.buildHeadingRow(resultSet.getResultVars());

        // measure the size of the heading (actually any row will do since we already computed
        // the column widths)
        lineWidth = TablePrintingUtils.measureTotalRowWidth(headingRow, colWidths, config);

        if (this.config.printHeaders) {
            TablePrintingUtils.printHeadingRow(pw, headingRow, lineWidth, this.config, this.colWidths);
        }

        // print out the results that were computed to calculate the columnd with
        TablePrintingUtils.writeResultSetBody(pw, widthComputedStoredResultSet, this.colWidths, this.valuePrinter, this.config);
    }

    public void writeSomeMore(PrintWriter pw, RowSet<T> resultSet) {
        // print out the reset of the results
        TablePrintingUtils.writeResultSetBody(pw, resultSet, this.colWidths, this.valuePrinter, this.config);
    }

    public void finishWriting(PrintWriter pw) {
        for ( int i = 0 ; i < lineWidth ; i++ ) pw.print('-') ;
        pw.println();
    }

    public Sink<T> openSink(final PrintWriter pw) {
        return this.openSink(new ConcurrentLinkedQueueSink<T>(), pw);
    }

    public Sink<T> openSink(final Sink<T> sink, final PrintWriter pw) {
        final TablePrinter printer = this;

        sinkThread = new Thread(new Runnable() {
            private final int measuringCapacity = config.measuringRows;
            private int rowsMeasured = 0;
            private List<Row<T>> initialRowSet = new ArrayList<Row<T>>();

            private void processRow(Row<T> row) {
                // if enough rows have not been processed yet to make measurements, just add to the buffer
                if (!reachedMeasuringCapacity()) {
                    initialRowSet.add(row);
                    rowsMeasured++;
                    return;
                }
                // if we got here, there are enough rows to make the measurements but maybe we haven't
                // printed them yet. See if the buffer is not null in order to know that
                if (initialRowSet != null) {
                    // print the initial set of rows, then set it to null
                    printer.startWriting(pw, new ListRowSet(initialRowSet));
                    initialRowSet = null;
                    // also note that if we got here then there is additional row that got pulled off (in addition
                    // to these initial ones we have processed), this means that we also have to publish the additional row.
                }
                // finally, if we are at this point, we just print out additional rows since we have already measured
                printer.writeSomeMore(pw, new SingletonRowSet<T>(row));
            }

            private void flushIfStillMeasuring() {
                if (initialRowSet != null) printer.startWriting(pw, new ListRowSet(initialRowSet));
            }

            public boolean reachedMeasuringCapacity() {
                return rowsMeasured >= measuringCapacity;
            }

            public void sleep() {
                try {
                    Thread.sleep(printer.config.asyncSleepTime);
                } catch (InterruptedException e) {
                    if (config.silent) throw new RuntimeException(e);
                }
            }

            public void run() {
                while(printer.isOpen.get()) {
                    // TODO Think about doing batching on the sink pull (i.e. the sink should pull a batch of items)
                    // Also note that moving sleep out of the below while loop wont effectively substitute for a batch pull
                    // since if we leave it out, the sink.canPull/processRow will run in a loop that is so hot a concurrent
                    // queue will go into thread context thus letting the latest data off the queue.
                    while(sink.canPull()) {
                        processRow(sink.pull());
                        sleep();
                    }
                }

                // Need to check again if we have put anything on the thread during the last sleep that happened
                // (immediately before the printer was marked close) since during this invocation of sleep
                // immediately before this while, additional items may have been placed on the queue.
                while(sink.canPull()) {
                    processRow(sink.pull());
                }

                flushIfStillMeasuring();
                // Note that for linewidth to be correct in the first place,
                // the startWriting method has to be called since the line width obviously
                // cannot be computed until the column widths are (sto)
                printer.finishWriting(pw);
                pw.flush();
            }
        });

        sinkThread.start();

        return sink;
    }

    public void close() {
        this.isOpen.set(false);
        try {
            sinkThread.join();
        } catch (InterruptedException e) {
            if (config.silent) throw new RuntimeException(e);
        }
    }

    /** Textual representation : layout using given separator.
     *  Ensure the PrintWriter can handle UTF-8.
     *  @param pw         PrintWriter
     *  @param resultSet      Column separator
     */
    public void writeAll(PrintWriter pw, RowSet<T> resultSet) {
        if ( resultSet.getResultVars().size() == 0 ) {
            pw.println("==== No Data ====") ;
            return;
        }

        startWritingInternal(pw, resultSet, config.measuringRows);
        writeSomeMore(pw, resultSet);
        finishWriting(pw);
    }
}
