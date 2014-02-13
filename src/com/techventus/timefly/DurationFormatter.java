package com.techventus.timefly;

public class DurationFormatter {
	
	public static String getFormattedTime(long millis)
	{
		return formatMillis(millis);
	}
	
	public static String getFormattedTime(int seconds)
	{
		long millis = 1000*seconds;
		return formatMillis(millis);
	}
	
	private static  String formatMillis(long millis)
	{
		int secs =(int)(millis/1000)%60;
		int mins =((int) (millis/(1000*60))%60);
		int hours = ((int) (millis/(1000*60*60))%24);
		int days = ((int) (millis/(1000*60*60*24)));
		
		if(days>0)
		{
			String dayUnit = "days";
			if(days==1)
			{
				dayUnit = "day";
			}
			if(hours>0)
			{
				return ""+days+" "+dayUnit+", "+hours+" h";
			}
			else
				return ""+days+" "+dayUnit;
		}
		else if(hours>0)
		{
			if(mins>0)
				return ""+hours+" h, "+mins+" min";
			else
				return ""+hours+" h";
		}
		else if(mins>0)
			return ""+mins+" min";
		else 
			return secs+" secs";
	}
}
