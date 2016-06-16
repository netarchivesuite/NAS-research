package dk.netarkivet.research.interval;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.netarkivet.research.exception.ArgumentCheck;
import dk.netarkivet.research.utils.DateUtils;

/**
 * Extracts URL intervals from a CSV file.
 * 
 * Must be in the format:
 * 'W/X';'url';'earliest date';'latest date';
 */
public class CsvUrlIntervalReader {
    /** Logging mechanism. */
    private static Logger logger = LoggerFactory.getLogger(CsvUrlIntervalReader.class);
    
	/** The CSV file with the URL interval data.*/
	protected File csvFile;

	/** 
	 * Constructor.
	 * @param csvFile The CSV file.
	 */
	public CsvUrlIntervalReader(File csvFile) {
		ArgumentCheck.checkIsFile(csvFile, "File csvFile");
		this.csvFile = csvFile;
	}

	/**
	 * Retrieves all the URL intervals from the CSV file.
	 * Ignores malformed and empty lines.
	 * @return The collection of URL intervals.
	 */
	public Collection<UrlInterval> extractAllUrlIntervals() {
		List<UrlInterval> res = new ArrayList<UrlInterval>();
		String line;
		try (
			InputStream fis = new FileInputStream(csvFile);
			InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
			BufferedReader br = new BufferedReader(isr);
		) {
			while ((line = br.readLine()) != null) {
				UrlInterval urlInterval = extractUrlInterval(line);
				if(urlInterval != null) {
					res.add(urlInterval);
				}
			}
		} catch (IOException e) {
			throw new IllegalStateException("Failed extracting URL intervals from file '" + csvFile + "'.", e);
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
	protected UrlInterval extractUrlInterval(String line) {
		String[] split = line.split("[;,]");
		
		// Ignore, if it is an empty line
		if(split.length == 0) {
			logger.debug("Ignoring an empty line.");
			return null;
		}
		if(split.length < 2) {
			logger.debug("Ignoring too short a line.");
			return null;
		}
		if(!split[0].equalsIgnoreCase("x") && !split[0].equalsIgnoreCase("w")) {
			logger.trace("Failed to extract WID from line '" + line + "', since "
					+ "it does not start with 'X' or 'W'.");
			return null;			
		}
		
		String url = split[1];
		Date earliestDate = null;
		Date latestDate = null;
		if(split.length > 2) {
			earliestDate = DateUtils.extractCsvDate(split[2]);
			if(split.length > 3) {
				latestDate = DateUtils.extractCsvDate(split[3]);
			}
		}
		
		return new UrlInterval(url, earliestDate, latestDate);		
	}
}
