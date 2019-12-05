package portefeuille.tables;

import java.math.BigDecimal;
import java.util.Date;

public class Meerwaarden
{
	String theTickerId;
	Date theDate;
	int theNumber;
	BigDecimal theMeerwaarde;

	public Meerwaarden(String aTickerId, Date aDate, int aNumber, BigDecimal aMeerwaarde)
	{
		setTickerId(aTickerId);
		setDate( aDate);
		setNumber(aNumber);
		setMeerwaarde(aMeerwaarde);
	}

	public void setTickerId(String aTickerId)
	{
		this.theTickerId = aTickerId;		
	}
	
	public void setDate(Date aDate)
	{
		this.theDate = aDate;		
	}

	public void setNumber(int aNumber)
	{
		this.theNumber = aNumber;
	}

	public void setMeerwaarde(BigDecimal aMeerwaarde)
	{
		this.theMeerwaarde = aMeerwaarde;		
	}
	public String getTickerId()
	{
		return this.theTickerId;		
	}
	
	public Date getDate()
	{
		return this.theDate;		
	}

	public int getNumber()
	{
		return this.theNumber;
	}

	public BigDecimal getMeerwaarde()
	{
		return this.theMeerwaarde;		
	}
	
	public void print()
	{
		System.out.printf("%-6.6s  %tF  %05d\t%08.3f\n",theTickerId,theDate,theNumber,theMeerwaarde);
	}

}
