package net.deusaquilus.tableprinter;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.deusaquilus.tableprinter.results.impl.ListRow;
import net.deusaquilus.tableprinter.results.impl.basic.ListRowSet;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		PrintWriter pw = new PrintWriter(System.out);

		List<String> vars = Arrays.asList("One", "Two", "Three");

		ConcurrentTablePrinter<String> printer = new ConcurrentTablePrinter<String>(pw, vars);
		printer.start();
		for (int i=0; i<100000000; i++) {
			printer.add(new ListRow<String>(Arrays.asList("Blin"+i, "Yatz"+i, "Batz"+i), vars));
		}

		printer.stop();
	}

	public static void printInMem() {
		TablePrinter<String> printer = new TablePrinter<String>();

		ArrayList<List<String>> output = new ArrayList<List<String>>();
		for (int i=0; i<1000; i++) {
			output.add(Arrays.asList("Blin"+i, "Yatz"+i, "Batz"+i));
		}

		ListRowSet<String> listRowSet = new ListRowSet<String>(
				output,
				Arrays.asList("One", "Two", "Three"));

		PrintWriter pw = new PrintWriter(System.out);
		printer.format(pw, listRowSet);
		pw.flush();
	}

}
