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
import java.util.Enumeration;

import javax.sql.DataSource;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
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
import portefeuille.tables.EffectList;
import portefeuille.tables.TransactieList;
import portefeuille.tables.TriggerCodeList;
import portefeuille.tables.AankoopTriggerList;
import portefeuille.util.ColumnsAutoSizer;
import portefeuille.util.PortefeuilleTableCellRenderer;

public class AankoopTriggerDialog extends JDialog implements TableModelListener, ListSelectionListener
{
	private static final long serialVersionUID = 1L;
	
	final int colTickerId = 1;
	final int colCode = 2;
	final int colWaarde = 3;
	final int colAantal = 4;
	final int colGemAankWaarde = 5;
	final int colDoelKoers = 6;
	final int colInvestering = 7;

	Object[][] tableData;
	Object[] columnNames;
	EffectList theEList;
	TransactieList theTList;
	AankoopTriggerList theAKTList;
	TriggerCodeList theTCList;
	DefaultTableModel tableModel;
	JButton applyButton;
	EffectenFrame theEFrame;
	
	DataSource ds;
	Connection con;

	public AankoopTriggerDialog(EffectenFrame theParent)
	{
		super(theParent,"Aankoop triggers",false);
		theEFrame = theParent;
		theEList = theParent.getEList();
		theTList = theParent.getTList();
		ds = theParent.getDs();
		con = theParent.getCon();
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		JTable table = createAankoopTriggerTable();
		JScrollPane scrollPane = new JScrollPane(table,ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		table.setFillsViewportHeight(true);
		scrollPane.setAutoscrolls(true);
		scrollPane.setMinimumSize(new Dimension(360, 250));
		scrollPane.setPreferredSize(new Dimension(360, 250));
//		scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 0,0));
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
					String dbUpdateResult = updateAankoopTriggerTable(sqllist);
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
		setMinimumSize(new Dimension(520,380));
		setPreferredSize(new Dimension(520,380));
		setLocationRelativeTo(null);
		
		setVisible(true);
	}

	JTable createAankoopTriggerTable()
	{
		
		theAKTList = new AankoopTriggerList(ds);
		theTCList = new TriggerCodeList(ds);
		tableData = theAKTList.getTableData();
		columnNames = theAKTList.getColumnNames();
		Object[] gemidAankWaarde = new Object[tableData.length];
		Object[] aankoopKoers = new Object[tableData.length];
		Object[] investering = new Object[tableData.length];
		for(int i=0;i<tableData.length;i++)
		{
			gemidAankWaarde[i] = theTList.getGemiddleAankoopKoers((String) tableData[i][colTickerId]);
    	BigDecimal doelKoers = berekenKoers((String)tableData[i][colCode],(BigDecimal)gemidAankWaarde[i], (BigDecimal)tableData[i][colWaarde]);
    	aankoopKoers[i] = doelKoers;
    	investering[i] = doelKoers.multiply(new BigDecimal((int)tableData[i][colAantal]));
    	tableData[i][colWaarde]=((BigDecimal)tableData[i][colWaarde]).divide(BigDecimal.ONE, 2,  RoundingMode.HALF_DOWN);
		}
		
	  final Class<?>[] columnClass = new Class<?>[] {String.class, String.class, String.class, BigDecimal.class, Integer.class, BigDecimal.class, BigDecimal.class, BigDecimal.class};

		tableModel = new DefaultTableModel()
		{
			private static final long serialVersionUID = 1L;

			@Override 
	    public boolean isCellEditable(int row, int column)
	    {
	      if(column>0 && column<5)
	      	return true;
	      else
	      	return false;
	    }
			@Override
      public Class<?> getColumnClass(int columnIndex)
      {
          return columnClass[columnIndex];
      }
		};
		
		tableModel.setDataVector(tableData, columnNames);
		tableModel.addColumn("Gem.Aank.Waarde", gemidAankWaarde);
		tableModel.addColumn("Aankoop koers", aankoopKoers);
		tableModel.addColumn("Investering", investering);
		tableModel.addRow(new Object[] {});
		
		tableModel.addTableModelListener(this);
		JTable table = new JTable(tableModel);
		
		JComboBox<String> tickerIdCombo = theEList.getTickerIdComboBox();
		JComboBox<String> codeIdCombo = theTCList.getCodeComboBox();
		
		Enumeration<TableColumn> cNames = table.getColumnModel().getColumns();
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
		int oldRowCount = theAKTList.getRowCount();
//		System.out.println("newRowCount = "+newRowCount);
//		System.out.println("oldRowCount = "+oldRowCount);
		while(newRowCount>0 && tableModel.getValueAt(newRowCount-1,1)==null) newRowCount--;
//		System.out.println("Updated newRowCount = "+newRowCount);
		String[] theResult = new String[newRowCount];

		for(int i=0; i <oldRowCount; i++)
		{
			StringBuilder sb = new StringBuilder("UPDATE `AankoopTrigger` SET ");
			boolean bFound = false;
			for(int j=1; j<columnNames.length; j++)
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
			sb.append(" WHERE (`Id` = '"+tableData[i][0]+"');");
			theResult[i]=sb.toString();
			countSQLStatements++;
		}
		for(int i=oldRowCount; i<newRowCount; i++)
		{
			try
			{
				// INSERT INTO `AankoopTrigger` (`Id`,`TickerId`,`Code`,`Waarde`,	`Aantal` )
				// VALUES (
				StringBuilder sb = new StringBuilder("INSERT INTO `AankoopTrigger`");
				sb.append('(');
				sb.append("`TickerId`,`Code`,`Waarde`,`Aantal` ");
				sb.append(')');
				sb.append(" VALUES ");
				sb.append('(');
				sb.append("\""+tableModel.getValueAt(i, 1).toString()+"\", ");
				sb.append("\""+tableModel.getValueAt(i, 2).toString()+"\", ");
				sb.append("\""+tableModel.getValueAt(i, 3).toString()+"\", ");
				sb.append("\""+tableModel.getValueAt(i, 4).toString()+"\" ");
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
			result = refValue.multiply(new BigDecimal(100).subtract(delta));
			result = result.divide(new BigDecimal(100),2, RoundingMode.HALF_DOWN);
		}
		return result;
	}

	@Override
	public void valueChanged(ListSelectionEvent e)
	{
		// TODO Auto-generated method stub
//		System.out.println("valueChanged called "+e.toString());

	}

	@Override
	public void tableChanged(TableModelEvent e)
	{
		if(e.getType()==TableModelEvent.INSERT) return;
		int row = e.getFirstRow();
    int column = e.getColumn();
    DefaultTableModel model = (DefaultTableModel)e.getSource();
    if(column==3 || column==4)
    {
    	BigDecimal gemiddeldeAankoopKoers = theTList.getGemiddleAankoopKoers((String)model.getValueAt(row, colTickerId));
    	if(column==3)
    	{ 
    		model.setValueAt(gemiddeldeAankoopKoers,row,colGemAankWaarde);
    	}
//     	System.out.println("model.getValueAt(row, colCode) = "+model.getValueAt(row, colCode)+", model.getValueAt(row, colWaarde) = "+model.getValueAt(row, colWaarde));
//    	System.out.println("model.getValueAt(row, colWaarde) class = "+model.getValueAt(row, colWaarde).getClass().getName());
//    	System.out.println("String.class = "+String.class);
    	BigDecimal delta = BigDecimal.ZERO;
    	if(model.getValueAt(row, colWaarde).getClass()==String.class)
    	{
    		delta = new BigDecimal((String)model.getValueAt(row, colWaarde));
    	}
    	else
    	{
    		delta = (BigDecimal)model.getValueAt(row, colWaarde);
    	}
    	BigDecimal doelKoers = berekenKoers((String)model.getValueAt(row, colCode),gemiddeldeAankoopKoers, delta);
    	if(column==3)
    	{
    		model.setValueAt(doelKoers,row,colDoelKoers);
    	}
    	BigDecimal count = BigDecimal.ZERO;
    	if(model.getValueAt(row, colAantal)!=null)
    	{
	    	if(model.getValueAt(row, colAantal).getClass()==Integer.class)
	    	{
	       	Integer iCount = (Integer)model.getValueAt(row, colAantal);
	        count = new BigDecimal(iCount);   		
	    	}
	    	else
	    	{
	    		String sCount = (String)model.getValueAt(row, colAantal);
	    		count = new BigDecimal(sCount);
	    	}
    	}
    	model.setValueAt(doelKoers.multiply(count), row, colInvestering);
    }
    if(row+1==model.getRowCount())
    {
    	model.addRow(new Object[] {});
    	model.fireTableRowsInserted(row+1, row+1);
    }
    if(!applyButton.isEnabled()) applyButton.setEnabled(true);
	}

}
