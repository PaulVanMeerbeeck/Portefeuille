package portefeuille.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;;

public class Client
{
	private static String url = "https://www.tijd.be/mijn-diensten/portefeuille?portfolio=urn:portfolio:10501726";
	private final String USER_AGENT = "Safari/12.0.2";

	public String get()
	{
		URL obj;
		try
		{
			obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		
			// optional default is GET
			con.setRequestMethod("GET");
	
			//add request header
			con.setRequestProperty("User-Agent", USER_AGENT);
			int responseCode = con.getResponseCode();
			System.out.println("\nSending 'GET' request to URL : " + url);
			System.out.println("Response Code : " + responseCode);
			BufferedReader in = new BufferedReader(
	        new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) 
			{
				response.append(inputLine);
			}
			in.close();
			return response.toString();
		}
		catch (MalformedURLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return e.getMessage();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return e.getMessage();
		}

	}
	

}
