package portefeuille.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DBHelper
{
	public DBHelper() {}
	
	public String formatDate(Date aDate, String formatStr)
	{
    DateFormat dateFormat = new SimpleDateFormat( formatStr );
    //dateFormat.setCalendar(Calendar.getInstance(TimeZone.getTimeZone("IST")));
    return dateFormat.format(aDate);
	}

}
