package dk.netarkivet.research.wid;

import java.util.Date;

public class SimpleWID implements WID {

	/** The webarchive for the WPID.*/
	protected final String webarchive;
	/** The URL for the webresource.*/
	protected final String url;
	/** The date when the webresource was harvested/archived.*/
	protected final Date date;

	/** 
	 * Constructor.
	 * @param webarchive The webarchive with the resource.
	 * @param url The URL for the webresource.
	 * @param date The date for the harvest of the webresource.
	 */
	public SimpleWID(String webarchive, String url, Date date) {
		this.webarchive = webarchive;
		this.url = url;
		this.date = date;
	}
	
	@Override
	public String getWebarchive() {
		return webarchive;
	}

	@Override
	public String getUrl() {
		return url;
	}

	@Override
	public Date getDate() {
		return date;
	}
}
