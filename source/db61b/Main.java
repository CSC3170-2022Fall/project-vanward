package db61b;

import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

/** The main program for db61b.
 *  @author P. N. Hilfinger
 */
public class Main {

    /** Version designation for this program. */
    private static final String VERSION = "2.0";

    /** Starting with an empty database, read and execute commands from
     *  System.in until receiving a 'quit' ('exit') command or until
     *  reaching the end of input. */
    public static void main(String[] unused) {
        // System.out.printf("DB61B System.  Version %s.%n", VERSION);
        // String col_titles[] = {"c1", "c2", "c3"}; 
        // Table t1 = new Table(col_titles);
        // String[] s1 = {"1","2","3"};
        // String[] s2 = {"4","5","6"};
        // String[] s3 = {"7","8","9"};
        // Row r1 = new Row(s1); 
        // Row r2 = new Row(s2); 
        // Row r3 = new Row(s3);
        // t1.add(r1);
        // t1.add(r2);
        // t1.add(r3);
        // String col_titles1[] = {"c1", "c4", "c5"};
        // Table t2 = new Table(col_titles1);
        // String[] s4 = {"1","2","3"};
        // String[] s5 = {"4","5","6"};
        // String[] s6 = {"7","8","9"};
        // Row r4 = new Row(s4); 
        // Row r5 = new Row(s5); 
        // Row r6 = new Row(s6);
        // t2.add(r4);
        // t2.add(r5);
        // t2.add(r6);
        // Column c1 = new Column("c2", t1, t2);
        // Column c2 = new Column("c4", t1, t2);    
        // List<Column> columns = new ArrayList<>();
        // columns.add(c1);
        // columns.add(c2);
        // Row testa = new Row(columns, r1, r4);
        // System.out.println(testa.get(0) + " " + testa.get(1));
    System.out.println("Welcome to Vanward Database Management System!");
    System.out.println("Version: 1.0.0  Custom version for Qatar World Cup\n");
    System.out.println("Copyright 2022, Vanward Database Team\n");
    System.out.println("Type 'exit' to terminate the database management system.\n");
    Scanner input = new Scanner(System.in);
        CommandInterpreter interpreter =
            new CommandInterpreter(input, System.out);

        while (true) {
            try {
                if (!interpreter.statement()) {
                    break;
                }
            } catch (DBException e) {
                System.out.printf("Error: %s%n", e.getMessage());
                interpreter.skipCommand();
            }
        }
    
    }

}

