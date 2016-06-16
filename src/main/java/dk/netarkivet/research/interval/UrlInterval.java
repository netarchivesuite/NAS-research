package dk.netarkivet.research.interval;

import java.util.Date;

/**
 * Container for the URL interval format.
 */
public class UrlInterval {
	/** The URL for the interval.*/
	protected final String url;
	/** The earliest date for the interval.*/
	protected Date earliestDate;
	/** The latest date for the interval.*/
	protected Date latestDate;
	
	/**
	 * Constructor.
	 * @param url The URL for the interval.
	 * @param earliestDate The earliest date for the interval.
	 * @param latestDate The latest date for the interval.
	 */
	public UrlInterval(String url, Date earliestDate, Date latestDate) {
		this.url = url;
		this.earliestDate = earliestDate;
		this.latestDate = latestDate;
	}
	
	/**
	 * @return The URL for the interval.
	 */
	public String getUrl() {
		return url;
	}
	
	/**
	 * @return The earlist date for the interval.
	 */
	public Date getEarliestDate() {
		return earliestDate;
	}
	
	/**
	 * @return The latest date for the interval.
	 */
	public Date getLatestDate() {
		return latestDate;
	}
}
