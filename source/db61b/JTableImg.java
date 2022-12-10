package db61b;

import java.util.Vector;
import java.awt.BorderLayout;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;


public class JTableImg extends JFrame {
	private JPanel contentPane = null;
	private JTable jtable;

	public JTableImg(Table table) {
		Vector<Object> column_names = new Vector<>();
		String[] columns = table.get_column_titles();
		int country_index = -1;
		for(int i = 0; i < columns.length; ++i){
			if(columns[i].equals("Country")) {
				country_index = i;
				column_names.add("flag");
			}
			column_names.add(columns[i]);
		}

		Vector<Vector<Object>> rows = new Vector<>();
		for(Row row : table){
            String[] row_data = row.get_data();
            Vector<Object> tmp = new Vector<>();
			for(int i = 0; i < row_data.length; ++i){	
				if(i == country_index){
					String path_name = "C:/Users/联想/github-classroom/CSC3170-2022Fall/project-vanward/source/WCG_picture/" 
						+ row_data[i] + ".png";
 					Icon icon = new ImageIcon(path_name);
					tmp.add(icon);
				}
				tmp.add(row_data[i]);
			}
			rows.add(tmp);
        }
		setSize(600, 600);
		setLocation(200, 200);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JPanel panel = new JPanel();
		panel.setBounds(10, 10, 1000, 1000);
		contentPane.add(panel);
    
		jtable = new JTable();
		jtable.setModel(new DefaultTableModel(rows, column_names){
			@Override   
			public Class<?> getColumnClass(int columnIndex) {
				return getValueAt(0, columnIndex).getClass();
			}
		});
		JScrollPane jsp = new JScrollPane(jtable);
		jsp.setSize(800, 800);
		panel.add(jsp);
	}

    public void display(String table_name) {
		this.setTitle(table_name);
		this.setVisible(true);
	}
}
