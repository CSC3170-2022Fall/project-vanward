package db61b;

import java.util.*;
import javax.swing.*;

public class Jtable extends JFrame {
    public Jtable(Table table){
        String[] columns = table.get_column_titles();

        _rows = new Vector<>();
        _column_names = new Vector<>();
        for(String name : columns)  _column_names.add(name);

        for(Row row : table){
            String[] row_data = row.get_data();

            Vector<Object> tmp = new Vector<>();
            for(String value : row_data)    tmp.add(value);
            _rows.add(tmp);
        }
        _jTable = new JTable(_rows, _column_names);
        _jScrollPane = new JScrollPane(_jTable);
        this.add(_jScrollPane);
        
        this.setLocation(200, 200);
        this.setSize(500,500);
        // this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    public void display(String table_name){
        this.setTitle(table_name);
        this.setVisible(true);
    }
    
    private JTable _jTable = null;
    private JScrollPane _jScrollPane = null;
    private Vector<Object> _column_names = null;
    private Vector<Vector<Object>> _rows = null;
}
