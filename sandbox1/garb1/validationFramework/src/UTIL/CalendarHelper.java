/*
 * Created on Jul 16, 2005
 */

package util;

import java.util.Calendar;

//it is utility class to get formatted date and time representation
//is used to build testing report

public class CalendarHelper {
	private static Calendar calendar;

	public static String getCurrentDate() {
		calendar = Calendar.getInstance();
		return getFormattedCurrentDate() + "-" +  getFormattedCurrentTime();
	}
	
	private static String getFormattedCurrentTime() {
		return calendar.get(Calendar.HOUR) + "-" + 
		       calendar.get(Calendar.MINUTE) + "-" + 
			   calendar.get(Calendar.SECOND); 
	}
	
	private static String getFormattedCurrentDate() {
		return calendar.get(Calendar.YEAR) + "-" + 
		       (calendar.get(Calendar.MONTH) + 1) + "-" + 
			   calendar.get(Calendar.DATE);
	}
}
