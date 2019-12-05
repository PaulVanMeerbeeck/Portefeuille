package portefeuille.tables;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.sql.DataSource;
import javax.swing.JComboBox;

public class EffectList extends ArrayList<Effect>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EffectList(DataSource ds)
	{
		super();
		if(ds==null) return;

		Connection con = null;
		Statement stmtRM = null;
		ResultSet rsRM = null;
		String sql = "select * from Effect order by Naam";
		try 
		{
			con = ds.getConnection();
			stmtRM = con.createStatement();
			rsRM = stmtRM.executeQuery(sql);
			while (rsRM.next()) 
			{
				String naam = rsRM.getString("Naam");
				String tickerId = rsRM.getString("TickerId");
				String isinCode = rsRM.getString("ISIN");
				String categorie = rsRM.getString("Categorie");
				String risico = rsRM.getString("Risico");
				BigDecimal koers = rsRM.getBigDecimal("Koers");
				BigDecimal divident = rsRM.getBigDecimal("Div");
				int aantalGekocht = rsRM.getInt("AantalGekocht");
				BigDecimal aankoopWaarde = rsRM.getBigDecimal("AankoopWaarde");
				BigDecimal aankoopKost = rsRM.getBigDecimal("AankoopKost");
				int aantalVerkocht = rsRM.getInt("AantalVerkocht");
				BigDecimal verkoopWaarde = rsRM.getBigDecimal("VerkoopWaarde");
				BigDecimal verkoopKost = rsRM.getBigDecimal("VerkoopKost");
				
				Effect aEffect =  new Effect(naam,tickerId,isinCode,categorie,risico,koers,divident);
				aEffect.setAantalGekocht(aantalGekocht);
				aEffect.setAankoopWaarde(aankoopWaarde);
				aEffect.setAankoopKost(aankoopKost);
				aEffect.setAantalVerkocht(aantalVerkocht);
				aEffect.setVerkoopWaarde(verkoopWaarde);
				aEffect.setVerkoopKost(verkoopKost);
				
				this.add(aEffect);
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
	
	public Effect getEffectBijTicker(String ticker)
	{
		for(Effect e :this)
		{
			if(e.getTickerId().compareToIgnoreCase(ticker)==0) return e;
		}
		return null;
	}

	public Effect getEffectBijNaam(String Naam)
	{
		for(Effect e :this)
		{
			if(e.getName().compareToIgnoreCase(Naam)==0) return e;
		}
		return null;
	}

	public Object[][] getEffectTableData()
	{
		Object[][] result = new Object[this.size()][13];
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
	
	public ArrayList<String> getTickerIdList()
	{
		ArrayList<String> theResult = new ArrayList<String>();
		for(Effect e : this)
		{
			theResult.add(e.getTickerId());
		}
		return theResult;
	}
	
	public JComboBox<String> getTickerIdComboBox()
	{
		JComboBox<String> theResult=new JComboBox<String>();
		for(Effect e : this)
		{
			theResult.addItem(e.getTickerId());
		}
		return theResult;
	}
	
	public void print()
	{
		System.out.format("Effecten lijst\n");
		for(Effect e : this)
		{
			e.print();
		}
	}

	public Object[][] readBoleroFile(String f)
	{
		Object[][] result = new Object[this.size()][2];
		int updateCount = 0;
		try
		{
			FileInputStream fstream = new FileInputStream(f);
			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
			
			String strLine;
			int koersIndex=8,isinIndex=14,typeIndex=0, muntIndex=1;
			//Read File Line By Line
			while ((strLine = br.readLine()) != null && updateCount<this.size())   
			{  // Print the content on the console
				String[] tokens=strLine.split(";;");
				if(tokens.length<14) continue;
				if(tokens[muntIndex].compareTo("EUR")!=0) continue;
				if(tokens[typeIndex].compareTo(";Aandeel")==0 || tokens[typeIndex].compareTo(";ETF")==0)
				{
					result[updateCount][0]=tokens[isinIndex];
					result[updateCount][1]=tokens[koersIndex].replaceFirst(",", ".");
					updateCount++;
				}
			}
			//Close the input stream
			br.close();		
		}
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
}
