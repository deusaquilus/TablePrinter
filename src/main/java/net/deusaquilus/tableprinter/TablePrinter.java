package net.deusaquilus.tableprinter;


import java.io.PrintWriter;

import net.deusaquilus.tableprinter.results.RowSet;
import net.deusaquilus.tableprinter.results.RowSetRewindable;
import net.deusaquilus.tableprinter.results.impl.RowSetMem;

public class TablePrinter<T> {

    private int[] colWidths;

    private TablePrinterConfig config = new TablePrinterConfig();

    private ValuePrinter<T> valuePrinter = new StringValuePrinter<T>();


    /** Textual representation : default layout using " | " to separate columns.
     *  Ensure the PrintWriter can handle UTF-8.
     *  OutputStream version is preferred.
     *  @param pw         A PrintWriter
     *  @param resultSet  ResultSet
     */
    public void format(PrintWriter pw, RowSet<T> resultSet) {
    	write(pw, resultSet) ;
    }


    private RowSetRewindable<T> computeWidthsAndConsumeResults(RowSet<T> resultSet) {
    	RowSetRewindable<T> rewindableSlice = new RowSetMem<T>(resultSet, config.measuringRows);
    	this.colWidths = TablePrintingUtils.measureColWidths(rewindableSlice, config, valuePrinter);
    	rewindableSlice.rewind();
    	return rewindableSlice;
    }


    /** Textual representation : layout using given separator.
     *  Ensure the PrintWriter can handle UTF-8.
     *  @param pw         PrintWriter
     *  @param colSep      Column separator
     */
    public void write(PrintWriter pw, RowSet<T> resultSet)
    {
        if ( resultSet.getResultVars().size() == 0 )
        {
            pw.println("==== No variables ====") ;
            return;
        }

        // use a section of the result set to compute the column widths
        RowSetRewindable<T> widthComputedStoredResultSet = this.computeWidthsAndConsumeResults(resultSet);

        // create the heading row
        String headingRow[] = TablePrintingUtils.buildHeadingRow(resultSet.getResultVars());

        // measure the size of the heading (actually any row will do since we already computed
        // the column widths)
        int lineWidth = TablePrintingUtils.measureTotalRowWidth(headingRow, colWidths, config);

        TablePrintingUtils.printHeadingRow(pw, headingRow, lineWidth, this.config, this.colWidths);

        // print out the results that were computed to calculate the columnd with
        TablePrintingUtils.writeResultSetBody(pw, widthComputedStoredResultSet, this.colWidths, this.valuePrinter, this.config);

        // print out the reset of the results
        TablePrintingUtils.writeResultSetBody(pw, resultSet, this.colWidths, this.valuePrinter, this.config);

        for ( int i = 0 ; i < lineWidth ; i++ ) pw.print('-') ;
        pw.println();
    }


    protected String printValue(T value) {
    	return valuePrinter.printValue(value);
    }


	public ValuePrinter<T> getValuePrinter() {
		return valuePrinter;
	}


	public void setValuePrinter(ValuePrinter<T> valuePrinter) {
		this.valuePrinter = valuePrinter;
	}


	public int[] getColWidths() {
		return colWidths;
	}


	public void setColWidths(int[] colWidths) {
		this.colWidths = colWidths;
	}
}
