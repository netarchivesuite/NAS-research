package dk.netarkivet.research.wpid;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.netarkivet.research.utils.DateUtils;

/**
 * Readers a CSV file with WPIDs from research projects in Netarkivet.dk.
 * 
 * Only uses the lines with an X in the first column.
 * It must have the URL and the date in the two following columns.
 * The other columns are ignored.
 * 
 * All rows which does not have an X in the first column is ignored.
 * 
 * Also, the file must be in UTF-8 format.
 */
public class CsvWpidReader implements WPidReader {
    /** Logging mechanism. */
    private static Logger logger = LoggerFactory.getLogger(CsvWpidReader.class);
    
	/** The CSV file with the WPID data.*/
	protected File csvFile;

	/** 
	 * Constructor.
	 * @param csvFile The CSV file.
	 */
	public CsvWpidReader(File csvFile) {
		this.csvFile = csvFile;
	}

	@Override
	public Collection<WPID> extractAllWPIDs() {
		List<WPID> res = new ArrayList<WPID>();
		String line;
		try (
			InputStream fis = new FileInputStream(csvFile);
			InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
			BufferedReader br = new BufferedReader(isr);
		) {
			while ((line = br.readLine()) != null) {
				WPID wpid = extractWPID(line);
				if(wpid != null) {
					res.add(wpid);
				}
			}
		} catch (Exception e) {
			logger.info("Failed extracting PWIDs from file '" + csvFile + "'.", e);
			return null;
		}
		return res;
	}
	
	/**
	 * Extracts a WPID from a line in the CSV file.
	 * The line must have the format 'x;[URL];[DATE];*'.
	 * @param line The line.
	 * @return The WPID, or null if the line does not have the right format.
	 */
	protected WPID extractWPID(String line) {
		String split[];
		// split on ',' or ';'
		if(line.contains(";")) {
			split = line.split(";");
		} else {
			split = line.split(",");
		}
		
		// Ignore, if it is an empty line
		if(split.length == 0) {
			logger.debug("Ignoring an empty line.");
			return null;
		}
		
		// Ignore, if first column is not X
		if(!split[0].equalsIgnoreCase("x")) {
			logger.trace("Failed to extract PWID from line '" + line + "', since "
					+ "it does not start with 'X'.");
			return null;			
		}
		
		// Ignore, if the line has less that 3 elements
		if(split.length < 3) {
			logger.info("Failed to extract PWID from line '" + line + "', since "
					+ "it does not have at least 3 elements.");
			return null;
		}
		
		String url = split[2];
		// Ignore, if the url is empty or not valid
		if(url == null || url.isEmpty()) {
			logger.info("Failed to extract PWID from line '" + line + "', since "
					+ "the URL is missing.");
			return null;
		}
		try {
			new URL(url);
		} catch (MalformedURLException e) {
			logger.info("Failed to extract PWID from line '" + line + "', since "
					+ "the URL is invalid.", e);
			return null;
		}
		
		String dateString = split[3];
		// Ignore, if the date is missing or invalid
		if(dateString == null || dateString.isEmpty()) {
			logger.info("Failed to extract PWID from line '" + line + "', since "
					+ "the date is missing.");
			return null;
		}
		Date date = DateUtils.extractCsvDate(dateString);
		if(date == null) {
			logger.info("Failed to extract PWID from line '" + line + "', since "
					+ "the date cannot be extracted.");
			return null;
		}
		
		return WPID.createNarkWPid(url, date);
	}

}
