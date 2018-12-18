package portefeuille.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.NoSuchFileException;
import java.util.Properties;

import javax.swing.JOptionPane;

public class Config
{
	public Config()
	{ // Empty constructor
	}

	public Properties getProperties(String fileName)
	{
		Properties props = new Properties();
		try
		{
			InputStream is = getResource("Resource/"+fileName);
			props.load(is);
			is.close();
		}
		catch(IOException e)
		{
			JOptionPane.showMessageDialog(null,
          "Configuration file "+fileName+" not found!",
          "Error Message",
          JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
		return props;
	}
	
  public InputStream getResource(String filePath) throws NoSuchFileException
  {
      ClassLoader classLoader = this.getClass().getClassLoader();

      InputStream inputStream = classLoader.getResourceAsStream(filePath);

      if(inputStream == null)
      {
          throw new NoSuchFileException("Resource file not found. Note that the current directory is the source folder!");
      }

      return inputStream;
  }

}
