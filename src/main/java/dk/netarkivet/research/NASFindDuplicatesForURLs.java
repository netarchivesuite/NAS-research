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
import dk.netarkivet.research.duplicates.DuplicateFinder;
import dk.netarkivet.research.utils.FileUtils;
import dk.netarkivet.research.utils.ListUtils;
import dk.netarkivet.research.utils.UrlUtils;

public class NASFindDuplicatesForURLs {

	public static void main( String[] args ) {
		if(args.length < 2) {
			System.err.println("Not enough arguments. Requires the following arguments:");
			System.err.println(" 1. Input file, containing lines where the first element is the URL to search for");
			System.err.println(" 2. the base URL to the CDX-server.");
			System.err.println(" 3. (OPTIONAL) output directory, otherwise it is printed.");
			System.exit(-1);
		}

		File inputFile = new File(args[0]);
		if(!inputFile.isFile()) {
			System.err.println("The input file '" + inputFile.getAbsolutePath() + "' is not a valid file "
					+ "(either does not exists or is a directory)");
			System.exit(-1);
		}

		String cdxServerBaseUrl = args[1];
		try {
			new URL(cdxServerBaseUrl);
		} catch (IOException e) {
			System.err.println("The CSX Server url '" + cdxServerBaseUrl + "' is invalid.");
			e.printStackTrace(System.err);
			System.exit(-1);
		}

		File outDir;
		if(args.length > 2) {
			outDir = new File(args[2]);
		} else {
			outDir = new File(".");
		}
		if(outDir.isFile()) {
			System.err.println("The location for the output file is not vacent.");
			System.exit(-1);
		} else {
			outDir.mkdirs();
		}
		CDXExtractor extractor = new DabCDXExtractor(cdxServerBaseUrl);
		DuplicateFinder finder = new DuplicateFinder(extractor);


		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile)))) {
			String line;
			while((line = reader.readLine()) != null) {
				String url = line;
				if(line.contains(";")) {
					url = line.split(";")[0];
				}
				if(url.contains(" ")) {
					url = url.split(" ")[0];
				}
				makeDuplicateFilesForUrl(finder, url, outDir);
			}
			
		} catch (IOException e) {
			System.err.println("Failed to read or write data.");
			e.printStackTrace();
			System.exit(-1);
		}
	}

	/**
	 * Creates 2 files from the duplicate results.
	 * One for the map directly ("url;checksum;date" for all entries)
	 * The other for specs about each unique checksum ("checksum;amount;earliest;latest")
	 * 
	 * @param finder The duplicate finder.
	 * @param url The URL to retrieve the 
	 * @param outDir The directory where the output files should be placed.
	 */
	protected static void makeDuplicateFilesForUrl(DuplicateFinder finder, String url, File outDir) 
			throws IOException {
		Map<String, List<Long>> map = finder.makeDuplicateMap(url);
		String urlFilename = UrlUtils.fileEncodeUrl(url);
		
		File mapOutputFile = FileUtils.ensureNewFile(outDir, urlFilename + ".map");
		try(FileOutputStream fos = new FileOutputStream(mapOutputFile)) {
			fos.write("url;checksum;date".getBytes());
			for(Map.Entry<String, List<Long>> entry : map.entrySet()) {
				for(Long l : entry.getValue()) {
					String csvLine = url + ";" + entry.getKey() + ";" + new Date(l).toString() + "\n";
					fos.write(csvLine.getBytes());
				}
			}
		}
		
		File txtOutputFile = FileUtils.ensureNewFile(outDir, urlFilename + ".txt");
		try(FileOutputStream fos = new FileOutputStream(txtOutputFile)) {
			fos.write("checksum;amount;earliest date;latest date".getBytes());
			for(Map.Entry<String, List<Long>> entry : map.entrySet()) {
				String csvLine = entry.getKey() + ";" + entry.getValue().size() + ";" 
						+ new Date(ListUtils.getSmallest(entry.getValue())).toString() 
						+ ";" + new Date(ListUtils.getLargest(entry.getValue())).toString() + "\n";
				fos.write(csvLine.getBytes());
			}
		}
	}
}
