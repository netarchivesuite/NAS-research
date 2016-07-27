package dk.netarkivet.research.links;

import java.util.Date;

import dk.netarkivet.research.cdx.CDXEntry;
import dk.netarkivet.research.cdx.CDXExtractor;
import dk.netarkivet.research.wid.WaybackWID;

/**
 * CDX Link locator.
 * Extracting the links from WARC records using a link extractor, and checking whether the links can be found in 
 * the CDX server.
 */
public class CDXLinksLocator extends LinksLocator {
	/** The CDX extractor.*/
	protected final CDXExtractor cdxExtractor;
	
	/**
	 * Constructor.
	 * @param linkExtractor The extractor of the links.
	 * @param cdxExtractor The extractor of CDX entries.
	 */
	public CDXLinksLocator(LinkExtractor linkExtractor, CDXExtractor cdxExtractor) {
		super(linkExtractor);
		this.cdxExtractor = cdxExtractor;
	}

	@Override
	protected LinkStatus getLinkStatus(String link, String originalUrl, Date originalDate) {
		String extractLink = link.contains("#") ? link.split("#")[0] : link;

		WaybackWID wid = WaybackWID.createNarkWaybackWID(null, extractLink, originalDate);
		CDXEntry entry = cdxExtractor.retrieveCDX(wid);
		if(entry == null) {
			return new LinkStatus(false, link, null, originalUrl, originalDate, "cdx");
		} else {
			return new LinkStatus(true, link, new Date(entry.getDate()), originalUrl, originalDate, "cdx");
		}
	}
}
