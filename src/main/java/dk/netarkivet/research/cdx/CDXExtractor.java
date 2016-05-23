package dk.netarkivet.research.cdx;

import java.util.Collection;

import dk.netarkivet.research.wid.WPID;
import dk.netarkivet.research.wid.WaybackWID;

/**
 * Extractor for CDX entries.
 */
public interface CDXExtractor {
	/**
	 * Retrieves a CDX entry from a WPID.
	 * @param wpid The WPID to use for extracting the CDX.
	 * @return The XDCEntry for the WPID, or null if not CDXEntry matched the WPID.
	 */
	CDXEntry retrieveCDX(WPID wpid);
	
	/**
	 * Receives the CDX for a Wayback WID.
	 * @param wid The Wayback web identifier.
	 * @return The CDX entry for Wayback WID.
	 */
	CDXEntry retrieveCDX(WaybackWID wid);
	
	/**
	 * Retrieves all the CDX indices for the a given URL
	 * 
	 * @param url The url to retrieve all the CDX indices for.
	 * @return The collection of CDX indices for the URL.
	 */
	Collection<CDXEntry> retrieveAllCDX(String url);
}
