package dk.netarkivet.research.wid;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.netarkivet.research.exception.ArgumentCheck;
import dk.netarkivet.research.utils.DateUtils;

/**
 * Readers a CSV file with WPIDs from research projects in Netarkivet.dk.
 * 
 * Only uses the lines with an X or W in the first column.
 * 
 * The lines with X in the first column must have the format:
 * "X;ignore;URL;DATE;ignore..."
 * Where the 'ignore' columns are ignored. Neither URL nor DATE may be null.
 * 
 * The lines with W in the first column must have the following format:
 * "W;ignore;URL;DATE;FILENAME;ignore..."
 * Where the 'ignore' columns are ignored. The URL may not be null, and either
 * DATE or FILENAME must be present, though one of them is allowed to be null.
 * 
 * All rows which does not have X or W in the first column is ignored.
 * 
 * Also, the file must be in UTF-8 format.
 */
public class CsvWidReader implements WidReader {
    /** Logging mechanism. */
    private static Logger logger = LoggerFactory.getLogger(CsvWidReader.class);
    
	/** The CSV file with the WPID data.*/
	protected File csvFile;

	/** 
	 * Constructor.
	 * @param csvFile The CSV file.
	 */
	public CsvWidReader(File csvFile) {
		ArgumentCheck.checkIsFile(csvFile, "File csvFile");
		this.csvFile = csvFile;
	}

	@Override
	public Collection<WID> extractAllWIDs() {
		List<WID> res = new ArrayList<WID>();
		String line;
		try (
			InputStream fis = new FileInputStream(csvFile);
			InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
			BufferedReader br = new BufferedReader(isr);
		) {
			while ((line = br.readLine()) != null) {
				WID wid = extractWID(line);
				if(wid != null) {
					res.add(wid);
				}
			}
		} catch (IOException e) {
			logger.info("Failed extracting PWIDs from file '" + csvFile + "'.", e);
			return null;
		}
		return res;
	}
	
	/**
	 * Extracts a WPID from a line in the CSV file.
	 * The line must have the format 'x;[URL];[DATE];*'.
	 * We split on both ',' and ';'
	 * @param line The line.
	 * @return The WPID, or null if the line does not have the right format.
	 */
	protected WID extractWID(String line) {
		String split[] = line.split("[;,]");
		
		// Ignore, if it is an empty line
		if(split.length == 0) {
			logger.debug("Ignoring an empty line.");
			return null;
		}
		
		if(split[0].equalsIgnoreCase("x")) {
			return extractFulltextWPID(split);
		} else if(split[0].equalsIgnoreCase("w")) {
			return extractWaybackWID(split);
		}
		
		logger.trace("Failed to extract WID from line '" + line + "', since "
				+ "it does not start with 'X' or 'W'.");
		return null;			
		
	}
	
	/**
	 * Extracts the WPID from a fulltext line.
	 * @param splitLine The array of line segments.
 	 * @return The WPID, or null if the arguments are not valid.
	 */
	protected WID extractFulltextWPID(String[] splitLine) {
		// Ignore, if the line has less that 3 elements
		if(splitLine.length < 4) {
			logger.info("Failed to extract WPID from line elements '" + Arrays.asList(splitLine) + "', since "
					+ "it does not have at least 3 elements.");
			return null;
		}
		
		String url = splitLine[2];
		// Ignore, if the url is empty or not valid
		if(url.isEmpty()) {
			logger.info("Failed to extract PWID from line elements '" + Arrays.asList(splitLine) + "', since "
					+ "the URL is missing.");
			return null;
		}
		try {
			new URL(url);
		} catch (MalformedURLException e) {
			logger.info("Failed to extract PWID from line elements '" + Arrays.asList(splitLine) + "', since "
					+ "the URL is invalid.", e);
			return null;
		}
		
		String dateString = splitLine[3];
		// Ignore line, if the date is missing or invalid
		if(dateString.isEmpty()) {
			logger.info("Failed to extract PWID from line elements '" + Arrays.asList(splitLine) + "', since "
					+ "the date is missing.");
			return null;
		}
		Date date = DateUtils.extractCsvDate(dateString);
		if(date == null) {
			logger.info("Failed to extract PWID from line elements '" + Arrays.asList(splitLine) + "', since "
					+ "the date cannot be extracted.");
			return null;
		}
		
		return WPID.createNarkWPid(url, date);
	}
	
	/**
	 * Creates a WaybackWID from the line from wayback.
	 * @param splitLine The array of line segments.
	 * @return the WaybackWID or null if something went wrong.
	 */
	protected WID extractWaybackWID(String[] splitLine) {
		// Ignore, if the line has less that 3 elements
		if(splitLine.length < 4) {
			logger.info("Failed to extract WPID from line elements '" + Arrays.asList(splitLine) + "', since "
					+ "it does not have at least 3 elements.");
			return null;
		}
		
		String url = splitLine[2];
		// Ignore, if the url is empty or not valid
		if(url.isEmpty()) {
			logger.info("Failed to extract PWID from line elements '" + Arrays.asList(splitLine) + "', since "
					+ "the URL is missing.");
			return null;
		}
		try {
			new URL(url);
		} catch (MalformedURLException e) {
			logger.info("Failed to extract PWID from line elements '" + Arrays.asList(splitLine) + "', since "
					+ "the URL is invalid.", e);
			return null;
		}
		
		String dateString = splitLine[3];
		Date date = DateUtils.extractCsvDate(dateString);
		
		String filename = null;
		if(splitLine.length >= 5) {
			filename = splitLine[4];
		}
		
		if(date == null && filename == null) {
			logger.info("Requires either date or filename for the wayback.");
			return null;
		}
		
		return WaybackWID.createNarkWaybackWID(filename, url, date);
	}
}
