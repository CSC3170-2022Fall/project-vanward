package db61b;

import java.util.Scanner;

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
        System.out.printf("DB61B System.  Version %s.%n", VERSION);
        String[] s1 = {"1","2","3"};
        String[] s2 = {"1","2","3"};
        String[] s3 = {"4","5","6"};
        Row r1 = new Row(s1); 
        Row r2 = new Row(s2); 
        Row r3 = new Row(s3);
        System.out.println("Test get() r1.get(0): " + r1.get(0)); 
        System.out.println("Test size() r1.size(): " + r1.size()); 
        System.out.println("Test equals() r1.equals(r2): " + r1.equals(r2)); 
        System.out.println("Test equals() r1.equals(r3): " + r1.equals(r3)); 
    //     Scanner input = new Scanner(System.in);
    //     CommandInterpreter interpreter =
    //         new CommandInterpreter(input, System.out);

    //     while (true) {
    //         try {
    //             if (!interpreter.statement()) {
    //                 break;
    //             }
    //         } catch (DBException e) {
    //             System.out.printf("Error: %s%n", e.getMessage());
    //             interpreter.skipCommand();
    //         }
    //     }
    }

}

