package dk.netarkivet.research.links;

import java.util.Date;

/**
 * Container class for the state of a single link.
 * Contains whether the link was found in the archive, the URL for the link, the date for the link, 
 * the date for the resource where the link was found, and the date for the resource where the link was found. 
 */
public class LinkStatus {
	/** Whether or not the link was found.*/
	protected final boolean found;
	/** The URL for the link.*/
	protected String linkUrl;
	/** The date of the found link. Null if not found.*/
	protected Date linkDate;
	/** The URL for the resource, where the link was discovered.*/
	protected String referralUrl;
	/** The date for the resource, where the link was discovered.*/
	protected Date referralDate;
	
	/**
	 * Constructor.
	 * @param found Whether or not the link was found.
	 * @param url The URL for the link.
	 * @param date The date for the found link (null if not found).
	 * @param referralUrl The URL for the resource, which contained the link.
	 * @param referralDate The date for the resource, which contained the link.
	 */
	public LinkStatus(boolean found, String url, Date date, String referralUrl, Date referralDate) {
		this.found = found;
		this.linkUrl = url;
		this.linkDate = date;
		this.referralUrl = referralUrl;
		this.referralDate = referralDate;
	}
	
	/**
	 * @return Whether or not the link was found.
	 */
	public boolean isFound() {
		return found;
	}
	
	/**
	 * @return The URL of the link.
	 */
	public String getLinkUrl() {
		return linkUrl;
	}
	
	/**
	 * @return The date for the link.
	 */
	public Date getLinkDate() {
		return linkDate;
	}
	
	/**
	 * @return The URL for the resource where the link was found.
	 */
	public String getReferralUrl() {
		return referralUrl;
	}
	
	/**
	 * @return The date for the resource where the link was found.
	 */
	public Date getReferralDate() {
		return referralDate;
	}
}
