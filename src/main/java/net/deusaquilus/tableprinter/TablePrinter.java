package net.deusaquilus.tableprinter;


import java.io.PrintWriter;

import net.deusaquilus.tableprinter.results.RowSet;
import net.deusaquilus.tableprinter.results.RowSetRewindable;
import net.deusaquilus.tableprinter.results.impl.RowSetMem;

public class TablePrinter<T> {

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
