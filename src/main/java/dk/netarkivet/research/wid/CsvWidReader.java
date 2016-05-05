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
		if(csvFile == null || !csvFile.isFile()) {
			throw new IllegalArgumentException("The csv file '" + csvFile + "' is not a file.");
		}
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
				WID wpid = extractWID(line);
				if(wpid != null) {
					res.add(wpid);
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
			// TODO
		}
		
		logger.trace("Failed to extract WID from line '" + line + "', since "
				+ "it does not start with 'X' or 'W'.");
		return null;			
		
	}
	
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

}
