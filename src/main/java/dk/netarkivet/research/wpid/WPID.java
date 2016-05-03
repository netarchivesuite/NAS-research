package dk.netarkivet.research.wpid;

import java.util.Date;

/**
 * Webarchive Persistent Identifier, WPID.
 * Persistently identifies an archived webresource within a webarchive.
 * 
 * Consists of a webarchive, an url and a date.
 */
public class WPID {
	/** The default for Netarkivet.dk. */
	public static final String NETARCHIVE_DK_WEBARCHIVE = "Netarkivet.dk";
	/** The webarchive for the WPID.*/
	protected String webarchive;
	/** The URL for the webresource.*/
	protected String url;
	/** The date when the webresource was harvested/archived.*/
	protected Date date;

	/** 
	 * Constructor.
	 * @param webarchive The webarchive with the resource.
	 * @param url The URL for the webresource.
	 * @param date The date for the harvest of the webresource.
	 */
	public WPID(String webarchive, String url, Date date) {
		this.webarchive = webarchive;
		this.url = url;
		this.date = date;
	}
	
	/**
	 * Creates a WPID for a resources in Netarkivet.dk.
	 * @param url The URL for the webresource.
	 * @param date The date for the harvest of the webresource.
	 * @return The WPID for the Netarkivet.dk resource.
	 */
	public static WPID createNarkWPid(String url, Date date) {
		return new WPID(NETARCHIVE_DK_WEBARCHIVE, url, date);
	}

	/**
	 * @return The webarchive.
	 */
	public String getWebarchive() {
		return webarchive;
	}
	
	/**
	 * @return The URL.
	 */
	public String getUrl() {
		return url;
	}
	
	/**
	 * @return The date.
	 */
	public Date getDate() {
		return date;
	}
}
