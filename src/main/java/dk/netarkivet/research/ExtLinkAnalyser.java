package dk.netarkivet.research;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Collection;

import org.jwat.warc.WarcRecord;

import dk.netarkivet.research.cdx.CDXExtractor;
import dk.netarkivet.research.cdx.DabCDXExtractor;
import dk.netarkivet.research.http.HttpRetriever;
import dk.netarkivet.research.links.CDXLinksLocator;
import dk.netarkivet.research.links.HtmlLinkExtractor;
import dk.netarkivet.research.links.LinkExtractor;
import dk.netarkivet.research.links.LinkStatus;
import dk.netarkivet.research.links.LinksLocator;
import dk.netarkivet.research.links.LiveLinksLocator;
import dk.netarkivet.research.utils.DateUtils;
import dk.netarkivet.research.utils.FileUtils;
import dk.netarkivet.research.warc.WarcExtractor;

/**
 * Extracts all the links from a HMTL page, and tries to find them in the CDX server or on the live net.
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
public class ExtLinkAnalyser {
	/**
	 * Main method.
	 * @param args Arguments. Must have the WARC file as first argument, the second argument
	 * must be the URL for the DAB cdx-server.
	 * Can optionally have the output file as third argument.
	 */
	public static void main( String[] args ) {

		if(args.length < 2) {
			System.err.println("Not enough arguments. Requires the following arguments:");
			System.err.println(" 1. WARC file");
			System.err.println(" 2. URL for the DAB CDX server, or 'n'/'no' to check the live net instead.");
			System.err.println(" 3. (OPTIONAL) output file location. Otherwise it will be named after the WARC file");
			System.exit(-1);
		}

		File warcFile = new File(args[0]);
		if(!warcFile.isFile()) {
			System.err.println("The WARC file '" + warcFile.getAbsolutePath() + "' is not a file "
					+ "(either does not exists or is a directory)");
			System.exit(-1);
		}
		
		LinkExtractor linkExtractor = new HtmlLinkExtractor();
		LinksLocator linkLocator = getLinksLocatorForArgs(args[1], linkExtractor);

		File outFile;
		if(args.length > 2) {
			outFile = new File(args[2]);
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

		
		ExtLinkAnalyser wtf = new ExtLinkAnalyser(linkLocator);
		wtf.analyseWarcFile(warcFile, outFile);

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
	
	/**
	 * Creates the link extractor matching the argument.
	 * @param arg Either 'n'/'no' for locating the links on the live web, or the URL for the DAB-CDX server to use
	 * to locate the links in the CDX-indices.
	 * @param linkExtractor The link extractor.
	 * @return The LinksLocator.
	 */
	protected static LinksLocator getLinksLocatorForArgs(String arg, LinkExtractor linkExtractor) {
		if(arg.equals("n") || arg.equals("no")) {
			return new LiveLinksLocator(linkExtractor, new HttpRetriever());
		} else {
			String cdxBaseUrl = arg;
			try {
				new URL(cdxBaseUrl);
			} catch (MalformedURLException e) {
				System.err.println("Invalid URL for the CDX server");
				e.printStackTrace(System.err);
				System.exit(-1);
			}
			CDXExtractor cdxExtractor = new DabCDXExtractor(cdxBaseUrl, new HttpRetriever());

			return new CDXLinksLocator(linkExtractor, cdxExtractor);
		}
	}

	/** The CDX extractor. */
	protected final LinksLocator linkLocator;

	/**
	 * Constructor.
	 * @param linkLocator The link locator to use to extract and locate the links from the records.
	 */
	public ExtLinkAnalyser(LinksLocator linkLocator) {
		this.linkLocator = linkLocator;
	}
	/**
	 * Extracts the links from each HTML record, analyse them and print the results.
	 * @param warcFile The file to extract links from.
	 * @param outputFile Output CSV file for the results.
	 */
	public void analyseWarcFile(File warcFile, File outputFile) {
		try (FileOutputStream fos = new FileOutputStream(outputFile)) {
			fos.write(("URL of referral;Date for referral;Status for Link URL;Link URL;"
					+ "Closest date for Link URL;Comment\n").getBytes(Charset.defaultCharset()));
			
			WarcExtractor we = new WarcExtractor(warcFile);
			WarcRecord wr;
			while((wr = we.getNext()) != null) {
				printLinks(linkLocator.locateLinks(wr), fos);
			}
			
			fos.flush();
		} catch (IOException e) {
			throw new IllegalStateException("Issue occured when ", e);
		}
	}
	
	/**
	 * Prints the links and their states to the output file.
	 * Each link will be printed in the following format:
	 * 	URL of referral;Date for referral;Status for Link URL;Link URL;Closest date for Link URL
	 * @param links The links along with their states.
	 * @param out The output stream.
	 * @throws IOException If something goes wrong when writing.
	 */
	protected void printLinks(Collection<LinkStatus> links, OutputStream out) throws IOException {
		for(LinkStatus ls : links) {
			StringBuilder sb = new StringBuilder();
			sb.append(ls.getReferralUrl() + ";");
			sb.append("\'" + DateUtils.dateToWaybackDate(ls.getReferralDate()) + "\';");
			if(ls.isFound()) {
				sb.append("EXISTS_IN_ARCHIVE;");
			} else {
				sb.append("NOT_IN_ARCHIVE;");
			}
			sb.append(ls.getLinkUrl() + ";");
			if(ls.getLinkDate() != null) {
				sb.append("\'" + DateUtils.dateToWaybackDate(ls.getLinkDate()) + "\';");
			} else {
				sb.append("N/A;");
			}
			sb.append(ls.getComment());
			sb.append("\n");
			out.write(sb.toString().getBytes(Charset.defaultCharset()));
		}
	}
}
