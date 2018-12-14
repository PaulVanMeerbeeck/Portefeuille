package portefeuille.util;

import java.awt.Color;
import java.math.BigDecimal;

import javax.swing.table.DefaultTableCellRenderer;

public class WinstRenderer extends DefaultTableCellRenderer
{
	private static final long serialVersionUID = 1L;

	public WinstRenderer()
	{
		super();
	}
	
	public void setValue(Object value)
	{
		if(value ==null) return;
		if(value.toString().isEmpty()) 
		{
			setText(" ");
			return; 
		}
//		System.out.println("value class = "+value.getClass().getName()+", "+BigDecimal.class.getName());
		if(value.getClass().getName().compareTo(BigDecimal.class.getName())==0)
		{
			BigDecimal v = (BigDecimal)value;
//			System.out.println("v = "+v);
			if( v.signum() < 0)
			{
				this.setForeground(Color.red);
			}
			else
				this.setForeground(Color.BLACK);
			setText(String.format("%.2f", v));
			setHorizontalAlignment(RIGHT);
		}
		else if(value.getClass().getName().compareTo(Integer.class.getName())==0)
		{
			setText(String.format("%d", value));
			setHorizontalAlignment(RIGHT);	
		}
		else if(value.getClass().getName().compareTo(String.class.getName())==0)
		{
			setText(value.toString());
			setHorizontalAlignment(LEFT);	
		}
	}
}
