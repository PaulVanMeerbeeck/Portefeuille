package portefeuille.tables;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;

public class VerkoopTriggerList
{
	public final int colTickerId = 1;
	public final int colCode = 2;
	public final int colWaarde = 3;
	public final int colAantal = 4;
	public final int colGemAankWaarde = 5;
	public final int colDoelKoers = 6;
	public final int colOmzet = 7;
	public final int colWinst = 8;
	public final int colStatus =9;
	public final int colDatum = 10;

	Object[][] tableData;
	Object[] columnNames;
	int rowCount = 0;
	int colCount = 0;

	public VerkoopTriggerList(DataSource ds)
	{
		super();
		if(ds==null) return;

		Connection con = null;
		Statement stmtRM = null;
		ResultSet rsRM = null;
		String sql = "select * from VerkoopTrigger order by Status, Datum";
		try 
		{
			con = ds.getConnection();
			stmtRM = con.createStatement();
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
					try
					{
						tableData[i][j]=rsRM.getObject(j+1);
					}
					catch(SQLException e)
					{
						tableData[i][j]="";
					}
				}
				i++;
			}
		}
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(rsRM != null) rsRM.close();
				if(con != null) con.close();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
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
