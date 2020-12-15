package portefeuille.tables;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

public class Transactie
{
	String theTickerId;
	Date theDate;
	int theNumber;
	BigDecimal thePrice;
	BigDecimal theMakelaarsloon;
	BigDecimal theBeurstaks;


	public Transactie(String aTickerId, Date aDate, int aNumber, BigDecimal aPrice, BigDecimal aMakelaarsloon, BigDecimal aBeurstaks)
	{
		setTickerId(aTickerId);
		setDate( aDate);
		setNumber(aNumber);
		setPrice(aPrice);
		setMakelaarsloon(aMakelaarsloon);
		setBeurstaks(aBeurstaks);
	}


	public String getTickerId()
	{
		return theTickerId;
	}


	public void setTickerId(String aTickerId)
	{
		this.theTickerId = aTickerId;
	}


	public Date getDate()
	{
		return theDate;
	}


	public void setDate(Date aDate)
	{
		this.theDate = aDate;
	}


	public int getNumber()
	{
		return theNumber;
	}


	public void setNumber(int aNumber)
	{
		this.theNumber = aNumber;
	}


	public BigDecimal getPrice()
	{
		return thePrice;
	}


	public void setPrice(BigDecimal aPrice)
	{
		this.thePrice = aPrice;
	}


	public BigDecimal getMakelaarsloon()
	{
		return theMakelaarsloon;
	}


	public void setMakelaarsloon(BigDecimal aMakelaarsloon)
	{
		this.theMakelaarsloon = aMakelaarsloon;
	}


	public BigDecimal getBeurstaks()
	{
		return theBeurstaks;
	}


	public void setBeurstaks(BigDecimal aBeurstaks)
	{
		this.theBeurstaks = aBeurstaks;
	}
	
	public ArrayList<String> getFieldNames()
	{
		ArrayList<String> l = new ArrayList<String>();
		l.add("Ticker");
		l.add("Datum");
		l.add("Aantal");
		l.add("Prijs");
		l.add("Makelaarsloon");
		l.add("Beurstaks");
		return l;
	}
	
	public ArrayList<Object> getFieldValues()
	{
		ArrayList<Object> l = new ArrayList<Object>();
		l.add(getTickerId());
		l.add(getDate());
		l.add(getNumber());
		l.add(getPrice());
		l.add(getMakelaarsloon());
		l.add(getBeurstaks());
		return l;
	}
	
	public BigDecimal getTransactionCost()
	{
		return this.theBeurstaks.add(this.theMakelaarsloon);
	}
	
	public BigDecimal getPurchaseAmount()
	{
		BigDecimal totalAmount = BigDecimal.ZERO;
		if(this.theNumber>0)
		{
			totalAmount=totalAmount.add(this.thePrice.multiply(new BigDecimal(this.theNumber)));
			totalAmount=totalAmount.add(this.getTransactionCost());
		}
		return totalAmount;
	}
	
	public BigDecimal getSaleAmount()
	{
		BigDecimal totalAmount = BigDecimal.ZERO;
		if(this.theNumber<0)
		{
			totalAmount=totalAmount.add(this.thePrice.multiply(new BigDecimal(this.theNumber*-1)));
			totalAmount=totalAmount.subtract(this.getTransactionCost());
		}
		return totalAmount;
	}
	
	public void print()
	{
		System.out.printf("%-6.6s  %tF  %05d\t%08.3f\t%05.2f\t%05.2f\n",theTickerId,theDate,theNumber, thePrice, theMakelaarsloon, theBeurstaks );
	}


}
