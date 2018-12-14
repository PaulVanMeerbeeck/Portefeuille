package portefeuille.screens;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JDialog;

public class PasswordDialog extends JDialog
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	EffectenFrame theParent;

	public PasswordDialog(EffectenFrame aParent) 
	{
		super(aParent,"Portefeuille - Enter Password",true);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		theParent = aParent;
		
		PasswordPanel pane = new PasswordPanel(this);
	  pane.setOpaque(true); //content panes must be opaque
	  setContentPane(pane);
	  addWindowListener(new WindowAdapter() 
	  {
	  	public void windowActivated(WindowEvent e) 
	  	{
	  		pane.resetFocus();
      }
	  });
	  
	  pack();
	  setLocationRelativeTo(null);	
	  setVisible(true);
	}
	
	void setPasswordValid(boolean value)
	{
		theParent.setPasswordValid(value);
	}
}
