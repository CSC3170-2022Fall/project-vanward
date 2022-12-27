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
        
        System.out.println("Welcome to Vanward Database Management System!");
        System.out.println("Version: 1.0.0  Custom version for Qatar World Cup\n");
        System.out.println("Copyright 2022, Vanward Database Team\n");
        System.out.println("Type 'exit' or 'quit' to terminate the database management system.\n");
        
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

