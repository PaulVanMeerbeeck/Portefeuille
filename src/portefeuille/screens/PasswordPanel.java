package portefeuille.screens;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

public class PasswordPanel extends JPanel implements ActionListener
{
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static String OK = "ok";
  private static String QUIT = "quit";

  private PasswordDialog controllingFrame; //needed for dialogs
  private JPasswordField passwordField;
  
  private int maxAttempts = 3;
  private int attempts = 0;

  public PasswordPanel(PasswordDialog f) 
  {	//Use the default FlowLayout.
		controllingFrame = f;
		
		//Create everything.
		passwordField = new JPasswordField(10);
		passwordField.setActionCommand(OK);
		passwordField.addActionListener(this);
		
		JLabel label = new JLabel("Enter the password: ");
		label.setLabelFor(passwordField);
		
		JComponent buttonPane = createButtonPanel();
		
		//Lay out everything.
		JPanel textPane = new JPanel(new FlowLayout(FlowLayout.TRAILING));
		textPane.add(label);
		textPane.add(passwordField);
		
		add(textPane);
		add(buttonPane);
  }

  protected JComponent createButtonPanel() 
  {
	  JPanel p = new JPanel(new GridLayout(0,1));
	  JButton okButton = new JButton("OK");
	  JButton helpButton = new JButton("Quit");
	
	  okButton.setActionCommand(OK);
	  helpButton.setActionCommand(QUIT);
	  okButton.addActionListener(this);
	  helpButton.addActionListener(this);
	
	  p.add(okButton);
	  p.add(helpButton);
	
	  return p;
  }


	@Override
	public void actionPerformed(ActionEvent e)
	{
    String cmd = e.getActionCommand();
    
    if (OK.equals(cmd)) 
    { //Process the password.
      char[] input = passwordField.getPassword();
      if (isPasswordCorrect(input)) 
      {
/*            JOptionPane.showMessageDialog(controllingFrame,
                "Success! You typed the right password.");*/
          controllingFrame.setPasswordValid(true); 
          controllingFrame.dispose();
      } 
      else 
      {
      	attempts++;
        JOptionPane.showMessageDialog(controllingFrame,
            "Invalid password. Try again. "+(maxAttempts-attempts)+" attempts remaining.",
            "Error Message",
            JOptionPane.ERROR_MESSAGE);
        if(maxAttempts-attempts < 1) 
        {
        	doQuit();
        }
      }
      //Zero out the possible password, for security.
      Arrays.fill(input, '0');
      passwordField.selectAll();
      resetFocus();
    } 
    else 
    {
    	doQuit();
    }
  }
  /**
   * Checks the passed-in array against the correct password.
   * After this method returns, you should invoke eraseArray
   * on the passed-in array.
   */
  private  boolean isPasswordCorrect(char[] input) 
  {
    boolean isCorrect = true;
    char[] correctPassword = { 'p', 'v', 'm'};

    if (input.length != correctPassword.length) 
    {
        isCorrect = false;
    } 
    else 
    {
        isCorrect = Arrays.equals (input, correctPassword);
    }
    //Zero out the password.
    Arrays.fill(correctPassword,'0');

    return isCorrect;
  }
  
  //Must be called from the event dispatch thread.
  protected void resetFocus() 
  {
    passwordField.requestFocusInWindow();
  }
  
  protected void doQuit()
  {
  	controllingFrame.setVisible(false);
  	JOptionPane.showMessageDialog(null,"See you ...\n");
  	controllingFrame.dispose();
 }
}
