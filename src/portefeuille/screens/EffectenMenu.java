package portefeuille.screens;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class EffectenMenu extends JMenuBar implements ActionListener
{
	private static final long serialVersionUID = 1L;
	
	JMenuBar menuBar = new JMenuBar();
	JDialog dEffect;
	JDialog dTransactie;
	JDialog dKalender;
	JDialog dDividend;
	JDialog dWK;
	JDialog dPreOct;
	JDialog dDivVU;
	EffectenFrame theEffectenFrame;

	public EffectenMenu(EffectenFrame theParent)
	{
		theEffectenFrame = theParent;
		JMenu menuTables = new JMenu("Tables");
		menuBar.add(menuTables);
		JMenuItem miEffect = new JMenuItem("Effect");
		miEffect.setActionCommand("Effect");
		miEffect.addActionListener(this);
		menuTables.add(miEffect);
		JMenuItem miTransactie = new JMenuItem("Transactie");
		miTransactie.setActionCommand("Transactie");
		miTransactie.addActionListener(this);
		menuTables.add(miTransactie);
		JMenuItem miKalender = new JMenuItem("Kalender");
		miKalender.setActionCommand("Kalender");
		miKalender.addActionListener(this);
		menuTables.add(miKalender);
		JMenuItem miDividend = new JMenuItem("Dividend");
		miDividend.setActionCommand("Dividend");
		miDividend.addActionListener(this);
		menuTables.add(miDividend);
		JMenuItem wisselkoersTrigger = new JMenuItem("Wisselkoersen");
		wisselkoersTrigger.setActionCommand("WK");
		wisselkoersTrigger.addActionListener(this);
		menuTables.add(wisselkoersTrigger);
		
		JMenu menuReports = new JMenu("Reports");
		menuBar.add(menuReports);
		JMenuItem miPreOct = new JMenuItem("Pré Oct18");
		miPreOct.setActionCommand("miPreOct");
		miPreOct.addActionListener(this);
		menuReports.add(miPreOct);
		JMenuItem miDivVU = new JMenuItem("Dividend vooruitzicht");
		miDivVU.setActionCommand("divVU");
		miDivVU.addActionListener(this);
		menuReports.add(miDivVU);
		
	}

	protected JMenuBar getMenuBar()
	{
		return menuBar;
	}

	void setMenuBar(JMenuBar menuBar)
	{
		this.menuBar = menuBar;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		String action = e.getActionCommand();
//		System.out.println("Menu action "+action);
		if(action.compareTo("Effect")==0)
		{
			if(dEffect!=null && dEffect.isDisplayable()) return;
			dEffect = new EffectenUpdateDialog(theEffectenFrame);
		}
		else if(action.compareTo("Transactie")==0)
		{
			if(dTransactie!=null && dTransactie.isDisplayable()) return;
			dTransactie = new TransactiesUpdateDialog(theEffectenFrame);
		}
		else if(action.compareTo("Kalender")==0)
		{
			if(dKalender!=null && dKalender.isDisplayable()) return;
			dKalender = new KalenderUpdateDialog(theEffectenFrame);
		}
		else if(action.compareTo("Dividend")==0)
		{
			if(dDividend!=null && dDividend.isDisplayable()) return;
			dDividend = new DividendenUpdateDialog(theEffectenFrame);
		}
		else if(action.compareTo("WK")==0)
		{
			if(dWK!=null && dWK.isDisplayable()) return;
			dWK = new WisselkoersUpdateDialog(theEffectenFrame);
		}
		else if(action.compareTo("miPreOct")==0)
		{
			if(dPreOct!=null && dPreOct.isDisplayable()) return;
			dPreOct = new PreOctDialog(theEffectenFrame);
		}
		else if(action.compareTo("divVU")==0)
		{
			if(dDivVU!=null && dDivVU.isDisplayable()) return;
			dDivVU = new DividendUitkeringDialog(theEffectenFrame);
		}
	}
}
