package net.deusaquilus.tableprinter.examples;

import net.deusaquilus.tableprinter.Sink;
import net.deusaquilus.tableprinter.TablePrinter;
import net.deusaquilus.tableprinter.TablePrinterConfig;
import net.deusaquilus.tableprinter.results.Row;
import net.deusaquilus.tableprinter.results.impl.ListRow;
import net.deusaquilus.tableprinter.results.impl.ListRowSet;
import net.deusaquilus.tableprinter.results.impl.SingletonRowSet;

import java.io.PrintWriter;
import java.util.*;

/**
 * Created by aioffe on 5/31/16.
 */
public class Examples {

    public static class Person {
        private String firstName;
        public String lastName;
        public String title;

        public Person(String firstName, String lastName, String title) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.title = title;
        }

        public String getFirstName() {
            return firstName;
        }
        public String getLastName() {
            return this.lastName;
        }
        public String getTitle() {
            return this.title;
        }

    }

    public static class DataPipe {
        Iterator<Person> data;
        public DataPipe() {
            data = new ArrayList<Person>(Arrays.asList(
                    new Person("Daenerys", "Targaryen", "Queen"),
                    new Person("Jaime", "Lannister", "Prince"),
                    new Person("Ned", "Stark", "Warden of the North"))).iterator();
        }

        public boolean hasNext() {
            return data.hasNext();
        }

        public Person next() {
            return data.next();
        }
    }

    public static String[] toHeading(Person person) {
        return new String[]{person.getFirstName(), person.getLastName(), person.getTitle()};
    }

    public static void main(String[] args) {
        oneShotMethod();
        iterativeReadMethod();
        hybridIterativeMethod();
    }

    public static void hybridIterativeMethod() {
        DataPipe dataPipe = new DataPipe();
        Collection<String> headings = Arrays.asList("First Name", "Last Name", "Title");

        PrintWriter writer = new PrintWriter(System.out);
        TablePrinter<String> tablePrinter = new TablePrinter<String>();

        // write initial rows in order to measure the column widths on a one-shot
        ListRowSet initialRows = ListRowSet.construct(
                new ListRow(headings, toHeading(dataPipe.next())),
                new ListRow(headings, toHeading(dataPipe.next())));
        tablePrinter.startWriting(writer, initialRows);

        // then switch to the streaming api for all additional rows
        while (dataPipe.hasNext()) {
            Person nextData = dataPipe.next();
            tablePrinter.writeSomeMore(writer, new ListRow<String>(
                    headings, nextData.getFirstName(), nextData.getLastName(), nextData.getTitle()));
        }
        tablePrinter.finishWriting(writer);
    }


    public static void iterativeReadMethod() {
        DataPipe dataPipe = new DataPipe();
        Collection<String> headings = Arrays.asList("First Name", "Last Name", "Title");

        PrintWriter writer = new PrintWriter(System.out);
        TablePrinter<String> tablePrinter = new TablePrinter<String>();
        Sink<String> sink = tablePrinter.openSink(writer);
        while (dataPipe.hasNext()) {
            Person nextData = dataPipe.next();
            sink.push(new ListRow<String>(
                    headings, nextData.getFirstName(), nextData.getLastName(), nextData.getTitle()));
        }
        tablePrinter.close(); // Remember to close the table printer when using sink
    }


    public static void oneShotMethod() {
        Collection<String> headings = Arrays.asList("First Name", "Last Name", "Title");
        ListRowSet<String> data = ListRowSet.construct(
                new ListRow(headings, "Daenerys", "Targaryen", "Queen"),
                new ListRow(headings, "Jaime", "Lannister", "Prince"),
                new ListRow(headings, "Ned", "Stark", "Warden of the North"));
        TablePrinter<String> tablePrinter = new TablePrinter<String>();
        PrintWriter writer = new PrintWriter(System.out);
        tablePrinter.writeAll(writer, data);
    }

}
