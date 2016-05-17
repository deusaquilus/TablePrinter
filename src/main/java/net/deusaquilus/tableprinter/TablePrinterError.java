package net.deusaquilus.tableprinter;

/**
 * A simple error class for the table printer
 * @author aioffe
 *
 */
public class TablePrinterError extends RuntimeException {

	/** Serial version id. */
	private static final long serialVersionUID = -781892281520967768L;

	/**
	 * Create an table printing error.
	 * @param message The error message
	 * @param cause  The cause of the error.
	 */
	public TablePrinterError(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Create an table printing error.
	 * @param message The error message
	 */
	public TablePrinterError(String message) {
		super(message);
	}

}
