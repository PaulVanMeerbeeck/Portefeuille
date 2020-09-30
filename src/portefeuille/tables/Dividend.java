package portefeuille.tables;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

public class Dividend
{
	private String theTickerId;
	private Date theDay;
	private BigDecimal theDividend;
	private int theAantal;
	private BigDecimal theBruto;
	private BigDecimal theVoorheffing;
	private BigDecimal theNetto;

	public Dividend(String aTickerId, Date aDay, BigDecimal aDividend, int aAantal, BigDecimal aBruto, BigDecimal aVoorheffing, BigDecimal aNetto )
	{
		setTickerId(aTickerId);
		setDay(aDay);
		setDividend(aDividend);
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

	BigDecimal getDividend()
	{
		return theDividend;
	}

	void setDividend(BigDecimal aDividend)
	{
		theDividend = aDividend;
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
		l.add("Dividend");
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
		l.add(getDividend());
		l.add(getAantal());
		l.add(getBruto());
		l.add(getVoorheffing());
		l.add(getNetto());
		return l;
	}
	
	public void print()
	{
		System.out.printf("%-6.6s  %tF  %05d\t%05.3f\t%08.2f\t%08.2f\t%08.2f\n",theTickerId,theDay,theAantal, theDividend, theBruto, theVoorheffing, theNetto );
	}

}
