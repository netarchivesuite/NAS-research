package dk.netarkivet.research.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.netarkivet.research.cdx.CDXEntry;

/**
 * Utility class with functions for extracted specified data from CDX entries.
 */
public class CDXUtils {
    /** Logging mechanism. */
    private static Logger logger = LoggerFactory.getLogger(CDXUtils.class);
	
	/**
	 * Extracts the job ID info for the harvest job id in the filename in the CDX entry.
	 * @param entry The CDX entry.
	 * @return The harvest job ID. Or null if no ID could be extracted from the CDX entry.
	 */
	public static Long extractJobID(CDXEntry entry) {
		if(entry == null) {
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
		return Long.parseLong(filename.split("[-]")[0]);
	}
	

	
	/**
	 * Adds a CDX element to a string buffer.
	 * Will add a '-' if the element is null or empty.
	 * @param element The element to add to the string buffer. 
	 * @param sb The string buffer where the element should be added.
	 */
	public static void addCDXElementToStringBuffer(Object element, StringBuilder sb) {
		if(element == null) {
			sb.append("-");
		} else {
			String s = (element instanceof String) ? (String) element : element.toString();
			if(s.isEmpty()) {
				sb.append("-");
			} else {
				sb.append(s);
			}
		}
	}
}
