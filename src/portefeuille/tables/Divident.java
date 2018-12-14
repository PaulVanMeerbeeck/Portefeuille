package portefeuille.tables;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

public class Divident
{
	private String theTickerId;
	private Date theDay;
	private BigDecimal theDivident;
	private int theAantal;
	private BigDecimal theBruto;
	private BigDecimal theVoorheffing;
	private BigDecimal theNetto;

	public Divident(String aTickerId, Date aDay, BigDecimal aDivident, int aAantal, BigDecimal aBruto, BigDecimal aVoorheffing, BigDecimal aNetto )
	{
		setTickerId(aTickerId);
		setDay(aDay);
		setDivident(aDivident);
		setAantal(aAantal);
		setBruto(aBruto);
		setVoorheffing(aVoorheffing);
		setNetto(aNetto);
	}

	String getTickerId()
	{
		return theTickerId;
	}

	void setTickerId(String aTickerId)
	{
		theTickerId = aTickerId;
	}

	Date getDay()
	{
		return theDay;
	}

	void setDay(Date aDay)
	{
		theDay = aDay;
	}

	BigDecimal getDivident()
	{
		return theDivident;
	}

	void setDivident(BigDecimal aDivident)
	{
		theDivident = aDivident;
	}

	int getAantal()
	{
		return theAantal;
	}

	void setAantal(int aAantal)
	{
		theAantal = aAantal;
	}

	BigDecimal getBruto()
	{
		return theBruto;
	}

	void setBruto(BigDecimal aBruto)
	{
		theBruto = aBruto;
	}

	BigDecimal getVoorheffing()
	{
		return theVoorheffing;
	}

	void setVoorheffing(BigDecimal aVoorheffing)
	{
		theVoorheffing = aVoorheffing;
	}

	BigDecimal getNetto()
	{
		return theNetto;
	}

	void setNetto(BigDecimal aNetto)
	{
		theNetto = aNetto;
	}
	
	public ArrayList<String> getFieldNames()
	{
		ArrayList<String> l = new ArrayList<String>();
		l.add("TickerId");
		l.add("Datum");
		l.add("Divident");
		l.add("Aantal");
		l.add("Bruto");
		l.add("Voorheffing");
		l.add("Netto");
		return l;
	}
	
	public ArrayList<Object> getFieldValues()
	{
		ArrayList<Object> l = new ArrayList<Object>();
		l.add(getTickerId());
		l.add(getDay());
		l.add(getDivident());
		l.add(getAantal());
		l.add(getBruto());
		l.add(getVoorheffing());
		l.add(getNetto());
		return l;
	}
	
	public void print()
	{
		System.out.printf("%-6.6s  %tF  %05d\t%05.3f\t%08.2f\t%08.2f\t%08.2f\n",theTickerId,theDay,theAantal, theDivident, theBruto, theVoorheffing, theNetto );
	}

}
