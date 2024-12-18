package portefeuille.screens;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.sql.DataSource;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import portefeuille.tables.EffectList;
import portefeuille.tables.TransactieList;
import portefeuille.tables.TriggerCodeList;
import portefeuille.tables.VerkoopTriggerList;
import portefeuille.util.ColumnsAutoSizer;

public class VerkoopTriggerDialog extends JDialog implements TableModelListener, ListSelectionListener
{
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(EffectenFrame.class.getName());
	
	Object[][] tableData;
	Object[] columnNames;
	EffectList theEList;
	TransactieList theTList;
	VerkoopTriggerList theVKTList;
	TriggerCodeList theTCList;
	DefaultTableModel tableModel;
	TableRowSorter<DefaultTableModel> sorter;
	JTable theTable;
	int selectedRow = -1;

	JComboBox<String> selectStatusButton;
	JComboBox<String> selectTickerButton;
	JButton applyButton;
	JButton removeButton;
	EffectenFrame theEFrame;
	String[] statusCodes = { "Nieuw","Geplaatst","Wijzigen","Geannuleerd","Uitgevoerd" };
	
	DataSource ds;
	Connection con;

	public VerkoopTriggerDialog(EffectenFrame theParent)
	{
		super(theParent,"Verkoop triggers",false);
		logger.traceEntry("VerkoopTriggerDialog");
		theEFrame = theParent;
		theEList = theParent.getEList();
		theTList = theParent.getTList();
		ds = theParent.getDs();
		con = theParent.getCon();
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		theTable = createVerkoopTriggerTable();
		
//		table.setAutoCreateRowSorter(true);
		JScrollPane scrollPane = new JScrollPane(theTable,ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		theTable.setFillsViewportHeight(true);
		scrollPane.setAutoscrolls(true);
		scrollPane.setMinimumSize(new Dimension(400, 250));
		scrollPane.setPreferredSize(new Dimension(500, 250));
//		scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 0,0));
		this.add(scrollPane,BorderLayout.CENTER);
		JPanel buttonPane  = new JPanel();
		JLabel selectTicker = new JLabel("Ticker");
		buttonPane.add(selectTicker,BorderLayout.WEST);
		selectTickerButton = theEList.getTickerIdComboBox();
		selectTickerButton.insertItemAt("All", 0);
		selectTickerButton.setSelectedIndex(0);
		selectTickerButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				newFilter();
			}
		});
		buttonPane.add(selectTickerButton,BorderLayout.WEST);
		
		JLabel selectLabel = new JLabel("Status");
		buttonPane.add(selectLabel,BorderLayout.WEST);
		selectStatusButton = new JComboBox<String> (statusCodes);
		selectStatusButton.insertItemAt("All", 0);
		selectStatusButton.setSelectedIndex(0);
		selectStatusButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e)
			{
//				System.out.println("Select combobox action event command "+e.getActionCommand());
//				System.out.println("Select combobox getSource = "+e.getSource());
				newFilter();
			}});
		buttonPane.add(selectStatusButton,BorderLayout.WEST);
		
		applyButton = new JButton("Apply");
		applyButton.setEnabled(false);
		applyButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
//				System.out.println("Apply button action event command "+e.getActionCommand());
				String[] sqllist = generateSQLStatements();
				if(confirmUpdates(sqllist))
				{
					String dbUpdateResult = updateAankoopTriggerTable(sqllist);
					if(dbUpdateResult.compareTo("OK")==0)
					{
						String msg = String.format("%d row(s) updated!", sqllist.length);
						JOptionPane.showMessageDialog((Component)e.getSource(),msg,"DB Update", JOptionPane.INFORMATION_MESSAGE);
						theEFrame.CreateJFrameContents();
						theEList = theEFrame.getEList();
						theEFrame.validate();
						applyButton.setEnabled(false);
						removeButton.setEnabled(false);
						getTableData();
//						setVisible(false);
//						dispose(); 
					}
					else
					{
						JOptionPane.showMessageDialog((Component)e.getSource(),dbUpdateResult,"DB Update - Error", JOptionPane.ERROR_MESSAGE);
					}
					
				}
			}});
		buttonPane.add(applyButton,BorderLayout.WEST);
		
		removeButton = new JButton("Remove");
		removeButton.setEnabled(false);
		removeButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				tableModel.removeRow(selectedRow);
				String msg = String.format("Row %d removed.", selectedRow);
				JOptionPane.showMessageDialog((Component)e.getSource(),msg,"DB Update", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		buttonPane.add(removeButton,BorderLayout.CENTER);

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
		setMinimumSize(new Dimension(680,380));
		setPreferredSize(new Dimension(680,380));
		setLocationRelativeTo(null);
		
		setVisible(true);
		logger.traceExit("VerkoopTriggerDialog");
	}
	void newFilter()
	{
		List<RowFilter<DefaultTableModel,Object>> rfs = 
		    new ArrayList<RowFilter<DefaultTableModel,Object>>(2);
		
		RowFilter<DefaultTableModel,Object> rf1 = null;
		if(selectTickerButton.getSelectedItem().toString().compareTo("All")!=0)
		{
			rf1=RowFilter.regexFilter(selectTickerButton.getSelectedItem().toString(), theVKTList.colTickerId);
		}
		else
			rf1=RowFilter.notFilter(RowFilter.regexFilter(selectTickerButton.getSelectedItem().toString(), theVKTList.colTickerId));
		rfs.add(rf1);
		
		RowFilter<DefaultTableModel,Object> rf2 = null;
		if(selectStatusButton.getSelectedItem().toString().compareTo("All")!=0)
		{
			rf2=RowFilter.regexFilter(selectStatusButton.getSelectedItem().toString(), theVKTList.colStatus);
		}
		else
			rf2=RowFilter.notFilter(RowFilter.regexFilter(selectStatusButton.getSelectedItem().toString(), theVKTList.colStatus));
		rfs.add(rf2);
		sorter.setRowFilter(RowFilter.andFilter(rfs));
	}
	void getTableData()
	{
		theVKTList = new VerkoopTriggerList(ds);
		theTCList = new TriggerCodeList(ds);
		tableData = theVKTList.getTableData();
		columnNames = theVKTList.getColumnNames();
		
		for(int i=0;i<tableData.length;i++)
		{
    	tableData[i][theVKTList.colWaarde]=((BigDecimal)tableData[i][theVKTList.colWaarde]).setScale(2, RoundingMode.HALF_DOWN);
  		tableData[i][theVKTList.colGemAankWaarde] = ((BigDecimal)tableData[i][theVKTList.colGemAankWaarde]).setScale(2, RoundingMode.HALF_DOWN);
  		tableData[i][theVKTList.colDoelKoers] = ((BigDecimal)tableData[i][theVKTList.colDoelKoers]).setScale(2, RoundingMode.HALF_DOWN); 
   		tableData[i][theVKTList.colOmzet] = ((BigDecimal)tableData[i][theVKTList.colOmzet]).setScale(2, RoundingMode.HALF_DOWN); 
  		tableData[i][theVKTList.colWinst] = ((BigDecimal)tableData[i][theVKTList.colWinst]).setScale(2, RoundingMode.HALF_DOWN); 
   		String date = tableData[i][theVKTList.colDatum].toString();
   		if(date.length()>10)
   		{
   			tableData[i][theVKTList.colDatum]=date.substring(0,10);
   		}
		}
	}
	
	JTable createVerkoopTriggerTable()
	{
		getTableData();
		
	  final Class<?>[] columnClass = new Class<?>[] {Integer.class, String.class, String.class, BigDecimal.class, Integer.class, BigDecimal.class, BigDecimal.class, BigDecimal.class, BigDecimal.class, String.class, String.class};

		tableModel = new DefaultTableModel()
		{
			private static final long serialVersionUID = 1L;

			@Override 
	    public boolean isCellEditable(int row, int column)
	    {
				boolean canDo = false;
				if(row<tableData.length)
				{
					if(tableData[row][theVKTList.colStatus].toString().compareTo("Uitgevoerd")==0)
						return canDo;
				}
				if(	getValueAt(row,theVKTList.colStatus)==null || 
						getValueAt(row,theVKTList.colStatus).toString().compareTo("Nieuw")==0 ||
						getValueAt(row,theVKTList.colStatus).toString().compareTo("Wijzigen")==0
					)
				{
					canDo = true;
				}
	      if((column>0 && column<theVKTList.colGemAankWaarde && canDo) || column > theVKTList.colWinst)
	      {
	      	return true;
	      }
	      else
	      {
	      	return false;
	      }
	    }
			@Override
      public Class<?> getColumnClass(int columnIndex)
      {
          return columnClass[columnIndex];
      }
		};
		
		for(int i=0;i<tableData.length;i++)
		{
    	if(tableData[i][theVKTList.colStatus] == null) 
    	{ 
    		tableData[i][theVKTList.colStatus] = " "; 
    	}
		}
		tableModel.setDataVector(tableData, columnNames);
		tableModel.addRow(new Object[] {0, "", "", BigDecimal.ZERO, 0, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, "Nieuw", ""});
		
		tableModel.addTableModelListener(this);
		sorter = new TableRowSorter<DefaultTableModel>(tableModel);
		sorter.setSortsOnUpdates(true);
		JTable aTable = new JTable(tableModel);
		aTable.setRowSorter(sorter);
		
		JComboBox<String> tickerIdCombo = theEList.getTickerIdComboBox();
		JComboBox<String> codeIdCombo = theTCList.getCodeComboBox();
		
		JComboBox<String> statusCombo = new JComboBox<String>(statusCodes);
		
		Enumeration<TableColumn> cNames = aTable.getColumnModel().getColumns();
		while(cNames.hasMoreElements())
		{
			TableColumn c= cNames.nextElement();
			if(c.getHeaderValue().toString().compareToIgnoreCase("TickerId")==0)
			{
				c.setCellEditor(new DefaultCellEditor(tickerIdCombo));
			}
			else if(c.getHeaderValue().toString().compareToIgnoreCase("Code")==0)
			{
				c.setCellEditor(new DefaultCellEditor(codeIdCombo));
			}
			else if(c.getHeaderValue().toString().compareToIgnoreCase("Status")==0)
			{
				c.setCellEditor(new DefaultCellEditor(statusCombo));
			}
		}
		ColumnsAutoSizer as = new ColumnsAutoSizer();
		as.sizeColumnsToFit(aTable);
		aTable.setSelectionBackground(Color.LIGHT_GRAY);
//		table.setDefaultRenderer(Object.class,new PortefeuilleTableCellRenderer());

		ListSelectionModel listSelectionModel = aTable.getSelectionModel();
    listSelectionModel.addListSelectionListener(this);
    aTable.setSelectionModel(listSelectionModel);

		return aTable;		
	}
	
	String[] generateSQLStatements()
	{
		int countSQLStatements = 0;
		int newRowCount = tableModel.getRowCount();
		int oldRowCount = theVKTList.getRowCount();
//		System.out.println("newRowCount = "+newRowCount);
//		System.out.println("oldRowCount = "+oldRowCount);
		while(newRowCount>0 && tableModel.getValueAt(newRowCount-1,1)==null) newRowCount--;
//		System.out.println("Updated newRowCount = "+newRowCount);
		String[] theResult = new String[newRowCount];

		for(int i=0; i <oldRowCount; i++)
		{
			StringBuilder sb = new StringBuilder("UPDATE `VerkoopTrigger` SET ");
			boolean bFound = false;
			for(int j=1; j<columnNames.length; j++)
			{
				String newValue = tableModel.getValueAt(i, j).toString();
				String oldValue = "";
				if(tableData[i][j] != null) oldValue = tableData[i][j].toString();
				if(newValue.compareTo(oldValue)==0) continue;
//				System.out.println("Detected change at ["+i+"]["+j+"]. New value "+newValue+", old value "+oldValue);
//				System.out.println("Column name = "+columnNames[j]);
				if(bFound) sb.append(", ");
				sb.append("`"+columnNames[j]+"` = '"+newValue+"'");
				bFound = true;
			}
			if(!bFound) continue;
			sb.append(" WHERE (`Id` = '"+tableData[i][0]+"');");
			theResult[i]=sb.toString();
			countSQLStatements++;
		}
		for(int i=oldRowCount; i<newRowCount; i++)
		{
			try
			{
				if(tableModel.getValueAt(i, theVKTList.colTickerId).toString().isEmpty()) continue;
				if(tableModel.getValueAt(i, theVKTList.colCode).toString().isEmpty()) continue;
				// INSERT INTO `VerkoopTrigger` (`Id`,`TickerId`,`Code`,`Waarde`,	`Aantal` )
				// VALUES (
				StringBuilder sb = new StringBuilder("INSERT INTO `VerkoopTrigger`");
				sb.append('(');
				sb.append("`TickerId`,`Code`,`Waarde`,`Aantal`,`Gem.Aank.Waarde`,`Verkoop koers`,`Omzet`,`Winst`,`Status`,`Datum` ");
				sb.append(')');
				sb.append(" VALUES ");
				sb.append('(');
				sb.append("\""+tableModel.getValueAt(i, theVKTList.colTickerId).toString()+"\", ");
				sb.append("\""+tableModel.getValueAt(i, theVKTList.colCode).toString()+"\", ");
				sb.append("\""+tableModel.getValueAt(i, theVKTList.colWaarde).toString()+"\", ");
				sb.append("\""+tableModel.getValueAt(i, theVKTList.colAantal).toString()+"\", ");
				sb.append("\""+tableModel.getValueAt(i, theVKTList.colGemAankWaarde).toString()+"\", ");
				sb.append("\""+tableModel.getValueAt(i, theVKTList.colDoelKoers).toString()+"\", ");
				sb.append("\""+tableModel.getValueAt(i, theVKTList.colOmzet).toString()+"\", ");
				sb.append("\""+tableModel.getValueAt(i, theVKTList.colWinst).toString()+"\", ");
				sb.append("\""+tableModel.getValueAt(i, theVKTList.colStatus).toString()+"\", ");
				sb.append("\""+tableModel.getValueAt(i, theVKTList.colDatum).toString()+"\" ");
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
	
	String updateAankoopTriggerTable(String[] sqls)
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
				con.close();
			}
			catch (SQLException e1) {	}
			con = null;
		}
		return theResult;
	}
	
	BigDecimal berekenKoers(String code, BigDecimal refValue, BigDecimal delta)
	{
		BigDecimal result = BigDecimal.ZERO;
		if(code==null) return result;
		if(code.compareTo("K")==0)
		{
			result = delta.divide(BigDecimal.ONE,2, RoundingMode.HALF_DOWN);
		}
		else if(code.compareTo("P")==0)
		{
			result = refValue.multiply(new BigDecimal(100).add(delta));
			result = result.divide(new BigDecimal(100),2, RoundingMode.HALF_DOWN);
		}
		return result;
	}

	@Override
	public void valueChanged(ListSelectionEvent e)
	{
		if(e.getValueIsAdjusting()) return;
		DefaultListSelectionModel sm = (DefaultListSelectionModel)e.getSource();
		if(sm.isSelectionEmpty())
		{
	    if(removeButton.isEnabled()) removeButton.setEnabled(false);			
			return;
		}
		selectedRow = sm.getMinSelectionIndex();
		int newRowCount = tableModel.getRowCount();
		int oldRowCount = theVKTList.getRowCount();
		if(selectedRow<oldRowCount || oldRowCount+1==newRowCount)
		{
	    if(removeButton.isEnabled()) removeButton.setEnabled(false);			
		}
		else
		{
	    if(!removeButton.isEnabled()) removeButton.setEnabled(true);			
		}
	}

	@Override
	public void tableChanged(TableModelEvent e)
	{
		if(e.getType()==TableModelEvent.INSERT || e.getType()==TableModelEvent.DELETE) return;
		int row = e.getFirstRow();
    int column = e.getColumn();
    DefaultTableModel model = (DefaultTableModel)e.getSource();
    if(column==theVKTList.colDatum)
    {
 //   	System.out.println("colDatum! in tableChanged");
    }
    if(column==theVKTList.colCode)
    {
//       	System.out.println("colCode in tableChanged");
    }
    if(column==theVKTList.colWaarde || column==theVKTList.colAantal || column==theVKTList.colCode)
    {
    	BigDecimal gemiddeldeAankoopKoers = theTList.getGemiddeldeAankoopKoers((String)model.getValueAt(row, theVKTList.colTickerId));
    	if(column==theVKTList.colWaarde || column==theVKTList.colCode)
    	{ 
    		model.setValueAt(gemiddeldeAankoopKoers,row,theVKTList.colGemAankWaarde);
    	}
//     	System.out.println("model.getValueAt(row, colCode) = "+model.getValueAt(row, colCode)+", model.getValueAt(row, colWaarde) = "+model.getValueAt(row, colWaarde));
//    	System.out.println("model.getValueAt(row, colWaarde) class = "+model.getValueAt(row, colWaarde).getClass().getName());
//    	System.out.println("String.class = "+String.class);
    	BigDecimal delta = BigDecimal.ZERO;
    	if(model.getValueAt(row, theVKTList.colWaarde).getClass()==String.class)
    	{
    		delta = new BigDecimal((String)model.getValueAt(row, theVKTList.colWaarde));
    	}
    	else
    	{
    		delta = (BigDecimal)model.getValueAt(row, theVKTList.colWaarde);
    	}
    	BigDecimal doelKoers = berekenKoers((String)model.getValueAt(row, theVKTList.colCode),gemiddeldeAankoopKoers, delta);
    	if(column==theVKTList.colWaarde || column==theVKTList.colCode)
    	{
    		model.setValueAt(doelKoers,row,theVKTList.colDoelKoers);
    	}
    	BigDecimal count = BigDecimal.ZERO;
    	if(model.getValueAt(row, theVKTList.colAantal)!=null)
    	{
	    	if(model.getValueAt(row,theVKTList. colAantal).getClass()==Integer.class)
	    	{
	       	Integer iCount = (Integer)model.getValueAt(row, theVKTList.colAantal);
	        count = new BigDecimal(iCount);   		
	    	}
	    	else
	    	{
	    		String sCount = (String)model.getValueAt(row, theVKTList.colAantal);
	    		count = new BigDecimal(sCount);
	    	}
    	}
    	model.setValueAt(doelKoers.multiply(count), row, theVKTList.colOmzet);
    	BigDecimal winst=doelKoers.subtract(gemiddeldeAankoopKoers).multiply(count);
    	model.setValueAt(winst,row, theVKTList.colWinst);
    }
    if(row+1==model.getRowCount())
    {
    	model.addRow(new Object[] {0, "", "", BigDecimal.ZERO, 0, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, "Nieuw", ""});
//    	model.fireTableRowsInserted(row+1, row+1);
    	if(!removeButton.isEnabled()) removeButton.setEnabled(true);
    }
    if(!applyButton.isEnabled()) applyButton.setEnabled(true);
	}

}
