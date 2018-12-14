package portefeuille.util;

import javax.swing.table.AbstractTableModel;

public class DataTableModel extends AbstractTableModel
{
	/**
	 * Constructs the table model
	 * @param aResultSet the result set to display
	 */
	private static final long serialVersionUID = 1L;
	
	Object[][] data;
	String[] columnNames;
	
	
	public DataTableModel(Object[][] d, String[] cN)
	{
		data=d;
		columnNames=cN;
	}

	public String getColumnName(int c)
	{
		return columnNames[c];
	}
	
	@Override
	public int getRowCount()
	{
//		System.out.println("data.length = "+data.length);
		return data.length;
	}

	@Override
	public int getColumnCount()
	{
		return columnNames.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex)
	{
//		System.out.println("rowIndex = "+rowIndex+", columnIndex = "+columnIndex);
		if(data[rowIndex][columnIndex]==null) return "";
		return data[rowIndex][columnIndex];
	}

  public Class<?> getColumnClass (int columnIndex)
  {
  	return data[0][columnIndex].getClass();
  }

}
