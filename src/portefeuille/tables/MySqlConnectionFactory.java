package portefeuille.tables;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import portefeuille.util.Config;
import java.lang.ClassNotFoundException;

public class MySqlConnectionFactory {
  public static Connection getMySqlConnection(String configId) throws ClassNotFoundException, SQLException 
  {
    Config theConfig = null;
		Properties props = null;
    theConfig = new Config();
    String fileName = String.format("%s_schema.properties", configId);
    props = theConfig.getProperties(fileName);
    String driverClassName = props.getProperty("MYSQL_DB_DRIVER_CLASS");
    String url = props.getProperty("MYSQL_DB_URL");
    String user = props.getProperty("MYSQL_DB_USERNAME");
    String password = props.getProperty("MYSQL_DB_PASSWORD");

    Class.forName(driverClassName);
  
    return DriverManager.getConnection(url, user, password);
  }
}
