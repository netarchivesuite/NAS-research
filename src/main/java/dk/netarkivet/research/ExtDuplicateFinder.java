package dk.netarkivet.research;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.netarkivet.research.cdx.CDXEntry;
import dk.netarkivet.research.cdx.CDXExtractor;
import dk.netarkivet.research.cdx.DabCDXExtractor;
import dk.netarkivet.research.duplicates.DuplicateExtractor;
import dk.netarkivet.research.duplicates.DuplicateMap;
import dk.netarkivet.research.harvestdb.HarvestJobExtractor;
import dk.netarkivet.research.harvestdb.HarvestJobInfo;
import dk.netarkivet.research.harvestdb.NasHarvestJobExtractor;
import dk.netarkivet.research.http.HttpRetriever;
import dk.netarkivet.research.interval.UrlInterval;
import dk.netarkivet.research.utils.DateUtils;
import dk.netarkivet.research.utils.FileUtils;
import dk.netarkivet.research.utils.UrlUtils;

/**
 * Find duplicates for URLs in the CDX
 * 
 * Uses a CSV-file argument in the format:
 * "url;earliest date; latest date"
 * Both the dates may be missing or the empty string, if an earliest or latest date respectively is not wanted.
 */
public class ExtDuplicateFinder {
    /** Logging mechanism. */
    private static Logger logger = LoggerFactory.getLogger(ExtDuplicateFinder.class);

    /**
     * Main program.
     * @param args Arguments for the program.
     */
	public static void main(String ... args ) {
		if(args.length < 2) {
			System.err.println("Not enough arguments. Requires the following arguments:");
			System.err.println(" 1. Input file, containing lines where the first element is the URL to search for");
			System.err.println(" 2. the base URL to the CDX-server.");
			System.err.println(" 3. (OPTIONAL) output directory, otherwise it is printed.");
			System.err.println(" 4. (OPTIONAL) whether or not to use the actual job database.");
			throw new IllegalArgumentException();
		}

		File inputFile = new File(args[0]);
		if(!inputFile.isFile()) {
			throw new IllegalArgumentException("The input file '" + inputFile.getAbsolutePath() + "' "
					+ "is not a valid file (either does not exists or is a directory)");
		}

		String cdxServerBaseUrl = args[1];
		try {
			new URL(cdxServerBaseUrl);
		} catch (IOException e) {
			throw new IllegalArgumentException("The CSX Server url '" + cdxServerBaseUrl + "' is invalid.", e);
		}

		File outDir;
		outDir = FileUtils.createDir( args.length > 2 ? args[2] : ".");

		HarvestJobExtractor jobExtractor;
		if(args.length > 3 && args[3].startsWith("y")) {
			jobExtractor = null;
		} else {
			jobExtractor = new NasHarvestJobExtractor();
		}
		
		CDXExtractor cdxExtractor = new DabCDXExtractor(cdxServerBaseUrl, new HttpRetriever());
		DuplicateExtractor duplicateExtractor = new DuplicateExtractor(cdxExtractor, jobExtractor);

		ExtDuplicateFinder findDuplicates = new ExtDuplicateFinder(duplicateExtractor, inputFile, outDir);
		findDuplicates.findDuplicates();
	}
	
	/** The extractor of duplicates.*/
	private final DuplicateExtractor extractor;
	/** The CSV input file.*/
	private final File csvFile;
	/** The output directory.*/
	private final File outputDir;
	
	/**
	 * Constructor.
	 * @param duplicateExtractor The duplicate extractor.
	 * @param csvFile The input CSV file.
	 * @param outputDir The output directory.
	 */
	protected ExtDuplicateFinder(DuplicateExtractor duplicateExtractor, File csvFile, File outputDir) {
		this.extractor = duplicateExtractor;
		this.csvFile = csvFile;
		this.outputDir = outputDir;
	}
	
	/**
	 * Goes through every line in the input file, and extracts the duplicates.
	 */
	protected void findDuplicates() {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(csvFile), 
				Charset.defaultCharset()))) {
			String line;
			while((line = reader.readLine()) != null) {
				String[] split = line.split("[;,]");
				if(split != null && split.length > 0 && ("x".equalsIgnoreCase(split[0]) 
						|| "w".equalsIgnoreCase(split[0]))) {
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

		DuplicateMap map = extractor.makeDuplicateMap(new UrlInterval(url, earliestDate, latestDate));
		String urlFilename = UrlUtils.fileEncodeUrl(url);
		
		createMapResultFile(map, urlFilename, url);
	}
	
	/**
	 * Creates the map-results file.
	 * @param map The map with the duplicate results.
	 * @param filename The basename of the file.
	 * @param url The URL of the results.
	 * @throws IOException If it fails to to write the output file.
	 */
	protected void createMapResultFile(DuplicateMap map, String filename, String url) throws IOException {
		File mapOutputFile = FileUtils.ensureNewFile(outputDir, filename + ".txt");
		try(FileOutputStream fos = new FileOutputStream(mapOutputFile)) {
			String firstLine = "duplicate number;date;checksum;status;url;harvestjob;harvest name;harvest type\n";
			fos.write(firstLine.getBytes(Charset.defaultCharset()));
			List<String> checksumIndices = new ArrayList<String>();
			for(Map.Entry<Long, CDXEntry> entry : map.getDateToChecksumMap().entrySet()) {
				String csvIndex = "-1";
				if(entry.getValue().getStatusCode() == 200) {
					int checksumIndex = checksumIndices.indexOf(entry.getValue().getDigest());
					if(checksumIndex == -1) {
						checksumIndex = checksumIndices.size();
						checksumIndices.add(entry.getValue().getDigest());
					}
					csvIndex = "" + (checksumIndex+1);
				}
				String output = csvIndex + ";" + DateUtils.dateToWaybackDate(new Date(entry.getKey())) + ";" 
						+ entry.getValue().getDigest() + ";" + entry.getValue().getStatusCode() + ";" + url;
				HarvestJobInfo jobInfo = map.getHarvestJobInfo(entry.getValue());
				if(jobInfo != null) {
					output += ";" + jobInfo.getId() + ";" + jobInfo.getName() + ";" + jobInfo.getType();
				} else {
					output += ";N/A;N/A;N/A";
				}
				fos.write((output + "\n").getBytes(Charset.defaultCharset()));
			}
		}
	}
}
