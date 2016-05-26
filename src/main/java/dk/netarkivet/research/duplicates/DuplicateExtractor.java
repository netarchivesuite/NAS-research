package dk.netarkivet.research.duplicates;

import java.util.Collection;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.netarkivet.research.cdx.CDXEntry;
import dk.netarkivet.research.cdx.CDXExtractor;
import dk.netarkivet.research.harvestdb.HarvestJobExtractor;
import dk.netarkivet.research.harvestdb.HarvestJobInfo;
import dk.netarkivet.research.utils.DateUtils;

/**
 * Finds duplicates.
 * Extracts CDX indices and compare the digest checksum to find duplicates.
 * Creates a map between the checksum and a list of dates for the checksums (dates in millis from epoch).
 */
public class DuplicateExtractor {
    /** Logging mechanism. */
    private static Logger logger = LoggerFactory.getLogger(DuplicateExtractor.class);

	/** The CDX extractor.*/
	protected final CDXExtractor cdxExtractor;
	/** The Harvest Job extractor.*/
	protected HarvestJobExtractor jobExtractor;
	
	/**
	 * Constructor.
	 * @param cdxExtractor The CDX extractor.
	 * @param jobExtractor The Harvest Job extractor.
	 */
	public DuplicateExtractor(CDXExtractor cdxExtractor, HarvestJobExtractor jobExtractor) {
		this.cdxExtractor = cdxExtractor;
		this.jobExtractor = jobExtractor;
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
		Collection<CDXEntry> cdxs = cdxExtractor.retrieveAllCDX(url);

		DuplicateMap res = new DuplicateMap();
		
		for(CDXEntry entry : cdxs) {
			if(DateUtils.checkDateInterval(entry, earliestDate, latestDate)) {
				res.addElement(entry, extractJobInfo(entry));
			}
		}
		
		return res;
	}
	
	/**
	 * Extracts the harvest job info for the harvest job id in the filename in the CDX entry.
	 * @param entry The CDX entry.
	 * @return The harvest job info. Or null if something goes wrong, e.g. malformed filename or missing extractor.
	 */
	protected HarvestJobInfo extractJobInfo(CDXEntry entry) {
		if(jobExtractor == null) {
			return null;
		}
		String filename = entry.getFilename();
		if(filename == null || filename.isEmpty()) {
			return null;
		}
		if(!filename.contains("-")) {
			logger.warn("CDXEntry with odd filename: " + filename);
			return null;
		}
		Long jobId = Long.parseLong(filename.split("[-]")[0]);
		return jobExtractor.extractJob(jobId);
	}
}
