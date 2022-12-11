CONTENTS:

ReadMe                  This file.

Result Display

When you run the program db61b.db61b.main, you can just type the contents of .txt files in testing. The table result will be showed in two places: terminal and pop-up windows.


db61b:                   A subdirectory containing db61b programs and WCG pictures

    db61b                   A subdirectory containing skeletons for the 
                            db61b package:
        Main.java             The main program---entry point to the db61b system.
        Database.java         Abstraction for an entire collection of tables.  
        Table.java            Abstraction for one table.
        Row.java              Abstraction for one row of a table.
    WCG_picture             A subdirectory containing flag-pictures of participating countries in the World Cup
                            Used for our presentation.

testing:                 Subdirectory holding files for integration testing:
    The basic implementation about DBMS (Required by CS61B)

    students.db, enrolled.db, courses.db(These .db file is in the outside)
                        Sample database tables from the project handout.

    test1.in, test2.in    Input files for testing.  The makefile will respond
                        to 'make check' by running these files through your
                        program, filtering the output through 
                        testing/test-filter, and comparing the results with 
                        the corresponding .out files.  You should add more 
                        files to the list in Makefile.
                        REMINDER: These are samples only.  They DON'T 
                        constitute adequate tests.

    test1.out, test2.out  Output that is supposed to result from test1.in
                        and test2.in, with the first line, all prompts,
                        and all blank lines removed (which is what 
                        test-filter does).

    WorldCupGroups.db, Shooters.db(These .db file is in the outside)
                    Sample database tables from the project handout.
