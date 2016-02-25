package dk.netarkivet.research.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Simple utility class for converting dates between Java date format and the Wayback date format.
 */
public class DateUtils {
	/** CDX date format string as specified in the CDX documentation. */
	public static final String CDX_DATE_FORMAT = "yyyyMMddHHmmss";

	/** Basic <code>DateFormat</code> is not thread safe. */
	protected static final ThreadLocal<DateFormat> CDX_DATE_PARSER_THREAD = new ThreadLocal<DateFormat>() {
		@Override
		public DateFormat initialValue() {
			DateFormat dateFormat = new SimpleDateFormat(CDX_DATE_FORMAT);
			dateFormat.setLenient(false);
			dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
			return dateFormat;
		}
	};
	
	/**
	 * Converts from date to Wayback date string.
	 * @param date The date to convert from.
	 * @return The Wayback format for the date.
	 */
	public static String dateToWaybackDate(Date date) {
		return CDX_DATE_PARSER_THREAD.get().format(date);
	}
	
	/**
	 * Converts from the Wayback date string to java.utils.Date.
	 * @param date The date string from wayback.
	 * @return The Date.
	 * @throws ParseException If it cannot be parsed.
	 */
	public static Date waybackDateToDate(String date) throws ParseException {
		return CDX_DATE_PARSER_THREAD.get().parse(date);		
	}
}
