// This is a SUGGESTED skeleton for a class that parses and executes database
// statements.  Be sure to read the STRATEGY section, and ask us if you have any
// questions about it.  You can throw this away if you want, but it is a good
// idea to try to understand it first.  Our solution adds or changes about 50
// lines in this skeleton.

// Comments that start with "//" are intended to be removed from your
// solutions.
package db61b;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Scanner;

import javax.swing.text.TabSet;

import static db61b.Utils.*;
import static db61b.Tokenizer.*;

/** An object that reads and interprets a sequence of commands from an
 *  input source.
 *  @author */
class CommandInterpreter {

    /* STRATEGY.
     *
     *   This interpreter parses commands using a technique called
     * "recursive descent." The idea is simple: we convert the BNF grammar,
     * as given in the specification document, into a program.
     *
     * First, we break up the input into "tokens": strings that correspond
     * to the "base case" symbols used in the BNF grammar.  These are
     * keywords, such as "select" or "create"; punctuation and relation
     * symbols such as ";", ",", ">="; and other names (of columns or tables).
     * All whitespace and comments get discarded in this process, so that the
     * rest of the program can deal just with things mentioned in the BNF.
     * The class Tokenizer performs this breaking-up task, known as
     * "tokenizing" or "lexical analysis."
     *
     * The rest of the parser consists of a set of functions that call each
     * other (possibly recursively, although that isn't needed for this
     * particular grammar) to operate on the sequence of tokens, one function
     * for each BNF rule. Consider a rule such as
     *
     *    <create statement> ::= create table <table name> <table definition> ;
     *
     * We can treat this as a definition for a function named (say)
     * createStatement.  The purpose of this function is to consume the
     * tokens for one create statement from the remaining token sequence,
     * to perform the required actions, and to return the resulting value,
     * if any (a create statement has no value, just side-effects, but a
     * select clause is supposed to produce a table, according to the spec.)
     *
     * The body of createStatement is dictated by the right-hand side of the
     * rule.  For each token (like create), we check that the next item in
     * the token stream is "create" (and report an error otherwise), and then
     * advance to the next token.  For a metavariable, like <table definition>,
     * we consume the tokens for <table definition>, and do whatever is
     * appropriate with the resulting value.  We do so by calling the
     * tableDefinition function, which is constructed (as is createStatement)
     * to do exactly this.
     *
     * Thus, the body of createStatement would look like this (_input is
     * the sequence of tokens):
     *
     *    _input.next("create");
     *    _input.next("table");
     *    String name = name();
     *    Table table = tableDefinition();
     *    _input.next(";");
     *
     * plus other code that operates on name and table to perform the function
     * of the create statement.  The .next method of Tokenizer is set up to
     * throw an exception (DBException) if the next token does not match its
     * argument.  Thus, any syntax error will cause an exception, which your
     * program can catch to do error reporting.
     *
     * This leaves the issue of what to do with rules that have alternatives
     * (the "|" symbol in the BNF grammar).  Fortunately, our grammar has
     * been written with this problem in mind.  When there are multiple
     * alternatives, you can always tell which to pick based on the next
     * unconsumed token.  For example, <table definition> has two alternative
     * right-hand sides, one of which starts with "(", and one with "as".
     * So all you have to do is test:
     *
     *     if (_input.nextIs("(")) {
     *         _input.next("(");
     *         // code to process "<column name>,  )"
     *     } else {
     *         // code to process "as <select clause>"
     *     }
     *
     * As a convenience, you can also write this as
     *
     *     if (_input.nextIf("(")) {
     *         // code to process "<column name>,  )"
     *     } else {
     *         // code to process "as <select clause>"
     *     }
     *
     * combining the calls to .nextIs and .next.
     *
     * You can handle the list of <column name>s in the preceding in a number
     * of ways, but personally, I suggest a simple loop:
     *
     *     ... = columnName();
     *     while (_input.nextIs(",")) {
     *         _input.next(",");
     *         ... = columnName();
     *     }
     *
     * or if you prefer even greater concision:
     *
     *     ... = columnName();
     *     while (_input.nextIf(",")) {
     *         ... = columnName();
     *     }
     *
     * (You'll have to figure out what do with the names you accumulate, of
     * course).
     */


    /** A new CommandInterpreter executing commands read from INP, writing
     *  prompts on PROMPTER, if it is non-null. */
    CommandInterpreter(Scanner inp, PrintStream prompter) {
        _input = new Tokenizer(inp, prompter);
        _database = new Database();
    }

    /** Parse and execute one statement from the token stream.  Return true
     *  iff the command is something other than quit or exit. */
    boolean statement() {
        switch (_input.peek()) {
        case "create":
            createStatement();
            break;
        case "load":
            loadStatement();
            break;
        case "exit": case "quit":
            exitStatement();
            return false;
        case "*EOF*":
            return false;
        case "insert":
            insertStatement();
            break;
        case "delete":
            deleteStatement();
            break;
        case "print":
            printStatement();
            break;
        case "select":
            selectStatement();
            break;
        case "store":
            storeStatement();
            break;
        case "column_plus":
            columnPlusStatement();
            break;
        case "column_minus":
            columnMinusStatement();
            break;
        default:
            throw error("unrecognizable command");
        }
        return true;
    }

    private static String[] insert(String[] arr, String... str) {
        int size = arr.length; 
        int newSize = size + str.length; 
        
        String[] tmp = new String[newSize]; 
        for (int i = 0; i < size; i++) { 
            tmp[i] = arr[i];
        }
        for (int i = size; i < newSize; i++) {
            tmp[i] = str[i - size];
        } 
        return tmp; 
    }

    // Column Plus
    void columnPlusStatement(){
        _input.next("column_plus");
        String table_name = name();
        Table table = _database.get(table_name);
        if(table==null){
            throw error("Table %s is not exist",table_name);
        }
        _input.next(":");
        String col_1_name = name();
        _input.next("and");
        String col_2_name = name();
        _input.next("to");
        String col_new = name();
        Table new_table = columnPlusCluase(table, col_1_name, col_2_name, col_new);
        _database.put(table_name, new_table);
        new_table.print();
        _input.next(";");
    }

    // column puls function
    Table columnPlusCluase(Table pre_table, String col_1_name, String col_2_name, String col_new){
        String[] new_column_titles = pre_table.get_column_titles();
        new_column_titles = insert(new_column_titles, col_new);
        Table new_table = new Table(new_column_titles);
        Column column1 = new Column(col_1_name, pre_table);
        Column column2 = new Column(col_2_name, pre_table);
        for(Row row : pre_table){
            String[] data = row.get_data();
            try{
                Double column1_value = Double.parseDouble(column1.getFrom(row));
                Double column2_value = Double.parseDouble(column2.getFrom(row));
                String new_column_value = String.valueOf(column1_value+column2_value);
                data = insert(data, new_column_value);
                Row new_row = new Row(data);
                new_table.add(new_row);
            }catch(Exception e){
                throw error("Plus data type false.");
            }
        }
        return new_table;
    }


    // Column PMinus
    void columnMinusStatement(){
        _input.next("column_minus");
        String table_name = name();
        Table table = _database.get(table_name);
        if(table==null){
            throw error("Table %s is not exist",table_name);
        }
        _input.next(":");
        String col_1_name = name();
        _input.next("and");
        String col_2_name = name();
        _input.next("to");
        String col_new = name();
        Table new_table = columnMinusCluase(table, col_1_name, col_2_name, col_new);
        _database.put(table_name, new_table);
        new_table.print();
        _input.next(";");
    }

    // column minus function
    Table columnMinusCluase(Table pre_table, String col_1_name, String col_2_name, String col_new){
        String[] new_column_titles = pre_table.get_column_titles();
        new_column_titles = insert(new_column_titles, col_new);
        Table new_table = new Table(new_column_titles);
        Column column1 = new Column(col_1_name, pre_table);
        Column column2 = new Column(col_2_name, pre_table);
        for(Row row : pre_table){
            String[] data = row.get_data();
            try{
                Double column1_value = Double.parseDouble(column1.getFrom(row));
                Double column2_value = Double.parseDouble(column2.getFrom(row));
                String new_column_value = String.valueOf(column1_value-column2_value);
                data = insert(data, new_column_value);
                Row new_row = new Row(data);
                new_table.add(new_row);
            }catch(Exception e){
                throw error("Minus data type false.");
            }
        }
        return new_table;
    }

    /** Parse and execute a create statement from the token stream. */
    void createStatement() {
        _input.next("create");
        _input.next("table");
        String name = name();
        Table table = tableDefinition();
        for(Row row : table.rows_count.keySet())
            table.rows_count.put(row, 1);
        _database.put(name, table);
        _input.next(";");
    }

    /** Parse and execute an exit or quit statement. Actually does nothing
     *  except check syntax, since statement() handles the actual exiting. */
    void exitStatement() {
        if (!_input.nextIf("quit")) {
            _input.next("exit");
        }
        _input.next(";");
    }



    /** Parse and execute an delete statement from the token stream. */
    void deleteStatement() {
        _input.next("delete");
        _input.next("from");
        String name = name();
        Table table = _database.get(name);
        if (table == null) {
            throw error("Delete Inexistent Table: %s", name);
        }
        _database.delete(name);
        _input.next(";");
    }

    /** Parse and execute an insert statement from the token stream. */
    void insertStatement() {
        _input.next("insert");
        _input.next("into");
        Table table = tableName();
        _input.next("values");

        ArrayList<String> values = new ArrayList<>();
        values.add(literal());
        while (_input.nextIf(",")) {
            values.add(literal());
        }

        table.add(new Row(values.toArray(new String[values.size()])));
        _input.next(";");
    }

    /** Parse and execute a load statement from the token stream. */
    void loadStatement() {
        _input.next("load");
        String table_name = name();
        Table new_table = Table.readTable(table_name);
        _database.put(table_name, new_table);
        System.out.printf("Loaded %s.db%n", table_name);
        _input.next(";");
    }

    /** Parse and execute a store statement from the token stream. */
    void storeStatement() {
        _input.next("store");
        String name = _input.peek();
        Table table = tableName();
        table.writeTable(name);
        System.out.printf("Stored %s.db%n", name);
        _input.next(";");
    }

    /** Parse and execute a print statement from the token stream. */
    void printStatement() {
        _input.next("print");
        String name = _input.peek();
        Table table = tableName();
        _input.next(";");
        System.out.printf("Contents of %s:%n", name);
        table.print();
    }

    /** Parse and execute a select statement from the token stream. */
    void selectStatement() {
        Table table = selectClause();
        _input.next(";");
        System.out.println("Select result:");
        table.print();
    }

    /** Parse and execute a table definition, returning the specified
     *  table. */
    Table tableDefinition() {
        Table table;
        if (_input.nextIf("(")) { // case: create table <table name> ( <column name>, ) 
            ArrayList<String> column_titles = new ArrayList<>();
            // obtain column names
            do{
                column_titles.add(columnName());   
            }while(_input.nextIf(","));
            _input.next(")");
            // obtain table according one of table constructors
            table = new Table(column_titles);
        } else { // case: create table <table name> as <select clause>
            _input.next("as");
            table = selectClause();
        }
        
        return table;
    }

   Row table_aggregate_function(Table ori_table, ArrayList<String> titles, ArrayList<String> types){
        int titles_length = titles.size();
        String[] values = new String[titles_length];
        for(int i = 0; i < titles_length; ++i)  values[i] = null;
        // the columns we need
        ArrayList<Column> columns = new ArrayList<>();
        for (String title : titles) columns.add(new Column(title, ori_table));
        // the set in order to obtain count
        ArrayList<HashSet<String>> value_numbers = new ArrayList<>();
        for(int i = 0; i < titles_length; ++i) value_numbers.add(new HashSet<>());
        int total_num = 0;
        for(Row row : ori_table){
            total_num += ori_table.rows_count.get(row);
            for(int i = 0; i < titles_length; ++i){
                Column column = columns.get(i);
                if(types.get(i).equals("normal"))   values[i] = column.getFrom(row);
                else if(types.get(i).equals("avg") || types.get(i).equals("sum") )   {
                    double tmp;
                    try{
                        tmp = Double.parseDouble(column.getFrom(row));
                    }
                    catch(Exception e){
                        throw error("The value type cannot convert to double!"); 
                    }
                    if(values[i] == null)  {
                        values[i] = String.valueOf((tmp * ori_table.rows_count.get(row)));
                    }
                    else{
                        double value = Double.parseDouble(values[i]);
                        value += tmp * ori_table.rows_count.get(row);
                        values[i] = String.valueOf(value);
                    }
                }
                else if(types.get(i).equals("min")){
                    String tmp = column.getFrom(row);
                    if(values[i] == null)  values[i] = tmp;
                    else{ 
                        try{
                            double value = Double.parseDouble(values[i]);
                            double tmp_value = Double.parseDouble(tmp);

                            if(tmp_value < value)   values[i] = tmp;
                        }
                        catch(Exception e){
                            if(tmp.compareTo(values[i]) < 0) values[i] = tmp;
                        }
                    }
                }
                else if(types.get(i).equals("max")){
                    String tmp = column.getFrom(row);
                    if(values[i] == null)  values[i] = tmp;
                    else{ 
                        try{
                            double value = Double.parseDouble(values[i]);
                            double tmp_value = Double.parseDouble(tmp);
                            if(tmp_value > value)   values[i] = tmp;
                        }
                        catch(Exception e){
                            if(tmp.compareTo(values[i]) > 0) values[i] = tmp;
                        }
                    }
                }
                else if(types.get(i).equals("count")){
                    String tmp = column.getFrom(row);
                    value_numbers.get(i).add(tmp);
                }
            }
        }
        for(int i = 0; i < titles_length; ++i){
            if(types.get(i).equals("avg")){
                double value = Double.parseDouble(values[i]);
                values[i] = String.valueOf((value / total_num));
            }
            else if(types.get(i).equals("count")){
                values[i] = String.valueOf(value_numbers.get(i).size());
            }
        }
        
        return new Row(values);
    }

    /** Parse and execute a select clause from the token stream, returning the
     *  resulting table. */
    Table selectClause() {  //select <column name>, from <tables> <condition clause>
        _input.next("select");
        // obtain selected column titles
        ArrayList<String> column_titles = new ArrayList<>();
        ArrayList<String> return_titles = new ArrayList<>();
        ArrayList<String> aggregate_types = new ArrayList<>();

        do {
            if(_input.nextIf("avg")){
                _input.next("(");
                String name = columnName();
                return_titles.add(name);
                aggregate_types.add("avg");
                _input.next(")");
            }
            else if(_input.nextIf("sum")){
                _input.next("(");
                String name = columnName();
                return_titles.add(name);
                aggregate_types.add("sum");
                _input.next(")");
            }
            else if(_input.nextIf("min")){
                _input.next("(");
                String name = columnName();
                return_titles.add(name);
                aggregate_types.add("min");
                _input.next(")");
            }
            else if(_input.nextIf("max")){
                _input.next("(");
                String name = columnName();
                return_titles.add(name);
                aggregate_types.add("max");
                _input.next(")");
            }
            else if(_input.nextIf("count")){
                _input.next("(");
                String name = columnName();
                return_titles.add(name);
                aggregate_types.add("count");
                _input.next(")");
            }
            else {
                String name = columnName();
                return_titles.add(name);
                aggregate_types.add("normal");
            }
        } while (_input.nextIf(","));
        // check whether it contains dupliace columns(titles + aggregate)
        int return_column_length = return_titles.size();
        for(int i = return_column_length - 1; i > 0; --i){
            for(int j = i-1; j >=0; --j){
                if(return_titles.get(i).equals(return_titles.get(j))
                    && aggregate_types.get(i).equals(aggregate_types.get(j))){
                        throw error("duplicate column name: %s and aggregate type: %s",
                            return_titles.get(i), aggregate_types.get(i));
                    }
            }
        }
        // obtain different column titles
        for(int i = 0; i < return_column_length; ++i){
            String tmp_title = return_titles.get(i);
            Boolean flag = false;
            for(String compare_title : column_titles){
                if(tmp_title.equals(compare_title)) flag = true;
            }
            if(!flag) column_titles.add(tmp_title);
        }
        // obtain target tables (one or two)
        _input.next("from");
        Table table1 = tableName();
        Table table2 = null;
        if (_input.nextIf(",")) {
            table2 = tableName();
        }
        // obtain conditions and the table after basic select based on conditions
        Table select_table = null;
        ArrayList<Condition> conditions;
        if (table2!=null) {
            conditions = conditionClause(table1, table2);
            select_table = table1.select(table2, column_titles, conditions);
        }
        else {
            conditions = conditionClause(table1);
            select_table = table1.select(column_titles, conditions);
        }
        // select_table.print();
        // Order by
        Table order_table = null;
        if (_input.nextIf("order")) {
            _input.next("by");
            order_table = new Table(column_titles);
            String column_name = name();
            if(_input.nextIf("desc")){
                Column col = new Column(column_name, select_table);
                while(select_table.size() != 0){
                    Row max_row = null;
                    try{
                        Double max = null;
                        String value = null;
                        for(Row row : select_table){
                            value = col.getFrom(row);
                            if(max == null){
                                max = Double.valueOf(value);
                                max_row = row;
                                continue;
                            }
                            if(max < Double.valueOf(value)){
                                max = Double.valueOf(value);
                                max_row = row;
                            }
                        }   
                    }catch(Exception e){
                        String max = null;
                        for(Row row : select_table){
                            if(max == null){
                                max = col.getFrom(row);
                                max_row = row;
                                continue;
                            }
                            if(max.compareTo(col.getFrom(row)) < 0){
                                max = col.getFrom(row);
                                max_row = row;
                            }
                        }   
                    }
                    select_table.remove(max_row);
                    order_table.add(max_row);
                }
            }
            else{
                _input.nextIf("asc");
                Column col = new Column(column_name, select_table);
                while(select_table.size() != 0){
                    Row min_row = null;
                    try{
                        Double min = null;
                        String value = null;
                        for(Row row : select_table){
                            value = col.getFrom(row);
                            if(min == null){
                                min = Double.valueOf(value);
                                min_row = row;
                                continue;
                            }
                            if(min > Double.valueOf(value)){
                                min = Double.valueOf(value);
                                min_row = row;
                            }
                        }   
                    }catch(Exception e){
                        String min = null;
                        for(Row row : select_table){
                            if(min == null){
                                min = col.getFrom(row);
                                min_row = row;
                                continue;
                            }
                            if(min.compareTo(col.getFrom(row)) > 0){
                                min = col.getFrom(row);
                                min_row = row;
                            }
                        }   
                    }
                    select_table.remove(min_row);
                    order_table.add(min_row);
                }
            }     
        }
        else order_table = select_table;
        // group by
        ArrayList<String> final_titles = new ArrayList<>();
        for(int i = 0; i < return_column_length; ++i){
            if(aggregate_types.get(i) == "normal")  final_titles.add(return_titles.get(i));
            else    final_titles.add(aggregate_types.get(i) + "-" + return_titles.get(i));
        }
        Table final_table = new Table(final_titles);
        if(_input.nextIf("group")){
            _input.next("by");
            String group_title = name();
            // check only the group title is only title of "normal" aggregate type
            for(int i = 0; i < return_column_length; ++i){
                if(return_titles.get(i).equals(group_title) 
                    && !aggregate_types.get(i).equals("normal")) 
                        throw error("Group by is error format!");
                else if(!return_titles.get(i).equals(group_title) 
                    && aggregate_types.get(i).equals("normal"))
                        throw error("Group by is error format!");
            }
           // obtain different groups
            Column group_Column = new Column(group_title, order_table);
            LinkedHashMap <String, Table> groups = new LinkedHashMap<>();
            for(Row row : order_table){
                String value = group_Column.getFrom(row);
                if(groups.get(value) == null){
                    Table current = new Table(column_titles);
                    current.add(row);
                    current.rows_count.put(row, order_table.rows_count.get(row));
                    groups.put(value, current);
                }
                else {
                    groups.get(value).add(row);
                    groups.get(value).rows_count.put(row, order_table.rows_count.get(row));
                }
            }
           
            // aggregate functions
            for(String name : groups.keySet()){
                Table tmp_table = groups.get(name);
                final_table.add(table_aggregate_function(tmp_table, return_titles, aggregate_types));
            }
        } 
        else{
            String same_type = aggregate_types.get(0);
            for(String tmp : aggregate_types){
                if(same_type.equals("normal") && !same_type.equals(tmp)) 
                    throw error("This is an invalid select statement!\nAggregate function is not allowed here!");
                else if(!same_type.equals("normal") && tmp.equals("normal"))
                    throw error("This is an invalid select statement!\nAll attributes should be aggregate functions!");
            }
            if(same_type.equals("normal") ) final_table = order_table;
            else final_table.add(table_aggregate_function(order_table, return_titles, aggregate_types));
        }
        return final_table;
    }

    /** Parse and return a valid name (identifier) from the token stream. */
    String name() {
        return _input.next(Tokenizer.IDENTIFIER);
    }

    /** Parse and return a valid column name from the token stream. Column
     *  names are simply names; we use a different method name to clarify
     *  the intent of the code. */
    String columnName() {
        return name();
    }

    /** Parse a valid table name from the token stream, and return the Table
     *  that it designates, which must be loaded. */
    Table tableName() {
        String name = name();
        Table table = _database.get(name);
        if (table == null) {
            throw error("unknown table: %s", name);
        }
        return table;
    }

    /** Parse a literal and return the string it represents (i.e., without
     *  single quotes). */
    String literal() {
        String lit = _input.next(Tokenizer.LITERAL);
        return lit.substring(1, lit.length() - 1).trim();
    }

    /** Parse and return a list of Conditions that apply to TABLES from the
     *  token stream.  This denotes the conjunction (`and') zero
     *  or more Conditions. */
    ArrayList<Condition> conditionClause(Table... tables) {
        ArrayList<Condition> conditions = new ArrayList<>();
        if(_input.nextIf("where")){
            do{
                Column column1 = new Column(_input.next(Tokenizer.IDENTIFIER), tables);
                String relation = _input.next(Tokenizer.RELATION);
                if(_input.nextIs(Tokenizer.IDENTIFIER)){
                    Column column2 = new Column(_input.next(Tokenizer.IDENTIFIER), tables);
                    Condition condition = new Condition(column1, relation, column2);
                    conditions.add(condition);
                }
                else if(_input.nextIs(Tokenizer.LITERAL)){
                    String value = literal();
                    Condition condition = new Condition(column1, relation, value);
                    conditions.add(condition);
                }
            }
            while(_input.nextIf("and") || _input.nextIf("AND"));
        }
        
        return conditions;
    }

    /** Parse and return a Condition that applies to TABLES from the
     *  token stream. */
    /*
    Condition condition(Table... tables) {
        Column column1 = new Column(_input.next(Tokenizer.IDENTIFIER), tables);
        String relation = _input.next(Tokenizer.RELATION);
        Condition condition;
        if(_input.nextIs(Tokenizer.IDENTIFIER)){
            Column column2 = new Column(_input.next(Tokenizer.IDENTIFIER), tables);
            condition = new Condition(column1, relation, column2);    
        }
        else{
            String value = _input.next(Tokenizer.LITERAL);
            condition = new Condition(column1, relation, value);
        }
        return condition;
    }
    */

    /** Advance the input past the next semicolon. */
    void skipCommand() {
        while (true) {
            try {
                while (!_input.nextIf(";") && !_input.nextIf("*EOF*")) {
                    _input.next();
                }
                return;
            } catch (DBException excp) {
                /* No action */
            }
        }
    }

    /** The command input source. */
    private Tokenizer _input;
    /** Database containing all tables. */
    private Database _database;
}
