package portefeuille.screens;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LogViewDialog extends JDialog
{

  private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(EffectenFrame.class.getName());

  public LogViewDialog(EffectenFrame theParent)
  {
    super();
    logger.traceEntry("LogViewDialog");
    setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);	
    JPanel buttonPanel = new JPanel();
    JButton wrapButton = new JButton("Wrap");
    wrapButton.setText(theParent.getSwingLogger().getLineWrap() ? "No Wrap" : "Wrap");
    buttonPanel.add(wrapButton);
    wrapButton.addActionListener(new
      ActionListener()
      {
        @Override
        public void actionPerformed(ActionEvent event)
        {  
            boolean wrap = !theParent.getSwingLogger().getLineWrap();
            theParent.getSwingLogger().setLineWrap(wrap);
            wrapButton.setText(wrap ? "No Wrap" : "Wrap");
        }
      }
    );
    JButton quitButton = new JButton("Quit");
    buttonPanel.add(quitButton);
    quitButton.addActionListener(new
      ActionListener()
      {
        @Override
        public void actionPerformed(ActionEvent event)
        {
          theParent.setSwingLoggerSize(getSize());
          dispose();
        }
      }
    );
    add(buttonPanel, BorderLayout.SOUTH);
    JScrollPane sPane = new JScrollPane(theParent.getSwingLogger());
    add(sPane);
    setSize(theParent.getSwingLoggerSize());
    logger.trace("set dialog minimum size to "+theParent.getSwingLoggerSize());
    setLocationRelativeTo(null);
    setVisible(true);
    setAlwaysOnTop(true);
    logger.traceExit("LogViewDialog");
  }

}
