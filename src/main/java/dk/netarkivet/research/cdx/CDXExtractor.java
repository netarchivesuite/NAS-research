package dk.netarkivet.research.cdx;

import java.util.Collection;
import java.util.Date;

import dk.netarkivet.research.wid.WID;
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
	 * Retrieves all the CDX entries for all the WIDs.
	 * If no CDX can be retrieved for a WID, then a null is returned in its place.
	 * @param wids The collection of WIDs.
	 * @return The collection of CDX entries for all the WIDs.
	 */
	Collection<CDXEntry> retrieveCDXentries(Collection<WID> wids);
	
	/**
	 * Retrieves all the CDX indices for the a given URL
	 * 
	 * @param url The url to retrieve all the CDX indices for.
	 * @return The collection of CDX indices for the URL.
	 */
	Collection<CDXEntry> retrieveAllCDX(String url);
	
	/**
	 * Retrieves all the CDX entries in the given date interval.
	 * @param url The URL whose CDX indies should be retrieved.
	 * @param earliestDate The earliest date for the CDX indices.
	 * @param latestDate The latest date for the CDX indices.
	 * @return The collection of CDX indices for the given interval.
	 */
	Collection<CDXEntry> retrieveCDXForInterval(String url, Date earliestDate, Date latestDate);
}
