package net.deusaquilus.tableprinter;


import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.sun.tools.javac.util.Name;
import net.deusaquilus.tableprinter.results.Row;
import net.deusaquilus.tableprinter.results.RowSet;
import net.deusaquilus.tableprinter.results.RowSetRewindable;
import net.deusaquilus.tableprinter.results.impl.ListRowSet;
import net.deusaquilus.tableprinter.results.impl.RowSetMem;
import net.deusaquilus.tableprinter.results.impl.SingletonRowSet;
import org.apache.log4j.Logger;

public class TablePrinter<T> {

    private final Logger logger = Logger.getLogger(TablePrinter.class);

    private int[] colWidths;

    private TablePrinterConfig config = new TablePrinterConfig();

    private ValuePrinter<T> valuePrinter = new StringValuePrinter<T>();

    private int lineWidth;

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

    public static interface Sink<T> {
        boolean isOpen();
        void push(Row<T> row);

        boolean canPull();
        Row<T> pull();
    }

    public Sink<T> openSink(final Sink<T> sink, final PrintWriter pw) {
        final TablePrinter printer = this;

        new Thread(new Runnable() {
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
                    return;
                }
                // finally, if we are at this point, we just print out additional rows since we have already measured
                printer.writeSomeMore(pw, new SingletonRowSet<T>(row));
            }

            private void flushIfStillMeasuring() {
                printer.startWriting(pw, new ListRowSet(initialRowSet));
            }

            public boolean reachedMeasuringCapacity() {
                return rowsMeasured >= measuringCapacity;
            }

            public void sleep() {
                try {
                    Thread.sleep(printer.config.asyncSleepTime);
                } catch (InterruptedException e) {

                }
            }

            public void run() {
                while(sink.isOpen()) {
                    while(sink.canPull()) {
                        processRow(sink.pull());
                        sleep();
                    }
                }
                printer.finishWriting(pw);
                flushIfStillMeasuring();
                pw.flush();
            }
        });
        return sink;
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
