package dk.netarkivet.research.cdx;

import java.util.Collection;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.netarkivet.research.wid.WaybackWID;

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
	 * 
	 * @param allCDXforUrl
	 * @param filename
	 * @return
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
