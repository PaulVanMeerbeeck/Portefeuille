package portefeuille.tables;

import java.math.BigDecimal;

public class Toestand
{
	
	private String theName;
	private int theNumber;
	private BigDecimal thePurchaseValue;
	private BigDecimal theCost;
	private BigDecimal thePrice;
	private BigDecimal thePresentValue;
	private BigDecimal theProfit;

	public Toestand(String aName, int aNumber, BigDecimal aPurchaseValue, BigDecimal aCost, BigDecimal aPrice, BigDecimal aPresentValue, BigDecimal aProfit)
	{
		setName(aName);
		setNumber(aNumber);
		setThePurchaseValue(aPurchaseValue);
		setTheCost(aCost);
		setThePrice(aPrice);
		setThePresentValue(aPresentValue);
		setTheProfit(aProfit);
	}

	public String getName()
	{
		return theName;
	}

	public void setName(String aName)
	{
		this.theName = aName;
	}

	public int getNumber()
	{
		return theNumber;
	}

	public void setNumber(int aNumber)
	{
		this.theNumber = aNumber;
	}

	public BigDecimal getThePurchaseValue()
	{
		return thePurchaseValue;
	}

	public void setThePurchaseValue(BigDecimal aPurchaseValue)
	{
		this.thePurchaseValue = aPurchaseValue;
	}

	public BigDecimal getTheCost()
	{
		return theCost;
	}

	public void setTheCost(BigDecimal aCost)
	{
		this.theCost = aCost;
	}

	public BigDecimal getThePrice()
	{
		return thePrice;
	}

	public void setThePrice(BigDecimal aPrice)
	{
		this.thePrice = aPrice;
	}

	public BigDecimal getThePresentValue()
	{
		return thePresentValue;
	}

	public void setThePresentValue(BigDecimal aPresentValue)
	{
		this.thePresentValue = aPresentValue;
	}

	public BigDecimal getTheProfit()
	{
		return theProfit;
	}

	public void setTheProfit(BigDecimal aProfit)
	{
		this.theProfit = aProfit;
	}

	public void print()
	{
		System.out.printf("%-20.20s %05d %08.3f %08.2f %06.2f  %08.2f  %09.2f\n",
				getName(), getNumber(), getThePrice(), getThePurchaseValue(), getTheCost(), getThePresentValue(), getTheProfit());
	}
}
