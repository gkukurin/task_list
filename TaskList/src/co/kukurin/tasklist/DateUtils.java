package co.kukurin.tasklist;

import java.util.Calendar;
import java.util.Date;

public class DateUtils {

	/**
	 * extracts only date part from given date (sets time to 0:0:0:0)
	 * @param date
	 */
	public static Date truncDate(Date date){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

}
