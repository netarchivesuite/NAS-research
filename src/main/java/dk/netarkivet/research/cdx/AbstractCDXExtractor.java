package dk.netarkivet.research.cdx;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.netarkivet.research.utils.DateUtils;
import dk.netarkivet.research.wid.WaybackWID;

/**
 * Common abstract interface for CDX extractors.
 * Contains common methods for the different CDX extractors.
 */
public abstract class AbstractCDXExtractor implements CDXExtractor {
	/** The log.*/
	private static Logger logger = LoggerFactory.getLogger(AbstractCDXExtractor.class);

	@Override
	public CDXEntry retrieveCDX(WaybackWID wid) {
		Collection<CDXEntry> allCDXforUrl = retrieveAllCDX(wid.getUrl());
		CDXEntry res = findCDXwithFile(allCDXforUrl, wid.getFilename());
		if(res == null) {
			res = retrieveCDXclosestToDate(allCDXforUrl, wid.getDate());
		}
		return res;
	}
	
	@Override
	public Collection<CDXEntry> retrieveCDXForInterval(String url, Date earliestDate, Date latestDate) {
		Collection<CDXEntry> entries = retrieveAllCDX(url);
		if(earliestDate == null && latestDate == null) {
			return entries;
		}
		
		List<CDXEntry> res = new ArrayList<CDXEntry>();
		for(CDXEntry entry : entries) {
			if(DateUtils.checkDateInterval(entry, earliestDate, latestDate)) {
				res.add(entry);
			}
		}
		return res;
	}
	
	/**
	 * Retrieves the CDX index closest to the date.
	 * @param allCDXforUrl The list of all extracted CDX entries.
	 * @param date The date.
	 * @return The CDX entry closest to the date.
	 */
	protected CDXEntry retrieveCDXclosestToDate(Collection<CDXEntry> allCDXforUrl, Date date) {
		if(allCDXforUrl == null || allCDXforUrl.isEmpty()) {
			logger.info("No CDX indices. Returning null.");
			return null;
		}
		if(date == null) {
			logger.info("No date. Returning null.");
			return null;
		}
		
		long closestDate = Long.MAX_VALUE;
		CDXEntry res = null;
		
		for(CDXEntry entry : allCDXforUrl) {
			Long timeDiff = Math.abs(entry.getDate() - date.getTime());
			if(timeDiff < closestDate) {
				closestDate = timeDiff;
				res = entry;
			}
		}
		
		return res;
	}
	
	/**
	 * Finds the CDX entry with a given filename within a collection of CDX indices. 
	 * @param allCDXforUrl The list of CDX indices.
	 * @param filename The name of the file.
	 * @return The entry with the filename. Or null, if no such entry was found.
	 */
	protected CDXEntry findCDXwithFile(Collection<CDXEntry> allCDXforUrl, String filename) {
		for(CDXEntry entry : allCDXforUrl) {
			if(filename.equalsIgnoreCase(entry.getFilename())) {
				return entry;
			}
		}
		return null;
	}	
}
