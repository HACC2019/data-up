package us.hacc.ev.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class Tools
{
	public static final long DATE_MinuteInMillis = 60000;
	public static final long DATE_HourInMillis = DATE_MinuteInMillis*60;
	public static final long DATE_DayInMillis = DATE_HourInMillis*24;
	public static final long DATE_WeekInMillis = DATE_DayInMillis*7;
	public static final SimpleDateFormat sdfDate = new SimpleDateFormat("MM-dd-yyyy");
	public static final SimpleDateFormat sdfDateShort = new SimpleDateFormat("MM/dd/yy");
	public static final SimpleDateFormat sdfDatetime = new SimpleDateFormat("MM-dd-yy HH:mm:ss");
	public static final SimpleDateFormat sdfDatetimeAlt = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
	public static final SimpleDateFormat sdfHours = new SimpleDateFormat("HH");
	public static final double onPeak = 0.57, midDay = 0.49, offPeak = 0.54; 
	
	public static Date convertStringToDate(String s)
	{
		return convertStringToDate(s, sdfDatetime);
	}
	
	private static Date convertStringToDate(String s, SimpleDateFormat sdf)
	{
		try
		{
			return sdf.parse(s);	
		}
		catch (ParseException e)
		{
			return convertStringToDate(s, sdfDatetimeAlt);				
		}
	}
}
