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
import portefeuille.tables.Effect;
import portefeuille.tables.EffectList;
import portefeuille.tables.Transactie;
import portefeuille.tables.TransactieList;
import portefeuille.util.ColumnsAutoSizer;
import portefeuille.util.DataTableModel;

public class PreOctDialog extends JDialog
{
	private static final long serialVersionUID = 1L;
	
	EffectList theEList;
	TransactieList theTList;
	EffectenFrame theEFrame;
	
	final Dimension dim = new Dimension(1162,225);
	
	DataSource ds;
	Connection con;

	public PreOctDialog(EffectenFrame theParent)
	{
		super(theParent,"Rapport - Pré Oct 2018");
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
	}
	
	JTable CreateTable()
	{
		String[] columnNames = {"Omschrijving","Pre Oct 18", "Oct 18", "Nov 18", "Dec 18", "Jan 19", "Feb 19", "Mar 19", "Apr 19", "May 19", "Jun 19", "Jul 19", "Aug 19", "Sep 19", "Oct 19", "Nov 19"};
		Object[][] data = {
				{ "Total share value purchased",BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO},
				{ "Total makerlaars cost",BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO},
				{ "Total beurs taks",BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO},
				{ "Total investment",BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO},
				{ " "," "," "," "," "," "," "," "," "," "," "," "," "," "," "," "},
				{ "Present value of investement",BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO},
				{ "Present profit/loss €",BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO},
				{ "Present profit/loss %",BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO}
		};
		
		int columnIndex = 1;
		Date fromDate = new GregorianCalendar(2008,1,1).getTime();
		Date toDate = new GregorianCalendar(2018,9,1).getTime();
		setTotals(fromDate,toDate,data,columnIndex);
		
		columnIndex = 2;
		fromDate = new GregorianCalendar(2018,9,1).getTime();
		toDate = new GregorianCalendar(2018,10,1).getTime();
		setTotals(fromDate,toDate,data,columnIndex);
		
		columnIndex = 3;
		fromDate = new GregorianCalendar(2018,10,1).getTime();
		toDate = new GregorianCalendar(2018,11,1).getTime();
		setTotals(fromDate,toDate,data,columnIndex);
		
		columnIndex = 4;
		fromDate = new GregorianCalendar(2018,11,1).getTime();
		toDate = new GregorianCalendar(2019,0,1).getTime();
		setTotals(fromDate,toDate,data,columnIndex);
		
		columnIndex = 5;
		fromDate = new GregorianCalendar(2019,0,1).getTime();
		toDate = new GregorianCalendar(2019,1,1).getTime();
		setTotals(fromDate,toDate,data,columnIndex);
		
		columnIndex = 6;
		fromDate = new GregorianCalendar(2019,1,1).getTime();
		toDate = new GregorianCalendar(2019,2,1).getTime();
		setTotals(fromDate,toDate,data,columnIndex);
		
		columnIndex = 7;
		fromDate = new GregorianCalendar(2019,2,1).getTime();
		toDate = new GregorianCalendar(2019,3,1).getTime();
		setTotals(fromDate,toDate,data,columnIndex);
		
		columnIndex = 8;
		fromDate = new GregorianCalendar(2019,3,1).getTime();
		toDate = new GregorianCalendar(2019,4,1).getTime();
		setTotals(fromDate,toDate,data,columnIndex);
		
		columnIndex = 9;
		fromDate = new GregorianCalendar(2019,4,1).getTime();
		toDate = new GregorianCalendar(2019,5,1).getTime();
		setTotals(fromDate,toDate,data,columnIndex);
		
		columnIndex = 10;
		fromDate = new GregorianCalendar(2019,5,1).getTime();
		toDate = new GregorianCalendar(2019,6,1).getTime();
		setTotals(fromDate,toDate,data,columnIndex);
		
		columnIndex = 11;
		fromDate = new GregorianCalendar(2019,6,1).getTime();
		toDate = new GregorianCalendar(2019,7,1).getTime();
		setTotals(fromDate,toDate,data,columnIndex);
		
		columnIndex = 12;
		fromDate = new GregorianCalendar(2019,7,1).getTime();
		toDate = new GregorianCalendar(2019,8,1).getTime();
		setTotals(fromDate,toDate,data,columnIndex);
		
		columnIndex = 13;
		fromDate = new GregorianCalendar(2019,8,1).getTime();
		toDate = new GregorianCalendar(2019,9,1).getTime();
		setTotals(fromDate,toDate,data,columnIndex);
		
		columnIndex = 14;
		fromDate = new GregorianCalendar(2019,9,1).getTime();
		toDate = new GregorianCalendar(2019,10,1).getTime();
		setTotals(fromDate,toDate,data,columnIndex);
		
		columnIndex = 15;
		fromDate = new GregorianCalendar(2019,10,1).getTime();
		toDate = new GregorianCalendar(2019,12,1).getTime();
		setTotals(fromDate,toDate,data,columnIndex);
		
		DataTableModel model = new DataTableModel(data,columnNames);
		JTable table = new JTable(model);
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
			if(t.getDate().compareTo(toDate) < 0 && t.getDate().compareTo(fromDate) >= 0 && t.getNumber() > 0) 
			{
				selectedTransactions.add(t);
			}
		}
		BigDecimal invest = selectedTransactions.getSharesPurchasedValue().add(selectedTransactions.getMakerlaarsAankoopCost()).add(selectedTransactions.getBeursAankoopTaks());
		BigDecimal presentValue = BigDecimal.ZERO;

		for(Transactie t : selectedTransactions)
		{
			Effect e = theEList.getEffectBijTicker(t.getTickerId());
			if(e==null) continue;
			BigDecimal shareValue = e.getKoers();
			BigDecimal count = new BigDecimal(t.getNumber());
			if(count==BigDecimal.ZERO) continue;
			presentValue=presentValue.add(shareValue.multiply(count));
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
		data[0][colIdx] = selectedTransactions.getSharesPurchasedValue().setScale(2, BigDecimal.ROUND_HALF_DOWN); //  .setScale(2, BigDecimal.ROUND_HALF_DOWN);
		data[1][colIdx] = selectedTransactions.getMakerlaarsAankoopCost().setScale(2, BigDecimal.ROUND_HALF_DOWN);
		data[2][colIdx] = selectedTransactions.getBeursAankoopTaks().setScale(2, BigDecimal.ROUND_HALF_DOWN);
		data[3][colIdx] = invest.setScale(2, BigDecimal.ROUND_HALF_DOWN);
		data[5][colIdx] = presentValue.setScale(2, BigDecimal.ROUND_HALF_DOWN);
		data[6][colIdx] = profit.setScale(2, BigDecimal.ROUND_HALF_DOWN);
		data[7][colIdx] = profitPercentage.setScale(2, BigDecimal.ROUND_HALF_DOWN);
		return;
	}

}
