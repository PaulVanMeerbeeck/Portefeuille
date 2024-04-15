package portefeuille.main;

import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

//import portefeuille.http.Client;
import portefeuille.screens.EffectenFrame;
import portefeuille.tables.DataSourceFactory;
import portefeuille.tables.DividendList;
import portefeuille.tables.Effect;
import portefeuille.tables.EffectList;
import portefeuille.tables.ToestandList;
import portefeuille.tables.Transactie;
import portefeuille.tables.TransactieList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import com.sun.tools.javac.Main;


public class Portefeuille
{
	private static final Logger logger = LogManager.getLogger(Portefeuille.class.getName());

	public Portefeuille()
	{
	  logger.traceEntry("Portefeuille");
		DataSource ds = DataSourceFactory.getInputDataSource("pvm");
		if(ds==null)
		{
			System.out.println("Niet gelukt!");
		}
		else
		{
			System.out.println("Het is OK!");
		}
		EffectList theEffectList = new EffectList(ds);
		TransactieList theTransactionList = new TransactieList(ds);
		ToestandList theToestandList = new ToestandList(ds);
		DividendList theDividendList = new DividendList(ds);
		BigDecimal sharesPurchasedValue = theTransactionList.getSharesPurchasedValue();
		BigDecimal makelaarsCost = theTransactionList.getMakerlaarsAankoopCost();
		BigDecimal beursTaks = theTransactionList.getBeursAankoopTaks();
		BigDecimal thePresentValue = theToestandList.getTotalPresentValueShares();
		BigDecimal winst = thePresentValue.subtract(sharesPurchasedValue).subtract(makelaarsCost).subtract(beursTaks);
		System.out.println("Aantal effecten = "+theEffectList.size());
		theEffectList.print();
		System.out.println("Aantal transacties = "+theTransactionList.size());
		theTransactionList.print();
		System.out.println("Aantal toestanden = "+theToestandList.size());
		theToestandList.print();
		System.out.printf("Winst (verlies) volgens toestand is: %(,.2f€\n", theToestandList.getTotalProfits());
		System.out.printf("Total share value at purchase price %,.2f€\n",sharesPurchasedValue);
		System.out.printf("Total Makerlaars cost %,.2f€\n",makelaarsCost);
		System.out.printf("Total Beurs taks %,.2f€\n",beursTaks);
		System.out.printf("Total investment is: %,.2f€\n", sharesPurchasedValue.add(makelaarsCost).add(beursTaks));
		System.out.printf("Huidige waarde is: %,.2f€\n", thePresentValue);
		System.out.printf("Winst (verlies) is: %(,.2f€\n", winst);
		System.out.println();
		Date compDate = new GregorianCalendar(2018,9,1).getTime();


		TransactieList selectedTransactions = new TransactieList(theTransactionList.size());

		for(Transactie t : theTransactionList)
		{
			if(t.getDate().compareTo(compDate) > 0 )
			{
				selectedTransactions.add(t);
			}
		}

		System.out.printf("Total share value purchased since 01/10/2018  %,.2f€\n",selectedTransactions.getSharesPurchasedValue());
		System.out.printf("Total Oct Makerlaars cost %,.2f€\n",selectedTransactions.getMakerlaarsAankoopCost());
		System.out.printf("Total Oct Beurs taks %,.2f€\n",selectedTransactions.getBeursAankoopTaks());
		BigDecimal octInvest = selectedTransactions.getSharesPurchasedValue().add(selectedTransactions.getMakerlaarsAankoopCost()).add(selectedTransactions.getBeursAankoopTaks());
		System.out.printf("Total Oct investment is: %,.2f€\n", octInvest);

		BigDecimal octValue = BigDecimal.ZERO;

		for(Transactie t : selectedTransactions)
		{
			Effect e = theEffectList.getEffectBijTicker(t.getTickerId());
			if(e==null) continue;
			BigDecimal shareValue = e.getKoers();
			BigDecimal count = new BigDecimal(t.getNumber());
			octValue=octValue.add(shareValue.multiply(count));
		}

		BigDecimal octWinst = octValue.subtract(octInvest);
		System.out.printf("Huidige Oct waarde is: %,.2f€\n", octValue);
		System.out.printf("Winst Oct (verlies) is: %(,.2f€\n", octWinst);
		System.out.println();

		selectedTransactions.clear();

		for(Transactie t : theTransactionList)
		{
			if(t.getDate().compareTo(compDate) < 0 )
			{
				selectedTransactions.add(t);
			}
		}

		System.out.printf("Total share value purchased before 01/10/2018  %,.2f€\n",selectedTransactions.getSharesPurchasedValue());
		System.out.printf("Total preOct2018 Makerlaars cost %,.2f€\n",selectedTransactions.getMakerlaarsAankoopCost());
		System.out.printf("Total preOct2018 Beurs taks %,.2f€\n",selectedTransactions.getBeursAankoopTaks());
		BigDecimal preOctInvest = selectedTransactions.getSharesPurchasedValue().add(selectedTransactions.getMakerlaarsAankoopCost()).add(selectedTransactions.getBeursAankoopTaks());
		System.out.printf("Total Oct investment is: %,.2f€\n", preOctInvest);

		BigDecimal preOctValue = BigDecimal.ZERO;

		for(Transactie t : selectedTransactions)
		{
			Effect e = theEffectList.getEffectBijTicker(t.getTickerId());
			if(e==null) continue;
			BigDecimal shareValue = e.getKoers();
			BigDecimal count = new BigDecimal(t.getNumber());
			preOctValue=preOctValue.add(shareValue.multiply(count));
		}

		BigDecimal preOctWinst = preOctValue.subtract(preOctInvest);
		System.out.printf("Huidige preOct2018 waarde is: %,.2f€\n", preOctValue);
		System.out.printf("Winst preOct2018 (verlies) is: %(,.2f€\n", preOctWinst);

		System.out.println();
		System.out.println("Aantal dividenden = "+theDividendList.size());
		theDividendList.print();

		System.out.println();
		Map<String, DividendList> divMap = theDividendList.getDividendMap();

		Set<String> keys= divMap.keySet();


		for(String k: keys)
		{
			DividendList aList = divMap.get(k);
			System.out.println("Dividend lijst voor: "+k);
			aList.printTickerYearTotals();
			System.out.println();
		}


		System.out.println("Gedaan!");
    logger.traceExit("Gedaan!");
	}

	public static void main(String[] args)
	{
		String osName = System.getProperty("os.name").toLowerCase();
		String resourceFileName = "log4j2_ux.xml";
		if(osName.startsWith("windows")) resourceFileName = "log4j2_dos.xml";
		String resourceFilePath = "Resource/"+resourceFileName;
//		System.out.println(resourceFilePath);
    URL configFile = Main.class.getClassLoader().getResource(resourceFilePath);
    if(configFile==null) 
    	System.out.println(resourceFileName+" NIET gevonden!");
    else
    {
    	try
     	{
    		Configurator.reconfigure(configFile.toURI());
 //       LoggerContext context = (LoggerContext)LogManager.getContext(false);
 //       context.setConfigLocation(configFile.toURI());
 //       context.reconfigure();     

				if(logger.isTraceEnabled())
					logger.traceEntry("Starting main ...");
				else
					System.out.println("Logger trace not enabled !!!");

				if(logger.isInfoEnabled())
					logger.info("Info is enabled");
				else
					System.out.println("Logger Info not enabled !!!");

				if(logger.isErrorEnabled())
					logger.error("Error is enabled");
				else
					System.out.println("Logger Error not enabled !!!");
			}
			catch (URISyntaxException e) 
			{
				System.out.println(e.getLocalizedMessage());
				e.printStackTrace();
			}
		}
    

/*	Client c = new Client();
		String tata= c.get();
		System.out.println(tata); 
		System.out.println("Start hier"); */
		try
		{
	//		new Portefeuille();
			try
			{
    		logger.trace("OS is: "+osName);
				if(osName.startsWith("mac os x"))
				{
					System.setProperty("apple.laf.useScreenMenuBar", "true");
//							UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
					System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Portefeuille");
				}
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			}
			catch(Exception evt)
			{
    		logger.error("Exception "+evt.getStackTrace());
			}
//					for(String s: args) { System.out.println("Argument = "+s);}
			logger.trace("We zijn al hier");
			
			SwingUtilities.invokeLater
			(
				new Runnable()
				{
					public void run()
					{
						URL imageURL = this.getClass().getClassLoader().getResource("Resource/Portefeuille.png");
						if(imageURL != null)
						{
							ImageIcon ic = new ImageIcon(imageURL,"Portefeuille");
							String arg1="pvm";
							if(args.length>1)
							{
								arg1 = args[1];
							}
							if(args.length>0)
							{
								logger.info("args[0] = "+args[0]);
								new EffectenFrame(args[0],arg1,ic);
							}
							else
							{
								new EffectenFrame("secure",arg1,ic);
							}
						}
						else
						{
							logger.error("Portefeuille.png niet gevonden!");
						}
					}
				}
			);
		}
		catch(Exception e)
		{
    	logger.error(e);
			e.printStackTrace();
		}
	  logger.traceExit("Ending main!");
	}
}
