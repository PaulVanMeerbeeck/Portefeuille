package portefeuille.tables;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.sql.DataSource;

public class ToestandList extends ArrayList<Toestand> 
{
	private static final long serialVersionUID = 1L;
	
//	private boolean scrolling = false;

	private BigDecimal totalPresentValueShares = BigDecimal.ZERO;
	private BigDecimal totalPurchaseValueShares = BigDecimal.ZERO;
	private BigDecimal totalCosts = BigDecimal.ZERO;
	private BigDecimal totalProfits = BigDecimal.ZERO;

	public ToestandList(DataSource ds)
	{
		super();
		if(ds==null) return;

		Connection con = null;
		Statement stmtRM = null;
		ResultSet rsRM = null;
		String sql = "select * from toestand";

		try 
		{
			con = ds.getConnection();
//			scrolling = con.getMetaData().supportsResultSetType(ResultSet.TYPE_SCROLL_INSENSITIVE);
//			System.out.println("TYPE_SCROLL_INSENSITIVE = "+scrolling);
			stmtRM = con.createStatement();
			rsRM = stmtRM.executeQuery(sql);
			while (rsRM.next()) 
			{
				String name = rsRM.getString("Naam");
				BigDecimal aantal = rsRM.getBigDecimal("Aantal");
				BigDecimal sharesPurchaseValue = rsRM.getBigDecimal("Aankoop");
				totalPurchaseValueShares=totalPurchaseValueShares.add(sharesPurchaseValue);
				BigDecimal aveargeSharePurchaseValue=BigDecimal.ZERO;
				if(aantal.compareTo(BigDecimal.ZERO)>0)aveargeSharePurchaseValue = sharesPurchaseValue.divide(aantal,2, RoundingMode.HALF_UP);
				BigDecimal presentSharesValue = rsRM.getBigDecimal("Waarde");
//				System.out.println("aantal = "+aantal+ " sharesPurchaseValue = "+sharesPurchaseValue+" aveargeSharePurchaseValue = "+aveargeSharePurchaseValue);
				totalPresentValueShares=totalPresentValueShares.add(presentSharesValue);
				BigDecimal cost = rsRM.getBigDecimal("AankoopKosten");
				totalCosts=totalCosts.add(cost);
				cost = rsRM.getBigDecimal("VerkoopKosten");
				totalCosts=totalCosts.add(cost);
				BigDecimal profit = rsRM.getBigDecimal("Winst");
				totalProfits=totalProfits.add(profit);
				Toestand aToestand = new Toestand(name, aantal.intValue(), sharesPurchaseValue,cost,aveargeSharePurchaseValue,presentSharesValue, profit);
				this.add(aToestand);
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
	
	public BigDecimal getTotalPurchaseValueShares()
	{
		return totalPurchaseValueShares;
	}

	public BigDecimal getTotalPresentValueShares()
	{
		return totalPresentValueShares;
	}

	public BigDecimal getTotalCosts()
	{
		return totalCosts;
	}

	public BigDecimal getTotalProfits()
	{
		return totalProfits;
	}

	public void print()
	{
		for(Toestand t : this)
		{
			t.print();
		}		
	}
}