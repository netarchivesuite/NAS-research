package dk.netarkivet.research.index;

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
}
