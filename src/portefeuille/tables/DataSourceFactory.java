package portefeuille.tables;

import java.io.IOException;
import java.util.Properties;

import javax.sql.DataSource;

import portefeuille.util.Config;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

public class DataSourceFactory
{
	public static DataSource getInputDataSource()
	{
		Config theConfig = null;
		Properties props = null;
		MysqlDataSource DS = null;
	
		try
		{
			theConfig = new Config();
			props = theConfig.getProperties("pvm_schema.properties");
			DS = new MysqlDataSource();
			DS.setURL(props.getProperty("MYSQL_DB_URL"));
			DS.setUser(props.getProperty("MYSQL_DB_USERNAME"));
			DS.setPassword(props.getProperty("MYSQL_DB_PASSWORD"));
//			System.out.println("MYSQL_DB_URL = " + DS.getURL());
//			System.out.println("MYSQL_DB_USERNAME = " + DS.getUser());
//			System.out.println("MYSQL_DB_PASSWORD = "+ DS.getPasswordCharacterEncoding());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return DS;
	}
}
