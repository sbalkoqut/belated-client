package qut.belated.helpers;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class HumanReadableDate {
	
	static DateFormat timeFormat;
	static DateFormat dateFormat;
	static Date start;
	static Date end;
	static String result;
	
	private static void InitialiseDateFormats()
	{
		timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT);
		dateFormat = DateFormat.getDateInstance(DateFormat.SHORT);	
	}
	
	public static String ToString(Date start, Date end) {
		HumanReadableDate.start = start;
		HumanReadableDate.end = end;

		InitialiseDateFormats();
		if (SameDay(start, end))
			singleDayEvent();
		else
			multipleDayEvent();

		return result;
	}
	
	private static void singleDayEvent()
	{
		if (SameDay(start, new Date()))
			result = "Today, ";
		else
			result = dateFormat.format(start);
		result += timeFormat.format(start) + " - " + timeFormat.format(end);
	}
	
	private static void multipleDayEvent()
	{
		result = dateFormat.format(start) + ", " + timeFormat.format(start) 
				+ " - " + dateFormat.format(end) + ", " + timeFormat.format(end);
	}
	
	private static boolean SameDay(Date start, Date end)
	{
		Calendar initial = Calendar.getInstance(Locale.getDefault());
		Calendar compare = Calendar.getInstance(Locale.getDefault());

		initial.setTime(start);
		compare.setTime(end);
		return (initial.get(Calendar.YEAR) == compare.get(Calendar.YEAR)) &&
				(initial.get(Calendar.DAY_OF_YEAR) == compare.get(Calendar.DAY_OF_YEAR));
	}
}
