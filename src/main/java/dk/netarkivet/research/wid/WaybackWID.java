package dk.netarkivet.research.wid;

import java.util.Date;

import dk.netarkivet.research.exception.ArgumentCheck;

public class WaybackWID extends SimpleWID {

	/** The name file with the */
	protected final String filename;
	
	/** 
	 * Constructor.
	 * @param webarchive The webarchive with the resource.
	 * @param url The URL for the webresource.
	 * @param date The date for the harvest of the webresource.
	 */
	private WaybackWID(String filename, String webarchive, String url, Date date) {
		super(webarchive, url, date);
		this.filename = filename;
	}
	
	public static WaybackWID createWaybackWID(String filename, String webarchive, String url, Date date) {
		ArgumentCheck.checkNotNull(filename, "String filename");
		ArgumentCheck.checkNotNull(webarchive, "String webarchive");
		ArgumentCheck.checkNotNull(url, "String url");
		return new WaybackWID(filename, webarchive, url, date);
	}
	
	public static WaybackWID createNarkWaybackWID(String filename, String url, Date date) {
		return createWaybackWID(filename, WIDConstants.NETARCHIVE_DK_WEBARCHIVE, url, date);
	}
}
