// This is a SUGGESTED skeleton for a class that represents a single
// Table.  You can throw this away if you want, but it is a good
// idea to try to understand it first.  Our solution changes or adds
// about 100 lines in this skeleton.

// Comments that start with "//" are intended to be removed from your
// solutions.
package db61b;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import static db61b.Utils.*;

/** A single table in a database.
 *  @author P. N. Hilfinger
 */
class Table implements Iterable<Row> {
    /** A new Table whose columns are given by COLUMNTITLES, which may
     *  not contain dupliace names. */
    Table(String[] columnTitles) {
        for (int i = columnTitles.length - 1; i >= 1; i -= 1) {
            for (int j = i - 1; j >= 0; j -= 1) {
                if (columnTitles[i].equals(columnTitles[j])) {
                    throw error("duplicate column name: %s",
                                columnTitles[i]);
                }
            }
        }
        column_titles = columnTitles; // FILL IN
    }

    /** A new Table whose columns are give by COLUMNTITLES. */
    Table(List<String> columnTitles) {
        this(columnTitles.toArray(new String[columnTitles.size()]));
    }

    /** Return the number of columns in this table. */
    public int columns() {
        return column_titles.length ;  // REPLACE WITH SOLUTION
    }

    /** Return the title of the Kth column.  Requires 0 <= K < columns(). */
    public String getTitle(int k) {
        return column_titles[k];  // REPLACE WITH SOLUTION
    }

    /** Return the number of the column whose title is TITLE, or -1 if
     *  there isn't one. */
    public int findColumn(String title) {
        for (int i = 0 ; i < column_titles.length; i ++ ){
            if (title.equals (column_titles[i] )){
                return i;
            }
        }
        return -1;// REPLACE WITH SOLUTION
    }

    /** Return the number of Rows in this table. */
    public int size() {
        return _rows.size();  // REPLACE WITH SOLUTION
    }

    /** Returns an iterator that returns my rows in an unspecfied order. */
    @Override
    public Iterator<Row> iterator() {
        return _rows.iterator();
    }

    /** Add ROW to THIS if no equal row already exists.  Return true if anything
     *  was added, false otherwise. */
    public boolean add(Row row) {
        Iterator i = this.iterator();
        while(i.hasNext()) {
            if(i.next().equals(row)){
                return false;
            }
        }
        _rows.add(row);
        return true;   // REPLACE WITH SOLUTION
    }

    /** Read the contents of the file NAME.db, and return as a Table.
     *  Format errors in the .db file cause a DBException. */
    static Table readTable(String name) {
        BufferedReader input;
        Table table;
        input = null;
        table = null;
        try {
            input = new BufferedReader(new FileReader(name + ".db"));
            String header = input.readLine();
            if (header == null) {
                throw error("missing header in DB file");
            }
            String[] columnNames = header.split(",");
            // FILL IN
        } catch (FileNotFoundException e) {
            throw error("could not find %s.db", name);
        } catch (IOException e) {
            throw error("problem reading from %s.db", name);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    /* Ignore IOException */
                }
            }
        }
        return table;
    }

    /** Write the contents of TABLE into the file NAME.db. Any I/O errors
     *  cause a DBException. */
    void writeTable(String name) {
        PrintStream output;
        output = null;
        try {
            String sep;
            sep = "";
            output = new PrintStream(name + ".db");
            // FILL THIS IN
        } catch (IOException e) {
            throw error("trouble writing to %s.db", name);
        } finally {
            if (output != null) {
                output.close();
            }
        }
    }

    /** Print my contents on the standard output. */
    void print() {
        Row row;
        Iterator rows_it =_rows.iterator();
        printArray(column_titles);
        while(rows_it.hasNext()) {
            System.out.println();
            row = (Row) rows_it.next();
            
            for (int i = 0; i < column_titles.length; i++){
                System.out.print(rows_it.get(i) + " ");
            }
        }
        // FILL IN
    }

    /** Return a new Table whose columns are COLUMNNAMES, selected from
     *  rows of this table that satisfy CONDITIONS. */
    Table select(List<String> columnNames, List<Condition> conditions) {
        Table result = new Table(columnNames);
        Iterator<Row> eachRow = _rows.iterator();
        while (eachRow.hasNext()) {
            Row currentRow = eachRow.next();

            List<String> temp = new ArrayList<String>();
            for (int i = 0; i < columnNames.size(); i++) {
                int numOfCol = this.findColumn(columnNames.get(i));
                if (numOfCol != -1 && Condition.test(conditions, currentRow)) {
                    String rowValue = currentRow.get(numOfCol);
                    temp.add(rowValue);
                }
            }

            String[] bar = temp.toArray(new String[columnNames.size()]);
            Row finalRow = new Row(bar);
            if (!(finalRow.get(0) == null)) {
                result.add(finalRow);
            }
        }
        return result;
    }

    /** Return a new Table whose columns are COLUMNNAMES, selected
     *  from pairs of rows from this table and from TABLE2 that match
     *  on all columns with identical names and satisfy CONDITIONS. */
    Table select(Table table2, List<String> columnNames,
                 List<Condition> conditions) {
        List<Column> incommon = new ArrayList<Column>();
        List<Column> inCommon = new ArrayList<Column>();
        Table result = new Table(columnNames);

        for (int r = 0; r < this.columns(); r++) {
            for (int k = 0; k < table2.columns(); k++) {

                if (table2.getTitle(k).equals(this.getTitle(r))) {
                    Column blah2 = new Column(table2.getTitle(k), table2);
                    Column blah1 = new Column(this.getTitle(r), this);
                    inCommon.add(blah2);
                    incommon.add(blah1);
                }
            }
        }
        for (Row oneTime: _rows) {
            for (Row twoTime: _rows) {
                if (equijoin(incommon, inCommon, oneTime, twoTime)) {
                    Row almostDone = new Row(incommon, oneTime, twoTime);
                    result.add(almostDone);
                }
            }
        }
        Table theResult = result.select(columnNames, conditions);
        return theResult;
    }

    /** Return true if the columns COMMON1 from ROW1 and COMMON2 from
     *  ROW2 all have identical values.  Assumes that COMMON1 and
     *  COMMON2 have the same number of elements and the same names,
     *  that the columns in COMMON1 apply to this table, those in
     *  COMMON2 to another, and that ROW1 and ROW2 come, respectively,
     *  from those tables. */
    private static boolean equijoin(List<Column> common1, List<Column> common2,
                                    Row row1, Row row2) {
        for (int i = 0; i < common1.size(); i++) {
            Column column1 = common1.get(i);
            Column column2 = common2.get(i);
            if (!(column1.getFrom(row1).equals(column2.getFrom(row2)))) {
                return false;
            }
        }
        return true;
    }

    /** My rows. */
    private HashSet<Row> _rows = new HashSet<>();
    private String[] column_titles;
    // FILL IN
}

