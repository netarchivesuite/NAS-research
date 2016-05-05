package dk.netarkivet.research.wid;

import java.util.Date;

import dk.netarkivet.research.exception.ArgumentCheck;

/**
 * Webarchive Persistent Identifier, WPID.
 * Persistently identifies an archived webresource within a webarchive.
 * 
 * Consists of a webarchive, an url and a date.
 */
public class WPID extends SimpleWID {
	/** 
	 * Constructor.
	 * @param webarchive The webarchive with the resource.
	 * @param url The URL for the webresource.
	 * @param date The date for the harvest of the webresource.
	 */
	private WPID(String webarchive, String url, Date date) {
		super(webarchive, url, date);
	}
	
	/**
	 * Creates a WPID for a resources in Netarkivet.dk.
	 * @param url The URL for the webresource.
	 * @param date The date for the harvest of the webresource.
	 * @return The WPID for the Netarkivet.dk resource.
	 */
	public static WPID createNarkWPid(String url, Date date) {
		return createWPid(WIDConstants.NETARCHIVE_DK_WEBARCHIVE, url, date);
	}
	
	/**
	 * Creates a WPID.
	 * @param webarchive The webarchive with the resource.
	 * @param url The URL for the webresource.
	 * @param date The date for the harvest of the webresource.
	 * @return The WPID.
	 */
	public static WPID createWPid(String webarchive, String url, Date date) {
		ArgumentCheck.checkNotNull(webarchive, "String webarchive");
		ArgumentCheck.checkNotNull(url, "String url");
		ArgumentCheck.checkNotNull(date, "Date date");
		return new WPID(webarchive, url, date);
	}
}
