package portefeuille.util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRootPane;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
/*
import com.apple.eawt.AboutHandler;
import com.apple.eawt.AppEvent.AboutEvent;
import com.apple.eawt.AppEvent.PreferencesEvent;
import com.apple.eawt.AppEvent.QuitEvent;
import com.apple.eawt.PreferencesHandler;
import com.apple.eawt.QuitHandler;
import com.apple.eawt.QuitResponse;
*/

import java.awt.desktop.*;

public class MacOSXController implements AboutHandler, QuitHandler, PreferencesHandler
{
	JFrame theFrame;
	boolean aboutPanelDisplayed=false;
	JDialog aboutDialog;
	
	public MacOSXController(JFrame f)
	{
		theFrame = f;
	}
	@Override
	public void handleQuitRequestWith(QuitEvent e, QuitResponse r)
	{
//		System.out.println("handleQuitRequestWith - response = "+r.toString());
		if(JOptionPane.showConfirmDialog(theFrame, "Do you really want to quit?", "Portefeuille", JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE)==JOptionPane.YES_OPTION)
		{
			System.exit(0);
		}
		r.cancelQuit();
	}

	@Override
	public void handleAbout(AboutEvent e)
	{
		if(aboutPanelDisplayed)
		{
			aboutDialog.setVisible(true);
			return;
		}
		aboutDialog = new JDialog(theFrame);
		aboutDialog.setTitle("Portefeuille");
		aboutDialog.setUndecorated(true);
		aboutDialog.getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);
	//	aboutDialog.setLayout(new BorderLayout());
	 
		aboutDialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		JTextPane p = new JTextPane();
		p.setEditable(false);
		p.setText("Tata was here! ...\nTutu? ...");
		p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		p.addMouseListener(new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent e)
			{
				aboutDialog.setVisible(false);
				aboutPanelDisplayed=false;
			}

			@Override
			public void mousePressed(MouseEvent e)
			{
				// Auto-generated method stub
			}

			@Override
			public void mouseReleased(MouseEvent e)
			{
				// Auto-generated method stub
			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
				// Auto-generated method stub
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				// Auto-generated method stub
			}});
	
		aboutDialog.add(p, BorderLayout.CENTER);
		JLabel l = new JLabel("Copyright pvm ©2018");
		l.setHorizontalAlignment(SwingConstants.CENTER);
		l.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

	//	l.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 5));
		p.setBackground(l.getBackground());
		aboutDialog.add(l,BorderLayout.PAGE_END);
		aboutDialog.getRootPane().setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 5));
		aboutDialog.setSize(200,100);
		aboutDialog.setResizable(false);
		aboutDialog.setLocationRelativeTo(null); 
		aboutDialog.validate();
		aboutDialog.setModal(true);
		aboutDialog.addMouseListener(new MouseListener()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				// Auto-generated method stub
				// System.out.println("handleAbout - mouseClicked = "+e.toString());
			}

			@Override
			public void mousePressed(MouseEvent e)
			{
				// Auto-generated method stub
			}

			@Override
			public void mouseReleased(MouseEvent e)
			{
				e.getComponent().setVisible(false);				
			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
				// Auto-generated method stub
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				// Auto-generated method stub
			}
		});
		aboutDialog.addWindowListener(new WindowListener()
		{
			@Override
			public void windowOpened(WindowEvent e)
			{
				aboutPanelDisplayed=true;
			}

			@Override
			public void windowClosing(WindowEvent e)
			{
				aboutDialog.setVisible(false);
				aboutPanelDisplayed=false;
			}

			@Override
			public void windowClosed(WindowEvent e)
			{
				// Auto-generated method stub
			}

			@Override
			public void windowIconified(WindowEvent e)
			{
				// Auto-generated method stub
			}

			@Override
			public void windowDeiconified(WindowEvent e)
			{
				// Auto-generated method stub
			}

			@Override
			public void windowActivated(WindowEvent e)
			{
				// Auto-generated method stub
			}

			@Override
			public void windowDeactivated(WindowEvent e)
			{
				// Auto-generated method stub
			}
		});
		aboutDialog.setVisible(true);
//		System.out.println("handleAbout - AboutEvent = "+e.toString());
	}

	@Override
	public void handlePreferences(PreferencesEvent e)
	{
//		System.out.println("handlePreferences - PreferencesEvent = "+e.toString());
	}
}