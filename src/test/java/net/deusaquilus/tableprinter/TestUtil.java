package net.deusaquilus.tableprinter;

import org.junit.Assert;

/**
 * Created by aioffe on 5/29/16.
 */
public class TestUtil {
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
}
