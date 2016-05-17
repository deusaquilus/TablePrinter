package net.deusaquilus.tableprinter;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

public class RowSplitUtilTest {

	public void assertArraysEqual(String[] one, String[] two) {
		Assert.assertTrue(
				new ArrayList<String>(Arrays.asList(one)).equals(
						new ArrayList<String>(Arrays.asList(two))));
	}

	public void assertRowEmpty(String[] row, boolean empty) {
		Assert.assertEquals(empty, !RowWrappingUtil.rowHasContent(row));
	}

	@Test
	public void testSingleRowSplitting() {
		String[] output = new String[1];
		String[] input = new String[]{"abcde"};
		RowWrappingUtil.wrapRowOnce(input, new int[]{3}, output);

		assertArraysEqual(output, new String[]{"abc"});
		assertArraysEqual(input, new String[]{"de"});
		assertRowEmpty(input, false);

		RowWrappingUtil.wrapRowOnce(input, new int[]{3}, output);

		assertArraysEqual(output, new String[]{"de"});
		assertArraysEqual(input, new String[]{""});
		assertRowEmpty(input, true);
	}

	@Test
	public void testMultipleRowSplitting() {
		String[] output = new String[4];
		String[] input = new String[]{"abcde", "1234", "efg", "h"};
		RowWrappingUtil.wrapRowOnce(input, new int[]{3, 3, 3, 3}, output);

		assertArraysEqual(output, new String[]{"abc", "123", "efg", "h"});
		assertArraysEqual(input, new String[]{"de", "4", "", ""});
		assertRowEmpty(input, false);

		RowWrappingUtil.wrapRowOnce(input, new int[]{3, 3, 3, 3}, output);

		assertArraysEqual(output, new String[]{"de", "4", "", ""});
		assertArraysEqual(input, new String[]{"", "", "", ""});
		assertRowEmpty(input, true);
	}

}
