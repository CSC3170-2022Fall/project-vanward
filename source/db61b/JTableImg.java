package db61b;

import java.util.Vector;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Color;
import java.awt.Component;
import java.util.Enumeration;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
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
					String path_name = "source/db61b/WCG_picture/" 
						+ row_data[i] + ".png";
 					Icon icon = new ImageIcon(path_name);
					tmp.add(icon);
				}
				tmp.add(row_data[i]);
			}
			rows.add(tmp);
        }
		
		
		setSize(1000, 700);
		// setLocation(200, 200);
		contentPane = new JPanel();
		// contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JPanel panel = new JPanel();
		// panel.setBounds(10, 10, 1000, 1000);
		contentPane.add(panel);
    
		jtable = new JTable();
		jtable.setModel(new DefaultTableModel(rows, column_names){
			@Override   
			public Class<?> getColumnClass(int columnIndex) {
				return getValueAt(0, columnIndex).getClass();
			}
		});

		jtable.setShowGrid(false);
		for(int i = 0; i < column_names.size(); ++i) setTableHeaderColor(jtable, i, new Color(24, 236, 255));
		jtable.setBackground(new Color(250, 250, 253));
		jtable.setRowHeight(35);
		jtable.setPreferredScrollableViewportSize(new Dimension(800,600));
		DefaultTableCellRenderer tcr = new DefaultTableCellRenderer();
		tcr.setHorizontalAlignment(JLabel.CENTER);
		jtable.setDefaultRenderer(Object.class, tcr);
		jtable.setFont(new Font("Calibri", Font.BOLD, 15));
		fitTableColumns(jtable);

		JScrollPane jsp = new JScrollPane(jtable);
		panel.add(jsp);
	}

    public void display(String table_name) {
		this.setTitle(table_name);
		this.setVisible(true);
	}


	@SuppressWarnings("rawtypes")
    private static void fitTableColumns(JTable myTable)
    {
         myTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
         JTableHeader header = myTable.getTableHeader();
         int rowCount = myTable.getRowCount();
         Enumeration columns = myTable.getColumnModel().getColumns();
         while(columns.hasMoreElements())
         {
             TableColumn column = (TableColumn)columns.nextElement();
             int col = header.getColumnModel().getColumnIndex(column.getIdentifier());
             int width = (int)header.getDefaultRenderer().getTableCellRendererComponent
             (myTable, column.getIdentifier(), false, false, -1, col).getPreferredSize().getWidth();
             for(int row = 0; row < rowCount; row++)
             {
                 int preferedWidth = (int)myTable.getCellRenderer(row, col).getTableCellRendererComponent
                 (myTable, myTable.getValueAt(row, col), false, false, row, col).getPreferredSize().getWidth();
                 width = Math.max(width, preferedWidth);
             }
             header.setResizingColumn(column);
             column.setWidth(width+myTable.getIntercellSpacing().width);
        }
	}

	public static void setTableHeaderColor(JTable table, int columnIndex, Color c) {
        TableColumn column = table.getTableHeader().getColumnModel()
                .getColumn(columnIndex);
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            /** serialVersionUID */
            private static final long serialVersionUID = 43279841267L;

			@Override
            public Component getTableCellRendererComponent(JTable table, 
                    Object value, boolean isSelected,boolean hasFocus,
                    int row, int column) {

                setHorizontalAlignment(JLabel.CENTER);
                ((DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer())
                        .setHorizontalAlignment(DefaultTableCellRenderer.CENTER);

                return super.getTableCellRendererComponent(table, value, 
                        isSelected, hasFocus, row, column);
            }
        };
        cellRenderer.setBackground(c);
        column.setHeaderRenderer(cellRenderer);
    }
}
