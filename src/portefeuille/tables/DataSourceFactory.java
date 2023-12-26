package portefeuille.tables;

import java.util.Properties;

import javax.sql.DataSource;
import portefeuille.util.Config;

import com.mysql.cj.jdbc.MysqlDataSource;

public class DataSourceFactory
{
	public static DataSource getInputDataSource(String configId)
	{
		Config theConfig = null;
		Properties props = null;
		MysqlDataSource DS = null;
	
		try
		{
//			System.out.println("ConfigId = "+configId);
			theConfig = new Config();
			String fileName = String.format("%s_schema.properties", configId);
			props = theConfig.getProperties(fileName);
			DS = new MysqlDataSource();
			DS.setURL(props.getProperty("MYSQL_DB_URL"));
			DS.setUser(props.getProperty("MYSQL_DB_USERNAME"));
			DS.setPassword(props.getProperty("MYSQL_DB_PASSWORD"));
//			System.out.println("MYSQL_DB_URL = " + DS.getURL());
//			System.out.println("MYSQL_DB_USERNAME = " + DS.getUser());
//			System.out.println("MYSQL_DB_PASSWORD = "+ DS.getPasswordCharacterEncoding());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return DS;
	}
}
