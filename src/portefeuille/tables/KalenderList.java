package portefeuille.tables;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;

public class KalenderList
{
	Object[][] tableData;
	Object[] columnNames;
	int rowCount = 0;
	int colCount = 0;

	public KalenderList(DataSource ds)
	{
		super();
		if(ds==null) return;

		Connection con = null;
		Statement stmtRM = null;
		ResultSet rsRM = null;
		String sql = "select * from Kalender";
		try 
		{
			con = ds.getConnection();
			stmtRM = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
			rsRM = stmtRM.executeQuery(sql);
			ResultSetMetaData rsMtd = rsRM.getMetaData();
			colCount=rsMtd.getColumnCount();
			columnNames=new Object[colCount];
			for(int i=0; i<colCount; i++)
			{
				columnNames[i]=rsMtd.getColumnName(i+1);
//				System.out.println("colName["+i+"] = "+columnNames[i]);
			}
			if (rsRM.last()) 
			{
				rowCount = rsRM.getRow();
		    rsRM.beforeFirst();
			}
			tableData = new Object[rowCount][colCount];
			int i = 0;
			while (rsRM.next()) 
			{
				for(int j=0; j < colCount; j++)
				{
					tableData[i][j]=rsRM.getObject(j+1);
				}
				i++;
			}
			if(rsRM != null) rsRM.close();
			if(con != null) con.close();
		}
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}

	public Object[][] getTableData()
	{
		return tableData;
	}

	public Object[] getColumnNames()
	{
		return columnNames;
	}

	public int getRowCount()
	{
		return rowCount;
	}

	public int getColCount()
	{
		return colCount;
	}

}
