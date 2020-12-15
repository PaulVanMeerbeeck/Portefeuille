package portefeuille.tables;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import portefeuille.tables.Transactie;
import portefeuille.tables.TransactieList;

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
	int theAantalInBezit;
	BigDecimal theGemiddeldePrijs;
	BigDecimal theGerealiseerdeMeerwaarde;


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
		theAantalInBezit=0;
		theGemiddeldePrijs=BigDecimal.ZERO;
		theGerealiseerdeMeerwaarde=BigDecimal.ZERO;
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
	
	public int getAantalInBezit()
	{
		return this.theAantalInBezit;
	}
	
	public void setAantalInBezit(int aAantalInBezit)
	{
		this.theAantalInBezit = aAantalInBezit;
	}
	
	public BigDecimal getGemiddeldePrijs()
	{
		return this.theGemiddeldePrijs;
	}
	
	public void setGemiddeldePrijs(BigDecimal aGemiddeldePrijs)
	{
		this.theGemiddeldePrijs = aGemiddeldePrijs;
	}

	public BigDecimal getGerealiseerdeMeerwaarde()
	{
		return this.theGerealiseerdeMeerwaarde;
	}
	
	public void setGerealiseerdeMeerwaarde(BigDecimal aGerealiseerdeMeerwaarde)
	{
		this.theGerealiseerdeMeerwaarde = aGerealiseerdeMeerwaarde;
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
		l.add("AantalInBezit");
		l.add("GemiddeldePrijs");
		l.add("GerealiseerdeMeerwaarde");
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
		l.add(theAantalInBezit);
		l.add(theGemiddeldePrijs);
		l.add(theGerealiseerdeMeerwaarde);
		return l;
	}

	protected void ApplyTransactie(Transactie aTransactie)
	{
		if(aTransactie.getNumber()<0)
		{	// verkoop
			this.theAantalInBezit = this.theAantalInBezit+aTransactie.getNumber();
			this.theAantalVerkocht = this.theAantalVerkocht-aTransactie.getNumber();
			this.theGerealiseerdeMeerwaarde=this.theGerealiseerdeMeerwaarde.add(aTransactie.getSaleAmount());
			this.theGerealiseerdeMeerwaarde=this.theGerealiseerdeMeerwaarde.add(this.theGemiddeldePrijs.multiply(new BigDecimal(aTransactie.getNumber())));
			this.theVerkoopKost=this.theVerkoopKost.add(aTransactie.getTransactionCost());
			this.theVerkoopWaarde=this.theVerkoopWaarde.add(aTransactie.getSaleAmount());
		}
		else
		{	// aankoop
			BigDecimal totaalAankoopBedrag = aTransactie.getPurchaseAmount().add(this.theGemiddeldePrijs.multiply(new BigDecimal(theAantalInBezit)));
			this.theAantalInBezit = this.theAantalInBezit+aTransactie.getNumber();
			this.theGemiddeldePrijs = totaalAankoopBedrag.divide(new BigDecimal(theAantalInBezit),4, RoundingMode.HALF_UP);
			this.theAankoopKost = this.theAankoopKost.add(aTransactie.getTransactionCost());
			this.theAankoopWaarde=this.theAankoopWaarde.add(aTransactie.getPurchaseAmount());
			this.theAantalGekocht=this.theAantalGekocht+aTransactie.getNumber();
		}
		return;
	}
	
	protected String ApplyTransactieList(TransactieList aTransactieList)
	{
		int aantalGekocht = this.theAantalGekocht;
		int aantalVerkocht = this.theAantalVerkocht;
		int aantalInBezit = this.theAantalInBezit;
		BigDecimal aankoopWaarde = this.theAankoopWaarde;
		BigDecimal aankoopKost = this.theAankoopKost;
		BigDecimal verkoopWaarde = this.theVerkoopWaarde;
		BigDecimal verkoopKost = this.theVerkoopKost;
		BigDecimal gemiddeldePrijs = this.theGemiddeldePrijs;
		BigDecimal gerealiseerdeMeerwaarde = this.theGerealiseerdeMeerwaarde;
		
		this.theAantalGekocht=0;
		this.theAantalVerkocht=0;
		this.theAantalInBezit=0;
		this.theAankoopWaarde=BigDecimal.ZERO;
		this.theAankoopKost=BigDecimal.ZERO;
		this.theVerkoopWaarde=BigDecimal.ZERO;
		this.theVerkoopKost=BigDecimal.ZERO;
		this.theGemiddeldePrijs=BigDecimal.ZERO;
		this.theGerealiseerdeMeerwaarde=BigDecimal.ZERO;
		
		for(Transactie t:aTransactieList)
		{
			if(t.getTickerId().compareTo(theTickerId)!=0) continue;
			this.ApplyTransactie(t);
		}
		
		StringBuilder sb = new StringBuilder("UPDATE `Effect` SET ");
		
		boolean bUpdate = false;
		if(aantalGekocht != this.theAantalGekocht)
		{
			sb.append("`AantalGekocht` = '"+this.theAantalGekocht+"'");
			bUpdate = true;
		}
		if(aankoopWaarde.compareTo(this.theAankoopWaarde) != 0)
		{
			if(bUpdate) sb.append(", ");
			sb.append("`AankoopWaarde` = '"+this.theAankoopWaarde+"'");			
			bUpdate = true;
		}
		if(aankoopKost.compareTo(this.theAankoopKost) != 0)
		{
			if(bUpdate) sb.append(", ");
			sb.append("`AankoopKost` = '"+this.theAankoopKost+"'");			
			bUpdate = true;
		}
		if(aantalVerkocht != this.theAantalVerkocht)
		{
			if(bUpdate) sb.append(", ");
			sb.append("`AantalVerkocht` = '"+this.theAantalVerkocht+"'");
			bUpdate = true;
		}
		if(verkoopWaarde.compareTo(this.theVerkoopWaarde) != 0)
		{
			if(bUpdate) sb.append(", ");
			sb.append("`VerkoopWaarde` = '"+this.theVerkoopWaarde+"'");			
			bUpdate = true;
		}
		if(verkoopKost.compareTo(this.theVerkoopKost) != 0)
		{
			if(bUpdate) sb.append(", ");
			sb.append("`VerkoopKost` = '"+this.theVerkoopKost+"'");			
			bUpdate = true;
		}
		if(aantalInBezit != this.theAantalInBezit)
		{
			if(bUpdate) sb.append(", ");
			sb.append("`AantalInBezit` = '"+this.theAantalInBezit+"'");
			bUpdate = true;
		}
		if(gemiddeldePrijs.compareTo(this.theGemiddeldePrijs) != 0)
		{
			if(bUpdate) sb.append(", ");
			sb.append("`GemiddeldePrijs` = '"+this.theGemiddeldePrijs+"'");			
			bUpdate = true;
		}
		if(gerealiseerdeMeerwaarde.compareTo(this.theGerealiseerdeMeerwaarde) != 0)
		{
			if(bUpdate) sb.append(", ");
			sb.append("`GerealiseerdeMeerwaarde` = '"+this.theGerealiseerdeMeerwaarde+"'");			
			bUpdate = true;
		}
		if(bUpdate)
		{
			sb.append(" WHERE (`TickerId` = '"+this.theTickerId+"');");
			System.out.println(sb.toString());
			return sb.toString();
		}
		return "";
	}
	
	protected void print()
	{
		System.out.printf("%-20.20s  %-6.6s  %-12.12s  %s  %s  %07.3f\t%05.2f\n",theName,theTickerId,theIsinCode,theCategory, theRisc, theKoers, theDividend );
	}
}
