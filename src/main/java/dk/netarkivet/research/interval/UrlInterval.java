package dk.netarkivet.research.interval;

import java.util.Date;

/**
 * Container for the URL interval format.
 */
public class UrlInterval {
	/** The URL for the interval.*/
	protected final String url;
	/** The earliest date for the interval.*/
	protected Date earliestDate = null;
	/** The latest date for the interval.*/
	protected Date latestDate = null;
	
	/**
	 * Constructor.
	 * @param url The URL for the interval.
	 * @param earliestDate The earliest date for the interval.
	 * @param latestDate The latest date for the interval.
	 */
	public UrlInterval(String url, Date earliestDate, Date latestDate) {
		this.url = url;
		if(earliestDate != null) {
			this.earliestDate = new Date(earliestDate.getTime());
		}
		if(latestDate != null){ 
			this.latestDate = new Date(latestDate.getTime());
		} 
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
		if(earliestDate == null) {
			return null;
		}
		return new Date(earliestDate.getTime());
	}
	
	/**
	 * @return The latest date for the interval.
	 */
	public Date getLatestDate() {
		if(latestDate == null) {
			return null;
		}
		return new Date(latestDate.getTime());
	}
}
