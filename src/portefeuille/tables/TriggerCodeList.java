package portefeuille.tables;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.sql.DataSource;
import javax.swing.JComboBox;

public class TriggerCodeList
{
	Object[][] tableData;
	Object[] columnNames;
	int rowCount = 0;
	int colCount = 0;

	public TriggerCodeList(DataSource ds)
	{
		super();
		if(ds==null) return;

		Connection con = null;
		Statement stmtRM = null;
		ResultSet rsRM = null;
		String sql = "select * from TriggerCode";
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
	
	public ArrayList<String> getCodeList()
	{
		ArrayList<String> theResult=new ArrayList<String>();
		int codeColumn = -1;
		for(int i=0; i< columnNames.length; i++)
		{
			if(columnNames[i].toString().compareToIgnoreCase("Code")!=0) continue;
			codeColumn=i;
			break;
		}
		if(codeColumn>-1)
		{
			for(int i=0; i<rowCount; i++)
			{
				theResult.add(tableData[i][codeColumn].toString());
			}
		}
			
		return theResult;
	}
	
	public JComboBox<String> getCodeComboBox()
	{
		JComboBox<String> theResult=new JComboBox<String>();
		int codeColumn = -1;
		for(int i=0; i< columnNames.length; i++)
		{
			if(columnNames[i].toString().compareToIgnoreCase("Code")!=0) continue;
			codeColumn=i;
			break;
		}
		if(codeColumn>-1)
		{
			for(int i=0; i<rowCount; i++)
			{
				theResult.addItem(tableData[i][codeColumn].toString());
			}
		}
		return theResult;
	}

}
