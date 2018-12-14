package portefeuille.screens;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class EffectenMenu extends JMenuBar implements ActionListener
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	JMenuBar menuBar = new JMenuBar();
	JDialog dEffect;
	JDialog dTransactie;
	JDialog dKalender;
	JDialog dDivident;
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
		JMenuItem miDivident = new JMenuItem("Divident");
		miDivident.setActionCommand("Divident");
		miDivident.addActionListener(this);
		menuTables.add(miDivident);
		
		JMenu menuReports = new JMenu("Reports");
		menuBar.add(menuReports);
		JMenuItem miPreOct = new JMenuItem("Pré Oct18");
		miPreOct.setActionCommand("miPreOct");
		miPreOct.addActionListener(this);
		menuReports.add(miPreOct);
		JMenuItem miDivVU = new JMenuItem("Divident vooruitzicht");
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
		else if(action.compareTo("Divident")==0)
		{
			if(dDivident!=null && dDivident.isDisplayable()) return;
			dDivident = new DividentenUpdateDialog(theEffectenFrame);
		}
		else if(action.compareTo("miPreOct")==0)
		{
			if(dPreOct!=null && dPreOct.isDisplayable()) return;
			dPreOct = new PreOctDialog(theEffectenFrame);
		}
		else if(action.compareTo("divVU")==0)
		{
			if(dDivVU!=null && dDivVU.isDisplayable()) return;
			dDivVU = new DividentUitkeringDiallog(theEffectenFrame);
		}
	}
}
