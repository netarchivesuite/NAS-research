package dk.netarkivet.research.cdx;

import java.util.Collection;

import dk.netarkivet.research.wpid.WPID;

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
	 * Retrieves all the CDX indices for the a given URL
	 * 
	 * @param URL The url to retrieve all the CDX indices for.
	 * @return The collection of CDX indices for the URL.
	 */
	Collection<CDXEntry> retrieveAllCDX(String URL);
}
