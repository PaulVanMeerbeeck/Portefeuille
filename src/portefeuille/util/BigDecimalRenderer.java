package portefeuille.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import javax.swing.table.DefaultTableCellRenderer;

public class BigDecimalRenderer extends DefaultTableCellRenderer
{
	private static final long serialVersionUID = 1L;

	public BigDecimalRenderer()
	{
		super();
	}
	
	public void setValue(Object value)
	{
		if(value==null) return;
		if(value.toString().isEmpty()) 
		{
			setText(" ");
			return; 
		}
		if(value instanceof java.sql.Date)
		{
			setText(((java.sql.Date) value).toString());
		}
		if(value instanceof BigDecimal)
		{
			BigDecimal v = (BigDecimal)value;
			DecimalFormat df = new DecimalFormat("#,##0.00");
			setText(df.format(v));
			setHorizontalAlignment(RIGHT);
		}
		else if(value instanceof Integer)
		{
			setText(String.format("%d", value));
			setHorizontalAlignment(RIGHT);	
		}
		else if(value instanceof String)
		{
			setText(value.toString());
			setHorizontalAlignment(LEFT);	
		}
	}
}
