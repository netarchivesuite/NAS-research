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
 * Uses the extracted WARC records from WarcToFolder to make diffs between them.
 * They must have the format 'url'-'date', and it will only make diffs between files with the same 
 * 'URL' part of their filename.
 * 
 * The only argument required is the path to the folder.
 */
public class ExtDiffFilesInFolder {
	public static void main( String[] args ) {

		System.err.println("NOT IMPLEMENTED YET!!!");
		System.exit(-1);
		// TODO: FIXME: Implement!
		// Use google-diff in the pom.
		if(args.length < 1) {
			System.err.println("Not enough arguments. Requires the following arguments:");
			System.err.println(" 1. WARC file");
			System.err.println(" 2. (OPTIONAL) output directory. If not given, then the WARC file content will "
					+ " be extracted to a file with a name similar to the WARC file.");
			System.exit(-1);
		}

		File warcFile = new File(args[0]);
		if(!warcFile.isFile()) {
			System.err.println("The WARC file '" + warcFile.getAbsolutePath() + "' is not a file "
					+ "(either does not exists or is a directory)");
			System.exit(-1);
		}

		File outDir;
		if(args.length > 1) {
			outDir = new File(args[1]);
		} else {
			outDir = new File(getDirectoryNameFromFileName(warcFile.getName()));
		}
		if(!outDir.isDirectory() && !outDir.mkdir()) {
			System.err.println("The output directory '" + outDir.getAbsolutePath() + "' is not a valid "
					+ "directory (either is a file or it cannot be instantiated as a directory)");
			System.exit(-1);
		}

		ExtDiffFilesInFolder wtf = new ExtDiffFilesInFolder(warcFile, outDir);
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
	public ExtDiffFilesInFolder(File warcFile, File outDir) {
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

	protected String getFileName(WarcRecord wr) throws IOException {
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
