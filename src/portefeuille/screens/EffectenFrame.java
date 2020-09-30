package portefeuille.screens;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
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
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
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
import portefeuille.tables.MeerwaardenList;
import portefeuille.util.BigDecimalRenderer;
import portefeuille.util.ColumnsAutoSizer;
import portefeuille.util.DataTableModel;
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
	final int DEFAULT_WIDTH = 1385;
	final int DEFAULT_HEIGHT = 852; //652; //622
	
	final int bannerOneWidth = 600;
	final int bannerTwoWidth = 350;
	final int bannerThreeWidth = 250;
	final int bannerFourWidth = 185; //175
	
	boolean passwordValid = false;

	EffectList eList;
	TransactieList tList;
	EffectenMenu menu;
	JTable overzichtTableGlobal;
	JTable transactiesTable;
	JTable effectDividendenTable;
	JScrollPane overzicht;
	JPanel totalsPane;
	JPanel ePane;
	JPanel cPane; // category totals pane
	JPanel aankooporderPanel;
	JPanel verkooporderPanel;
	JPanel meerwaardenPanel;
	JScrollPane transacties;
	JScrollPane effectDividenden;
	JScrollPane alleDividenden;
	JScrollPane forecastDividenden;
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
		this.setPreferredSize(new Dimension(DEFAULT_WIDTH,DEFAULT_HEIGHT));
		this.setMinimumSize(new Dimension(DEFAULT_WIDTH,DEFAULT_HEIGHT));
//		this.setMaximumSize(new Dimension(DEFAULT_WIDTH,DEAFULT_HEIGHT));
/*		int width=node.getInt("width", DEFAULT_WIDTH);
		int height=node.getInt("height", DEAFULT_HEIGHT);
		System.out.println("width = "+width);
		System.out.println("height = "+height);
		Dimension aDim = new Dimension(width,height);
		setPreferredSize(aDim); */
		
		setLocationRelativeTo(null);
		setVisible(true);
		menu = new EffectenMenu(this);
		setJMenuBar(menu.getMenuBar());
		
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
			JOptionPane.showMessageDialog(this,"DB not available","Database", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		GridBagLayout gridbagLayout = new GridBagLayout();
		setLayout(gridbagLayout);
		CreateJFrameContents();
		pack();
//		MeerwaardenList ml = new MeerwaardenList(ds);
//		ml.print();
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
		gbc.anchor=GridBagConstraints.NORTHWEST;
		gbc.fill=GridBagConstraints.NONE;
		if(totalsPane!=null) remove(totalsPane);
		totalsPane = CreateTotalsPane();
		gbc.gridx=0;
		gbc.gridy=0;
		gbc.gridwidth=24;
		gbc.gridheight=3;
		add(totalsPane, gbc);
		
		if(cPane!=null) remove(cPane);
		cPane = CreateTotalsByCategoryPane();
		gbc.gridx=24;
		gbc.gridy=0;
		gbc.gridwidth=14;
		gbc.gridheight=7;
		add(cPane,gbc);
		
		if(divInkomsten!=null) remove(divInkomsten);
		divInkomsten = new JLabel("Dividend inkomsten");
		divInkomsten.setPreferredSize(new Dimension(bannerThreeWidth, 20));
		gbc.gridx=38;
		gbc.gridy=0;
		gbc.gridwidth=10;
		gbc.gridheight=2;
		add(divInkomsten,gbc);
		
		if(alleDividenden!=null) remove(alleDividenden);
		String sqlAlleDiv = "SELECT YEAR(Datum) AS Jaar,MONTH(Datum) as Maand, sum(Bruto) as Bruto, sum(Netto) as Netto FROM Divident group by YEAR(Datum),MONTH(Datum) order by YEAR(Datum),MONTH(Datum)";
		alleDividenden = CreateSQLPane(sqlAlleDiv,new Dimension(bannerThreeWidth, 610));
		gbc.gridx=38;
		gbc.gridy=2;
		gbc.gridwidth=10;
		gbc.gridheight=63;
		add(alleDividenden,gbc);
		
		if(divForecast!=null) remove(divForecast);
		divForecast = new JLabel("Dividend vooruitzicht");
		divForecast.setPreferredSize(new Dimension(bannerFourWidth, 20));
		gbc.gridx=48;
		gbc.gridy=0;
		gbc.gridwidth=7;
		gbc.gridheight=2;
		add(divForecast,gbc);
		
		if(forecastDividenden!=null) remove(forecastDividenden);
//		String sqlDivForecast = "select k.Maand, round(sum(t.Aantal)*k.Divident,2) as Bruto, round(sum(t.Aantal)*k.Divident*(1-k.Voorheffing),2) as Netto from Effect e, pvm.Kalender k, pvm.transactie t where e.TickerId = k.TickerId and e.TickerId = t.Ticker group by k.Maand order by k.Maand";
		String sqlDivForecast = "select Maand, Sum(Bruto) as Bruto, Sum(Netto) as Netto from `divident_uitkeringen` group by Maand order by Maand";
		forecastDividenden = CreateSQLPane(sqlDivForecast,new Dimension(bannerFourWidth, 380));
		gbc.gridx=48;
		gbc.gridy=2;
		gbc.gridwidth=7;
		gbc.gridheight=38;
		add(forecastDividenden,gbc);
		
		if(meerwaardenPanel != null)
		{
			remove(meerwaardenPanel);
		}
		meerwaardenPanel = CreateMeerwaardenPanel(230);
		gbc.gridx = 48;
		gbc.gridy=40;
		gbc.gridwidth=7;
		gbc.gridheight=25;
		add(meerwaardenPanel,gbc);
		
		if(overzicht != null)
		{
			remove(overzicht);
		}
		overzicht = CreateOverzichtPane();
		gbc.gridx=0;
		gbc.gridy=3;
		gbc.gridwidth=24;
		gbc.gridheight=82; //60; //57
		//GBC(int gridx, int gridy, int gridwidth, int gridheight)
		add(overzicht,gbc);
		
		if( selectedOverzichtRow!=-1 )
		{
			overzichtTableGlobal.setRowSelectionInterval(selectedOverzichtRow, selectedOverzichtRow);
		}
		else
		{
			doEffectTransactieDividend(null,gbc);
		}
		
		if(aankooporderPanel != null)
		{
			remove(aankooporderPanel);
		}
		aankooporderPanel = CreateAankooporderPanel();
		gbc.gridx=38;
//		gbc.gridy=45;
		gbc.gridy=65;
		gbc.gridwidth=17;
//		gbc.gridheight=15;
		gbc.gridheight=20;
		add(aankooporderPanel,gbc);

		if(verkooporderPanel != null)
		{
			remove(verkooporderPanel);
		}
		verkooporderPanel = CreateVerkooporderPanel();
		gbc.gridx=24;
//		gbc.gridy=45;
		gbc.gridy=65;
		gbc.gridwidth=14;
//		gbc.gridheight=15;
		gbc.gridheight=20;
		add(verkooporderPanel,gbc);
		return;
	}

	private JPanel CreateTotalsPane()
	{
			JPanel pane =  new JPanel();
			pane.setPreferredSize(new Dimension(bannerOneWidth,30));
		
	//		String sql = "select sum(Aantal) as 'Aantal aandelen', sum(Aankoop) as 'aankoop waarde', sum(Kosten) as kosten, sum(Waarde) as 'huidige waarde', sum(Winst) as meerwaarde from toestand ";
			String sql = "select sum(Aantal) as 'Aantal aandelen', sum(Aankoop) as 'aankoop waarde', sum(AankoopKosten) as kosten, sum(Waarde) as 'huidige waarde', sum(Winst) as meerwaarde from toestand where Aantal > 0";
			try
			{
				Statement stmt = con.createStatement();
				ResultSet rs = stmt.executeQuery(sql);
				rs.next();
				ResultSetMetaData rsmd = rs.getMetaData();
				int countCol = rsmd.getColumnCount();
				pane.setLayout(new GridLayout(2,countCol));
				for(int j=0; j<countCol; j++)
				{
					String colName = rsmd.getColumnName(j+1);
					JLabel l = new JLabel(colName);
					l.setHorizontalAlignment(JLabel.CENTER);
					pane.add(l);
				}
				
				for(int j=0; j<countCol; j++)
				{
					JLabel l = new JLabel();
					if(j==0)
					{
						l.setText(String.format(" %d",rs.getInt(j+1)));
					}
					else
					{
						l.setText(String.format(" %.2f€",rs.getBigDecimal(j+1)));
						if(j+1 == countCol)
						{
							if(rs.getBigDecimal(j+1).signum()>0)
								l.setForeground(Color.green);
							else
								l.setForeground(Color.red);
						}
					}
					l.setHorizontalAlignment(JLabel.CENTER);
					pane.add(l);
					pane.setBackground(Color.lightGray);
//					pane.setMinimumSize(new Dimension(bannerOneWidth,30));
//					pane.setMaximumSize(new Dimension(bannerOneWidth,30));
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
		Font f = pane.getFont();

		String sql = "SELECT Categorie.Omschrijving, Sum(`toestand`.`Waarde`) as Waarde, Sum(`toestand`.`Winst`) as Winst "+
								 "FROM toestand, Effect, Categorie WHERE toestand.Naam = Effect.Naam and Effect.Categorie = Categorie.Code "+
								 "GROUP BY Categorie.Omschrijving";
		try
		{
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sql);

			ResultSetTableModel model = new ResultSetTableModel(rs);
			
			JTable table = new JTable(model);
			TableColumnModel colModel=table.getColumnModel();
			for(int i=0; i<colModel.getColumnCount();i++)
			{
				TableColumn aColumn = colModel.getColumn(i);
				aColumn.setCellRenderer(new BigDecimalRenderer());
			}
			table.setPreferredScrollableViewportSize(new Dimension(bannerTwoWidth, 70));
			table.setFillsViewportHeight(true);
			
			ColumnsAutoSizer as = new ColumnsAutoSizer();
			as.sizeColumnsToFit(table);
	//		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			table.setBackground(Color.YELLOW);
			table.setEnabled(false);
			table.setRowHeight(14);
			table.setFont(new Font(f.getName(),Font.PLAIN, 11));


			pane.add(table);
//			pane.setMinimumSize(new Dimension(270, 65));
//			pane.setMaximumSize(new Dimension(270, 65));
			pane.setPreferredSize(new Dimension(bannerTwoWidth,70));
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
//			String sql = "select * from toestand";
			String sql = "SELECT `toestand`.`Naam`, `toestand`.`Aantal`, `toestand`.`Waarde`, `toestand`.`Aantal gekocht` as `gekocht`, "+
					"`toestand`.`AankoopPrijs` AS `Aank.Prijs`,  `toestand`.`Aantal verkocht` as `verkocht`, `toestand`.`VerkoopPrijs` AS `Verk.Prijs`, "+
					"`toestand`.`Winst`, `toestand`.`Cat` FROM `toestand` where `Aantal` > 0 or `VerkoopPrijs` >0";
//			boolean scrolling = con.getMetaData().supportsResultSetType(ResultSet.TYPE_SCROLL_SENSITIVE);
//			System.out.println("TYPE_SCROLL_INSENSITIVE = "+scrolling);
			Statement stmtRM = con.createStatement();
			ResultSet rsRM = stmtRM.executeQuery(sql);
	
			ResultSetTableModel model = new ResultSetTableModel(rsRM);
			
			JTable overzichtTable = new JTable(model);
	//		overzichtTable.setAutoCreateRowSorter(true);
			overzichtTable.setPreferredScrollableViewportSize(new Dimension(bannerOneWidth, 800));
			overzichtTable.setFillsViewportHeight(true);
			
			ColumnsAutoSizer as = new ColumnsAutoSizer();
			as.sizeColumnsToFit(overzichtTable);
			overzichtTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
			ListSelectionModel sm = overzichtTable.getSelectionModel();
			sm.addListSelectionListener(this);
			
			for( int i=0; i<overzichtTable.getColumnCount(); i++)
			{
//				String colName = overzichtTable.getColumnName(i);
//				if(colName.compareToIgnoreCase("Aantal")==0) continue;
				TableColumn theCol = overzichtTable.getColumnModel().getColumn(i);
				theCol.setCellRenderer(new WinstRenderer());
			}
			
			overzichtTable.setSelectionBackground(Color.LIGHT_GRAY);
			JScrollPane scrollPane = new JScrollPane(overzichtTable);
			scrollPane.setAutoscrolls(true);
			scrollPane.setMinimumSize(new Dimension(bannerOneWidth, 800));
			scrollPane.setPreferredSize(new Dimension(bannerOneWidth, 800));
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
			transactiesTable.setPreferredScrollableViewportSize(new Dimension(bannerTwoWidth, 200));
			transactiesTable.setFillsViewportHeight(true);
			transactiesTable.setSelectionBackground(Color.LIGHT_GRAY);
			transactiesTable.setEnabled(false);
			
			ColumnsAutoSizer as = new ColumnsAutoSizer();
			as.sizeColumnsToFit(transactiesTable);
			transactiesTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
			
			JScrollPane scrollPane = new JScrollPane(transactiesTable);
			scrollPane.setAutoscrolls(true);
			scrollPane.setPreferredSize(new Dimension(bannerTwoWidth, 200));
			return scrollPane;			
		}
		catch(SQLException e)
		{
			e.printStackTrace();
			return null;
		}

	}
	
	JScrollPane CreateEffectDividendenPane(String naam)
	{
		try
		{
			String sql = "select YEAR(d.Datum) as Jaar, sum(d.Divident) as Dividend, Sum(d.Bruto) as Bruto, Sum(d.Voorheffing) as Voorheffing, Sum(d.Netto) as Netto, '' as Rendement from Divident d, Effect e where e.Naam = '"+naam+"' and e.TickerId = d.TickerId group by YEAR(Datum) order by Jaar";
			Statement stmtRM = con.createStatement();
			ResultSet rsRM = stmtRM.executeQuery(sql);

			ResultSetTableModel model = new ResultSetTableModel(rsRM);
			
			Object[][] data =  new Object[model.getRowCount()][model.getColumnCount()];
			String[] columnNames = new String[model.getColumnCount()];
			String tickerId=null;
			if(naam != null) tickerId = eList.getEffectBijNaam(naam).getTickerId();

			for(int i=0; i<model.getColumnCount(); i++)
			{
				columnNames[i]=model.getColumnName(i);
			}
			for(int i=0; i< model.getRowCount(); i++)
			{
				for(int j=0; j<model.getColumnCount(); j++)
				{
					data[i][j]=model.getValueAt(i, j);
				}
				if(tickerId != null)
				{
					int year = (int) model.getValueAt(i, 0);
					LocalDate theDate = LocalDate.of(year, Month.DECEMBER, 31);
					BigDecimal investment = tList.getInvestmentByTickerAndDate(tickerId, java.sql.Date.valueOf(theDate));
					try
					{
						BigDecimal rendement = (BigDecimal) data[i][model.getColumnCount()-2];
						rendement = rendement.divide(investment, 4, BigDecimal.ROUND_HALF_UP);
						rendement = rendement.multiply(new BigDecimal(100));
						data[i][model.getColumnCount()-1] = rendement.setScale(2);
					}
					catch(ArithmeticException e)
					{
						data[i][model.getColumnCount()-1] = "-";
					}
				}
			}
			DataTableModel theModel = new DataTableModel(data,columnNames);
			effectDividendenTable = new JTable(theModel);
			effectDividendenTable.setPreferredScrollableViewportSize(new Dimension(bannerTwoWidth, 230));
			effectDividendenTable.setFillsViewportHeight(true);
			effectDividendenTable.setSelectionBackground(Color.LIGHT_GRAY);
			effectDividendenTable.setEnabled(false);
			
			ColumnsAutoSizer as = new ColumnsAutoSizer();
			as.sizeColumnsToFit(effectDividendenTable);
			effectDividendenTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
			
			JScrollPane scrollPane = new JScrollPane(effectDividendenTable);
			scrollPane.setAutoscrolls(true);
			scrollPane.setPreferredSize(new Dimension(bannerTwoWidth, 230));
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
		pane.setPreferredSize(new Dimension(bannerTwoWidth, 130));
		pane.setBackground(Color.LIGHT_GRAY);
		if(naam==null) return pane;
			
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.weightx = 0;
	
		try
		{
			Effect e = eList.getEffectBijNaam(naam);
			
			List<String> names = e.getFieldNames().subList(0, 6);
			List<Object> values = e.getFieldValues().subList(0, 6);
			
			String tickerId = "";
			Dimension cellDim = new Dimension(bannerTwoWidth/2,130/(names.size()+1));
			for(int j=0; j < names.size(); j++)
			{
				gbc.gridy = j;

				if(names.get(j).compareToIgnoreCase("TickerId")==0) { tickerId = values.get(j).toString(); }
				
				gbc.gridx = 0;
				JLabel l = new JLabel(names.get(j));
				l.setPreferredSize(cellDim);
				pane.add(l,gbc);
				
				gbc.gridx = 1;
				JLabel v = new JLabel();
				v.setPreferredSize(cellDim);
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
				l.setPreferredSize(cellDim);
				JLabel v = new JLabel();
				v.setPreferredSize(cellDim);
				BigDecimal gem = tList.getGemiddeldeAankoopKoers(tickerId);
				v.setText(String.format("%.2f",gem));
				gbc.gridy = gbc.gridy+1;
				gbc.gridx = 0;
				pane.add(l,gbc);
				gbc.gridx = 1;
				pane.add(v,gbc);					
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return pane;
	}
	
	private JPanel CreateAankooporderPanel()
	{
		JPanel pane =  new JPanel(new BorderLayout());
//		pane.setPreferredSize(new Dimension(bannerThreeWidth+bannerFourWidth,150));
		pane.setPreferredSize(new Dimension(bannerThreeWidth+bannerFourWidth,200));
//		pane.setBackground(Color.ORANGE);
		JLabel head = new JLabel("Aankoop orders");
		head.setSize(new Dimension(bannerThreeWidth+bannerFourWidth,10));
		head.setBackground(Color.LIGHT_GRAY);
		pane.add(head,BorderLayout.NORTH);
		
		try
		{
			String sql = "select `TickerId`, `Aantal`,round(`Aankoop koers`,2) AS `Aank. koers`,round(`Investering`,2) AS `Investering`,`Status`,Date(`Datum`) AS Datum from AankoopTrigger where Status = 'Geplaatst' or Status ='Uitgevoerd' order by Status;";
			Statement stmtRM = con.createStatement();
			ResultSet rsRM = stmtRM.executeQuery(sql);
	
			ResultSetTableModel model = new ResultSetTableModel(rsRM);
			
			JTable akoTable = new JTable(model);
			
			TableColumnModel colModel = akoTable.getColumnModel();
			for(int i=0; i<colModel.getColumnCount();i++)
			{
				TableColumn aColumn = colModel.getColumn(i);
				aColumn.setCellRenderer(new BigDecimalRenderer());
			}
			
			
//			akoTable.setPreferredScrollableViewportSize(new Dimension(bannerThreeWidth+bannerFourWidth, 140));
			akoTable.setPreferredScrollableViewportSize(new Dimension(bannerThreeWidth+bannerFourWidth, 190));
			akoTable.setFillsViewportHeight(true);
			akoTable.setSelectionBackground(Color.LIGHT_GRAY);
			akoTable.setEnabled(true);
			akoTable.setAutoCreateRowSorter(true);
			
			ColumnsAutoSizer as = new ColumnsAutoSizer();
			as.sizeColumnsToFit(akoTable);
//			akoTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
			
			JScrollPane scrollPane = new JScrollPane(akoTable);
			scrollPane.setAutoscrolls(true);
//			scrollPane.setPreferredSize(new Dimension(bannerThreeWidth+bannerFourWidth, 140));
			scrollPane.setPreferredSize(new Dimension(bannerThreeWidth+bannerFourWidth, 190));
			pane.add(scrollPane,BorderLayout.CENTER);
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		return pane;			
	}
	
	private JPanel CreateVerkooporderPanel()
	{
		JPanel pane =  new JPanel(new BorderLayout());
		pane.setPreferredSize(new Dimension(bannerTwoWidth,200));
//		pane.setBackground(Color.ORANGE);
		JLabel head = new JLabel("Verkoop orders");
		head.setSize(new Dimension(bannerTwoWidth,10));
		head.setBackground(Color.LIGHT_GRAY);
		pane.add(head,BorderLayout.NORTH);
		
		try
		{
			String sql = "select `TickerId`, `Aantal`,round(`Verkoop koers`,2) AS `Verk. koers`,round(`Omzet`,2) AS `Omzet`,`Status`,Date(`Datum`) AS Datum from VerkoopTrigger where Status = 'Geplaatst' or Status ='Uitgevoerd' order by Status;";
			Statement stmtRM = con.createStatement();
			ResultSet rsRM = stmtRM.executeQuery(sql);
	
			ResultSetTableModel model = new ResultSetTableModel(rsRM);
			
			JTable akoTable = new JTable(model);
			
			TableColumnModel colModel = akoTable.getColumnModel();
			for(int i=0; i<colModel.getColumnCount();i++)
			{
				TableColumn aColumn = colModel.getColumn(i);
				aColumn.setCellRenderer(new BigDecimalRenderer());
			}
			
			akoTable.setPreferredScrollableViewportSize(new Dimension(bannerTwoWidth, 190));
			akoTable.setFillsViewportHeight(true);
			akoTable.setSelectionBackground(Color.LIGHT_GRAY);
			akoTable.setEnabled(true);
			akoTable.setAutoCreateRowSorter(true);
			
			ColumnsAutoSizer as = new ColumnsAutoSizer();
			as.sizeColumnsToFit(akoTable);
//			akoTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
			
			JScrollPane scrollPane = new JScrollPane(akoTable);
			scrollPane.setAutoscrolls(true);
			scrollPane.setPreferredSize(new Dimension(bannerTwoWidth, 190));
			pane.add(scrollPane,BorderLayout.CENTER);
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		return pane;			
	}

	private JPanel CreateMeerwaardenPanel(int height)
	{
		JPanel panel =  new JPanel(new BorderLayout());
		panel.setPreferredSize(new Dimension(bannerFourWidth,height));
//		pane.setBackground(Color.ORANGE);
		JLabel head = new JLabel("Gerealiseerde Meerwaarden");
		head.setSize(new Dimension(bannerFourWidth,10));
//		head.setBackground(Color.LIGHT_GRAY);
//		head.setOpaque(true);
		panel.add(head,BorderLayout.NORTH);
		int tableHeight = height-10;
		try
		{
			MeerwaardenList ml = new MeerwaardenList(ds);
			JTable akoTable = ml.CreateTable();
			akoTable.setPreferredScrollableViewportSize(new Dimension(bannerTwoWidth, tableHeight));
			akoTable.setFillsViewportHeight(true);
			akoTable.setEnabled(false);
			
			ColumnsAutoSizer as = new ColumnsAutoSizer();
			as.sizeColumnsToFit(akoTable);
//			akoTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
			
			JScrollPane scrollPane = new JScrollPane(akoTable);
			scrollPane.setAutoscrolls(true);
			scrollPane.setPreferredSize(new Dimension(bannerTwoWidth, tableHeight));
			panel.add(scrollPane,BorderLayout.CENTER);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return panel;			
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
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.weightx = 0;
	//	gbc.ipadx = 5;
		doEffectTransactieDividend(effect,gbc);		
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

	void doEffectTransactieDividend(String effect, GridBagConstraints gbc)
	{
		if(ePane != null) remove(ePane);
		ePane = CreateEffectPane(effect);
		gbc.gridx = 24;
		gbc.gridwidth = 14;
		gbc.gridy = 7;
		gbc.gridheight = 13;
		add(ePane,gbc);
		
		if(transacties!=null) remove(transacties);
		if(effect==null)
		{
			transacties = CreateTransactiesPane("None");
		}
		else
		{
			transacties = CreateTransactiesPane(effect);
		}
		gbc.gridx = 24;
		gbc.gridy=20;
		gbc.gridwidth = 14;
//		gbc.gridheight = 12;
		gbc.gridheight = 20;
		add(transacties,gbc);
		
		if(effectDividenden!=null) remove(effectDividenden);
		effectDividenden = CreateEffectDividendenPane(effect);
		gbc.gridx = 24;
//		gbc.gridy = 32;
		gbc.gridy = 40;
		gbc.gridwidth = 14;
//		gbc.gridheight = 13;
		gbc.gridheight = 25;
		add(effectDividenden,gbc);
		return;
	}
}
