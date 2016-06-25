package dk.netarkivet.research;

import java.io.File;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.netarkivet.research.utils.DateUtils;
import dk.netarkivet.research.utils.FileUtils;

/**
 * Uses the extracted WARC records from WarcToFolder to make diffs between them.
 * They must have the format 'url'-'date', and it will only make diffs between files with the same 
 * 'URL' part of their filename.
 * 
 * The diffs will consist of diff-per-character, diff-per-word and diff-per-line.
 * 
 * It is possible to either make diff against a specific file, or between one file and the next.
 * E.g. the files: url-1, url-2, url-3 can be made diffs in the following way:
 * Against specific file (url-1):
 * - diff url-1 url-2
 * - diff url-1 url-3
 * Or against a file and the next file:
 * - diff url-1 url-2
 * - diff url-2 url-3
 * 
 *  The output format is either verbose (the complete output - one file per diff), summary (only count - one file total)
 *  or both.
 *  
 *  An optional argument is the output directory. 
 */
public class ExtDiffFilesInFolder {
	
    /** Logging mechanism. */
    private static Logger logger = LoggerFactory.getLogger(ExtDiffFilesInFolder.class);

	public static void main( String[] args ) {

		if(args.length < 3) {
			System.err.println("Not enough arguments. Requires the following arguments:");
			System.err.println(" 1. Folder with the files to diff with the filename format 'url'-'date'");
			System.err.println(" 2. Diff strategy / file to make diff against");
			System.err.println("  - To make diffs against a specific file, then make the specific file the argument");
			System.err.println("  - To make diffs between a file and the next file, then give argument "
					+ "'n'/'no'/'next'");
			System.err.println(" 3. Output format, 'verbose', 'summary' or 'both'");
			System.err.println(" 4. (OPTIONAL) output directory. If not given, then the output will be placed in a "
					+ "'output' subfolder to the input file folder (argument 1).");
			System.exit(-1);
		}

		File inputDir = new File(args[0]);
		if(!inputDir.isDirectory()) {
			System.err.println("The folder '" + inputDir.getAbsolutePath() + "' is not a proper directory"
					+ "(either does not exists or is a file) - try giving the complete path.");
			System.exit(-1);
		}
		
		DiffStrategy diffStrategy = extractDiffStrategy(args[1]);
		File diffFile = null;
		if(diffStrategy == DiffStrategy.DIFF_STRATEGY_ONE_FILE) {
			diffFile = new File(args[1]);
		}
		
		OutputFormat outFormat = extractOutputFormat(args[2]);

		File outDir;
		if(args.length > 3) {
			outDir = new File(args[1]);
		} else {
			outDir = new File(inputDir, "output");
		}
		if(outDir.isFile() || !(outDir.isDirectory() && outDir.mkdirs())) {
			System.err.println("The output directory '" + outDir.getAbsolutePath() + "' is not a valid "
					+ "directory (either is a file or it cannot be instantiated as a directory)");
			System.exit(-1);
		}
		if(outDir.exists()) {
			FileUtils.deprecateFile(outDir);
		}

		ExtDiffFilesInFolder extDiff = new ExtDiffFilesInFolder(inputDir, outDir);
		if(diffStrategy == DiffStrategy.DIFF_STRATEGY_NEXT_FILE) {
			extDiff.performNextFileDiffStrategy(outFormat);
		} else {
			extDiff.performOneFileDiffStrategy(diffFile, outFormat);
		}

		System.out.println("Finished");
		System.exit(0);
	}
	
	/**
	 * 
	 * @param arg
	 * @return
	 */
	protected static DiffStrategy extractDiffStrategy(String arg) {
		if(arg.equalsIgnoreCase("n") || arg.equalsIgnoreCase("no") || arg.equalsIgnoreCase("next")) {
			return DiffStrategy.DIFF_STRATEGY_NEXT_FILE;
		} else if(new File(arg).isFile()) {
			return DiffStrategy.DIFF_STRATEGY_ONE_FILE;
		}
		
		throw new IllegalStateException("No argument for 'Next file' diff strategy, and argument does not point "
				+ "to a file for the 'one file' diff strategy. Path might be wrong (try using complete path)");
	}
	
	/**
	 * Extract the output format.
	 * @param arg 
	 * @return
	 */
	protected static OutputFormat extractOutputFormat(String arg) {
		if(arg.equalsIgnoreCase("verbose")) {
			return OutputFormat.OUTPUT_FORMAT_VERBOSE;
		} else if(arg.equalsIgnoreCase("summary")) {
			return OutputFormat.OUTPUT_FORMAT_SUMMARY;
		} else if(arg.equalsIgnoreCase("both")) {
			return OutputFormat.OUTPUT_FORMAT_BOTH;
		}
		
		logger.warn("Not the valid argument for the output format '" + arg + "'. "
				+ "Trying to decipher by using the first character.");
		if(arg.startsWith("v") || arg.startsWith("V")) {
			return OutputFormat.OUTPUT_FORMAT_VERBOSE;
		} else if(arg.startsWith("s") || arg.startsWith("S")) {
			return OutputFormat.OUTPUT_FORMAT_SUMMARY;
		} else if(arg.startsWith("b") || arg.startsWith("B")) {
			return OutputFormat.OUTPUT_FORMAT_BOTH;
			
		}

		throw new IllegalStateException("Invalid argument for the output format. Must be either: '"
				+ "verbose', 'summary' or 'both'");
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
	 * @param inputDirectory The input directory where the 
	 * @param outDir The directory where the WARC record content should be placed.
	 */
	public ExtDiffFilesInFolder(File inputDirectory, File outDir) {
		this.warcFile = inputDirectory;
		this.outputDirectory = outDir;
	}

	public void performNextFileDiffStrategy(OutputFormat outFormat) {
		// TODO
	}
	
	public void performOneFileDiffStrategy(File diffFile, OutputFormat outFormat) {
		// TODO
	}
	
	enum OutputFormat {
		OUTPUT_FORMAT_VERBOSE,
		OUTPUT_FORMAT_SUMMARY,
		OUTPUT_FORMAT_BOTH
	}
	
	enum DiffStrategy {
		DIFF_STRATEGY_ONE_FILE,
		DIFF_STRATEGY_NEXT_FILE
	}
}
