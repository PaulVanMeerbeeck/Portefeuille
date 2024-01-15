package portefeuille.util;

import java.awt.Frame;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;

import portefeuille.screens.EffectenFrame;

public class PortefeuilleTrayIcon extends TrayIcon implements ActionListener
{
  PopupMenu tiMenu;
  MenuItem aboutItem;
  MenuItem showItem;
  MenuItem hideItem;
  MenuItem minimiseItem;
  MenuItem exitItem;
  EffectenFrame ef;

  public PortefeuilleTrayIcon(ImageIcon ic, EffectenFrame efFrame)
  {
    super(ic.getImage());
    ef = efFrame;
    setImageAutoSize(true);
    setToolTip("Portefeuille");
    tiMenu = new PopupMenu("Portefeuille");
    aboutItem =  new MenuItem("About");
    aboutItem.addActionListener(this);
    tiMenu.add(aboutItem);
    tiMenu.addSeparator();
    showItem =  new MenuItem("Show");
    showItem.addActionListener(this);
    tiMenu.add(showItem);
    hideItem =  new MenuItem("Hide");
    hideItem.addActionListener(this);
    tiMenu.add(hideItem);
    minimiseItem =  new MenuItem("Minimise");
    minimiseItem.addActionListener(this);
    tiMenu.add(minimiseItem);
    tiMenu.addSeparator();
    exitItem =  new MenuItem("Quit");
    exitItem.addActionListener(this);
    tiMenu.add(exitItem);
    setPopupMenu(tiMenu);
    if(ef.isService())
    {
      hideItem.setEnabled(false);
      minimiseItem.setEnabled(false);
    }
  }

  @Override
  public void actionPerformed(ActionEvent e) 
  {
    MenuItem item = (MenuItem) e.getSource();
    switch(item.getLabel())
    {
      case "About":
        displayMessage("Portefeuille app", "Copyright pvm ©2018", TrayIcon.MessageType.NONE);
      break;

      case "Show":
        if(ef.getState()==Frame.ICONIFIED)
        {
          ef.setState(Frame.NORMAL);
       }
        ef.setVisible(true);
        hideItem.setEnabled(true);
        minimiseItem.setEnabled(true);
        showItem.setEnabled(false);
        ef.toFront();
      break;

      case "Minimise":
        if(ef.getQuitPending()==false)
        {
          ef.setState(Frame.ICONIFIED);
        }
      break;

      case "Hide":
        if(ef.getQuitPending()==false)
        {
          ef.setVisible(false);
          showItem.setEnabled(true);
          hideItem.setEnabled(false);
          minimiseItem.setEnabled(false);
        }
      break;

      case "Quit":
        item.setEnabled(false);
        if(ef.getQuitPending()) break;
        if(ef.quit())
        {
          SystemTray.getSystemTray().remove(this);
        };
        item.setEnabled(true);
    }
  }

  public void enableItem(String item, boolean value)
  {
    TrayIcon[] icons = SystemTray.getSystemTray().getTrayIcons();
    if(icons.length < 1) return;
    PortefeuilleTrayIcon theIcon = (PortefeuilleTrayIcon)icons[0];
    switch(item)
    {
      case "Show":
        theIcon.showItem.setEnabled(value);
      break;
      case "Hide":
        theIcon.hideItem.setEnabled(value);
      break;
      case "Minimise":
        theIcon.minimiseItem.setEnabled(value);
      break;
      case "Quit":
        theIcon.exitItem.setEnabled(value);
      break;
    }
  }
}