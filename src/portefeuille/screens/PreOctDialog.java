package portefeuille.screens;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.sql.DataSource;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import portefeuille.tables.Effect;
import portefeuille.tables.EffectList;
import portefeuille.tables.Transactie;
import portefeuille.tables.TransactieList;
import portefeuille.util.BigDecimalRenderer;
import portefeuille.util.ColumnsAutoSizer;
import portefeuille.util.DataTableModel;

public class PreOctDialog extends JDialog
{
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(EffectenFrame.class.getName());
	
	EffectList theEList;
	TransactieList theTList;
	EffectenFrame theEFrame;
	
	final Dimension dim = new Dimension(1350,250); //225);
	
	DataSource ds;
	Connection con;

	public PreOctDialog(EffectenFrame theParent)
	{
		super(theParent,"Rapport - Pré Oct 2018");
		logger.traceEntry("PreOctDialog");
		theEFrame = theParent;
		theEList = theParent.getEList();
		theTList = theParent.getTList();
		ds = theParent.getDs();
		con = theParent.getCon();
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
		JTable totalsTable = CreateTable();
		totalsTable.setGridColor(Color.LIGHT_GRAY);
		totalsTable.setShowVerticalLines(true);
		totalsTable.setShowHorizontalLines(false);
		JPanel tablePane = new JPanel(new BorderLayout());
//		tablePane.setSize(400,410);
		tablePane.setBorder(BorderFactory.createEmptyBorder(5, 5, 0,5));
		tablePane.add(totalsTable.getTableHeader(), BorderLayout.NORTH);
		tablePane.add(totalsTable,BorderLayout.CENTER);
    this.add(tablePane, BorderLayout.CENTER);
  	JPanel buttonPane  = new JPanel();
  	buttonPane.setBorder(BorderFactory.createEmptyBorder(5, 0, 0,0));
  	
    JButton button = new JButton("Ok");
    button.setMaximumSize(new Dimension(70, 25));
    button.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
						setVisible(false);
						dispose(); 
			}});
    buttonPane.add(button,BorderLayout.EAST);
    this.add(buttonPane,BorderLayout.PAGE_END);    
		setSize(dim);
		setMinimumSize(dim);
		setMaximumSize(dim);
		setLocationRelativeTo(null);

		setVisible(true);
		logger.traceExit("PreOctDialog");
	}
	
	JTable CreateTable()
	{
		String[] columnNames = {"Omschrijving","Pre Oct 18", "Oct 18", "Nov 18", "Dec 18", "2019 Q1", "2019 Q2", "2019 Q3", "2019 Q4", "2020 Q1", "2020 Q2", "2020 Q3", "2020 Q4", "2021 Q1", "2021 Q2", "2021 Q3", "Post Sep 18", "Totaal"};
		Object[][] data = {
				{ "Total share value purchased",BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO},
				{ "Total makerlaars cost",BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO},
				{ "Total beurs taks",BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO},
				{ "Total investment",BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO},
				{ " "," "," "," "," "," "," "," "," "," "," "," "," "," "," "," "," "," "},
				{ "Present value of investement",BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO},
				{ "Present profit/loss €",BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO},
				{ "Present profit/loss %",BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO},
				{ " "," "," "," "," "," "," "," "," "," "," "," "," "," "," "," "," "," "},
				{ "Sold value",BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO}
		};
		
		int columnIndex = 1; //Pre Oct 18
		Date fromDate = new GregorianCalendar(2008,1,1).getTime();
		Date toDate = new GregorianCalendar(2018,9,1).getTime();
		setTotals(fromDate,toDate,data,columnIndex);
		
		columnIndex = 2; //Oct 18
		fromDate = new GregorianCalendar(2018,9,1).getTime();
		toDate = new GregorianCalendar(2018,10,1).getTime();
		setTotals(fromDate,toDate,data,columnIndex);
		
		columnIndex = 3; //Nov 18
		fromDate = new GregorianCalendar(2018,10,1).getTime();
		toDate = new GregorianCalendar(2018,11,1).getTime();
		setTotals(fromDate,toDate,data,columnIndex);
		
		columnIndex = 4; //Dec 18
		fromDate = new GregorianCalendar(2018,11,1).getTime();
		toDate = new GregorianCalendar(2019,0,1).getTime();
		setTotals(fromDate,toDate,data,columnIndex);
		
		columnIndex = 5; // 2019Q1
		fromDate = new GregorianCalendar(2019,0,1).getTime();
		toDate = new GregorianCalendar(2019,3,1).getTime();
		setTotals(fromDate,toDate,data,columnIndex);
		
		columnIndex = 6; // 2019Q2
		fromDate = new GregorianCalendar(2019,3,1).getTime();
		toDate = new GregorianCalendar(2019,6,1).getTime();
		setTotals(fromDate,toDate,data,columnIndex);
		
		columnIndex = 7; // 2019Q3
		fromDate = new GregorianCalendar(2019,6,1).getTime();
		toDate = new GregorianCalendar(2019,9,1).getTime();
		setTotals(fromDate,toDate,data,columnIndex);
		
		columnIndex = 8; // 2019Q4
		fromDate = new GregorianCalendar(2019,9,1).getTime();
		toDate = new GregorianCalendar(2020,0,1).getTime();
		setTotals(fromDate,toDate,data,columnIndex);
		
		columnIndex = 9; // 2020Q1
		fromDate = new GregorianCalendar(2020,0,1).getTime();
		toDate = new GregorianCalendar(2020,3,1).getTime();
		setTotals(fromDate,toDate,data,columnIndex);
		
		columnIndex = 10; // 2020Q2
		fromDate = new GregorianCalendar(2020,3,1).getTime();
		toDate = new GregorianCalendar(2020,6,1).getTime();
		setTotals(fromDate,toDate,data,columnIndex);
		
		columnIndex = 11; // 2020Q3
		fromDate = new GregorianCalendar(2020,6,1).getTime();
		toDate = new GregorianCalendar(2020,9,1).getTime();
		setTotals(fromDate,toDate,data,columnIndex);
		
		columnIndex = 12; // 2020Q4
		fromDate = new GregorianCalendar(2020,9,1).getTime();
		toDate = new GregorianCalendar(2021,0,1).getTime();
		setTotals(fromDate,toDate,data,columnIndex);
		
		columnIndex = 13; // 2021Q1
		fromDate = new GregorianCalendar(2021,0,1).getTime();
		toDate = new GregorianCalendar(2021,3,1).getTime();
		setTotals(fromDate,toDate,data,columnIndex);
		
		columnIndex = 14; // 2021Q2
		fromDate = new GregorianCalendar(2021,3,1).getTime();
		toDate = new GregorianCalendar(2021,6,1).getTime();
		setTotals(fromDate,toDate,data,columnIndex);
		
		columnIndex = 15; // 2021Q3
		fromDate = new GregorianCalendar(2021,6,1).getTime();
		toDate = new GregorianCalendar(2021,9,1).getTime();
		setTotals(fromDate,toDate,data,columnIndex);
		
		columnIndex = 16; // post Sep 2018
		fromDate = new GregorianCalendar(2018,9,1).getTime();
		toDate = new GregorianCalendar(2022,0,1).getTime();
		setTotals(fromDate,toDate,data,columnIndex);
		
		columnIndex = 17; // Totaal
		fromDate = new GregorianCalendar(2008,1,1).getTime();
		toDate = new GregorianCalendar(2022,3,1).getTime();
		setTotals(fromDate,toDate,data,columnIndex);
		
		DataTableModel model = new DataTableModel(data,columnNames);
		JTable table = new JTable(model);
		TableColumnModel colModel = table.getColumnModel();
		for(int i=0; i<colModel.getColumnCount();i++)
		{
			TableColumn aColumn = colModel.getColumn(i);
			aColumn.setCellRenderer(new BigDecimalRenderer());
		}
		//		table.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		ColumnsAutoSizer as = new ColumnsAutoSizer();
		as.sizeColumnsToFit(table);
		table.setSelectionBackground(Color.LIGHT_GRAY);
//		table.setDefaultRenderer(Object.class,new PortefeuilleTableCellRenderer());
		
		return table;
	}
	
	void setTotals(Date fromDate, Date toDate, Object[][] data, int colIdx)
	{
		TransactieList selectedTransactions = new TransactieList(theTList.size());

		for(Transactie t : theTList)
		{
			Effect e = theEList.getEffectBijTicker(t.getTickerId());
			if(e.getAantalGekocht() == e.getAantalVerkocht()) continue;
//			if(t.getDate().compareTo(toDate) < 0 && t.getDate().compareTo(fromDate) >= 0 && t.getNumber() > 0) 
			if(t.getDate().compareTo(toDate) < 0 && t.getDate().compareTo(fromDate) >= 0) 
			{
				selectedTransactions.add(t);
			}
		}
		BigDecimal invest = selectedTransactions.getSharesPurchasedValue().add(selectedTransactions.getMakerlaarsAankoopCost()).add(selectedTransactions.getBeursAankoopTaks());
		BigDecimal presentValue = BigDecimal.ZERO;
		BigDecimal soldValue = selectedTransactions.getSharesSoldValue().subtract(selectedTransactions.getMakerlaarsVerkoopCost().add(selectedTransactions.getBeursVerkoopTaks()));

		for(Transactie t : selectedTransactions)
		{
			BigDecimal count = new BigDecimal(t.getNumber());
			if(count==BigDecimal.ZERO) continue;
			Effect e = theEList.getEffectBijTicker(t.getTickerId());
			if(e==null) continue;
			if(t.getNumber() > 0)
			{
				BigDecimal shareValue = e.getKoers();
				presentValue=presentValue.add(shareValue.multiply(count));
			}
			else
				presentValue=presentValue.add(t.getPrice().multiply(count));
		}

		BigDecimal profit = presentValue.subtract(invest);
		BigDecimal profitPercentage = BigDecimal.ZERO;
		if(invest!=BigDecimal.ZERO)
		{
			profitPercentage = presentValue.divide(invest,4,RoundingMode.CEILING);
		}
		if(profitPercentage != BigDecimal.ZERO)
		{
			profitPercentage=profitPercentage.subtract(BigDecimal.ONE);
			profitPercentage=profitPercentage.multiply(new BigDecimal(100));
		}
		data[0][colIdx] = selectedTransactions.getSharesPurchasedValue().setScale(2, RoundingMode.HALF_DOWN); //  .setScale(2, BigDecimal.ROUND_HALF_DOWN);
		data[1][colIdx] = selectedTransactions.getMakerlaarsAankoopCost().setScale(2, RoundingMode.HALF_DOWN);
		data[2][colIdx] = selectedTransactions.getBeursAankoopTaks().setScale(2, RoundingMode.HALF_DOWN);
		data[3][colIdx] = invest.setScale(2, RoundingMode.HALF_DOWN);
		data[5][colIdx] = presentValue.setScale(2, RoundingMode.HALF_DOWN);
		data[6][colIdx] = profit.setScale(2, RoundingMode.HALF_DOWN);
		data[7][colIdx] = profitPercentage.setScale(2, RoundingMode.HALF_DOWN);
		data[9][colIdx] = soldValue.setScale(2, RoundingMode.HALF_DOWN);
		return;
	}

}
