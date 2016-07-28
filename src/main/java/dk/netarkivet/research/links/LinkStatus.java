package dk.netarkivet.research.links;

import java.util.Date;

import dk.netarkivet.research.exception.ArgumentCheck;

/**
 * Container class for the state of a single link.
 * Contains whether the link was found in the archive, the URL for the link, the date for the link, 
 * the date for the resource where the link was found, and the date for the resource where the link was found. 
 */
public class LinkStatus {
	/** Whether or not the link was found.*/
	protected final boolean found;
	/** The URL for the link.*/
	protected final String linkUrl;
	/** The date of the found link. Null if not found.*/
	protected Date linkDate;
	/** The URL for the resource, where the link was discovered.*/
	protected final String referralUrl;
	/** The date for the resource, where the link was discovered.*/
	protected final Date referralDate;
	/** A comment about the status. E.g. where it was found.*/
	protected final String comment;
	
	/**
	 * Constructor.
	 * @param found Whether or not the link was found.
	 * @param linkUrl The URL for the link.
	 * @param linkDate The date for the found link (null if not found).
	 * @param referralUrl The URL for the resource, which contained the link.
	 * @param referralDate The date for the resource, which contained the link.
	 * @param comment Comment about how the reference was found.
	 */
	public LinkStatus(boolean found, String linkUrl, Date linkDate, String referralUrl, Date referralDate, 
			String comment) {
		ArgumentCheck.checkIsTrue(linkDate != null || !found, "If the link is found, then the date may not be null");
		this.found = found;
		this.linkUrl = linkUrl;
		if(linkDate != null) {
			this.linkDate = new Date(linkDate.getTime());
		} else {
			this.linkDate = null;
		}
		this.referralUrl = referralUrl;
		this.referralDate = new Date(referralDate.getTime());
		this.comment = comment;
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
	 * @return The date for the link. Or null, if the link had no date.
	 */
	public Date getLinkDate() {
		if(linkDate == null) {
			return null;
		}
		return new Date(linkDate.getTime());
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
		return new Date(referralDate.getTime());
	}
	
	/**
	 * @return The comment about the status.
	 */
	public String getComment() {
		return comment;
	}
}
