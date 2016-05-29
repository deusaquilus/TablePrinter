package net.deusaquilus.tableprinter;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;
import net.deusaquilus.tableprinter.results.Row;
import net.deusaquilus.tableprinter.results.impl.ListRow;
import net.deusaquilus.tableprinter.results.impl.RowSetMem;
import net.deusaquilus.tableprinter.results.impl.ListRowSet;

import org.junit.Test;

public class TablePrinterTest {

	private static String combineString(String[] str) {
		StringBuffer output = new StringBuffer();
		for (String piece : str) {
			output.append(piece).append("\n");
		}
		return output.toString();
	}

	public static void assertSame(String[] expected, String actual) {
		String combinedExpected = combineString(expected);
		Assert.assertEquals(combinedExpected.trim(), actual.trim());
	}

	@Test
	public void testColWidthsMeasurement() {
		List<String> vars = Arrays.asList("a", "aa", "aaa");
		ListRowSet<String> resultSet = ListRowSet.construct(
				Arrays.asList(
						new ListRow<String>(Arrays.asList("b", "b", "b"), vars),
						new ListRow<String>(Arrays.asList("1", "2", "3"), vars)),
				vars);

		int[] colWidths = TablePrintingUtils.measureColWidths(
				new RowSetMem<String>(resultSet),
				new TablePrinterConfig(),
				new StringValuePrinter<String>());

		Assert.assertEquals(
				toIntegerArray(1, 2, 3),
				toIntegerArray(colWidths));

	}

	@Test
	public void testColWidthsMeasurementEmpty2() {
		@SuppressWarnings("unchecked")
		List<String> vars = Arrays.asList("bbbb", "bbbbb", "bbbbbb");
		ListRowSet<String> resultSet = ListRowSet.construct(
				Arrays.asList(
						new ListRow<String>(Arrays.asList("a", "aa", "aaa"), vars),
						new ListRow<String>(Arrays.asList("1", "2", "3"), vars)
				), vars);

		int[] colWidths = TablePrintingUtils.measureColWidths(
				new RowSetMem<String>(resultSet),
				new TablePrinterConfig(),
				new StringValuePrinter<String>());

		Assert.assertEquals(
				toIntegerArray(4, 5, 6),
				toIntegerArray(colWidths));

	}


	@Test
	public void testColWidthsMeasurementEmpty() {
		@SuppressWarnings("unchecked")
		List<String> vars = Arrays.asList("", "", "");
		ListRowSet<String> resultSet = ListRowSet.construct(
				Arrays.asList(
						new ListRow<String>(Arrays.asList("", "", ""), vars),
						new ListRow<String>(Arrays.asList("", "", ""), vars)
				), vars);

		int[] colWidths = TablePrintingUtils.measureColWidths(
				new RowSetMem<String>(resultSet),
				new TablePrinterConfig(),
				new StringValuePrinter<String>());

		Assert.assertEquals(
				toIntegerArray(1, 1, 1),
				toIntegerArray(colWidths));

	}



	private ArrayList<Integer> toIntegerArray(int... values) {
		ArrayList<Integer> output = new ArrayList<Integer>();
		for (Integer value : values) {
			output.add(value);
		}
		return output;
	}

	@Test
	public void testSimplePrint() {

		String[] expectedStringArr = new String[]{
				"----------------------",
				"| Col1 | Col2 | Col3 |",
				"======================",
				"| a    | aa   | aaa  |",
				"| b    | b    | b    |",
				"----------------------"
		};

		@SuppressWarnings("unchecked")
		List<String> vars = Arrays.asList("Col1", "Col2", "Col3");
		ListRowSet<String> resultSet = ListRowSet.construct(
				Arrays.asList(
						new ListRow<String>(Arrays.asList("a", "aa", "aaa"), vars),
						new ListRow<String>(Arrays.asList("b", "b", "b"), vars)
						),
					vars
				);
		TablePrinter<String> tablePrinter = new TablePrinter<String>();

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		PrintWriter writer = new PrintWriter(outputStream);
		tablePrinter.writeAll(writer, resultSet);
		writer.flush();

		assertSame(expectedStringArr, outputStream.toString());
	}
}
