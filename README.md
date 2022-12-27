[![Open in Visual Studio Code](https://classroom.github.com/assets/open-in-vscode-c66648af7eb3fe8bc4f294546bfd86ef473780cde1dea487d3c4ff354943c9ae.svg)](https://classroom.github.com/online_ide?assignment_repo_id=9424643&assignment_repo_type=AssignmentRepo)
# CSC3170 Course Project

## Project Overall Description

This is our implementation for the course project of CSC3170, 2022 Fall, CUHK(SZ). For details of the project, you can refer to [project-description.md](project-description.md). In this project, we will utilize what we learned in the lectures and tutorials in the course, and implement either one of the following major jobs:

- [ ] **Application with Database System(s)**
- [x] **Implementation of a Database System**

## Team Members

Our team consists of the following members, listed in the table below (the team leader is shown in the first row, and is marked with 🚩 behind his/her name):

| Student ID | Student Name | GitHub Account (in Email)  | GitHub Username |
| ---------- | ------------ | -------------------------  | --------------- |
| 120090194  | 桂驰 🚩      | 120090194@link.cuhk.edu.cn |  @[Penguin-N](https://github.com/Penguin-N)      |
| 120090521  | 庞嘉扬        | 120090521@link.cuhk.edu.cn | @[PJYasuna](https://github.com/PJYasuna)       |
| 120090117  | 赵思远        | 120090117@link.cuhk.edu.cn |  @[ZhaoSiyuan120090117](https://github.com/ZhaoSiyuan120090117)|
| 120090671  | 从御政        | 120090671@link.cuhk.edu.cn |  @[Paligi](https://github.com/Paligi)|
| 120090026  | 孙思鹏        | 120090026@link.cuhk.edu.cn |  @[Sun8731](https://github.com/Sun8731)           |
| 121040084  | 钟嘉乐        | 121040084@link.cuhk.edu.cn | @[MikeZhong21](https://github.com/MikeZhong21)    |

## Project Specification


After thorough discussion, our team made the choice and the specification information is listed below:

- Our option choice is: **Option 3**
- For the progess summary, please refer to [Project-Specification.md](Project-Specification.md)

## How to run
1. Build a new directory.
2. Put source/direcotry, source/makefile, source/testing/files together. For example, Put db61b,makefile,Shooter.db,WroldCupGroups.db these four files together to test the data from the worldcup.
3.  Run "make" to compile.
4. Run "java db61b.Main" to run the DBMS.


## Project Abstract
  This project aims to implement a relational database management system. The DBMS consists of tables with row(s) and column(s), and query language to retrieve relevant information from the database. The project reflects our understanding on the low level working meachanism of database management system. Our implementation includes creation, deletion, updating of tables (more specifically, insertion of rows and etc.), and selection of desired information by stating conditions. Other than that, the DBMS will evaluate the sql commands and return relevant messages if errors occur for the purpose of reminding. For the ease of implementation, the "join" statement can be view as natural join by default. The implementation will also include retrieval of information from multiple tables. To test whether the implemented DBMS works properly and correctly or not, test cases will be provided for the enhancement of our DBMS and better user understanding. Additionnally, this project will extend some additional methods in order to re-implement Assignment2 such as in, order, etc.  

## Project Progress
 We have finished the basic implementation about database management system. i.e, we have passed the tests provided by CS61B and meet its requirement. We intend to realize some new funtions in the following days, like order by, delete, etc. Hopefully we will finish the program before 12/10. We would like to give the presentation in Tuesday.

 ## The Video Link and Slides.

[video link](https://www.bilibili.com/video/BV1ov4y1z7fN/?share_source=copy_web&vd_source=5803090007523b0754e865e69770294a)  

[slides](presentation.pdf)

## Project Design
The project is divided into ten classes. Some important classes will be introduced. The main class serves as the entry point and top-level control for the project. It is responsible for managing the overall flow of the program and coordinating the actions of the other classes. The command interpreter class is responsible for handling user input and communicating with the database class. It receives commands from the user and passes them on to the database class for processing. The database class contains tables, which are represented by the table class. The table class is responsible for performing various database operations, such as inserting, updating, and deleting rows, as well as interacting with files to read and write data. The row and column classes represent individual elements within the table, such as a specific row or column in a spreadsheet. These classes are used to store and manipulate data within the table.

## Functionality Implementation

**Notice**: In the section "statement structure", we use Backus–Naur Form (BNF) for the ease of understanding our version of SQL language structure.

### Order by

We iterate through the table n times (n is the number of the rows) to find the maximum row in each iteration, then add the row to a new table.

### Visualization of Table
  
We use jtable library to visualize the table. The different countries will be shown with their corresponding national flags. To implement this, we name the flag picture files with their country names. Thus, we can relate each country to its flag picture.

### Aggregate Functions

First, use a string list to record the type of aggregate functions for each column. Then assume there is no aggregate function and do a similar process until after group by.
Then for each column, do the corresponding operations.

### Condition Clause

Statement Structure: <font color=green> where \<column name> \<relation> \<column name> | constant </font>

In the class “Condition”, the program will get three variables: column1, relation and column2 or constant. Relation is one of the symbols: "<, >, =, <=, >=, !=". And then the program will filter the data matching the conditions.

### Remove_row Clause

Statement Structure: <font color=green> remove from \<tables> \<condition clause>; </font>

 “removeRowStatement”: the function is similar to the “select” except that it will delete rows satisfying the conditions of the specific table.

### Group by Clause

Statement structure: <font color=green> select \<column name> from \<tables> group by \<column name>; </font>

First, the program checks if the next token in the input is the string "group" and if the following token is the string "by". It then calls a function called name() to get the name of the column to group the rows by.

Then the program checks that the column obtained from name() is a "normal" aggregate type and that all other return columns are not "normal" aggregate types. If either of these conditions is not met, an error is thrown with the message "Group by is error format!".

After that, the program creates a new Column object with the name of the group column and the select table as arguments. It then creates a new LinkedHashMap called groups to store the different groups.

Finally, the code iterates over each row in the select table and for each row, it gets the value in the group column. It then checks if there is already a group with that value in the groups map. If there is not, it creates a new Table object with the column titles and adds the row to it. If there is already a group with that value, it adds the row to the existing group's table.

### Having Clause

Statement structure: <font color=green> select \<column name><sup>+</sup> from \<tables> group by \<column name>+ having 
\<condition clause>; </font>

The program creates a new table called having_table using the Table class and initializes it with the column titles from a table called group_table. It then creates an ArrayList of Condition objects called having_conditions and another ArrayList of String objects called column_titles_.

The program iterates through the column titles in group_table and adds them to column_titles_. After that, it calls a method called conditionClause and passes in group_table as an argument, and assigns the return value to having_conditions.

Finally, it calls the select method on group_table and passes in column_titles_ and having_conditions as arguments, and assigns the return value to having_table. It then assigns having_table to group_table.


### Select Clause

Statement Structure:  <font color=green>select \<column name><sup>+</sup>, from \<tables> \<condition clause>; </font> 

First, the program creates three array lists to store selected column titles (two array lists to obtain distinct column title and prevent duplicated column title) and the name of aggregate functions if there is any.
Then, by using the functions provided by the tokenizer class to check if there is any aggregate functions (“avg”, “sum”, “min”, “max”, “count”),  if there is aggregate function(s) in the statement, do the corresponding operation(s). At the same time, the program will store the selected column name(s) into the array list. Then the program will obtain targeted table(s) after the “from” statement.  
If the statement contains “where”, “group by”, “having”, “order by”, the program will do the corresponding operation(s) until it ends with “;”.

### Insert Clause

Statement Structure: <font color=green>insert into \<table name> values \<literal><sup>+</sup>, ;</font>

First, the program will add the literals into the value array list. Then, create a new row to hold these values(literals) (More specifically, transform the value array list to the type Row). Finally, add the new row into the corresponding table.

### Create Clause
Statement Structure: <font color=green> create table \<name> \<table definition>; </font> 

Notice: <font color=green> \<table definition> ::= ( \<column name><sup>+</sup>, ) | as \<select clause> </font>

In the function “tabledefinition”, the program will first use array list to store the column titles (column names). Then, create and initialize a new table object containing the column titles. If the statement contains “as”, the program will execute the function “selectclause” and select the corresponding information from the table to form a new table. Finally, put the new table and its name to the database.

### Column_minus Clause

Statement Structure: <font color=green> column_minus \<table name>: \<column name1> and \<column name2> to \<column name3>; </font>

Notice: <font color=green> \<column name3> ::= \<column name1> - \<column name2> </font>

First, the program will check if the first token of the string is "column_minus". Then, the program will get the table name (suppose the size of the table is m*n), two column names in the table, and the result column name after the minus operation. After that, the program will call the function "columnMinusClause" to do the minus operation. If the values of the two columns are not digits, the program will raise the error reminder. Then, the program will iterate each row in the table so that it can combine the original values of the row with the minus result value to form a new row. All the new rows together form a new table. Finally, the new table will be printed.

## Difficulty Encountered & Solutions

### Additional function

#### Write code over the given huge skeleton (CS61B)

1. Read the whole introduction carefully
2. Perfect each small program
3. Overall debugging modification

#### How to implement functionality "group by"

Suppose group by x, where x is the column name.

1. First, deal with a simple situation: only aggregate functions without group by
2. Then, according to column x, divide the table into several sub-tables. Then deal with each sub-table (To deal with each sub-table, group by can be ignored since the values of column x are the same. Therefore, using the step 1 approach to dealing with it)
3. Finally, integrate the results of each sub-table into a new table

#### How to implement functionality "aggregate functions"

Use one string list to record the aggregate function type of each column. Then for each column, do the corresponding operation.

### Cooperation among teammates

Learn the knowledge about GitHub, and divide the task into small tasks. Then each teammate does one of the small tasks. finally, integrate what we have done.






  
