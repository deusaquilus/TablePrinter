# Table Printer

Simple table printing utility that allows streaming results. Based on the table printing utility from the Jena Semantic Framework with several additions such as streaming, line wrapping, and writing out results as you go.

## Usage
In order to use the TablePrinter you will need some implementation of the parameterized `Row<T>` class, there are several provided.
Here's an example of how we might setup the data.
```java
Collection<String> headings = Arrays.asList("First Name", "Last Name", "Title");
ListRowSet<String> data = ListRowSet.construct(
        new ListRow(headings, "Daenerys", "Targaryen", "Queen"),
        new ListRow(headings, "Jaime", "Lannister", "Prince"),
        new ListRow(headings, "Ned", "Stark", "Warden of the North"));
```
The sample printout of this data way should be the following:
```
------------------------------------------------
| First Name | Last Name | Title               |
================================================
| Daenerys   | Targaryen | Queen               |
| Jaime      | Lannister | Prince              |
| Ned        | Stark     | Warden of the North |
------------------------------------------------
```

#### One Shot Method
The first idiomatic way of using this framework is to write all the results in one shot, the writeAll method is designed to do this.
```java
TablePrinter<String> tablePrinter = new TablePrinter<String>();
PrintWriter writer = new PrintWriter(System.out);
tablePrinter.writeAll(writer, data);
```

#### Iterative Read Method
If it is impossible to use the one-shot method (for example if you have results coming off a stream in a loop), you can use the openSink method to open a Sink object into which you can dump the rows as your data comes out. Keep in mind that in order to to this, the table printer has to spin up a new thread and use a
```java
PrintWriter writer = new PrintWriter(System.out);
TablePrinter<String> tablePrinter = new TablePrinter<String>();
Sink<String> sink = tablePrinter.openSink(writer);
while (dataPipe.hasNext()) {
    Person nextData = dataPipe.next();
    sink.push(new ListRow<String>(
            headings, nextData.getFirstName(), nextData.getLastName(), nextData.getTitle()));
}
tablePrinter.close(); // Remember to close the table printer when using a sink
```


Note that since the Sink interface is very simple it should be possible to implement one using an observeable. A demonstration of how to do this together with map/flatmap is coming soon.

### Hybrid Iterative Method
If you want to write results coming out of a loop and cannot open a new thread, there is still one available option. You can pass a number of initial rows into the TablePrinter using the startWriting to set the correct column measurements and then writeSomeMore method to continue with additional results iteratively.
```java
PrintWriter writer = new PrintWriter(System.out);
TablePrinter<String> tablePrinter = new TablePrinter<String>();
// ** Write initial rows in order to measure the column widths in one shot. **
ListRowSet initialRows = ListRowSet.construct(
        new ListRow(headings, toHeading(dataPipe.next())),
        new ListRow(headings, toHeading(dataPipe.next())));
tablePrinter.startWriting(writer, initialRows);
// ** Then switch to the streaming api for all additional rows. **
while (dataPipe.hasNext()) {
    Person nextData = dataPipe.next();
    tablePrinter.writeSomeMore(writer, new ListRow<String>(
            headings, nextData.getFirstName(), nextData.getLastName(), nextData.getTitle()));
}
tablePrinter.finishWriting(writer);   // Remember to call this to print the final divider
```
Note that you may need to check your data pipe to see if these initial results exist and write them into a temporary area so that you will protected against `NoSuchElementException`s.
In those kinds of situations (such as if the size of the data set is very small), you should be able to skip the writeSomeMore loop entirely.


### Configuration
In order to configure the table printer, create an instance of the TablePrinterConfig and pass it ot the TablePrinter constructor.
```java
TablePrinterConfig config = new TablePrinterConfig();
config.measuringRows = 2;
TablePrinter<String> printer = new TablePrinter<String>(config);
```
The measuringRows parameter is perhaps the most important to the TablePrinter, it determines how many rows will be consumed at first in order to measure the column widths before streaming results will being. Make sure that this is a high enough value that an adequate amount of data can be measured before streaming can being. If streaming has begun and the data in a column overflows the pre-calcualated column width, column wrapping will occour. For example if we use the writeAll or Sink methods with the above example the following will be the result.
```
-----------------------------------
| First Name | Last Name | Title  |
===================================
| Daenerys   | Targaryen | Queen  |
| Jaime      | Lannister | Prince |
| Ned        | Stark     | Warden |
|            |           |  of th |
|            |           | e Nort |
|            |           | h      |
-----------------------------------
```
Note that when using the Hyberid Iterative Method this configuration parameter will be ignore and the column widths will be based soley on what is passed to the startWriting method.
