package portefeuille.screens;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;

import javax.sql.DataSource;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListSelectionModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
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
import javax.swing.table.TableColumn;

import portefeuille.tables.DividendList;
import portefeuille.tables.EffectList;
import portefeuille.util.ColumnsAutoSizer;
import portefeuille.util.PortefeuilleTableCellRenderer;

public class DividendenUpdateDialog extends JDialog implements TableModelListener, ListSelectionListener
{
	private static final long serialVersionUID = 1L;
	
	Object[][] tableData;
	Object[] columnNames;
	EffectList theEList;
	DividendList theDList;
	DefaultTableModel tableModel;
	JButton applyButton;
	EffectenFrame theEFrame;
	
	DataSource ds;
	Connection con;

	public DividendenUpdateDialog(EffectenFrame theParent)
	{
		super(theParent, "Dividend inkomsten", false);
		theEFrame = theParent;
		theEList = theParent.getEList();
		ds = theParent.getDs();
		con = theParent.getCon();
		theDList = new DividendList(ds);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		JTable table = createDividendenTable();
		table.setFillsViewportHeight(true);
		table.setRowSelectionInterval(tableModel.getRowCount()-1,tableModel.getRowCount()-1);
//		table.setBorder(BorderFactory.createEmptyBorder(0, 10, 0,10));
		JScrollPane scrollPane = new JScrollPane(table,ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setAutoscrolls(true);
//		scrollPane.setMinimumSize(new Dimension(380, 320));
//		scrollPane.setPreferredSize(new Dimension(380, 320));
//		scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
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
					String dbUpdateResult = updateDividendTable(sqllist);
					if(dbUpdateResult.compareTo("OK")==0)
					{
						String msg = String.format("%d row(s) updated!", sqllist.length);
						JOptionPane.showMessageDialog((Component)e.getSource(),msg,"DB Update", JOptionPane.INFORMATION_MESSAGE);
						theEFrame.CreateJFrameContents();
						theEList = theEFrame.getEList();
						theEFrame.validate();
						setVisible(false);
						dispose(); 
					}
					else
					{
						JOptionPane.showMessageDialog((Component)e.getSource(),dbUpdateResult,"DB Update - fout", JOptionPane.ERROR_MESSAGE);
						applyButton.setEnabled(false);
						
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
		setMinimumSize(new Dimension(470,580));
		setPreferredSize(new Dimension(470,580));
		setLocationRelativeTo(null);
		
		setVisible(true);
	}

	JTable createDividendenTable()
	{
		tableData = theDList.getDividendTableData();
		columnNames = theDList.get(0).getFieldNames().toArray();
		tableModel = new DefaultTableModel();
		tableModel.setDataVector(tableData, columnNames);
		tableModel.addRow(new Object[] {});
		tableModel.addTableModelListener(this);
		JTable table = new JTable(tableModel);
		
		JComboBox<String> tickerIdCombo = theEList.getTickerIdComboBox();
		
		Enumeration<TableColumn> cNames = table.getColumnModel().getColumns();
		while(cNames.hasMoreElements())
		{
			TableColumn c= cNames.nextElement();
			if(c.getHeaderValue().toString().compareToIgnoreCase("TickerId")==0)
			{
//		    System.out.println("Column name = "+c.getHeaderValue());
				c.setCellEditor(new DefaultCellEditor(tickerIdCombo));
			}
		}
		ColumnsAutoSizer as = new ColumnsAutoSizer();
		as.sizeColumnsToFit(table);
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
		int oldRowCount = theDList.size();
		while(newRowCount>0 && tableModel.getValueAt(newRowCount-1,0)==null) newRowCount--;
//		System.out.println("Updated newRowCount = "+newRowCount);
		String[] theResult = new String[newRowCount];
		// UPDATE `Dividend` SET `Koers` = '66.8201', `Div` = '1.8001' WHERE (`TickerId` = 'ABI');
		for(int i=0; i <oldRowCount; i++)
		{
			StringBuilder sb = new StringBuilder("UPDATE `Dividend` SET ");
			boolean bFound = false;
			for(int j=0; j<columnNames.length; j++)
			{
				String newValue = tableModel.getValueAt(i, j).toString();
				String oldValue = tableData[i][j].toString();
				if(newValue.compareTo(oldValue)==0) continue;
//				System.out.println("Detected change at ["+i+"]["+j+"]. New value "+newValue+", old value "+oldValue);
//				System.out.println("Column name = "+columnNames[j]);
				if(bFound) sb.append(", ");
				sb.append("`"+columnNames[j]+"` = '"+newValue+"'");
				bFound = true;
			}
			if(!bFound) continue;
			sb.append(" WHERE (`TickerId` = '"+tableData[i][0]+"' and `Datum` = '"+tableData[i][1]+"');");
//			System.out.println("Constructed SQL: "+sb.toString());
			theResult[i]=sb.toString();
			countSQLStatements++;
		}
		for(int i=oldRowCount; i<newRowCount; i++)
		{
			try
			{
				// INSERT INTO `Dividend` (`TickerId`,`Datum`,`Dividend`,`Aantal`,`Bruto`,`Voorheffing`,`Netto`)
				// VALUES (
				StringBuilder sb = new StringBuilder("INSERT INTO `Dividend`");
				sb.append('(');
				sb.append("`TickerId`,`Datum`,`Dividend`,`Aantal`,`Bruto`,`Voorheffing`,`Netto`");
				sb.append(')');
				sb.append(" VALUES ");
				sb.append('(');
				sb.append("\""+tableModel.getValueAt(i, 0).toString()+"\", ");
				sb.append("\""+tableModel.getValueAt(i, 1).toString()+"\", ");
				sb.append("\""+tableModel.getValueAt(i, 2).toString()+"\", ");
				sb.append("\""+tableModel.getValueAt(i, 3).toString()+"\", ");
				sb.append("\""+tableModel.getValueAt(i, 4).toString()+"\", ");
				sb.append("\""+tableModel.getValueAt(i, 5).toString()+"\", ");
				sb.append("\""+tableModel.getValueAt(i, 6).toString()+"\" ");
				sb.append(");");
				theResult[i]=sb.toString();
				countSQLStatements++;
			}
			catch(Exception e)
			{
				e.printStackTrace();
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
      final ImageIcon icon = new ImageIcon("etc//Nieuwe_Auto.icns");
      Image image2 = icon.getImage().getScaledInstance(200,200,0);
			Object[] options = {"Yes","No"};
			int antwoord = JOptionPane.showOptionDialog(this, theText, "Confirm DB Update", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, new ImageIcon(image2), options, options[1]); 
			if(antwoord==0) theResult=true;
		}
		return theResult;
	}
	
	String updateDividendTable(String[] sqls)
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
//			System.out.println("No row selected!");
			return;
		}
	}

	@Override
	public void tableChanged(TableModelEvent e)
	{
		if(e.getType()==TableModelEvent.INSERT) return;
		int row = e.getFirstRow();
    DefaultTableModel model = (DefaultTableModel)e.getSource();
    if(row+1==model.getRowCount())
    {
    	model.addRow(new Object[] {});
    	model.fireTableRowsInserted(row+1, row+1);
    }
    if(!applyButton.isEnabled()) applyButton.setEnabled(true);
	}

}
