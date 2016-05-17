package net.deusaquilus.tableprinter;

public class TablePrinterConfig {

	/**
	 * Number of rows to use to measure the width of the columns,
	 * use -1 to use all of the rows.
	 */
	public int measuringRows = 20;

	public String colStart =  "| ";
	public String colEnd = " |";
	public String colSep = " | ";

	public int maxColumnWidth = -1;

	public int defaultBufferSize = 120;

}
