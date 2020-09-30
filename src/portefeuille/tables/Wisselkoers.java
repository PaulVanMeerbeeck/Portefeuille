package portefeuille.tables;

import java.math.BigDecimal;
import java.util.ArrayList;

public class Wisselkoers
{
	String van;
	String naar;
	BigDecimal koers;
	
	public Wisselkoers(String aMuntVan, String aMuntNaar, BigDecimal aKoers)
	{
		setVan(aMuntVan);
		setNaar(aMuntNaar);
		setKoers(aKoers);
	}

	public void setVan(String aMuntVan)
	{
		this.van=aMuntVan;
	}
	public void setNaar(String aMuntNaar)
	{
		this.naar=aMuntNaar;		
	}
	public void setKoers(BigDecimal aKoers)
	{
		this.koers=aKoers;
	}
	
	public String getVan()
	{
		return this.van;
	}
	public String getNaar()
	{
		return this.naar;
	}
	public BigDecimal getKoers()
	{
		return this.koers;
	}

	public ArrayList<String> getFieldNames()
	{
		ArrayList<String> l = new ArrayList<String>();
		l.add("van");
		l.add("naar");
		l.add("koers");
		return l;
	}

	public ArrayList<Object> getFieldValues()
	{
		ArrayList<Object> l = new ArrayList<Object>();
		l.add(getVan());
		l.add(getNaar());
		l.add(getKoers());
		return l;
	}
	


}
