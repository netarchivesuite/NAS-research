package dk.netarkivet.research;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import org.jwat.common.HeaderLine;
import org.jwat.warc.WarcConstants;
import org.jwat.warc.WarcRecord;

import dk.netarkivet.research.utils.DateUtils;
import dk.netarkivet.research.utils.FileUtils;
import dk.netarkivet.research.utils.StreamUtils;
import dk.netarkivet.research.utils.UrlUtils;
import dk.netarkivet.research.warc.WarcExtractor;

/**
 * Extracts all the links from a HMTL page, and tries to find them in the CDX server.
 * Takes an WARC file and analyzes all the HTML records, or especially the http responses containing HTML records.
 * Results are printed to a CSV file.
 * 
 * Output format for CSV file:
 * URL of referral;Date for referral;Status for Link URL;Link URL;Closest date for Link URL
 * 
 * URL of referral - The URL for the HTML page where the link is found
 * Date of referral - The date for the HTML page where the link is found
 * Status for Link URL - A status telling whether or not the URL for the link is found in the CDX server.
 * Link URL - The URL for the link, which we are trying to discover.
 * Closest date for the Link URL - 
 */
public class LinkAnalyser {
	/**
	 * 
	 * @param args
	 */
	public static void main( String[] args ) {

		if(args.length < 1) {
			System.err.println("Not enough arguments. Requires the following arguments:");
			System.err.println(" 1. WARC file");
			System.err.println(" 2. (OPTIONAL) output file location. Otherwise it will be named after tje WARC file");
			System.exit(-1);
		}

		File warcFile = new File(args[0]);
		if(!warcFile.isFile()) {
			System.err.println("The WARC file '" + warcFile.getAbsolutePath() + "' is not a file "
					+ "(either does not exists or is a directory)");
			System.exit(-1);
		}

		File outFile;
		if(args.length > 1) {
			outFile = new File(args[1]);
		} else {
			outFile = new File(getOutputFileNameFromWarcFileName(warcFile.getName()));
		}
		try {
			FileUtils.ensureNewFile(outFile);
		} catch (IOException e) {
			System.err.println("Could not instantiate the output file '" + outFile.getAbsolutePath() + "'");
			e.printStackTrace(System.err);
			System.exit(-1);
		}

		LinkAnalyser wtf = new LinkAnalyser(warcFile, outFile);
		wtf.analyseWarcFile();

		System.out.println("Finished");
		System.exit(0);
	}

	/**
	 * Finds an appropriate directory name for a WARC-filename.
	 * E.g. removes the extension, or adds the current date.
	 * @param filename The name of the WARC file, which we performing the link analysis upon.
	 * @return The name of the output file.
	 */
	protected static String getOutputFileNameFromWarcFileName(String filename) {
		String res = "";
		if(filename.endsWith(".warc")) {
			res = filename.substring(0, filename.length()-".warc".length());
		} else {
			res = filename;
		}

		return res + ".csv";
	}

	/** The file to extract.*/
	protected final File warcFile;
	/** Output CSV file for the results.*/
	protected final File outputFile;

	/**
	 * Constructor.
	 * @param warcFile The WARC file to extract.
	 * @param outFile The file where the output should be printed, in the described CSV format.
	 */
	public LinkAnalyser(File warcFile, File outFile) {
		this.warcFile = warcFile;
		this.outputFile = outFile;
	}

	/**
	 * Extracts the links from each HTML record, analyse them and print the results.
	 */
	public void analyseWarcFile() {
		try {
			WarcExtractor we = new WarcExtractor(warcFile);
			WarcRecord wr;
			while((wr = we.getNext()) != null) {
//				if(wr.header.warcTypeIdx != WarcConstants.FN_IDX_WARC_WARCINFO_ID) {
//					printRecord(wr);
//				}
			}
		} catch (IOException e) {
			throw new IllegalStateException("Issue occured when ", e);
		}
	}
}
