package dk.netarkivet.research.duplicates;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

import dk.netarkivet.research.cdx.CDXEntry;
import dk.netarkivet.research.harvestdb.HarvestJobInfo;

/**
 * The result map for duplicate result.
 */
public class DuplicateMap {
	/** The map between the CDX entry and the info about the job for the cdx entry.*/
	protected final Map<CDXEntry, HarvestJobInfo> map;
	
	/**
	 * Constructor.
	 */
	public DuplicateMap() {
		map = new HashMap<CDXEntry, HarvestJobInfo>();
	}
	
	/**
	 * Adds an element to the map.
	 * @param date The date in millis from epoc.
	 * @param checksum The checksum string.
	 */
	public void addElement(CDXEntry entry, HarvestJobInfo jobInfo) {
		map.put(entry, jobInfo);
	}
	
	/**
	 * Extract the map as a date-to-checksum map.
	 * @return The date-to-checksum map.
	 */
	public Map<Long, CDXEntry> getDateToChecksumMap() {
		Map<Long, CDXEntry> res = new ConcurrentSkipListMap<Long, CDXEntry>();
		for(CDXEntry entry : map.keySet()) {
			res.put(entry.getDate(), entry);
		}
		return res;
	}
	
	/**
	 * Extract the map as a checksum-to-list-of-dates.
	 * @return The checksum-to-list-of-dates map.
	 */
	public Map<String, List<Long>> getChecksumToDateListMap() {
		Map<String, List<Long>> res = new HashMap<String, List<Long>>();
		for(CDXEntry entry : map.keySet()) {
			insertEntryIntoMap(entry.getDate(), entry.getDigest(), res);
		}
		
		return res;
	}
	
	/**
	 * Retrieves the map between cdx entries and harvest job info.
	 * @return The map.
	 */
	public Map<CDXEntry, HarvestJobInfo> getMap() {
		return new HashMap<CDXEntry, HarvestJobInfo>(map);
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
