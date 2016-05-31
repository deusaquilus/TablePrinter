package net.deusaquilus.tableprinter;

import net.deusaquilus.tableprinter.results.impl.ListRow;
import net.deusaquilus.tableprinter.results.impl.ListRowSet;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import static net.deusaquilus.tableprinter.TestUtil.assertSame;

/**
 * Created by aioffe on 5/29/16.
 */
public class ConcurrentPrintingTest {

    ByteArrayOutputStream outputStream;
    PrintWriter writer;

    @Before
    public void setup() {
        outputStream = new ByteArrayOutputStream();
        writer = new PrintWriter(outputStream);
    }

    @Test
    public void testConcurrentSinkStillMeasuring() {

        String[] expectedStringArr = new String[]{
                "----------------------",
                "| Col1 | Col2 | Col3 |",
                "======================",
                "| a    | aa   | aaa  |",
                "| b    | b    | b    |",
                "----------------------"
        };

        TablePrinterConfig config = new TablePrinterConfig();
        config.measuringRows = 3;
        TablePrinter<String> printer = new TablePrinter<String>(config);
        Sink<String> sink = printer.openSink(writer);

        List<String> vars = Arrays.asList("Col1", "Col2", "Col3");
        sink.push(new ListRow<String>(Arrays.asList("a", "aa", "aaa"), vars));
        sink.push(new ListRow<String>(Arrays.asList("b", "b", "b"), vars));

        printer.close();

        assertSame(expectedStringArr, outputStream.toString());
    }


    @Test
    public void testStandardNonBlockedCase() {

        String[] expectedStringArr = new String[]{
                "----------------------",
                "| Col1 | Col2 | Col3 |",
                "======================",
                "| a    | aa   | aaa  |",
                "| b    | b    | b    |",
                "| c    | c    | c    |",
                "| d    | d    | d    |",
                "----------------------"
        };

        TablePrinterConfig config = new TablePrinterConfig();
        config.measuringRows = 2;
        TablePrinter<String> printer = new TablePrinter<String>(config);
        Sink<String> sink = printer.openSink(writer);

        List<String> vars = Arrays.asList("Col1", "Col2", "Col3");
        sink.push(new ListRow<String>(Arrays.asList("a", "aa", "aaa"), vars));
        sink.push(new ListRow<String>(Arrays.asList("b", "b", "b"), vars));
        sink.push(new ListRow<String>(Arrays.asList("c", "c", "c"), vars));
        sink.push(new ListRow<String>(Arrays.asList("d", "d", "d"), vars));

        printer.close();

        assertSame(expectedStringArr, outputStream.toString());
    }

}
