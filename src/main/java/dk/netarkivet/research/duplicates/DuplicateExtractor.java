package dk.netarkivet.research.duplicates;

import java.util.Collection;
import java.util.Date;

import dk.netarkivet.research.cdx.CDXEntry;
import dk.netarkivet.research.cdx.CDXExtractor;
import dk.netarkivet.research.utils.DateUtils;

/**
 * Finds duplicates.
 * Extracts CDX indices and compare the digest checksum to find duplicates.
 * Creates a map between the checksum and a list of dates for the checksums (dates in millis from epoch).
 */
public class DuplicateExtractor {

	/** The CDX extractor.*/
	protected final CDXExtractor extractor;
	
	/**
	 * Constructor.
	 * @param extractor The CDX extractor.
	 */
	public DuplicateExtractor(CDXExtractor extractor) {
		this.extractor = extractor;
	}
	
	/**
	 * 
	 * Extracts a map of unique checksums and a list of dates for each checksum.
	 * @param url The URL for extracting the checksum.
	 * @param earliestDate The earliest date for the results. 
	 * @param latestDate The latest date for the results.
	 * @return The map of checksums and their dates.
	 */
	public DuplicateMap makeDuplicateMap(String url, Date earliestDate, Date latestDate) {
		Collection<CDXEntry> cdxs = extractor.retrieveAllCDX(url);

		DuplicateMap res = new DuplicateMap();
		
		for(CDXEntry entry : cdxs) {
			if(DateUtils.checkDateInterval(entry, earliestDate, latestDate)) {
				res.addElement(entry.getDate(), entry.getDigest());
			}
		}
		
		return res;
	}
}
