package net.deusaquilus.tableprinter;

import org.apache.commons.lang.StringUtils;

/**
 * Simple utility wrap a row into multiple lines
 * @author aioffe
 *
 */
public class RowWrappingUtil {

	public static void wrapRowOnce(final String[] row, int[] colWidths, String[] rowSlice) {

		// while the rows still have content
		for (int i = 0; i < row.length; i++) {
			// get the maximum row size
			int rowSize = colWidths[i];

			String value = row[i];

			// write the next slice of the cell
			rowSlice[i] = StringUtils.substring(value, 0, rowSize);

			// write the remaining value of the cell (StringUtils will conveniently return ""
			// if there's nothing left (or null if the string is empty in the first place)
			row[i] =  StringUtils.substring(value, rowSize);
		}
	}

	public static boolean rowHasContent(String[] row) {
		// walk through all the rows and make sure they have content
		for (String cell : row) {
			if ((cell != null) && (!cell.equals(""))) {
				return true;
			}
		}
		return false;
	}

}
