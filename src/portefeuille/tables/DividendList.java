package portefeuille.tables;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TreeMap;

import javax.sql.DataSource;

public class DividendList extends ArrayList<Dividend>
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public DividendList()
	{
		super();
	} 

	public DividendList(DataSource ds)
	{
		super();
		Connection con = null;
		Statement stmtRM = null;
		ResultSet rsRM = null;
		String sql = "select * from Dividend order by Datum";
		try 
		{
			con = ds.getConnection();
			stmtRM = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
			rsRM = stmtRM.executeQuery(sql);
			while (rsRM.next()) 
			{
				String tickerId = rsRM.getString("TickerId");
				Date day = rsRM.getDate("Datum");
				int count = rsRM.getInt("Aantal");
				BigDecimal dividend = rsRM.getBigDecimal("Dividend");
				BigDecimal bruto = rsRM.getBigDecimal("Bruto");
				BigDecimal voorheffing = rsRM.getBigDecimal("Voorheffing");
				BigDecimal netto = rsRM.getBigDecimal("Netto");
				Dividend aDividend =  new Dividend(tickerId,day,dividend,count,bruto,voorheffing,netto);
				this.add(aDividend);
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

	public DividendList(int initialCapacity)
	{
		super(initialCapacity);
	}

	public DividendList(Collection<? extends Dividend> c)
	{
		super(c);
	}
	
	public void print()
	{
		System.out.format("Dividend lijst\n");
		for(Dividend t : this)
		{
			t.print();
		}
	}
	
	public void printTickerYearTotals()
	{
		String ticker="";
		GregorianCalendar calendar = new GregorianCalendar();
		BigDecimal div = BigDecimal.ZERO;
		BigDecimal bruto = BigDecimal.ZERO;
		BigDecimal voorheffing = BigDecimal.ZERO;
		BigDecimal netto = BigDecimal.ZERO;
		for(Dividend t : this)
		{
			ticker = t.getTickerId();
			calendar.setTime(t.getDay());
			div = div.add(t.getDividend());
			bruto = bruto.add(t.getBruto());
			voorheffing = voorheffing.add(t.getVoorheffing());
			netto = netto.add(t.getNetto());
			t.print();
		}
		int divYear = calendar.get(Calendar.YEAR);
		System.out.println("        ----                    -----   --------       ---------        --------");
		System.out.printf("%-6.6s  %4d            \t%05.3f\t%08.2f\t%08.2f\t%08.2f\n", ticker, divYear, div, bruto, voorheffing, netto );

	}

	public TreeMap<String, DividendList> getDividendMap()
	{
		TreeMap<String, DividendList> theMap = new TreeMap<String, DividendList>();
		GregorianCalendar calendar = new GregorianCalendar();
		for(Dividend t : this)
		{
			calendar.setTime(t.getDay());
			int divYear = calendar.get(Calendar.YEAR);
			String key = String.format("%s_%4d", t.getTickerId(),divYear);
//			System.out.println("key = "+key);
			DividendList aDividendList = theMap.get(key);
			if(aDividendList==null)
			{
				aDividendList=new DividendList();
				aDividendList.add(t);
				theMap.put(key, aDividendList);
			}
			else
			{
				aDividendList.add(t);
			}
		}
		return theMap;		
	}

	public Object[][] getDividendTableData()
	{
		Object[][] result = new Object[this.size()][7];
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


	/*
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
*/
}
