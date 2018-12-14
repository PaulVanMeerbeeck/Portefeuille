package portefeuille.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.ResultSetMetaData;

import javax.swing.table.AbstractTableModel;

public class ResultSetTableModel extends AbstractTableModel
{
	/**
	 * Constructs the table model
	 * @param aResultSet the result set to display
	 */
	private static final long serialVersionUID = 1L;
	
	private ResultSet rs;
	private ResultSetMetaData rsmd;

	public ResultSetTableModel(ResultSet aResultSet)
	{
		rs = aResultSet;
		try
		{
			rsmd = rs.getMetaData();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}

	public String getColumnName(int c)
	{
		try
		{
			return rsmd.getColumnName(c+1);
		}
		catch(SQLException e)
		{
			e.printStackTrace();
			return "";
		}
	}
	
	@Override
	public int getRowCount()
	{
		try
		{
			rs.last();
			return rs.getRow();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
			return 0;
		}
	}

	@Override
	public int getColumnCount()
	{
		try
		{
			return rsmd.getColumnCount();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
			return 0;
		}
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		try
		{
			rs.absolute(rowIndex+1);
			return rs.getObject(columnIndex+1);
		}
		catch(SQLException e)
		{
			e.printStackTrace();
			return "";
		}
	}

  public Class<?> getColumnClass (int columnIndex)
  {
  	try
		{
			if(rsmd != null)
			{
				String className = rsmd.getColumnClassName(columnIndex+1);
				if(className.contains("Integer")) return Number.class;
				if(className.contains("BigDecimal")) return Number.class;
				if(className.contains("String")) return String.class;
//				if(className.contains("Date")) return Date.class;
//				if(className.contains("Year")) return Year.class;
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
  	return Object.class;
  }

}
