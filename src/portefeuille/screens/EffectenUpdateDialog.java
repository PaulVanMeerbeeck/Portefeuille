package portefeuille.screens;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.prefs.Preferences;

import javax.sql.DataSource;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
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
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import portefeuille.tables.CategorieList;
import portefeuille.tables.EffectList;
import portefeuille.tables.RisicoList;
import portefeuille.util.ColumnsAutoSizer;
import portefeuille.util.PortefeuilleTableCellRenderer;

public class EffectenUpdateDialog extends JDialog implements TableModelListener, ListSelectionListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	final Preferences node=Preferences.userRoot().node("Portefeuille");
	
	Object[][] tableData;
	Object[] columnNames;
	EffectList theEList;
	DefaultTableModel tableModel;
	JButton applyButton;
	EffectenFrame theEFrame;
	
	DataSource ds;
	Connection con;

	public EffectenUpdateDialog(EffectenFrame theParent) throws HeadlessException
	{
		super(theParent,"Effecten lijstje ...");
		theEFrame = theParent;
		theEList = theParent.getEList();
		ds = theParent.getDs();
		con = theParent.getCon();
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		JTable table = createEffectenTable();
		table.setShowGrid(false);
		JScrollPane scrollPane = new JScrollPane(table,ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		table.setFillsViewportHeight(true);
		scrollPane.setAutoscrolls(true);
//		scrollPane.setMinimumSize(new Dimension(2060, 450)); //460 250
		scrollPane.setPreferredSize(new Dimension(1850, 450));
//		scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 0,0));
//		System.out.println("scrollPane is Focusable : "+scrollPane.isFocusable());
		this.add(scrollPane,BorderLayout.CENTER);
		
		JPanel buttonPane  = new JPanel();
		JButton fileButton = new JButton("File");
		fileButton.setEnabled(true);
		fileButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				File lastDirBoleroFile = new File(node.get("lastDirBoleroFile", "."));
				JFileChooser chooseFile = new JFileChooser();
				chooseFile.setCurrentDirectory(lastDirBoleroFile);
				chooseFile.setDialogTitle("Open Bolero file");
				chooseFile.setFileFilter(new FileFilter() {
					@Override
					public boolean accept(File f) {
            return f.getName().endsWith(".csv");
					}

					@Override
					public String getDescription() {
            return "CSV files";
					}
        });
				int result = chooseFile.showOpenDialog(getParent());
				if(result==JFileChooser.APPROVE_OPTION)
				{
					String newFileLocation = chooseFile.getSelectedFile().getAbsolutePath();
					lastDirBoleroFile=new File(chooseFile.getSelectedFile().getParent());
					node.put("lastDirBoleroFile", lastDirBoleroFile.getPath());
					Object[][] koersen = theEList.readBoleroFile(newFileLocation);
					int aantal = koersen.length;
					String[] sqllistTemp = new String[aantal];
					int aantalSqlStatements=0;
					for(int i=0; i<aantal; i++)
					{
						if(koersen[i][0] == null) continue;
						StringBuilder sb = new StringBuilder("UPDATE `portefeuille`.`effect` SET `Koers` = '"+koersen[i][1].toString()+"' ");
						sb.append("WHERE `ISIN` = '"+koersen[i][0].toString()+"';");
						sqllistTemp[aantalSqlStatements]=sb.toString();
						aantalSqlStatements++;
					}
					String[] sqllist = new String[aantalSqlStatements];
					int k=0;
					for(int j=0; j<aantal; j++)
					{
						if(sqllistTemp[j]==null) continue;
						sqllist[k]=sqllistTemp[j];
						k++;
					}
					
					if(confirmUpdates(sqllist))
					{
						String dbUpdateResult = updateEffectenTable(sqllist);
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
						}
						
					}
				}
			}});
		buttonPane.add(fileButton,BorderLayout.WEST);

		applyButton = new JButton("Apply");
		applyButton.setEnabled(false);
		applyButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
//				System.out.println("Apply button action event command "+e.getActionCommand());
				String[] sqllist = generateSQLStatements();
				if(confirmUpdates(sqllist))
				{
					String dbUpdateResult = updateEffectenTable(sqllist);
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
					}
					
				}
			}});
		buttonPane.add(applyButton,BorderLayout.CENTER);

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
		setSize(1380, 320); // 1050,320
		setMinimumSize(new Dimension(550,320));
		setLocationRelativeTo(null);
		
		setVisible(true);
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
		if(row<theEList.size())
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
	
	JTable createEffectenTable()
	{
	  final Class<?>[] columnClass = new Class<?>[] 
	  		{ String.class, String.class,String.class, String.class,
	  			Integer.class, BigDecimal.class, BigDecimal.class, 
	  			Integer.class, BigDecimal.class, BigDecimal.class, 
	  			Integer.class, BigDecimal.class, BigDecimal.class,
	  			Integer.class, BigDecimal.class, BigDecimal.class,
	  			Integer.class, BigDecimal.class, BigDecimal.class };
		tableData = theEList.getEffectTableData();
		columnNames = theEList.get(0).getFieldNames().toArray();
		tableModel = new DefaultTableModel()
		{
			private static final long serialVersionUID = 1L;

			@Override
      public Class<?> getColumnClass(int columnIndex)
      {
          return columnClass[columnIndex];
      }
	    @Override 
	    public boolean isCellEditable(int row, int column)
	    {
	        if(column < 7) return true;
	        return false;
	    }
		};
		tableModel.setDataVector(tableData, columnNames);
		tableModel.addRow(new Object[] {});
		tableModel.addTableModelListener(this);
		JTable table = new JTable(tableModel);
		
  	CategorieList theCList = new CategorieList(ds);
		JComboBox<String> catCodesCombo = theCList.getCodeComboBox();
		RisicoList theRList = new RisicoList(ds);
		JComboBox<String> riscCodesCombo = theRList.getCodeComboBox();
		
		Enumeration<TableColumn> cNames = table.getColumnModel().getColumns();
		while(cNames.hasMoreElements())
		{
			TableColumn c= cNames.nextElement();
			if(c.getHeaderValue().toString().compareToIgnoreCase("Categorie")==0)
			{
//		    System.out.println("Column name = "+c.getHeaderValue());
				c.setCellEditor(new DefaultCellEditor(catCodesCombo));
			}
			else if(c.getHeaderValue().toString().compareToIgnoreCase("Risc")==0)
			{
//		    System.out.println("Column name = "+c.getHeaderValue());
				c.setCellEditor(new DefaultCellEditor(riscCodesCombo));
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
		else
		{
			JOptionPane.showMessageDialog(this, "Geen wijzigingen gevonden!");
		}
		return theResult;
	}
	
	String[] generateSQLStatements()
	{
		int countSQLStatements = 0;
		int newRowCount = tableModel.getRowCount();
		int oldRowCount = theEList.size();
//		System.out.println("NewRowCount = "+newRowCount+", OldRowCount = "+oldRowCount);
		while(newRowCount>0 && tableModel.getValueAt(newRowCount-1,0)==null) newRowCount--;
//		System.out.println("Updated newRowCount = "+newRowCount);
		String[] theResult = new String[newRowCount];
		// UPDATE `Effect` SET `Koers` = '66.8201', `Div` = '1.8001' WHERE (`TickerId` = 'ABI');
		for(int i=0; i <oldRowCount; i++)
		{
			StringBuilder sb = new StringBuilder("UPDATE `Effect` SET ");
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
			sb.append(" WHERE (`TickerId` = '"+tableData[i][1]+"');");
//			System.out.println("Constructed SQL: "+sb.toString());
			theResult[i]=sb.toString();
			countSQLStatements++;
		}
		for(int i=oldRowCount; i<newRowCount; i++)
		{
			try
			{
				StringBuilder sb = new StringBuilder("INSERT INTO `Effect`");
				sb.append('(');
				sb.append("`Naam`, `TickerId`, `ISIN`, `Categorie`, `Risico`, `Koers`,`Div`");
				sb.append(')');
				sb.append("VALUES ");
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
	
	String updateEffectenTable(String[] sqls)
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
			catch (SQLException e1)
			{
				// e1.printStackTrace();
			}
			con = null;
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
/*		System.out.println("Source = "+e.getSource().toString());
		System.out.println("Value is adjusting = "+e.getValueIsAdjusting());
		System.out.println("selection index = "+sm.getMinSelectionIndex());
		System.out.println(); */
	}
 }
