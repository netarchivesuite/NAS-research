package dk.netarkivet.research.duplicates;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

import dk.netarkivet.research.cdx.CDXEntry;

/**
 * The result map for duplicate result.
 */
public class DuplicateMap {
	/** The map between the date and their checksum.*/
	protected final Map<Long, CDXEntry> map;
	
	/**
	 * Constructor.
	 */
	public DuplicateMap() {
		map = new ConcurrentSkipListMap<Long, CDXEntry>();
	}
	
	/**
	 * Adds an element to the map.
	 * @param date The date in millis from epoc.
	 * @param checksum The checksum string.
	 */
	public void addElement(Long date, CDXEntry entry) {
		map.put(date, entry);
	}
	
	/**
	 * Extract the map as a date-to-checksum map.
	 * @return The date-to-checksum map.
	 */
	public Map<Long, CDXEntry> getDateToChecksumMap() {
		return map;
	}
	
	/**
	 * Extract the map as a checksum-to-list-of-dates.
	 * @return The checksum-to-list-of-dates map.
	 */
	public Map<String, List<Long>> getChecksumToDateListMap() {
		Map<String, List<Long>> res = new HashMap<String, List<Long>>();
		for(Map.Entry<Long, CDXEntry> entry : map.entrySet()) {
			insertEntryIntoMap(entry.getKey(), entry.getValue().getDigest(), res);
		}
		
		return res;
	}
	

	/**
	 * Inserts the CDX entry date into the map.
	 * The date for the entry is inserted into a list for the checksum/digest. 
	 * @param entry The CDX entry.
	 * @param map The map to insert it into.
	 */
	protected void insertEntryIntoMap(Long date, String checksum, Map<String, List<Long>> map) {
		List<Long> dates = map.get(checksum);
		if(dates == null) {
			dates = new ArrayList<Long>();
		}
		
		dates.add(date);
		map.put(checksum, dates);
	}
}
