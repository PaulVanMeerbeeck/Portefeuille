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
	BigDecimal theDivident;

	public Effect(String aName, String aTickerId, String aIsinCode, String aCategory, String aRisc, BigDecimal aKoers, BigDecimal aDivident)
	{
		theName=aName;
		theTickerId=aTickerId;
		theIsinCode=aIsinCode;
		theCategory=aCategory;
		theRisc=aRisc;
		theKoers=aKoers;
		theDivident=aDivident;
		return;		
	}
	
	public String getName()
	{
		return theName;
	}

	public void setName(String aName)
	{
		this.theName = aName;
	}

	public String getTickerId()
	{
		return theTickerId;
	}

	public void setTickerId(String aTickerId)
	{
		this.theTickerId = aTickerId;
	}

	public String getIsinCode()
	{
		return theIsinCode;
	}

	public void setIsinCode(String aIsinCode)
	{
		this.theIsinCode = aIsinCode;
	}

	public String getCategory()
	{
		return theCategory;
	}

	public void setCategory(String aCategory)
	{
		this.theCategory = aCategory;
	}

	public String getRisc()
	{
		return theRisc;
	}

	public void setRisc(String aRisc)
	{
		this.theRisc = aRisc;
	}

	public BigDecimal getKoers()
	{
		return theKoers;
	}

	public void setKoers(BigDecimal aKoers)
	{
		this.theKoers = aKoers;
	}

	public BigDecimal getDivident()
	{
		return theDivident;
	}

	public void setDivident(BigDecimal aDivident)
	{
		this.theDivident = aDivident;
	}

	public ArrayList<String> getFieldNames()
	{
		ArrayList<String> l = new ArrayList<String>();
		l.add("Naam");
		l.add("TickerId");
		l.add("IsinCode");
		l.add("Category");
		l.add("Risc");
		l.add("Koers");
		l.add("Divident");
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
		l.add(theDivident);
		return l;
	}
	
	protected void print()
	{
		System.out.printf("%-20.20s  %-6.6s  %-12.12s  %s  %s  %07.3f\t%05.2f\n",theName,theTickerId,theIsinCode,theCategory, theRisc, theKoers, theDivident );
	}
}
