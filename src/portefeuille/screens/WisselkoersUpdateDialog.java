package portefeuille.screens;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;
import javax.swing.BorderFactory;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import portefeuille.tables.WisselkoersList;
import portefeuille.util.PortefeuilleTableCellRenderer;

public class WisselkoersUpdateDialog extends JDialog implements TableModelListener, ListSelectionListener
{

	/**
	 * Dialog for updating exchange rates
	 */
	private static final long serialVersionUID = 1L;
	
	Object[][] tableData;
	Object[] columnNames;
	WisselkoersList theWList;
	DefaultTableModel tableModel;
	JButton applyButton;
	
	DataSource ds;
	Connection con;
	
	public WisselkoersUpdateDialog(EffectenFrame theParent)
	{
		super(theParent,"Wisselkoers lijst",false);
		ds = theParent.getDs();
		con = theParent.getCon();
		theWList = new WisselkoersList(ds);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		JTable table = createWisselkoersTable();
		table.setFillsViewportHeight(true);
		table.setRowSelectionInterval(tableModel.getRowCount()-1,tableModel.getRowCount()-1);
		JScrollPane scrollPane = new JScrollPane(table,ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setAutoscrolls(true);
		scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 0,0));
		table.scrollRectToVisible(table.getCellRect(tableModel.getRowCount()-1,0, true)); 

		this.add(scrollPane,BorderLayout.CENTER);
		
		JPanel buttonPane  = new JPanel();
		applyButton = new JButton("Apply");
		applyButton.setEnabled(false);
		applyButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
//				System.out.println("Apply button action event command "+e.getActionCommand());
				String[] sqllist = generateSQLStatements();
				if(confirmUpdates(sqllist))
				{
					String dbUpdateResult = updateWisselkoersTable(sqllist);
					if(dbUpdateResult.compareTo("OK")==0)
					{
						String msg = String.format("%d row(s) updated!", sqllist.length);
						JOptionPane.showMessageDialog((Component)e.getSource(),msg,"DB Update", JOptionPane.INFORMATION_MESSAGE);
						setVisible(false);
						dispose(); 
					}
					else
					{
						JOptionPane.showMessageDialog((Component)e.getSource(),dbUpdateResult,"DB Update - Error", JOptionPane.ERROR_MESSAGE);
					}
					
				}
			}});
		buttonPane.add(applyButton,BorderLayout.WEST);

		JButton quitButton = new JButton("Quit");
		quitButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				setVisible(false);
				dispose();				
			}});
		buttonPane.add(quitButton,BorderLayout.EAST);
		
		this.add(buttonPane, BorderLayout.SOUTH);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
//		setSize(550, 320);
		setMinimumSize(new Dimension(250,200));
		setPreferredSize(new Dimension(250,200));
		setLocationRelativeTo(null);
		
		setVisible(true);
	}

	private JTable createWisselkoersTable()
	{
		tableData = theWList.getWisselkoersTableData();
		columnNames = theWList.get(0).getFieldNames().toArray();
		tableModel = new DefaultTableModel();
		tableModel.setDataVector(tableData, columnNames);
		tableModel.addRow(new Object[] {});
		tableModel.addTableModelListener(this);
		JTable table = new JTable(tableModel);
		
//		ColumnsAutoSizer as = new ColumnsAutoSizer();
//		as.sizeColumnsToFit(table);
		table.setSelectionBackground(Color.LIGHT_GRAY);
		table.setDefaultRenderer(Object.class,new PortefeuilleTableCellRenderer());
		ListSelectionModel listSelectionModel = table.getSelectionModel();
	  listSelectionModel.addListSelectionListener(this);
	  table.setSelectionModel(listSelectionModel);
	
		return table;	
	}
	
	String[] generateSQLStatements()
	{
		int countSQLStatements = 0;
		int newRowCount = tableModel.getRowCount();
		int oldRowCount = theWList.size();
		while(newRowCount>0 && tableModel.getValueAt(newRowCount-1,0)==null) newRowCount--;
		String[] theResult = new String[newRowCount];
		// UPDATE `Wisselkoers` SET `koers` = '66.8201' WHERE (`van` = 'USD' and `naar` = 'EURO');
		for(int i=0; i <oldRowCount; i++)
		{
			StringBuilder sb = new StringBuilder("UPDATE `Wisselkoers` SET ");
			boolean bFound = false;
			for(int j=0; j<columnNames.length; j++)
			{
				String newValue = tableModel.getValueAt(i, j).toString();
				String oldValue = tableData[i][j].toString();
				if(newValue.compareTo(oldValue)==0) continue;
				if(bFound) sb.append(", ");
				sb.append("`"+columnNames[j]+"` = '"+newValue+"'");
				bFound = true;
			}
			if(!bFound) continue;
			sb.append(" WHERE (`van` = '"+tableData[i][0]+"' and `naar` = '"+tableData[i][1]+"');");
			theResult[i]=sb.toString();
			countSQLStatements++;
		}
		for(int i=oldRowCount; i<newRowCount; i++)
		{
			try
			{
				// INSERT INTO `Wisselkoers` (`van`,`naar`,`koers`)
				// VALUES (
				StringBuilder sb = new StringBuilder("INSERT INTO `Wisselkoers`");
				sb.append('(');
				sb.append("`van`,`naar`,`koers`");
				sb.append(')');
				sb.append(" VALUES ");
				sb.append('(');
				sb.append("\""+tableModel.getValueAt(i, 0).toString()+"\", ");
				sb.append("\""+tableModel.getValueAt(i, 1).toString()+"\", ");
				sb.append("\""+tableModel.getValueAt(i, 2).toString()+"\" ");
				sb.append(");");
				theResult[i]=sb.toString();
				countSQLStatements++;
			}
			catch(Exception e)
			{
				JOptionPane.showMessageDialog(this,"Iets niet ingevuld?","Gegevens fout", JOptionPane.ERROR_MESSAGE);
			}
		}
		String[] finalResult = new String[countSQLStatements];
		int index = 0;
		for(String s : theResult)
		{
			if(s==null) continue;
			finalResult[index]=s;
			index++;
		}
		return finalResult;
	}
	
	Boolean confirmUpdates(String[] sqls)
	{
		boolean theResult = false;
		JTextArea theText = new JTextArea();
		theText.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		Font f = theText.getFont();
//		System.out.println("font size is: "+f.getSize()+", font name is: "+f.getName());
		theText.setFont(new Font(f.getName(),Font.PLAIN, 9));
		for(String s : sqls)
		{
			if(s!=null && s.isEmpty()==false) theText.append(s+"\n");
		}
		if(!theText.getText().isEmpty())
		{
			Object[] options = {"Yes","No"};
			int antwoord = JOptionPane.showOptionDialog(this, theText, "Confirm DB Update", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]); 
			if(antwoord==0) theResult=true;
		}
		return theResult;
	}
	
	String updateWisselkoersTable(String[] sqls)
	{
		String theResult="OK";
		try
		{
			if(con==null) con = ds.getConnection();
			for(String s : sqls)
			{
				Statement stmt = con.createStatement();
				stmt.execute(s);
				stmt.close();
				if(!con.getAutoCommit()) con.commit();
			}
		}
		catch (SQLException e)
		{
			theResult = e.getMessage();
			try
			{
				if(!con.getAutoCommit()) con.rollback();
//				con.close();
			}
			catch (SQLException e1)
			{
				// e1.printStackTrace();
			}
//			con = null;
		//	e.printStackTrace();
		}
		return theResult;
	}
	

	@Override
	public void valueChanged(ListSelectionEvent e)
	{
		if(e.getValueIsAdjusting()) return;
		DefaultListSelectionModel sm = (DefaultListSelectionModel)e.getSource();
		if(sm.isSelectionEmpty())
		{
			return;
		}
	}

	@Override
	public void tableChanged(TableModelEvent e)
	{
		if(e.getType()==TableModelEvent.INSERT) return;
		int row = e.getFirstRow();
//    int column = e.getColumn();
    DefaultTableModel model = (DefaultTableModel)e.getSource();
//    String columnName = model.getColumnName(column);
//    Object data = model.getValueAt(row, column);
//    System.out.println("At row = "+row+", col "+column);
/*    System.out.println("New value = "+data);
		if(row<theTList.size())
		{
			System.out.println("Old value = "+tableData[row][column]);
		} */
    if(row+1==model.getRowCount())
    {
    	model.addRow(new Object[] {});
    	model.fireTableRowsInserted(row+1, row+1);
    }
    if(!applyButton.isEnabled()) applyButton.setEnabled(true);
	}

}