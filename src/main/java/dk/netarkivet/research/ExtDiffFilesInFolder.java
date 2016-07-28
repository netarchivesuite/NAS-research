package dk.netarkivet.research;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.netarkivet.research.diff.Diff;
import dk.netarkivet.research.diff.DiffFiles;
import dk.netarkivet.research.diff.DiffOutputFormat;
import dk.netarkivet.research.diff.SimpleDiffFiles;
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
 *  The output format is either verbose (the complete output - one file per diff), 
 *  summary (only count - one file total) or both.
 *  
 *  An optional argument is the output directory. 
 */
public class ExtDiffFilesInFolder {

	/** Logging mechanism. */
	private static Logger logger = LoggerFactory.getLogger(ExtDiffFilesInFolder.class);

	/**
	 * Main program.
	 * @param args Arguments for the main program.
	 */
	public static void main(String ... args) {

		if(args.length < 3) {
			System.err.println("Not enough arguments. Requires the following arguments:");
			System.err.println(" 1. Folder with the files to diff with the filename format 'url'-'date'");
			System.err.println(" 2. Diff strategy / file to make diff against");
			System.err.println("  - To make diffs against a specific file, then make the specific file the argument");
			System.err.println("  - To make diffs between a file and the next file, then give argument "
					+ "'n'/'no'/'next'");
			System.err.println(" 3. Output format: 'verbose', 'summary' or 'both'");
			System.err.println(" 4. (OPTIONAL) output directory. If not given, then the output will be placed in a "
					+ "'output' subfolder to the input file folder (argument 1).");
			System.err.println(" 5. (OPTIONAL) Method: Simple, HTML elements, HTML paragraph text.");
			System.err.println("  - Only the Simple diff method is implemented so far.");
			throw new IllegalArgumentException("Not enough arguments.");
		}

		File inputDir = new File(args[0]);
		if(!inputDir.isDirectory()) {
			throw new IllegalArgumentException("The folder '" + inputDir.getAbsolutePath() + "' is not a proper "
					+ "directory (either does not exists or is a file) - try giving the complete path.");
		}

		DiffStrategy diffStrategy = extractDiffStrategy(args[1]);
		File diffFile = null;
		if(diffStrategy == DiffStrategy.DIFF_STRATEGY_ONE_FILE) {
			diffFile = new File(args[1]);
		}

		DiffOutputFormat outFormat = DiffOutputFormat.extractOutputFormat(args[2]);

		File outDir;
		if(args.length > 3) {
			outDir = new File(args[3]);
		} else {
			outDir = new File(inputDir, "output");
			if(outDir.exists()) {
				FileUtils.deprecateFile(outDir);
			}
		}
		if(!outDir.exists() && !outDir.mkdirs()) {
			throw new IllegalArgumentException("Cannot instantiate a new directory at '" 
					+ outDir.getAbsolutePath() + "'");
		}
		if(outDir.isFile()) {
			throw new IllegalArgumentException("The output directory '" + outDir.getAbsolutePath() + "' is not a "
					+ "valid directory (either is a file or it cannot be instantiated as a directory)");
		}
		DiffFiles diffMethod;
		if(args.length > 4) {
			diffMethod = getDiffFiles(args[4]);
		} else {
			diffMethod = getDiffFiles(null);
		}

		logger.info("Performing diff on files in catalogue: " + inputDir.getAbsolutePath());
		logger.info("Using diff stratygy: " + diffStrategy.name());
		logger.info("Delivering in the output format: " + outFormat.name());
		logger.info("And the output files will be placed in folder: " + outDir.getAbsolutePath());
		logger.info("Using diff method: " + diffMethod.getClass().getName());
		
		Diff diff = new Diff(diffMethod, outFormat, outDir);

		ExtDiffFilesInFolder extDiff = new ExtDiffFilesInFolder(inputDir, diff);
		if(diffStrategy == DiffStrategy.DIFF_STRATEGY_NEXT_FILE) {
			extDiff.performNextFileDiffStrategy();
		} else {
			extDiff.performOneFileDiffStrategy(diffFile);
		}

		System.out.println("Finished");
		System.exit(0);
	}

	/**
	 * Extracts the diff strategy from the argument.
	 * @param diffStrategyName The name of the diff strategy. 
	 * @return The diff strategy.
	 */
	protected static DiffStrategy extractDiffStrategy(String diffStrategyName) {
		if(diffStrategyName.equalsIgnoreCase("n") || diffStrategyName.equalsIgnoreCase("no") || 
				diffStrategyName.equalsIgnoreCase("next")) {
			return DiffStrategy.DIFF_STRATEGY_NEXT_FILE;
		} else if(new File(diffStrategyName).isFile()) {
			return DiffStrategy.DIFF_STRATEGY_ONE_FILE;
		}

		throw new IllegalStateException("No argument for 'Next file' diff strategy, and argument does not point "
				+ "to a file for the 'one file' diff strategy. Path might be wrong (try using complete path)");
	}

	/**
	 * Retrieves the method for making diffs corresponding to the argument.
	 * @param diffMethodName The name of the diff method.
	 * @return The class corresponding to the diff method.
	 */
	protected static DiffFiles getDiffFiles(String diffMethodName) {
		if(diffMethodName == null || diffMethodName.isEmpty()) {
			return new SimpleDiffFiles();
		} 
		if(diffMethodName.equalsIgnoreCase("SIMPLE")) {
			return new SimpleDiffFiles();
		}
		throw new IllegalArgumentException("Cannot instantiate the diff method '"
				+ diffMethodName + "'. It might not be implemented yet.");
	}

	/** The directory with the files to run diff upon.*/
	protected final File fileDir;
	/** The method for performing the diff.*/
	protected final Diff diffMethod;

	/**
	 * Constructor.
	 * @param inputDirectory The input directory where the 
	 * @param diff The diff handler.
	 */
	public ExtDiffFilesInFolder(File inputDirectory, Diff diff) {
		this.fileDir = inputDirectory;
		this.diffMethod = diff;
	}

	/**
	 * Performs the 'next file' diff strategy.
	 */
	public void performNextFileDiffStrategy() {
		Map<String, List<String>> fileMap = createFileNameMap();
		for(Map.Entry<String, List<String>> entry : fileMap.entrySet()) {
			Iterator<String> iterator = entry.getValue().iterator();
			File orig = new File(fileDir, entry.getKey() + "-" + iterator.next());
			File revised;
			String revisedFileSuffix;
			while(iterator.hasNext()) {
				revisedFileSuffix = iterator.next();
				revised = new File(fileDir, entry.getKey() + "-" + revisedFileSuffix);
				try {
					diffMethod.performDiff(orig, revised);
				} catch(IOException e) {
					logger.error("Issue occured when performing diff upon files '"
							+ orig.getName() + "' and '" + revised.getName() + "'", e);
				}
				
				orig = revised;
			}
			
			if(entry.getValue().size() < 2) {
				logger.warn("Cannot perform diff for URL '" + entry.getKey() + "', since it requires at least two.");
			}
		}
	}

	/**
	 * Performs the 'one file' diff strategy, where all the files in the file dir will be 
	 * diff'ed 
	 * @param diffFile The base file for the 'one file' diff strategy.
	 */
	public void performOneFileDiffStrategy(File diffFile) {
		for(String filename : FileUtils.getSortedListOfFilenames(fileDir)) {
			File revisedFile = new File(fileDir, filename);
			if(!diffFile.getAbsolutePath().equals(revisedFile.getAbsolutePath())) {
				try {
					diffMethod.performDiff(diffFile, revisedFile);
				} catch(IOException e) {
					logger.error("Issue occured when performing diff upon files '"
							+ diffFile.getName() + "' and '" + filename + "'", e);
				}
			}
		}
	}
	
	/**
	 * Creates a map between the filename prefix and list of suffices for files with same prefix.
	 * @return Map between prefixes and their suffices.
	 */
	protected Map<String, List<String>> createFileNameMap() {
		Map<String, List<String>> res = new HashMap<String, List<String>>();
		for(String filename : FileUtils.getSortedListOfFilenames(fileDir)) {
			String[] split = filename.split("-");
			if(split.length != 2) {
				logger.warn("The filename '" + filename + "' is not in the appropritate format 'url-date'. "
						+ "Ignoring it.");
				continue;
			}
			List<String> list = res.get(split[0]);
			if(list == null) {
				list = new ArrayList<String>();
			}
			list.add(split[1]);
			res.put(split[0], list);
		}
		
		return res;
	}

	/**
	 * Strategy for making diffs between files.
	 * Either diff all other files against one specific file,
	 * or diff each file against the previous file.
	 */
	enum DiffStrategy {
		/** The 'one file' strategy, where all other files are made diff against one specific file.*/
		DIFF_STRATEGY_ONE_FILE,
		/** The 'next file' strategy, where each file is made diff against the next file.*/
		DIFF_STRATEGY_NEXT_FILE
	}
}
