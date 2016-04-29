package dk.netarkivet.research;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Map;

import dk.netarkivet.research.cdx.CDXExtractor;
import dk.netarkivet.research.cdx.DabCDXExtractor;
import dk.netarkivet.research.duplicates.DuplicateExtractor;
import dk.netarkivet.research.utils.DateUtils;
import dk.netarkivet.research.utils.FileUtils;
import dk.netarkivet.research.utils.ListUtils;
import dk.netarkivet.research.utils.UrlUtils;

/**
 * Find duplicates for URLs in the CDX
 * 
 * Uses a CSV-file argument in the format:
 * "url;earliest date; latest date"
 * Both the dates may be missing or the empty string, if an earliest or latest date respectively is not wanted.
 */
public class NASFindDuplicatesForURLs {

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
		if(args.length > 2) {
			outDir = new File(args[2]);
			if(outDir.isFile()) {
				throw new IllegalArgumentException("The location for the output file is not vacent.");
			} else {
				outDir.mkdirs();
			}
		} else {
			outDir = new File(".");
		}

		CDXExtractor cdxExtractor = new DabCDXExtractor(cdxServerBaseUrl);
		DuplicateExtractor duplicateExtractor = new DuplicateExtractor(cdxExtractor);

		NASFindDuplicatesForURLs findDuplicates = new NASFindDuplicatesForURLs(duplicateExtractor, inputFile, outDir);
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
	protected NASFindDuplicatesForURLs(DuplicateExtractor duplicateExtractor, File csvFile, File outputDir) {
		this.extractor = duplicateExtractor;
		this.csvFile = csvFile;
		this.outputDir = outputDir;
	}
	
	/**
	 * Goes through every line in the input file, and extracts the duplicates.
	 */
	protected void findDuplicates() {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(csvFile)))) {
			String line;
			while((line = reader.readLine()) != null) {
				makeDuplicateFilesForCSVLine(line);
			}
			
		} catch (IOException e) {
			throw new IllegalStateException("Failed to read or write data.");
		}
	}

	/**
	 * Creates 2 files from the duplicate results.
	 * One for the map directly ("url;checksum;date" for all entries)
	 * The other for specs about each unique checksum ("checksum;amount;earliest;latest")
	 * 
	 * @param extractor The duplicate finder.
	 * @param line The CSV line in the format "url;earliest date;latest date" - with both dates as optional. 
	 * @param outDir The directory where the output files should be placed.
	 */
	protected void makeDuplicateFilesForCSVLine(String line) 
			throws IOException {
		String[] split = line.split(";");
		String url = split[0];
		Date earliestDate = null;
		Date latestDate = null;
		if(split.length > 1) {
			earliestDate = DateUtils.extractCsvDate(split[1]);
			if(split.length > 2) {
				latestDate = DateUtils.extractCsvDate(split[2]);
			}
		}

		Map<String, List<Long>> map = extractor.makeDuplicateMap(url, earliestDate, latestDate);
		String urlFilename = UrlUtils.fileEncodeUrl(url);
		
		createMapResultFile(map, urlFilename, url);
		
		createOtherResultFile(map, urlFilename, url);
	}
	
	/**
	 * Creates the map-results file.
	 * @param map The map with the duplicate results.
	 * @param filename The basename of the file.
	 * @param url The URL of the results.
	 * @throws IOException If it fails to to write the output file.
	 */
	protected void createMapResultFile(Map<String, List<Long>> map, String filename, String url) throws IOException {
		// TODO make it in the new format ('number;date;??;...'??)
		
		File mapOutputFile = FileUtils.ensureNewFile(outputDir, filename + ".map");
		try(FileOutputStream fos = new FileOutputStream(mapOutputFile)) {
			fos.write("url;checksum;date\n".getBytes());
			for(Map.Entry<String, List<Long>> entry : map.entrySet()) {
				for(Long l : entry.getValue()) {
					String csvLine = url + ";" + entry.getKey() + ";" + new Date(l).toString() + "\n";
					fos.write(csvLine.getBytes());
				}
			}
		}
	}
	
	/**
	 * Creates the other results file.
	 * @param map The map with the duplicate results.
	 * @param filename The basename of the file.
	 * @param url The URL of the results.
	 * @throws IOException If it fails to to write the output file.
	 */
	protected void createOtherResultFile(Map<String, List<Long>> map, String filename, String url) throws IOException {
		File txtOutputFile = FileUtils.ensureNewFile(outputDir, filename + ".txt");
		try(FileOutputStream fos = new FileOutputStream(txtOutputFile)) {
			fos.write("checksum;amount;earliest date;latest date\n".getBytes());
			for(Map.Entry<String, List<Long>> entry : map.entrySet()) {
				String csvLine = entry.getKey() + ";" + entry.getValue().size() + ";" 
						+ new Date(ListUtils.getSmallest(entry.getValue())).toString() 
						+ ";" + new Date(ListUtils.getLargest(entry.getValue())).toString() + "\n";
				fos.write(csvLine.getBytes());
			}
		}
	}
}
