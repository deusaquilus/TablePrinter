package net.deusaquilus.tableprinter;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.deusaquilus.tableprinter.results.impl.ListRow;
import net.deusaquilus.tableprinter.results.impl.RowSetMem;
import net.deusaquilus.tableprinter.results.impl.ListRowSet;

import net.deusaquilus.tableprinter.results.impl.SingletonRowSet;
import org.junit.Assert;
import org.junit.Test;

public class TablePrinterTest {

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
	public void testColWidthsLonger() {
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

		List<String> vars = Arrays.asList("Col1", "Col2", "Col3");
		ListRowSet<String> resultSet = ListRowSet.construct(
				vars,
				new ListRow<String>(Arrays.asList("a", "aa", "aaa"), vars),
				new ListRow<String>(Arrays.asList("b", "b", "b"), vars));

		TablePrinter<String> tablePrinter = new TablePrinter<String>();

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		PrintWriter writer = new PrintWriter(outputStream);
		tablePrinter.writeAll(writer, resultSet);
		writer.flush();

		TestUtil.assertSame(expectedStringArr, outputStream.toString());
	}

	@Test
	public void testIdiomaticForLoopPrint() {

		String[] expectedStringArr = new String[]{
				"----------------------",
				"| Col1 | Col2 | Col3 |",
				"======================",
				"| a    | aa   | aaa  |",
				"| b    | b    | b    |",
				"| cc   | c    | c    |",
				"| dd   | d    | d    |",
				"----------------------"
		};

		List<String> vars = Arrays.asList("Col1", "Col2", "Col3");
		ListRowSet<String> resultSet = ListRowSet.construct(
				vars,
				new ListRow<String>(vars, "a", "aa", "aaa"),
				new ListRow<String>(vars, "b", "b", "b"));

		TablePrinter<String> tablePrinter = new TablePrinter<String>();

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		PrintWriter writer = new PrintWriter(outputStream);

		tablePrinter.startWriting(writer, resultSet);
		tablePrinter.writeSomeMore(writer, SingletonRowSet.construct(vars, "cc", "c", "c"));
		tablePrinter.writeSomeMore(writer, SingletonRowSet.construct(vars, "dd", "d", "d"));
		tablePrinter.finishWriting(writer);
		writer.flush();

		TestUtil.assertSame(expectedStringArr, outputStream.toString());
	}


	@Test
	public void testIdiomaticForLoopPrintWithWrap() {

		String[] expectedStringArr = new String[]{
				"----------------------",
				"| Col1 | Col2 | Col3 |",
				"======================",
				"| a    | aa   | aaa  |",
				"| b    | b    | b    |",
				"| cc   | c    | c    |",
				"| dd   | d    | dddd |",
				"|      |      | d    |",
				"| ee   | e    | e    |",
				"----------------------"
		};

		List<String> vars = Arrays.asList("Col1", "Col2", "Col3");
		ListRowSet<String> resultSet = ListRowSet.construct(
				vars,
				new ListRow<String>(vars, "a", "aa", "aaa"),
				new ListRow<String>(vars, "b", "b", "b"));

		TablePrinter<String> tablePrinter = new TablePrinter<String>();

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		PrintWriter writer = new PrintWriter(outputStream);

		tablePrinter.startWriting(writer, resultSet);
		tablePrinter.writeSomeMore(writer, SingletonRowSet.construct(vars, "cc", "c", "c"));
		tablePrinter.writeSomeMore(writer, SingletonRowSet.construct(vars, "dd", "d", "ddddd"));
		tablePrinter.writeSomeMore(writer, SingletonRowSet.construct(vars, "ee", "e", "e"));
		tablePrinter.finishWriting(writer);
		writer.flush();

		TestUtil.assertSame(expectedStringArr, outputStream.toString());
	}

	@Test
	public void testRegularPrintWithWrap() {

		String[] expectedStringArr = new String[]{
				"----------------------",
				"| Col1 | Col2 | Col3 |",
				"======================",
				"| a    | aa   | aaa  |",
				"| b    | b    | b    |",
				"| cc   | c    | c    |",
				"| dd   | d    | dddd |",
				"|      |      | d    |",
				"| ee   | e    | e    |",
				"----------------------"
		};

		List<String> vars = Arrays.asList("Col1", "Col2", "Col3");
		ListRowSet<String> resultSet = ListRowSet.construct(
				vars,
				new ListRow<String>(vars, "a", "aa", "aaa"),
				new ListRow<String>(vars, "b", "b", "b"),
				new ListRow<String>(vars, "cc", "c", "c"),
				new ListRow<String>(vars, "dd", "d", "ddddd"),
				new ListRow<String>(vars, "ee", "e", "e")
				);

		TablePrinterConfig config = new TablePrinterConfig();
		config.measuringRows = 1;
		TablePrinter<String> tablePrinter = new TablePrinter<String>(config);

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		PrintWriter writer = new PrintWriter(outputStream);

		tablePrinter.writeAll(writer, resultSet);
		writer.flush();

		TestUtil.assertSame(expectedStringArr, outputStream.toString());
	}

	@Test
	public void testRegularPrintWithMultipleWrap() {

		String[] expectedStringArr = new String[]{
				"----------------------",
				"| Col1 | Col2 | Col3 |",
				"======================",
				"| a    | a    | a    |",
				"| dd   | dddd | dddd |",
				"|      | dd   | d    |",
				"| ee   | e    | e    |",
				"----------------------"
		};

		List<String> vars = Arrays.asList("Col1", "Col2", "Col3");
		ListRowSet<String> resultSet = ListRowSet.construct(
				vars,
				new ListRow<String>(vars, "a", "a", "a"),
				new ListRow<String>(vars, "dd", "dddddd", "ddddd"),
				new ListRow<String>(vars, "ee", "e", "e")
		);

		TablePrinterConfig config = new TablePrinterConfig();
		config.measuringRows = 1;
		TablePrinter<String> tablePrinter = new TablePrinter<String>(config);

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		PrintWriter writer = new PrintWriter(outputStream);

		tablePrinter.writeAll(writer, resultSet);
		writer.flush();

		TestUtil.assertSame(expectedStringArr, outputStream.toString());
	}

	// TODO Implement a mock slink that will:
	// - sink that will wait int he middle of initial measuring with no 'add-more' rows
	// - sink that will wait in one of the wait-some-more rows
	// - sink that will wait immediately on the first wait-some-more row
	// - sink that will wait on the last measuring row

}
