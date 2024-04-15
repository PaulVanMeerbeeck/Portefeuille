package portefeuille.screens;

import java.io.IOException;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

public class PortefeuilleDeTijd extends JFrame
{
    private static final long serialVersionUID = 1L;

    public PortefeuilleDeTijd()
    {
        this.setTitle("Portefeuille - De Tijd");
        try
        {
            JEditorPane webPane = new JEditorPane("http://www.tijd.be");
            System.out.println("webPane contents = "+webPane.getText());
            add(new JScrollPane(webPane));
            setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            setSize(800,600);
            setLocationRelativeTo(null);
        }
        catch (IOException e)
        {
             e.printStackTrace();
            System.out.println("PortefeuilleDeTijd: "+e.getLocalizedMessage());
        }
    }
}
