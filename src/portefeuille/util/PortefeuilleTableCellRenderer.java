package portefeuille.util;

import java.awt.Color;
import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;

public class PortefeuilleTableCellRenderer extends DefaultTableCellRenderer
{
	private static final long serialVersionUID = 1L;
	
	public PortefeuilleTableCellRenderer()
	{
		super();
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
	{
      JComponent comp = (JComponent) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
      if(hasFocus)
      {
      	Border border = BorderFactory.createLineBorder(Color.WHITE);
      	comp.setBorder(border);
      }
      return comp;
  }

}
