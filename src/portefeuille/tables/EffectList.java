package portefeuille.tables;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
	
	DataSource theDataSource;

	public EffectList(DataSource ds)
	{
		super();
		if(ds==null) return;
		theDataSource=ds;

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
				BigDecimal dividend = rsRM.getBigDecimal("Div");
				int aantalGekocht = rsRM.getInt("AantalGekocht");
				BigDecimal aankoopWaarde = rsRM.getBigDecimal("AankoopWaarde");
				BigDecimal aankoopKost = rsRM.getBigDecimal("AankoopKost");
				int aantalVerkocht = rsRM.getInt("AantalVerkocht");
				BigDecimal verkoopWaarde = rsRM.getBigDecimal("VerkoopWaarde");
				BigDecimal verkoopKost = rsRM.getBigDecimal("VerkoopKost");
				int aantalInBezit = rsRM.getInt("AantalInBezit");
				BigDecimal gemiddeldePrijs = rsRM.getBigDecimal("GemiddeldePrijs");
				BigDecimal gerealiseerdeMeerwaarde = rsRM.getBigDecimal("GerealiseerdeMeerwaarde");
				
				Effect aEffect =  new Effect(naam,tickerId,isinCode,categorie,risico,koers,dividend);
				aEffect.setAantalGekocht(aantalGekocht);
				aEffect.setAankoopWaarde(aankoopWaarde);
				aEffect.setAankoopKost(aankoopKost);
				aEffect.setAantalVerkocht(aantalVerkocht);
				aEffect.setVerkoopWaarde(verkoopWaarde);
				aEffect.setVerkoopKost(verkoopKost);
				aEffect.setAantalInBezit(aantalInBezit);
				aEffect.setGemiddeldePrijs(gemiddeldePrijs);
				aEffect.setGerealiseerdeMeerwaarde(gerealiseerdeMeerwaarde);
				
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
		Object[][] result = new Object[this.size()][16];
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
		WisselkoersList theWisselkoerList = new WisselkoersList(theDataSource);
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
//				if(tokens[muntIndex].compareTo("EUR")!=0) continue;
				if(tokens[typeIndex].compareTo(";Aandeel")==0 || tokens[typeIndex].compareTo(";ETF")==0 || tokens[typeIndex].compareTo(";Fonds")==0)
				{
					result[updateCount][0]=tokens[isinIndex];
					result[updateCount][1]=new BigDecimal(tokens[koersIndex].replaceFirst(",", "."));
					if(tokens[muntIndex].compareTo("EUR")!=0)
					{
						BigDecimal koers=theWisselkoerList.getWisselkoers(tokens[muntIndex], "EURO");
						if(koers!=null && koers.compareTo(BigDecimal.ZERO)!=0)
						{
							BigDecimal koersEffect=(BigDecimal)result[updateCount][1];
							result[updateCount][1]=koersEffect.multiply(koers).setScale(4,RoundingMode.HALF_UP);
							System.out.println("Wisselkoers van "+tokens[muntIndex]+" naar EURO = "+koers+". Koers effect = "+result[updateCount][1]);
							updateCount++;
						}
						else
						{
							System.out.println("Wisselkoers van "+tokens[muntIndex]+" naar EURO niet gevonden!");
						}
					}
					else
					{
						updateCount++;
					}
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

	public void ApplyTransactieList(TransactieList l)
	{

		String[] theResult = new String[this.size()];
		int index=0;
		for(Effect e :this)
		{
			String s= e.ApplyTransactieList(l);
			if(s.isEmpty()) continue;
			theResult[index]=s;
			index++;
		}
		if(index==0) return;
		Connection con=null;
		try
		{
			con = theDataSource.getConnection();
			for(int i=0; i<index; i++)
			{
				Statement stmt = con.createStatement();
				stmt.execute(theResult[i]);
				stmt.close();
				if(!con.getAutoCommit()) con.commit();
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			try
			{
				if(con!=null && !con.getAutoCommit()) con.rollback();
//				con.close();
			}
			catch (SQLException e1)
			{
				// e1.printStackTrace();
			}
		}
		return;
	}
}
