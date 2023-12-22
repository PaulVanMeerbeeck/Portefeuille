package portefeuille.tables;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.sql.DataSource;

public class TransactieList extends ArrayList<Transactie>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
//	private boolean scrolling;
	
	public TransactieList()
	{
		super();
	}

	public TransactieList(DataSource ds)
	{
		super();
		String sql = "select * from transactie order by Datum";
		makeList(ds,sql);
	}

	public TransactieList(DataSource ds, String sql)
	{
		super();
		makeList(ds,sql);
	}

	private void makeList(DataSource ds,String sql)
	{
		Connection con = null;
		Statement stmtRM = null;
		ResultSet rsRM = null;
		try 
		{
			con = ds.getConnection();
//			scrolling = con.getMetaData().supportsResultSetType(ResultSet.TYPE_SCROLL_SENSITIVE);
//			System.out.println("TYPE_SCROLL_SENSITIVE = "+scrolling);
			stmtRM = con.createStatement();
			rsRM = stmtRM.executeQuery(sql);
			while (rsRM.next()) 
			{
				String tickerId = rsRM.getString("Ticker");
				Date day = (Date) rsRM.getTimestamp("Datum");
				int count = rsRM.getInt("Aantal");
				BigDecimal price = rsRM.getBigDecimal("Prijs");
				BigDecimal makelaarsLoon = rsRM.getBigDecimal("Makelaarsloon");
				BigDecimal beursTax = rsRM.getBigDecimal("Beurstaks");
				Transactie aTransactie =  new Transactie(tickerId,day,count,price,makelaarsLoon,beursTax);
				this.add(aTransactie);
			}
			rsRM.close();
			if (rsRM != null)
				rsRM.close();
		}
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}

	public TransactieList(int initialCapacity)
	{
		super(initialCapacity);
	}

	public TransactieList(Collection<? extends Transactie> c)
	{
		super(c);
	}
	
	public Object[][] getTransactieTableData()
	{
		Object[][] result = new Object[this.size()][6];
		for(int i=0; i<this.size(); i++ )
		{
			ArrayList<Object> e = get(i).getFieldValues();
			for(int j=0; j< e.size(); j++)
			{
				result[i][j]=e.get(j);
			}
		}
		return result;
	}

	public void print()
	{
		System.out.format("Transactie lijst\n");
		for(Transactie t : this)
		{
			t.print();
		}
	}

	public BigDecimal getSharesPurchasedValue()
	{
		BigDecimal result = BigDecimal.ZERO;
		for(Transactie t : this)
		{
			if(t.getNumber() < 0) continue;
			BigDecimal count = new BigDecimal(t.getNumber());
			BigDecimal shareValue = t.getPrice().multiply(count);
			result=result.add(shareValue);
		}
		return result;		
	}

	public BigDecimal getSharesSoldValue()
	{
		BigDecimal result = BigDecimal.ZERO;
		for(Transactie t : this)
		{
			if(t.getNumber() > 0) continue;
			BigDecimal count = new BigDecimal(t.getNumber());
			BigDecimal shareValue = t.getPrice().multiply(count);
			result=result.subtract(shareValue);
		}
		return result;		
	}
	

	public BigDecimal getMakerlaarsAankoopCost()
	{
		BigDecimal result = BigDecimal.ZERO;
		for(Transactie t : this)
		{
			if(t.getNumber()<0) continue;
			result=result.add(t.getMakelaarsloon());
		}
		return result;		
	}

	public BigDecimal getBeursAankoopTaks()
	{
		BigDecimal result = BigDecimal.ZERO;
		for(Transactie t : this)
		{
			if(t.getNumber()<0) continue;
			result=result.add(t.getBeurstaks());
		}
		return result;		
	}

	public BigDecimal getGemiddeldeAankoopKoers(String tickerId)
	{
		BigDecimal result = BigDecimal.ZERO, aankoopWaarde = BigDecimal.ZERO;
		if(tickerId==null) return result;
		int aantal=0;
		for(Transactie t : this)
		{
			if(t.theTickerId.compareToIgnoreCase(tickerId)!=0 || t.getNumber() < 0) continue;
			aankoopWaarde=aankoopWaarde.add(t.getPurchaseAmount());
			aantal=aantal+t.getNumber();
		}
		if(aantal!=0)
		{
			result = aankoopWaarde.divide(new BigDecimal(aantal),2, RoundingMode.HALF_UP);
		}
		return result;
	}
	
	public BigDecimal getMakerlaarsVerkoopCost()
	{
		BigDecimal result = BigDecimal.ZERO;
		for(Transactie t : this)
		{
			if(t.getNumber()>0) continue;
			result=result.add(t.getMakelaarsloon());
		}
		return result;		
	}

	public BigDecimal getBeursVerkoopTaks()
	{
		BigDecimal result = BigDecimal.ZERO;
		for(Transactie t : this)
		{
			if(t.getNumber()>0) continue;
			result=result.add(t.getBeurstaks());
		}
		return result;		
	}

	public BigDecimal getGemiddeldeVerkoopKoers(String tickerId)
	{
		BigDecimal result = BigDecimal.ZERO, verkoopWaarde = BigDecimal.ZERO;
		if(tickerId==null) return result;
		int aantal=0;
		for(Transactie t : this)
		{
			if(t.theTickerId.compareToIgnoreCase(tickerId)!=0 || t.getNumber() > 0) continue;
			verkoopWaarde=verkoopWaarde.add(t.getSaleAmount());
			aantal=aantal+t.getNumber();
		}
		if(aantal!=0)
		{
			result = verkoopWaarde.divide(new BigDecimal(aantal),2, RoundingMode.HALF_UP);
		}
		return result;
	}
	
	public BigDecimal getInvestmentByTickerAndDate(String tickerId, Date aDate)
	{
		BigDecimal moneyInvested = BigDecimal.ZERO;
		int aantal=0;
		for(Transactie t : this)
		{
			if(t.getTickerId().compareToIgnoreCase(tickerId)!=0) continue;
			if(t.getDate().compareTo(aDate) > 0) continue;
			if(t.getNumber() > 0)
			{
				moneyInvested=moneyInvested.add(t.getPurchaseAmount());
				aantal=aantal+t.getNumber();
			}
			else
			{
				if(t.getNumber()==0) continue; // should not happen
				// handle sale
				BigDecimal gemiddeldePrijs = moneyInvested.divide(new BigDecimal(aantal), 2, RoundingMode.HALF_UP);
				aantal= aantal+t.getNumber();
				if(aantal != 0)
					moneyInvested = gemiddeldePrijs.multiply(new BigDecimal(aantal)); 
			}
		}
		return moneyInvested;		
	} 
} 
