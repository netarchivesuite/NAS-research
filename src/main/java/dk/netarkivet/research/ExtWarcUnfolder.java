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
 * Extracts all the WARC records of a WARC file to a folder.
 * Each WARC record will only be left with its HTTP payload content, which means
 * that both WARC header and HTTP headers will not be extracted.
 */
public class ExtWarcUnfolder {
	/**
	 * Main method.
	 * @param args The arguments for running the program.
	 */
	public static void main(String ... args) {

		if(args.length < 1) {
			System.err.println("Not enough arguments. Requires the following arguments:");
			System.err.println(" 1. WARC file");
			System.err.println(" 2. (OPTIONAL) output directory. If not given, then the WARC file content will "
					+ " be extracted to a file with a name similar to the WARC file.");
			throw new IllegalArgumentException("Not enough arguments.");
		}

		File warcFile = new File(args[0]);
		if(!warcFile.isFile()) {
			throw new IllegalArgumentException("The WARC file '" + warcFile.getAbsolutePath() + "' is not a file "
					+ "(either does not exists or is a directory)");
		}

		File outDir;
		if(args.length > 1) {
			outDir = new File(args[1]);
		} else {
			outDir = new File(getDirectoryNameFromFileName(warcFile.getAbsolutePath()));
		}
		if(!outDir.isDirectory() && !outDir.mkdir()) {
			throw new IllegalArgumentException("The output directory '" + outDir.getAbsolutePath() + "' is not a valid "
					+ "directory (either is a file or it cannot be instantiated as a directory)");
		}

		ExtWarcUnfolder wtf = new ExtWarcUnfolder(warcFile, outDir);
		wtf.extract();

		System.out.println("Finished");
		System.exit(0);
	}

	/**
	 * Finds an appropriate directory name for a WARC-filename.
	 * E.g. removes the extension, or adds the current date.
	 * @param filename The name of the file, who we should 
	 * @return 
	 */
	protected static String getDirectoryNameFromFileName(String filename) {
		String res = "";
		if(filename.endsWith(".warc")) {
			res = filename.substring(0, filename.length()-".warc".length());
			if(!(new File(res).exists())) {
				return res;
			}
		} else {
			res = filename;
		}

		return res + DateUtils.dateToWaybackDate(new Date());
	}

	/** The file to extract.*/
	protected final File warcFile;
	/** Directory to place the extracted WARC records.*/
	protected final File outputDirectory;

	/**
	 * Constructor.
	 * @param warcFile The WARC file to extract.
	 * @param outDir The directory where the WARC record content should be placed.
	 */
	public ExtWarcUnfolder(File warcFile, File outDir) {
		this.warcFile = warcFile;
		this.outputDirectory = outDir;
	}

	/**
	 * Extracts the warc file to the directory.
	 */
	public void extract() {
		try {
			WarcExtractor we = new WarcExtractor(warcFile);
			WarcRecord wr;
			while((wr = we.getNext()) != null) {
				if(wr.header.warcTypeIdx != WarcConstants.FN_IDX_WARC_WARCINFO_ID) {
					printRecord(wr);
				}
			}
		} catch (IOException e) {
			throw new IllegalStateException("Issue extracting the data.", e);
		}
	}

	/**
	 * Prints a given WARC record as a file to the output directory.
	 * @param wr The warc records.
	 * @throws IOException
	 */
	protected void printRecord(WarcRecord wr) throws IOException {
		String outputFileName = getFileName(wr);
		File outputFile = new File(outputDirectory, outputFileName);
		if(outputFile.exists()) {
			FileUtils.deprecateFile(new File(outputFile.getAbsolutePath()));
		}
		try (FileOutputStream fos = new FileOutputStream(outputFile)) {
			StreamUtils.printInputStreamToOutputStream(wr.getPayloadContent(), fos);
		}
	}

	/**
	 * Extracts a filename for a WARC record, based on the target URI (or record ID if no target URI),
	 * and the date (either from the HTTP header, or if no header, then the WARC record date).
	 * @param wr The WARC record.
	 * @return The filename.
	 */
	protected String getFileName(WarcRecord wr) {
		String res;
		if(wr.header.warcTargetUriStr != null && !wr.header.warcTargetUriStr.isEmpty()) {
			res = UrlUtils.fileEncodeUrl(wr.header.warcTargetUriStr);
		} else {
			res = wr.header.warcRecordIdStr;
		}

		Date d;
		HeaderLine hl;
		if(wr.getHttpHeader() != null && ((hl = wr.getHttpHeader().getHeader("Date")) != null)) {
			d = DateUtils.extractHttpHeaderDate(hl.value);
		} else {
			d = wr.header.warcDate;
		}
		if(d == null) {
			d = new Date();
		}
		
		return res + "-" + DateUtils.dateToWaybackDate(d);
	}
}
