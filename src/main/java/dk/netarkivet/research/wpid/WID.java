package dk.netarkivet.research.wpid;

import java.util.Date;

/**
 * Webarchive IDentifier.
 */
public interface WID {

	/**
	 * @return The webarchive.
	 */
	String getWebarchive();
	
	/**
	 * @return The URL.
	 */
	String getUrl();
	
	/**
	 * @return The date.
	 */
	Date getDate();
}
