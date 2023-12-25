package portefeuille.tables;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.sql.DataSource;

public class WisselkoersList extends ArrayList<Wisselkoers>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public WisselkoersList()
	{
		super();
	}

	public WisselkoersList(DataSource ds)
	{
		super();
		String sql = "select * from Wisselkoers order by van,naar";
		makeList(ds,sql);
	}

	private void makeList(DataSource ds, String sql)
	{
		Connection con = null;
		Statement stmtRM = null;
		ResultSet rsRM = null;
		try 
		{
			con = ds.getConnection();
			stmtRM = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
			rsRM = stmtRM.executeQuery(sql);
			while (rsRM.next()) 
			{
				String muntVan = rsRM.getString("van");
				String muntNaar = rsRM.getString("naar");
				BigDecimal koers = rsRM.getBigDecimal("koers");
				Wisselkoers aWisselkoers =  new Wisselkoers(muntVan,muntNaar,koers);
				this.add(aWisselkoers);
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
	
	public BigDecimal getWisselkoers(String muntVan, String muntNaar)
	{
		for(Wisselkoers w:this)
		{
			if(w.getVan().compareTo(muntVan)==0 && w.getNaar().compareTo(muntNaar)==0) return w.getKoers();
		}
		return null;
	}

	public Object[][] getWisselkoersTableData()
	{
		Object[][] result = new Object[this.size()][3];
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


}
