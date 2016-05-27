package dk.netarkivet.research;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.netarkivet.research.cdx.CDXEntry;
import dk.netarkivet.research.cdx.CDXExtractor;
import dk.netarkivet.research.cdx.CDXFileWriter;
import dk.netarkivet.research.cdx.DabCDXExtractor;
import dk.netarkivet.research.http.HttpRetriever;
import dk.netarkivet.research.utils.DateUtils;
import dk.netarkivet.research.utils.FileUtils;
import dk.netarkivet.research.utils.UrlUtils;

/**
 * Extracts the CDX for URLs with an optional time interval.
 * 
 * Uses a CSV-file argument in the format:
 * "url;earliest date; latest date"
 * Both dates are optional, and should only be applied if the interval is wanted.
 */
public class NASExtractCDXForURLs {
    /** Logging mechanism. */
    private static Logger logger = LoggerFactory.getLogger(NASExtractCDXForURLs.class);

    /**
     * Main.
     * Requires 2 arguments, and has 2 more optional arguments.
     * @param args The arguments.
     */
	public static void main(String ... args ) {
		if(args.length < 2) {
			System.err.println("Not enough arguments. Requires the following arguments:");
			System.err.println(" 1. Input file, containing lines where the first element is the URL to search for");
			System.err.println(" 2. the base URL to the CDX-server.");
			System.err.println(" 3. (OPTIONAL) output directory, otherwise it is printed.");
			throw new IllegalArgumentException();
		}

		File inputFile = new File(args[0]);
		if(!inputFile.isFile()) {
			throw new IllegalArgumentException("The input file '" + inputFile.getAbsolutePath() + "' is not a valid file "
					+ "(either does not exists or is a directory)");
		}

		String cdxServerBaseUrl = args[1];
		try {
			new URL(cdxServerBaseUrl);
		} catch (IOException e) {
			throw new IllegalArgumentException("The CSX Server url '" + cdxServerBaseUrl + "' is invalid.", e);
		}

		File outDir;
		outDir = FileUtils.createDir( args.length > 2 ? args[2] : ".");

		CDXExtractor cdxExtractor = new DabCDXExtractor(cdxServerBaseUrl, new HttpRetriever());

		NASExtractCDXForURLs findCDX = new NASExtractCDXForURLs(cdxExtractor, inputFile, outDir, DabCDXExtractor.getDefaultCDXFormat());
		findCDX.extractCDX();
	}
	
	/** The extractor of duplicates.*/
	private final CDXExtractor extractor;
	/** The CSV input file.*/
	private final File csvFile;
	/** The output directory.*/
	private final File outputDir;
	/** The format of the CDX file.*/
	private final Collection<Character> cdxFormat;
	
	/**
	 * Constructor.
	 * @param duplicateExtractor The duplicate extractor.
	 * @param csvFile The input CSV file.
	 * @param outputDir The output directory.
	 */
	protected NASExtractCDXForURLs(CDXExtractor cdxExtractor, File csvFile, File outputDir, 
			Collection<Character> cdxFormat) {
		this.extractor = cdxExtractor;
		this.csvFile = csvFile;
		this.outputDir = outputDir;
		this.cdxFormat = cdxFormat;
	}
	
	/**
	 * Goes through every line in the input file, and extracts the duplicates.
	 */
	protected void extractCDX() {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(csvFile), 
				Charset.defaultCharset()))) {
			String line;
			while((line = reader.readLine()) != null) {
				String[] split = line.split("[;,]");
				if(split != null && split.length > 0 && ("x".equalsIgnoreCase(split[0]) || "w".equalsIgnoreCase(split[0]))) {
					makeDuplicateFilesForCSVLine(split);
				} else {
					logger.debug("Ignoring line: " + line);
				}
			}
		} catch (IOException e) {
			throw new IllegalStateException("Failed to read or write data.", e);
		}
	}

	/**
	 * Creates 2 files from the duplicate results.
	 * One for the map directly ("url;checksum;date" for all entries)
	 * The other for specs about each unique checksum ("checksum;amount;earliest;latest")
	 * 
	 * @param cdxExtractor The duplicate finder.
	 * @param split The CSV line in the format "x;url;earliest date;latest date" - with both dates as optional. 
	 * @param outDir The directory where the output files should be placed.
	 */
	protected void makeDuplicateFilesForCSVLine(String[] split) 
			throws IOException {
		String url = split[1];
		Date earliestDate = null;
		Date latestDate = null;
		if(split.length > 2) {
			earliestDate = DateUtils.extractCsvDate(split[2]);
			if(split.length > 3) {
				latestDate = DateUtils.extractCsvDate(split[3]);
			}
		}

		Collection<CDXEntry> entries = extractor.retrieveCDXForInterval(url, earliestDate, latestDate);
		String urlFilename = UrlUtils.fileEncodeUrl(url);
		
		createResultFile(entries, urlFilename);
	}
	
	/**
	 * Creates the map-results file.
	 * @param map The map with the duplicate results.
	 * @param filename The basename of the file.
	 * @throws IOException If it fails to to write the output file.
	 */
	protected void createResultFile(Collection<CDXEntry> entries, String filename) throws IOException {
		File mapOutputFile = FileUtils.ensureNewFile(outputDir, filename + ".cdx");
		CDXFileWriter cdxWriter = new CDXFileWriter(mapOutputFile);
		cdxWriter.writeCDXEntries(entries, cdxFormat);
	}
}
