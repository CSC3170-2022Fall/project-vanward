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
        default:
            throw error("unrecognizable command");
        }
        return true;
    }

    /** Parse and execute a create statement from the token stream. */
    void createStatement() {
        _input.next("create");
        _input.next("table");
        String name = name();
        Table table = tableDefinition();
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

    private static String[] insert(String[] arr, String... str) {
        int size = arr.length; // 获取原数组长度
        int newSize = size + str.length; // 原数组长度加上追加的数据的总长度
        
        // 新建临时字符串数组
        String[] tmp = new String[newSize]; 
        // 先遍历将原来的字符串数组数据添加到临时字符串数组
        for (int i = 0; i < size; i++) { 
            tmp[i] = arr[i];
        }
        // 在末尾添加上需要追加的数据
        for (int i = size; i < newSize; i++) {
            tmp[i] = str[i - size];
        } 
        return tmp; // 返回拼接完成的字符串数组
    }


    Integer table_aggreate(Table result, ArrayList<String> column_titles, String target_column, String agg_type){
        Integer agg_result = 0;
        if(agg_type.equals("avg")){
            Integer sum = 0;
            Column col = new Column(target_column, result);
            for(Row row: result){
                sum = sum + Integer.parseInt(col.getFrom(row));
            }
            agg_result = sum/result.size();
        }else if(agg_type.equals("sum")){
            Integer sum = 0;
            Column col = new Column(target_column, result);
            for(Row row: result){
                sum = sum + Integer.parseInt(col.getFrom(row));
            }
            agg_result = sum;            
        }else if(agg_type.equals("max")){
            Integer max = null;
            Column col = new Column(target_column, result);
            for(Row row: result){
                Integer current = Integer.parseInt(col.getFrom(row));
                if(max==null){
                    max = current;
                }else if(max<current){
                    max = current;
                }
            agg_result = max;
            }            
        }else if(agg_type.equals("min")){
            Integer min = null;
            Column col = new Column(target_column, result);
            for(Row row: result){
                Integer current = Integer.parseInt(col.getFrom(row));
                if(min==null){
                    min = current;
                }else if(min>current){
                    min = current;
                }
            }
            agg_result = min;             
        }else if(agg_type.equals("count")){
            Integer count = 0;
            HashMap<String,Integer> agg_record = new HashMap<>();
            Column col = new Column(target_column, result);
            for(Row row: result){
                String value = col.getFrom(row);
                if(agg_record.get(value)==null){
                    agg_record.put(value, 1);
                    count+=1;
                }
            }            
            agg_result = count;
        }       
        return agg_result;
    }

    /** Parse and execute a select clause from the token stream, returning the
     *  resulting table. */
    Table selectClause() {  //select <column name>, from <tables> <condition clause>
        _input.next("select");
        // obtain selected column titles
        ArrayList<String> column_titles = new ArrayList<>();
        String type = "normal";
        String target_column = null;
        do {
            // Average type = 1
            if(_input.nextIf("avg")){
                _input.next("(");
                String name = columnName();
                target_column = name;
                column_titles.add(name);
                _input.next(")");
                type = "avg";
            }else if(_input.nextIf("sum")){
                _input.next("(");
                String name = columnName();
                target_column = name;
                column_titles.add(name);
                _input.next(")");
                type = "sum";
            }
            else if(_input.nextIf("min")){
                _input.next("(");
                String name = columnName();
                target_column = name;
                column_titles.add(name);
                _input.next(")");
                type = "min";
            }
            else if(_input.nextIf("max")){
                _input.next("(");
                String name = columnName();
                target_column = name;
                column_titles.add(name);
                _input.next(")");
                type = "max";
            }
            else if(_input.nextIf("count")){
                _input.next("(");
                String name = columnName();
                target_column = name;
                column_titles.add(name);
                _input.next(")");
                type = "count";
            }
            else{
                column_titles.add(columnName());
            }
        } while (_input.nextIf(","));
        // obtain target tables (one or two)
        _input.next("from");
        Table table1 = tableName();
        Table table2 = null;
        Table result = null;
        if (_input.nextIf(",")) {
            table2 = tableName();
        }
        // obtain conditions
        ArrayList<Condition> conditions;
        if (table2!=null) {
            conditions = conditionClause(table1, table2);
            result = table1.select(table2, column_titles, conditions);
        }
        else {
            conditions = conditionClause(table1);
            result = table1.select(column_titles, conditions);
        }
        // Order by
        if (_input.nextIf("order")) {
            _input.next("by");
            Table order_table = new Table(column_titles);
            String columnName = name();
            if(_input.nextIf("desc")){
                Column col = new Column(columnName, result);
                while(result.size()!=0){
                    String max = null;
                    Row max_row = null;
                    for(Row row: result){
                        if(max==null){
                            max = col.getFrom(row);
                            max_row = row;
                            continue;
                        }
                        if(max.compareTo(col.getFrom(row))<0){
                            max = col.getFrom(row);
                            max_row = row;
                        }
                    }
                    result.remove(max_row);
                    order_table.add(max_row);
                }
            }else if(_input.nextIf("asc")){
                Column col = new Column(columnName, result);
                while(result.size()!=0){
                    String min = null;
                    Row min_row = null;
                    for(Row row: result){
                        if(min==null){
                            min = col.getFrom(row);
                            min_row = row;
                            continue;
                        }
                        if(min.compareTo(col.getFrom(row))>0){
                            min = col.getFrom(row);
                            min_row = row;
                        }
                    }
                    result.remove(min_row);
                    order_table.add(min_row);
            }
            }else{
                Column col = new Column(columnName, result);
                while(result.size()!=0){
                    String min = null;
                    Row min_row = null;
                    for(Row row: result){
                        if(min==null){
                            min = col.getFrom(row);
                            min_row = row;
                            continue;
                        }
                        if(min.compareTo(col.getFrom(row))>0){
                            min = col.getFrom(row);
                            min_row = row;
                        }
                    }
                    result.remove(min_row);
                    order_table.add(min_row);
            }
            }
            result = order_table;      
        }
        // Group by
        if(_input.nextIf("group")){
            _input.next("by");
            String group_title = name();
            Column group_Column = new Column(group_title,result);
            HashMap <String, Table> groups = new HashMap<>();
            for(Row row:result){
                String value = group_Column.getFrom(row);
                if(groups.get(value)==null){
                    Table current = new Table(column_titles);
                    current.add(row);
                    groups.put(value, current);
                }else{
                    groups.get(value).add(row);
                }
            }
            // Agg functions
            if(type.equals("normal")){
                ;
            }else{
                Table agg_table = new Table(column_titles);
                String[] title_ = {group_title,type + "-" + target_column};
                Row title = new Row(title_);
                agg_table.add(title);
                for(String key:groups.keySet()){
                    Table current_table = groups.get(key); 
                    Integer agg_result = table_aggreate(current_table, column_titles, target_column, type);
                    String[] agg_ = {key,String.valueOf(agg_result)};                    
                    Row agg_row = new Row(agg_);
                    agg_table.add(agg_row);                   
                }
                result = agg_table;
                type = "normal";
            }
        }
        // Aggregate Functions Judge the type
        if(type.equals("normal")){
            ;
        }else{
            Integer agg_result = table_aggreate(result, column_titles, target_column, type);
            Table agg_table = new Table(column_titles);
            String[] agg_ = {String.valueOf(agg_result)};
            String[] title_ = {type + "-" + target_column};
            Row agg_row = new Row(agg_);
            Row title = new Row(title_);
            agg_table.add(title);
            agg_table.add(agg_row);
            result = agg_table;
        }
        return result;
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
