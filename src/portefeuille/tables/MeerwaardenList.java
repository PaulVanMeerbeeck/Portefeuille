package portefeuille.tables;

import java.awt.Color;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
//import portefeuille.tables.TransactieList;
import java.util.GregorianCalendar;

import javax.sql.DataSource;
import javax.swing.JTable;

import portefeuille.util.ColumnsAutoSizer;
import portefeuille.util.DataTableModel;

import portefeuille.tables.EffectList;

public class MeerwaardenList extends ArrayList<Meerwaarden>
{
	private static final long serialVersionUID = 1L;
	
	public MeerwaardenList()
	{
		super();
	}

	public MeerwaardenList(DataSource ds)
	{
		super();
		String sql = "select * from transactie order by Ticker,Datum";
		TransactieList transacties = new TransactieList(ds,sql);
		BigDecimal totalPrice = BigDecimal.ZERO;
		int countShares = 0;
		String tickerId = "";
		for(Transactie t: transacties)
		{
			if(t.getNumber() <0)
			{	// handle sale
				// calculate average purchase price
				int countSold = t.getNumber()*-1;
				BigDecimal averagePrice = totalPrice.divide(new BigDecimal(countShares),4, RoundingMode.HALF_UP);
				// calculate gross revenue
				BigDecimal grossRevenue = t.getPrice().multiply(new BigDecimal(countSold));
				// calculate net revenue
				BigDecimal netRevenue = grossRevenue.subtract(t.getBeurstaks());
				netRevenue = netRevenue.subtract(t.theMakelaarsloon);
				// calculate purchase value of shares sold
				BigDecimal purchaseValue = averagePrice.multiply(new BigDecimal(countSold));
				// calculate meerwaarde
				BigDecimal meerwaarde = netRevenue.subtract(purchaseValue);
				Meerwaarden aMeerwaarden = new Meerwaarden(tickerId,t.getDate(),countSold,meerwaarde);
				this.add(aMeerwaarden);
				// new shares count
				countShares = countShares+t.getNumber();
				// new shares values
				totalPrice = averagePrice.multiply(new BigDecimal(countShares));				
			}
			else
			{
				if(t.getTickerId().compareToIgnoreCase(tickerId) == 0)
				{	// calculate price and count shares
					totalPrice=totalPrice.add(t.getPrice().multiply(new BigDecimal(t.getNumber())));
					totalPrice=totalPrice.add(t.getMakelaarsloon());
					totalPrice=totalPrice.add(t.getBeurstaks());
					countShares=countShares+t.getNumber();
				}
				else
				{	//
					tickerId=t.getTickerId();
					totalPrice=t.getPrice().multiply(new BigDecimal(t.getNumber()));
					totalPrice=totalPrice.add(t.getMakelaarsloon());
					totalPrice=totalPrice.add(t.getBeurstaks());
					countShares=t.getNumber();
				}
			}
		}
	}

	public MeerwaardenList(EffectList anEffectList)
	{
		for(Effect e:anEffectList)
		{
			if(e.getAantalVerkocht()<1) continue;
			Meerwaarden aMeerwaarden = new Meerwaarden(e.getTickerId(),null,e.getAantalVerkocht(),e.getGerealiseerdeMeerwaarde());
			this.add(aMeerwaarden);
		}
		return;
	}
	
	public void print()
	{
		System.out.format("Meerwaarden lijst\n");
		for(Meerwaarden m : this)
		{
			m.print();
		}
	}
	
	public JTable CreateTable()
	{
		String[] columnNames = {"Ticker","Jaar", "Meerwaarde"};
		Object[][] data = new Object[this.size()+1][3];
		GregorianCalendar calendar = new GregorianCalendar();
		int rowIndex=0;
		BigDecimal totaleMw = BigDecimal.ZERO;
		BigDecimal mw = BigDecimal.ZERO;
		
		for(Meerwaarden m : this)
		{
			if(m.getDate()!=null)
			{
				calendar.setTime(m.getDate());
			}
			int mwYear = calendar.get(Calendar.YEAR);
			data[rowIndex][0] = m.getTickerId();
			data[rowIndex][1] = mwYear;
			mw = m.getMeerwaarde().setScale(2, RoundingMode.HALF_UP);
			data[rowIndex][2] = mw;
			totaleMw = totaleMw.add(mw);
			rowIndex++;
		}
		data[rowIndex][0] = "Totaal";
		data[rowIndex][2] = totaleMw;
		
		DataTableModel model = new DataTableModel(data,columnNames);
		JTable table = new JTable(model);
//		table.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		ColumnsAutoSizer as = new ColumnsAutoSizer();
		as.sizeColumnsToFit(table);
		table.setSelectionBackground(Color.LIGHT_GRAY);
//		table.setDefaultRenderer(Object.class,new PortefeuilleTableCellRenderer());
		table.setRowSelectionInterval(model.getRowCount()-1,model.getRowCount()-1);
		
		return table;
	}


}
