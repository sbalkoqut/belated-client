package qut.belated;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.google.android.gms.maps.model.LatLngBounds;

public class Utils {
	static String LOCATION_URL = "/REST/location";
	static String MEETINGS_URL = "/REST/meetings/";
	
	public static String getLocationUrl(String ip)
	{
		return "http://" + ip + LOCATION_URL;
	}
	
	public static String getMeetingsUrl(String ip, String email)
	{
		return "http://" + ip + MEETINGS_URL + email;
	}
	
	public static boolean sameDay(Date a, Date b)
	{
		Calendar aCal =Calendar.getInstance(Locale.getDefault());
		Calendar bCal =Calendar.getInstance(Locale.getDefault());

		aCal.setTime(a);
		bCal.setTime(b);
		return (aCal.get(Calendar.YEAR) == bCal.get(Calendar.YEAR)) &&
				(aCal.get(Calendar.DAY_OF_YEAR) == bCal.get(Calendar.DAY_OF_YEAR));
	}
	
	public static String readString(InputStream stream)
			throws IOException
	{
		StringBuffer stringBuffer = new StringBuffer();
		byte[] byteBuffer = new byte[4096];
		
		int bytesRead = 0;
		do 
		{
			bytesRead = stream.read(byteBuffer);
			if (bytesRead > 0)
			{
				String readString = new String(byteBuffer, 0, bytesRead);
				stringBuffer.append(readString);
			}
		}
		while (bytesRead > 0);
		
		return stringBuffer.toString();
	}
	
	public static LatLngBounds Combine(LatLngBounds a, LatLngBounds b)
	{
		LatLngBounds result = a.including(b.northeast);
		result = result.including(b.southwest);
		return result;
	}
}
