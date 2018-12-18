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
		Connection con = null;
		Statement stmtRM = null;
		ResultSet rsRM = null;
		String sql = "select * from transactie order by Datum";
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
				Date day = rsRM.getDate("Datum");
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
		// TODO Auto-generated constructor stub
	}

	public TransactieList(Collection<? extends Transactie> c)
	{
		super(c);
		// TODO Auto-generated constructor stub
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

	public BigDecimal getSharesPurchaseValue()
	{
		BigDecimal result = BigDecimal.ZERO;
		for(Transactie t : this)
		{
			BigDecimal count = new BigDecimal(t.getNumber());
			BigDecimal shareValue = t.getPrice().multiply(count);
			result=result.add(shareValue);
		}
		return result;		
	}
	
	public BigDecimal getMakerlaarsCost()
	{
		BigDecimal result = BigDecimal.ZERO;
		for(Transactie t : this)
		{
			result=result.add(t.getMakelaarsloon());
		}
		return result;		
	}

	public BigDecimal getBeursTaks()
	{
		BigDecimal result = BigDecimal.ZERO;
		for(Transactie t : this)
		{
			result=result.add(t.getBeurstaks());
		}
		return result;		
	}

	public BigDecimal getGemiddleAankoopKoers(String tickerId)
	{
		BigDecimal result = BigDecimal.ZERO, aankoopWaarde = BigDecimal.ZERO;
		if(tickerId==null) return result;
		int aantal=0;
		for(Transactie t : this)
		{
			if(t.theTickerId.compareToIgnoreCase(tickerId)!=0) continue;
			aankoopWaarde=aankoopWaarde.add(t.getPrice().multiply(new BigDecimal(t.getNumber())));
			aankoopWaarde=aankoopWaarde.add(t.getMakelaarsloon());
			aankoopWaarde=aankoopWaarde.add(t.getBeurstaks());
			aantal=aantal+t.getNumber();
		}
		if(aantal!=0)
		{
			result = aankoopWaarde.divide(new BigDecimal(aantal),2, RoundingMode.HALF_UP);
		}
		return result;
	}
}
