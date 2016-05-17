package net.deusaquilus.tableprinter;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.Iterator;

import net.deusaquilus.tableprinter.results.Row;
import net.deusaquilus.tableprinter.results.RowSet;
import net.deusaquilus.tableprinter.results.RowSetRewindable;
import net.deusaquilus.tableprinter.results.impl.ArrayRow;
import net.deusaquilus.tableprinter.results.impl.SparseRowAdapter;

public class TablePrintingUtils {

	private TablePrintingUtils() {
	}

    public static <T> void writeResultSetBody(
    		PrintWriter pw,
    		RowSet<T> resultSet,
    		int[] colWidths,
    		ValuePrinter<T> valuePrinter,
    		TablePrinterConfig config) {

    	Collection<String> allVars = resultSet.getResultVars();

        // allocate a row to fill with results
    	String[] row = new String[resultSet.getResultVars().size()];

    	// allocate a spare row to write wrapped content into
    	String[] spareRow = new String[resultSet.getResultVars().size()];

    	while (resultSet.hasNext()) {
    		Row<T> result = resultSet.next();

    		// fill the row with results
        	fillRow(result, allVars, row, valuePrinter);

        	// while we can wrap rows, fill the spare with the next slice and print it
        	while (RowWrappingUtil.rowHasContent(row)) {
        		RowWrappingUtil.wrapRowOnce(row, colWidths, spareRow);
        		printRow(pw, spareRow, config, colWidths);
        	}
    	}
    }

    public static void printHeadingRow(
    		PrintWriter pw,
    		String[] headingRow,
    		int lineWidth,
    		TablePrinterConfig config,
    		int[] colWidths) {

        for ( int i = 0 ; i < lineWidth ; i++ ) pw.print('-') ;
        pw.println();

        printRow(pw, headingRow, config, colWidths) ;

        for ( int i = 0 ; i < lineWidth ; i++ ) pw.print('=') ;
        pw.println();
    }

	public static String[] buildHeadingRow(Collection<String> allVars) {
    	String[] headingRow = new String[allVars.size()];
    	int i = 0;
    	for (String varName : allVars) {
    		headingRow[i] = varName;
    		i++;
    	}
    	return headingRow;
    }

	public static <T> Row<T> makeDense(Row<T> result, Collection<String> allVars) {
		if (result.isDense()) {
			return (ArrayRow<T>) result;
		}
		return new SparseRowAdapter<T>(result, allVars);
	}

    public static <T> int[] measureColWidths(
    		RowSetRewindable<T> results,
    		TablePrinterConfig config,
    		ValuePrinter<T> valuePrinter)
    {
        int numCols = results.getResultVars().size() ;
        int[] colWidths = new int[numCols] ;

        // Widths at least that of the variable name.  Assumes we will print col headings.
        int i = 0;
        for (String varName : results.getResultVars()) {
        	if (colWidths[i] < varName.length()) {
            	colWidths[i] = varName.length() ;
            }
        	i++;
        }

        // Preparation pass : find the maximum width for each column
        while(results.hasNext()) {
            Row<T> result = results.next() ;

            int col = 0;
            for (Iterator<T> t = result.values(); t.hasNext();) {
            	String printedValue = valuePrinter.printValue(t.next());

            	if (colWidths[col] < printedValue.length()) {
                	colWidths[col] = printedValue.length() ;
                }

            	col++;
            }
        }

        // Verification pass: go through the columns widths and make sure none of them
        // are larger then the configured maximum
        boundColumnWidths(colWidths, config);
        ensureColWidthsGreaterThenZero(colWidths);

        return colWidths;
    }


    private static void boundColumnWidths(int[] colWidths, TablePrinterConfig config) {

    	if (config.maxColumnWidth == -1) {
    		return;
    	}

    	for (int i = 0; i < colWidths.length; i++) {
        	if (colWidths[i] > config.maxColumnWidth) {
        		colWidths[i] = config.maxColumnWidth;
        	}
        }
    }

    private static void ensureColWidthsGreaterThenZero(int[] colWidths) {
    	for (int i = 0; i < colWidths.length; i++) {
        	if (colWidths[i] <= 0) {
        		colWidths[i] = 1;
        	}
        }
    }

	public static int measureTotalRowWidth(String[] row, int[] colWidths, TablePrinterConfig config) {
		int lineWidth = 0;
		for (int col = 0; col < row.length; col++) {
			lineWidth += colWidths[col];
			if (col > 0) {
				lineWidth += config.colSep.length();
			}
		}
		if (config.colStart != null) {
			lineWidth += config.colStart.length();
		}
		if (config.colEnd != null) {
			lineWidth += config.colEnd.length();
		}

		return lineWidth;
	}

    public static <T> void fillRow(Row<T> result, Collection<String> allVars, String[] row, ValuePrinter<T> valuePrinter) {
    	// TODO Going to need to test this and the length computing function with a row with zero content
    	// to make sure that use case works
    	int i = 0;
    	for (Iterator<T> t = result.values(); t.hasNext();) {
    		row[i] = valuePrinter.printValue(t.next());
    		i++;
    	}

    	if (!result.isDense()) {
    		result = new SparseRowAdapter<T>(result, allVars);
    	}
    }

	public static void printRow(PrintWriter out, String[] row, TablePrinterConfig config, int[] colWidths) {
		String colStart = config.colStart;
		String colEnd = config.colEnd;
		String colSep = config.colSep;

		out.print(colStart);
		for (int col = 0; col < colWidths.length; col++) {
			String s = row[col];
			int pad = colWidths[col];
			StringBuffer buff = new StringBuffer(config.defaultBufferSize);

			if (col > 0)
				buff.append(colSep);

			buff.append(s);
			for (int j = 0; j < pad - s.length(); j++) {
				buff.append(' ');
			}

			out.print(buff);
		}
		out.print(colEnd);
		out.println();
	}

}
