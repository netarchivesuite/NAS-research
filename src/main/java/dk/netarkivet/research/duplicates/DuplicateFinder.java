package dk.netarkivet.research.duplicates;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dk.netarkivet.research.cdx.CDXEntry;
import dk.netarkivet.research.cdx.CDXExtractor;

/**
 * Finds duplicates.
 * Extracts CDX indices and compare the digest checksum to find duplicates.
 * Creates a map between the checksum and a list of dates for the checksums (dates in millis from epoch).
 */
public class DuplicateFinder {

	/** The CDX extractor.*/
	protected final CDXExtractor extractor;
	
	/**
	 * Constructor.
	 * @param extractor The CDX extractor.
	 */
	public DuplicateFinder(CDXExtractor extractor) {
		this.extractor = extractor;
	}
	
	/**
	 * Extracts a map of unique checksums and a list of dates for each checksum.
	 * @param url The URL for extracting the checksum.
	 * @return The map of checksums and their dates.
	 */
	public Map<String, List<Long>> makeDuplicateMap(String url) {
		Collection<CDXEntry> cdxs = extractor.retrieveAllCDX(url);

		Map<String, List<Long>> res = new HashMap<String, List<Long>>();
		
		for(CDXEntry entry : cdxs) {
			insertEntryIntoMap(entry, res);
		}
		
		return res;
	}
	
	/**
	 * Inserts the CDX entry date into the map.
	 * The date for the entry is inserted into a list for the checksum/digest. 
	 * @param entry The CDX entry.
	 * @param map The map to insert it into.
	 */
	protected void insertEntryIntoMap(CDXEntry entry, Map<String, List<Long>> map) {
		List<Long> dates = map.get(entry.getDigest());
		if(dates == null) {
			dates = new ArrayList<Long>();
		}
		
		dates.add(entry.getDate());
		map.put(entry.getDigest(), dates);
	}
}
