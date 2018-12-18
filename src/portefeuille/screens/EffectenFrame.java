package portefeuille.screens;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.prefs.Preferences;

import javax.sql.DataSource;
import javax.swing.BorderFactory;
import javax.swing.DefaultListSelectionModel;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.apple.eawt.Application;

import portefeuille.tables.DataSourceFactory;
import portefeuille.tables.Effect;
import portefeuille.tables.EffectList;
import portefeuille.tables.TransactieList;
import portefeuille.util.ColumnsAutoSizer;
import portefeuille.util.MacOSXController;
import portefeuille.util.ResultSetTableModel;
import portefeuille.util.WinstRenderer;

public class EffectenFrame extends JFrame implements WindowListener, ListSelectionListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	final Preferences node=Preferences.userRoot().node("portefeuille");
	final int DEFAULT_WIDTH = 1345;
	final int DEAFULT_HEIGHT = 490;
	boolean passwordValid = false;

	EffectList eList;
	TransactieList tList;
	EffectenMenu menu;
	JTable overzichtTableGlobal;
	JTable transactiesTable;
	JTable effectDividentenTable;
	JScrollPane overzicht;
	JPanel totalsPane;
	JPanel ePane;
	JPanel cPane; // category totals pane
	JScrollPane transacties;
	JScrollPane effectDividenten;
	JScrollPane alleDividenten;
	JScrollPane forecastDividenten;
	JLabel divInkomsten;
	JLabel divForecast;
	DataSource ds;
	Connection con;
	int selectedOverzichtRow = -1;

	public EffectenFrame(String argument, String configId) throws HeadlessException 
	{
		super("Effecten overzicht");
//		ImageIcon img = new ImageIcon("etc//DSC00675.icns");
//		this.setIconImage(img.getImage());
		MacOSXController osController = new MacOSXController(this);
		Application macApplication = Application.getApplication();
		macApplication.setAboutHandler(osController);
		macApplication.setPreferencesHandler(null);
		macApplication.setQuitHandler(osController);
		URL url = this.getClass().getClassLoader().getResource("Resource/Portefeuille.png");
		ImageIcon icon =  new ImageIcon(url);
		if(icon!=null)
		{
			macApplication.setDockIconImage(icon.getImage());
		}
		setIconImage(icon.getImage());
		this.setMinimumSize(new Dimension(DEFAULT_WIDTH,DEAFULT_HEIGHT));
		this.setMaximumSize(new Dimension(DEFAULT_WIDTH,DEAFULT_HEIGHT));
		setLocationRelativeTo(null);
		setVisible(true);
		
		addWindowListener(this);
		
		if(argument!=null && argument.compareToIgnoreCase("unsecure")==0)
		{
			setPasswordValid(true);
		}
		else
		{
			new PasswordDialog(this);
		}
//		System.out.println("After pwdDialog, passwordValid = "+passwordValid);
		if(!passwordValid)
		{
//			System.out.println("Password not valid. Dispose frame");
			System.exit(0);
		}
		
		try
		{
			ds = DataSourceFactory.getInputDataSource(configId);
			con = ds.getConnection();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return;
		}
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

//		int width=node.getInt("width", DEFAULT_WIDTH);
//		int height=node.getInt("height", DEAFULT_HEIGHT);
//		System.out.println("width = "+width);
//		System.out.println("height = "+height);
//		Dimension aDim = new Dimension(width,height);
//		setPreferredSize(aDim);
		menu = new EffectenMenu(this);
		setJMenuBar(menu.getMenuBar());
		
		GridBagLayout gridbagLayout = new GridBagLayout();
		setLayout(gridbagLayout);
		CreateJFrameContents();
		pack();
	}

	EffectList getEList()
	{
		return eList;
	}

	TransactieList getTList()
	{
		return tList;
	}

	DataSource getDs()
	{
		return ds;
	}

	Connection getCon()
	{
		return con;
	}

	void setEList(EffectList eList)
	{
		this.eList = eList;
	}

	void setTList(TransactieList tList)
	{
		this.tList = tList;
	}

	void setDs(DataSource ds)
	{
		this.ds = ds;
	}

	void setCon(Connection con)
	{
		this.con = con;
	}

	void setPasswordValid(boolean value)
	{
		passwordValid=value;
	}
	
	public void CreateJFrameContents()
	{
		eList = new EffectList(ds);
		tList = new TransactieList(ds);
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 0.2;
		gbc.ipadx = 5;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		gbc.gridheight = 1;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		
		if(totalsPane!=null) remove(totalsPane);
		totalsPane = CreateTotalsPane();
		add(totalsPane, gbc);
		
		if(cPane!=null) remove(cPane);
		cPane = CreateTotalsByCategoryPane();
		gbc.gridx=5;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		gbc.gridheight = 2;
		add(cPane,gbc);
		
		if(divInkomsten!=null) remove(divInkomsten);
		divInkomsten = new JLabel("Divident inkomsten");
		divInkomsten.setPreferredSize(new Dimension(143, 20));
		gbc.gridx=7;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		add(divInkomsten,gbc);
		
		if(alleDividenten!=null) remove(alleDividenten);
		String sqlAlleDiv = "SELECT YEAR(Datum) AS Jaar,MONTH(Datum) as Maand, sum(Bruto) as Bruto, sum(Netto) as Netto FROM Divident group by YEAR(Datum),MONTH(Datum) order by YEAR(Datum),MONTH(Datum)";
		alleDividenten = CreateSQLPane(sqlAlleDiv,new Dimension(150, 421));
		gbc.gridx = 7;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.gridheight = 20;
		add(alleDividenten,gbc);
		
		if(divForecast!=null) remove(divForecast);
		divForecast = new JLabel("Divident vooruitzicht");
		divForecast.setPreferredSize(new Dimension(80, 20));
		gbc.gridx=9;
		gbc.gridy = 0;
		gbc.gridheight = 1;
		gbc.gridwidth = 1;
		add(divForecast,gbc);
		
		if(forecastDividenten!=null) remove(forecastDividenten);
//		String sqlDivForecast = "select k.Maand, round(sum(t.Aantal)*k.Divident,2) as Bruto, round(sum(t.Aantal)*k.Divident*(1-k.Voorheffing),2) as Netto from Effect e, pvm.Kalender k, pvm.transactie t where e.TickerId = k.TickerId and e.TickerId = t.Ticker group by k.Maand order by k.Maand";
		String sqlDivForecast = "select Maand, Sum(Bruto) as Bruto, Sum(Netto) as Netto from `divident_uitkeringen` group by Maand order by Maand";
		forecastDividenten = CreateSQLPane(sqlDivForecast,new Dimension(90, 421));
		gbc.gridx = 9;
		gbc.gridy = 1;
		gbc.gridheight = 20;
		gbc.gridwidth = 1;
		add(forecastDividenten,gbc);
		
		if(overzicht != null)
		{
			remove(overzicht);
		}
		overzicht = CreateOverzichtPane();
		gbc.gridx=0;
		gbc.gridy = 1;
		gbc.gridheight = 20;
		add(overzicht,gbc);
		
		if( selectedOverzichtRow!=-1 )
		{
			overzichtTableGlobal.setRowSelectionInterval(selectedOverzichtRow, selectedOverzichtRow);
		}
		return;
	}

	private JPanel CreateTotalsPane()
	{
			JPanel pane =  new JPanel(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.anchor = GridBagConstraints.FIRST_LINE_START;
			gbc.weightx = 0.25;
	
			String sql = "select sum(Aantal) as 'Aantal aandelen', sum(Aankoop) as 'aankoop waarde', sum(Kosten) as kosten, sum(Waarde) as 'huidige waarde', sum(Winst) as meerwaarde from toestand ";
			
			try
			{
				Statement stmt = con.createStatement();
				ResultSet rs = stmt.executeQuery(sql);
				rs.next();
				ResultSetMetaData rsmd = rs.getMetaData();
				int countCol = rsmd.getColumnCount();
				for(int j=0; j<countCol; j++)
				{
					String colName = rsmd.getColumnName(j+1);
					gbc.gridx = j;

					JLabel f0 = new JLabel(colName);
					gbc.gridy = 0;
					
					f0.setHorizontalAlignment(JLabel.RIGHT);
					pane.add(f0,gbc);
					
					JLabel f1 = new JLabel();
					if(j==0)
					{
						f1.setText(String.format(" %d",rs.getInt(j+1)));
					}
					else
					{
						f1.setText(String.format(" %.2f",rs.getBigDecimal(j+1)));
/*						if(colName.compareToIgnoreCase("meerwaarde")==0)
						{
							if(rs.getBigDecimal(j+1).signum()>0)
								f1.setBackground(Color.GREEN);
						} */
					}
					f1.setHorizontalAlignment(JLabel.RIGHT);
					gbc.gridy = 1;
					pane.add(f1,gbc);
//					pane.setBackground(Color.lightGray);
					pane.setPreferredSize(new Dimension(460,30));
					pane.setMinimumSize(new Dimension(460,30));
					pane.setMaximumSize(new Dimension(460,30));
				}
			}
			catch(SQLException e)
			{
				e.printStackTrace();
			}
			return pane;
	}
	
	private JScrollPane CreateSQLPane(String sql, Dimension dim)
	{

		try
		{
			Object[][] tableData;
			Object[] columnNames;
			int rowCount=0, colCount, colBruto = -1, colNetto=-1, colMaand=-1;
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
//			ResultSetTableModel model = new ResultSetTableModel(rs);
			ResultSetMetaData rsMtd = rs.getMetaData();
			colCount=rsMtd.getColumnCount();
			columnNames=new Object[colCount];
			for(int i=0; i<colCount; i++)
			{
				columnNames[i]=rsMtd.getColumnName(i+1);
				if(columnNames[i].toString().compareToIgnoreCase("Maand")==0)
				{
					colMaand=i;
				}
				else if(columnNames[i].toString().compareToIgnoreCase("Bruto")==0)
				{
					colBruto=i;
				}
				else if(columnNames[i].toString().compareToIgnoreCase("Netto")==0)
				{
					colNetto=i;
				}
//				System.out.println("colName["+i+"] = "+columnNames[i]);
			}
//			System.out.println("colBruto = "+colBruto+", colNetto = "+colNetto);
			if (rs.last()) 
			{
				rowCount = rs.getRow();
		    rs.beforeFirst();
			}
			BigDecimal totaalBruto=BigDecimal.ZERO, totaalNetto=BigDecimal.ZERO;
			tableData = new Object[rowCount][colCount];
			int i = 0;
			while (rs.next()) 
			{
				for(int j=0; j < colCount; j++)
				{
					tableData[i][j]=rs.getObject(j+1);
					if(j==colBruto) 
					{
						totaalBruto=totaalBruto.add(rs.getBigDecimal(j+1));
					}
					if(j==colNetto) 
					{
						totaalNetto=totaalNetto.add(rs.getBigDecimal(j+1));
					}
				}
				i++;
			}
			DefaultTableModel tableModel = new DefaultTableModel();
			tableModel.setDataVector(tableData, columnNames);
			if(columnNames.length<4)
				tableModel.addRow(new Object[] {"Totaal",totaalBruto,totaalNetto});
			else
				tableModel.addRow(new Object[] {"Totaal","",totaalBruto,totaalNetto});
					
			JTable table = new JTable(tableModel);
			TableColumnModel colModel = table.getColumnModel();
			TableColumn theColMaand = colModel.getColumn(colMaand);
			theColMaand.setCellRenderer(new WinstRenderer());
			TableColumn theColBruto = colModel.getColumn(colBruto);
			theColBruto.setCellRenderer(new WinstRenderer());
			TableColumn theColNetto = colModel.getColumn(colNetto);
			theColNetto.setCellRenderer(new WinstRenderer());
			table.setEnabled(false);
			table.setRowSelectionInterval(tableModel.getRowCount()-1,tableModel.getRowCount()-1);
	
			table.setPreferredScrollableViewportSize(dim);
			table.setFillsViewportHeight(true);
			
			ColumnsAutoSizer as = new ColumnsAutoSizer();
			as.sizeColumnsToFit(table);
			table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			table.setSelectionBackground(Color.LIGHT_GRAY);
			JScrollPane pane =  new JScrollPane(table);
			table.scrollRectToVisible(table.getCellRect(tableModel.getRowCount()-1,0, true)); 

			pane.setPreferredSize(dim);
			pane.setMinimumSize(dim);
			pane.setMaximumSize(dim);
			return pane;
		}
		catch(SQLException e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	private JPanel CreateTotalsByCategoryPane()
	{
		JPanel pane =  new JPanel(new BorderLayout());

		String sql = "SELECT Categorie.Omschrijving, Sum(`toestand`.`Waarde`) as Waarde, Sum(`toestand`.`Winst`) as Winst "+
								 "FROM toestand, Effect, Categorie WHERE toestand.Naam = Effect.Naam and Effect.Categorie = Categorie.Code "+
								 "GROUP BY Categorie.Omschrijving";
		try
		{
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sql);

			ResultSetTableModel model = new ResultSetTableModel(rs);
			
			JTable table = new JTable(model);
			table.setPreferredScrollableViewportSize(new Dimension(270, 65));
			table.setFillsViewportHeight(true);
			
			ColumnsAutoSizer as = new ColumnsAutoSizer();
			as.sizeColumnsToFit(table);
			table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			table.setBackground(Color.YELLOW);
			table.setEnabled(false);

			pane.add(table);
			pane.setMinimumSize(new Dimension(270, 65));
			pane.setMaximumSize(new Dimension(270, 65));
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		return pane;
	}

	JScrollPane CreateOverzichtPane()
	{
		try
		{
			String sql = "select * from toestand";
//			boolean scrolling = con.getMetaData().supportsResultSetType(ResultSet.TYPE_SCROLL_SENSITIVE);
//			System.out.println("TYPE_SCROLL_INSENSITIVE = "+scrolling);
			Statement stmtRM = con.createStatement();
			ResultSet rsRM = stmtRM.executeQuery(sql);
	
			ResultSetTableModel model = new ResultSetTableModel(rsRM);
			
			
			JTable overzichtTable = new JTable(model);
			overzichtTable.setPreferredScrollableViewportSize(new Dimension(460, 420));
			overzichtTable.setFillsViewportHeight(true);
			
			ColumnsAutoSizer as = new ColumnsAutoSizer();
			as.sizeColumnsToFit(overzichtTable);
			overzichtTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
			ListSelectionModel sm = overzichtTable.getSelectionModel();
			sm.addListSelectionListener(this);
			
			for( int i=0; i<overzichtTable.getColumnCount(); i++)
			{
				String colName = overzichtTable.getColumnName(i);
				if(colName.compareToIgnoreCase("Winst")!=0) continue;
				TableColumn theCol = overzichtTable.getColumnModel().getColumn(i);
				theCol.setCellRenderer(new WinstRenderer());
			}
			
			overzichtTable.setSelectionBackground(Color.LIGHT_GRAY);
			JScrollPane scrollPane = new JScrollPane(overzichtTable);
			scrollPane.setAutoscrolls(true);
			scrollPane.setMinimumSize(new Dimension(460, 420));
			scrollPane.setPreferredSize(new Dimension(460, 420));
			scrollPane.setBorder(BorderFactory.createLineBorder(Color.GRAY));
			overzichtTableGlobal = overzichtTable;
			return scrollPane;			
		}
		catch(SQLException e)
		{
			e.printStackTrace();
			return null;
		}

	}

	JScrollPane CreateTransactiesPane(String naam)
	{
		try
		{
			String sql = "select t.Datum, t.Aantal, t.Prijs, t.Makelaarsloon, t.Beurstaks from transactie t, Effect where Effect.TickerId = t.Ticker and Effect.Naam ='"+naam+"' order by Datum";
			Statement stmtRM = con.createStatement();
			ResultSet rsRM = stmtRM.executeQuery(sql);
	
			ResultSetTableModel model = new ResultSetTableModel(rsRM);
			
			transactiesTable = new JTable(model);
			transactiesTable.setPreferredScrollableViewportSize(new Dimension(270, 120));
			transactiesTable.setFillsViewportHeight(true);
			transactiesTable.setSelectionBackground(Color.LIGHT_GRAY);
			transactiesTable.setEnabled(false);
			
			ColumnsAutoSizer as = new ColumnsAutoSizer();
			as.sizeColumnsToFit(transactiesTable);
			transactiesTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
			
			JScrollPane scrollPane = new JScrollPane(transactiesTable);
			scrollPane.setAutoscrolls(true);
			scrollPane.setMinimumSize(new Dimension(270, 120));
			scrollPane.setMaximumSize(new Dimension(270, 120));
			return scrollPane;			
		}
		catch(SQLException e)
		{
			e.printStackTrace();
			return null;
		}

	}
	
	JScrollPane CreateEffectDividentenPane(String naam)
	{
		try
		{
			String sql = "select YEAR(d.Datum) as Jaar, sum(d.Divident) as Divident, Sum(d.Bruto) as Bruto, Sum(d.Voorheffing) as Voorheffing, Sum(d.Netto) as Netto from Divident d, Effect e where e.Naam = '"+naam+"' and e.TickerId = d.TickerId group by YEAR(Datum) order by Jaar";
			Statement stmtRM = con.createStatement();
			ResultSet rsRM = stmtRM.executeQuery(sql);
	
			ResultSetTableModel model = new ResultSetTableModel(rsRM);
			
			effectDividentenTable = new JTable(model);
			effectDividentenTable.setPreferredScrollableViewportSize(new Dimension(270, 117));
			effectDividentenTable.setFillsViewportHeight(true);
			effectDividentenTable.setSelectionBackground(Color.LIGHT_GRAY);
			effectDividentenTable.setEnabled(false);
			
			ColumnsAutoSizer as = new ColumnsAutoSizer();
			as.sizeColumnsToFit(effectDividentenTable);
			effectDividentenTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
			
			JScrollPane scrollPane = new JScrollPane(effectDividentenTable);
			scrollPane.setAutoscrolls(true);
			scrollPane.setPreferredSize(new Dimension(270, 117));
			scrollPane.setMinimumSize(new Dimension(270, 117));
			scrollPane.setMaximumSize(new Dimension(270, 117));
			return scrollPane;			
		}
		catch(SQLException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	private JPanel CreateEffectPane(String naam)
	{
//		System.out.println("CreateEffectPane called for - "+naam);
		JPanel pane =  new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.weightx = 0.25;
	
		try
		{
			Effect e = eList.getEffectBijNaam(naam);
			
			ArrayList<String> names = e.getFieldNames();
			ArrayList<Object> values = e.getFieldValues();
			
			String tickerId = "";
			
			for(int j=0; j < names.size(); j++)
			{
				gbc.gridy = j;

				if(names.get(j).compareToIgnoreCase("TickerId")==0) { tickerId = values.get(j).toString(); }
				
				gbc.gridx = 0;
				JLabel l = new JLabel(names.get(j));
				l.setHorizontalAlignment(JLabel.LEFT);
				pane.add(l,gbc);
				
				gbc.gridx = 1;
				JLabel v = new JLabel();
				if(values.get(j).getClass()==int.class)
				{
					v.setText(String.format("%d",values.get(j)));
				}
				else if(values.get(j).getClass()==BigDecimal.class)
				{
					v.setText(String.format("%.2f",values.get(j)));
				}
				else if(values.get(j).getClass()==String.class)
				{
					v.setText(values.get(j).toString());
				}
				pane.add(v,gbc);
			}
			
			if(tickerId.isEmpty()==false)
			{
				JLabel l = new JLabel("Gem.Aank.Koers");
				JLabel v = new JLabel();
				BigDecimal gem = tList.getGemiddleAankoopKoers(tickerId);
				v.setText(String.format("%.2f",gem));
				gbc.gridy = gbc.gridy+1;
				gbc.gridx = 0;
				pane.add(l,gbc);
				gbc.gridx = 1;
				pane.add(v,gbc);					
			}
			pane.setPreferredSize(new Dimension(280, 130));
			pane.setBackground(Color.LIGHT_GRAY);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return pane;
	}
	
	@Override
	public void windowOpened(WindowEvent e)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void windowClosing(WindowEvent e)
	{
		if(JOptionPane.showConfirmDialog(this, "Do you really want to quit?", "Portefeuille", JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE)==JOptionPane.YES_OPTION)
		{
			node.putInt("width", getWidth());
			node.putInt("height", getHeight());	
			if(con!=null) try
			{
				con.close();
			}
			catch (SQLException e1)
			{
				e1.printStackTrace();
			}
			dispose();
			System.exit(0);			
		}
	}

	@Override
	public void windowClosed(WindowEvent e)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void windowIconified(WindowEvent e)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void windowDeiconified(WindowEvent e)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void windowActivated(WindowEvent e)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void windowDeactivated(WindowEvent e)
	{
		// TODO Auto-generated method stub
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
//		System.out.println("Source = "+e.getSource().toString());
		selectedOverzichtRow = sm.getMinSelectionIndex();
		String effect  = overzichtTableGlobal.getModel().getValueAt(selectedOverzichtRow, 0).toString();
		if(ePane != null) remove(ePane);
//		if(transacties != null) remove(transacties);
//		if(effectDividenten != null) remove(effectDividenten);
		ePane = CreateEffectPane(effect);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 0.2;
		gbc.ipadx = 5;
		gbc.gridwidth = 2;
		gbc.gridheight = 5;
		gbc.anchor = GridBagConstraints.NORTHWEST;
//		gbc.gridwidth = 5;
		gbc.gridy = 3;
		gbc.gridx = 5;
		add(ePane,gbc);
		
		if(transacties!=null) remove(transacties);
		transacties = CreateTransactiesPane(effect);
		gbc.gridx = 5;
		gbc.gridy=8;
		gbc.gridwidth = 2;
		gbc.gridheight = 5;
		add(transacties,gbc); 
		
		if(effectDividenten!=null) remove(effectDividenten);
		effectDividenten = CreateEffectDividentenPane(effect);
		gbc.gridx = 5;
		gbc.gridy = 13;
		gbc.gridwidth = 2;
		gbc.gridheight = 5;
		add(effectDividenten, gbc);
		
//		pack();
//		this.repaint();
		this.validate();
//		System.out.println("Size (h,w) = "+transacties.getSize().getHeight()+ ", "+transacties.getSize().getWidth());
//		double h = transacties.getSize().getHeight()/3;
//		double w = transacties.getSize().getWidth()/2;
//		ePane.setSize((int)w, (int)h);
//		transacties.setSize((int)w, (int)h);
//		JOptionPane.showMessageDialog(null, "Deze is "+this.getClass());
		
	}

}
