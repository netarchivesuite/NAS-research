package dk.netarkivet.research.wid;

import java.util.Date;

import dk.netarkivet.research.exception.ArgumentCheck;

/**
 * Webresource IDentifier for resources found in Wayback.
 * Besides the webarchive, the url and the date, it can also contain the (w)arc filename.
 */
public class WaybackWID extends SimpleWID {

	/** The name file with the */
	protected final String filename;
	
	/** 
	 * Constructor.
	 * @param filename The name of the file in the webarchive containing the webresource.
	 * @param webarchive The webarchive with the resource.
	 * @param url The URL for the webresource.
	 * @param date The date for the harvest of the webresource.
	 */
	private WaybackWID(String filename, String webarchive, String url, Date date) {
		super(webarchive, url, date);
		this.filename = filename;
	}
	
	/**
	 * Instantiator.
	 * May have either filename or date as null.
	 * @param filename The name of the file in the webarchive containing the webresource.
	 * @param webarchive The webarchive with the resource. May not be null.
	 * @param url The URL for the webresource. May not be null.
	 * @param date The date for the harvest of the webresource.
	 * @return The WID for the wayback entry.
	 */
	public static WaybackWID createWaybackWID(String filename, String webarchive, String url, Date date) {
		ArgumentCheck.checkNotNull(webarchive, "String webarchive");
		ArgumentCheck.checkNotNull(url, "String url");
		ArgumentCheck.checkNotAllNull(filename, date);
		return new WaybackWID(filename, webarchive, url, date);
	}
	
	/**
	 * Instantiator for a resource in Netarkivet.dk.
	 * @param filename The name of the file in the webarchive containing the webresource.
	 * @param url The URL for the webresource. May not be null.
	 * @param date The date for the harvest of the webresource.
	 * @return The WID for the wayback entry.
	 */
	public static WaybackWID createNarkWaybackWID(String filename, String url, Date date) {
		return createWaybackWID(filename, WIDConstants.NETARCHIVE_DK_WEBARCHIVE, url, date);
	}
	
	/**
	 * @return The name of the file for the webresource. This may be null.
	 */
	public String getFilename() {
		return filename;
	}
}
