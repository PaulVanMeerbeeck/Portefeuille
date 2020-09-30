package portefeuille.tables;

import java.math.BigDecimal;
import java.util.ArrayList;

public class Effect
{
	String theName;
	String theTickerId;
	String theIsinCode;
	String theCategory;
	String theRisc;
	BigDecimal theKoers;
	BigDecimal theDividend;
	int theAantalGekocht;
	BigDecimal theAankoopWaarde;
	BigDecimal theAankoopKost;
	int theAantalVerkocht;
	BigDecimal theVerkoopWaarde;
	BigDecimal theVerkoopKost;


	public Effect(String aName, String aTickerId, String aIsinCode, String aCategory, String aRisc, BigDecimal aKoers, BigDecimal aDividend)
	{
		theName=aName;
		theTickerId=aTickerId;
		theIsinCode=aIsinCode;
		theCategory=aCategory;
		theRisc=aRisc;
		theKoers=aKoers;
		theDividend=aDividend;
		theAantalGekocht=0;
		theAantalVerkocht=0;
		theAankoopWaarde=BigDecimal.ZERO;
		theAankoopKost=BigDecimal.ZERO;
		theVerkoopWaarde=BigDecimal.ZERO;
		theVerkoopKost=BigDecimal.ZERO;
		return;		
	}
	
	public String getName()
	{
		return this.theName;
	}

	public void setName(String aName)
	{
		this.theName = aName;
	}

	public String getTickerId()
	{
		return this.theTickerId;
	}

	public void setTickerId(String aTickerId)
	{
		this.theTickerId = aTickerId;
	}

	public String getIsinCode()
	{
		return this.theIsinCode;
	}

	public void setIsinCode(String aIsinCode)
	{
		this.theIsinCode = aIsinCode;
	}

	public String getCategory()
	{
		return this.theCategory;
	}

	public void setCategory(String aCategory)
	{
		this.theCategory = aCategory;
	}

	public String getRisc()
	{
		return this.theRisc;
	}

	public void setRisc(String aRisc)
	{
		this.theRisc = aRisc;
	}

	public BigDecimal getKoers()
	{
		return this.theKoers;
	}

	public void setKoers(BigDecimal aKoers)
	{
		this.theKoers = aKoers;
	}

	public BigDecimal getDividend()
	{
		return this.theDividend;
	}

	public void setDividend(BigDecimal aDividend)
	{
		this.theDividend = aDividend;
	}

	public int getAantalGekocht()
	{
		return this.theAantalGekocht;
	}

	public void setAantalGekocht(int aAantalGekocht)
	{
		this.theAantalGekocht = aAantalGekocht;
	}

	public BigDecimal getAankoopWaarde()
	{
		return this.theAankoopWaarde;
	}

	public void setAankoopWaarde(BigDecimal aAankoopWaarde)
	{
		this.theAankoopWaarde = aAankoopWaarde;
	}

	public BigDecimal getAankoopKost()
	{
		return this.theAankoopKost;
	}

	public void setAankoopKost(BigDecimal aAankoopKost)
	{
		this.theAankoopKost = aAankoopKost;
	}
	
	public int getAantalVerkocht()
	{
		return this.theAantalVerkocht;
	}
	
	public void setAantalVerkocht(int aAantalVerkocht)
	{
		this.theAantalVerkocht = aAantalVerkocht;
	}
	
	public BigDecimal getVerkoopWaarde()
	{
		return this.theVerkoopWaarde;
	}
	
	public void setVerkoopWaarde(BigDecimal aVerkoopWaarde)
	{
		this.theVerkoopWaarde = aVerkoopWaarde;
	}

	public BigDecimal getVerkoopKost()
	{
		return this.theVerkoopKost;
	}
	
	public void setVerkoopKost(BigDecimal aVerkoopKost)
	{
		this.theVerkoopKost = aVerkoopKost;
	}

	public ArrayList<String> getFieldNames()
	{
		ArrayList<String> l = new ArrayList<String>();
		l.add("Naam");
		l.add("TickerId");
		l.add("ISIN");
		l.add("Categorie");
		l.add("Risico");
		l.add("Koers");
		l.add("Div");
		l.add("AantalGekocht");
		l.add("AankoopWaarde");
		l.add("AankoopKost");
		l.add("AantalVerkocht");
		l.add("VerkoopWaarde");
		l.add("VerkoopKost");
		return l;
	}
	
	public ArrayList<Object> getFieldValues()
	{
		ArrayList<Object> l = new ArrayList<Object>();
		l.add(theName);
		l.add(theTickerId);
		l.add(theIsinCode);
		l.add(theCategory);
		l.add(theRisc);
		l.add(theKoers);
		l.add(theDividend);
		l.add(theAantalGekocht);
		l.add(theAankoopWaarde);
		l.add(theAankoopKost);
		l.add(theAantalVerkocht);
		l.add(theVerkoopWaarde);
		l.add(theVerkoopKost);
		return l;
	}
	
	protected void print()
	{
		System.out.printf("%-20.20s  %-6.6s  %-12.12s  %s  %s  %07.3f\t%05.2f\n",theName,theTickerId,theIsinCode,theCategory, theRisc, theKoers, theDividend );
	}
}
