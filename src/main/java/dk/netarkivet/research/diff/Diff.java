package dk.netarkivet.research.diff;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Date;

import dk.netarkivet.research.utils.DateUtils;
import dk.netarkivet.research.utils.FileUtils;

/**
 * Handles the diff.
 */
public class Diff {
	/** The method for the diffs.*/
	protected final DiffFiles diffMethod;
	/** The output format for the diff.*/
	protected final DiffOutputFormat outputFormat;
	/** The output directory.*/
	protected final File outputDir;
	/** The file where the summary is written. Is only used, if the output format supports it.*/
	protected File summaryFile;
	
	/** Value for appending to a file, when creating an file output stream.*/
	protected static final Boolean APPEND_TO_FILE = true;
	
	/**
	 * Constructor.
	 * @param diffMethod The method.
	 * @param outputFormat The output format.
	 * @param outputDir The directory, where the output should be placed.
	 */
	public Diff(DiffFiles diffMethod, DiffOutputFormat outputFormat, File outputDir) {
		this.diffMethod = diffMethod;
		this.outputFormat = outputFormat;
		this.outputDir = outputDir;
	}
	
	/**
	 * Performs the diff between two files.
	 * @param orig The original file.
	 * @param revised The revised file.
	 * @throws IOException If something goes wrong.
	 */
	public void performDiff(File orig, File revised) throws IOException {
		try (InputStream origIs = new FileInputStream(orig);
				InputStream revisedIs = new FileInputStream(revised)) {
			DiffResultWrapper results = diffMethod.diff(origIs, revisedIs);
			if(outputFormat == DiffOutputFormat.OUTPUT_FORMAT_VERBOSE) {
				writeFileOutput(results, orig.getName(), revised.getName());
			} else if(outputFormat == DiffOutputFormat.OUTPUT_FORMAT_SUMMARY) {
				writeSummaryOutput(results, orig, revised);
			} else {
				writeFileOutput(results, orig.getName(), revised.getName());
				writeSummaryOutput(results, orig, revised);
			}
		}
	}
	
	/**
	 * Writes the complete diff result to a file.
	 * @param results The results to write.
	 * @param origFilename The name of the original file.
	 * @param revisedFilename The name of the revised file.
	 * @throws IOException If something goes wrong.
	 */
	protected void writeFileOutput(DiffResultWrapper results, String origFilename, String revisedFilename) 
			throws IOException {
		String filename = makeDiffFilename(origFilename, revisedFilename);
		File outputFile = FileUtils.ensureNewFile(outputDir, filename);
		printToFile(results.toString(), outputFile);
	}
	
	/**
	 * Writes the summary output for give results to the summary file.
	 * @param results The results to write the summary of.
	 * @param origFile The original file.
	 * @param revisedFile The revised file.
	 * @throws IOException If something goes wrong with the writing.
	 */
	protected void writeSummaryOutput(DiffResultWrapper results, File origFile, File revisedFile) 
			throws IOException {
		initialiseSummaryFile();
		
		StringBuffer sb = new StringBuffer();
		sb.append(origFile.getName() + ";");
		sb.append(origFile.length() + ";");
		sb.append(revisedFile.getName() + ";");
		sb.append(revisedFile.length() + ";");
		sb.append(results.getResults().size() + ";");
		sb.append(results.getOrigGroupCount(DiffResultType.LINE, DeltaType.INSERT_DELETE) + ";");
		sb.append(results.getOrigDiffCharCount(DiffResultType.LINE, DeltaType.INSERT_DELETE) + ";");
		sb.append(results.getRevisedGroupCount(DiffResultType.LINE, DeltaType.INSERT_DELETE) + ";");
		sb.append(results.getRevisedDiffCharCount(DiffResultType.LINE, DeltaType.INSERT_DELETE) + ";");
		sb.append(results.getOrigGroupCount(DiffResultType.LINE, DeltaType.CHANGE) + ";");
		sb.append(results.getRevisedGroupCount(DiffResultType.LINE, DeltaType.CHANGE) + ";");
		sb.append(results.getOrigDiffCharCount(DiffResultType.LINE, DeltaType.CHANGE) + ";");
		sb.append(results.getRevisedDiffCharCount(DiffResultType.LINE, DeltaType.CHANGE) + ";");
		sb.append(results.getOrigGroupCount(DiffResultType.WORD, DeltaType.CHANGE) + ";");
		sb.append(results.getRevisedGroupCount(DiffResultType.WORD, DeltaType.CHANGE) + ";");
		sb.append(results.getOrigDiffCharCount(DiffResultType.WORD, DeltaType.CHANGE) + ";");
		sb.append(results.getRevisedDiffCharCount(DiffResultType.WORD, DeltaType.CHANGE) + ";");
		sb.append(results.getOrigDiffCharCount(DiffResultType.CHAR, DeltaType.CHANGE) + ";");
		sb.append(results.getRevisedDiffCharCount(DiffResultType.CHAR, DeltaType.CHANGE) + ";");
		sb.append("\n");

		printToFile(sb.toString(), summaryFile);
	}
	
	/**
	 * Creates the name for the diff file.
	 * @param origFilename The name of the original file.
	 * @param revisedFilename The name of the revised file.
	 * @return The diff file name.
	 */
	protected String makeDiffFilename(String origFilename, String revisedFilename) {
		String[] origSplit = origFilename.split("-");
		String[] revisedSplit = revisedFilename.split("-");
		if(origSplit.length < 2 || revisedSplit.length < 2) {
			return "diff_" + origFilename + "_" + revisedFilename;
		} else {
			return "diff_" + origSplit[0] + "_" + origSplit[1] + "_" + revisedSplit[1];
		}
	}
	
	/**
	 * Initializes the summary file.
	 * Though only, if it is a output format, which uses the summary file.
	 * @throws IOException If something goes wrong with creating the new summary file.
	 */
	protected void initialiseSummaryFile() throws IOException {
		if(outputFormat == DiffOutputFormat.OUTPUT_FORMAT_VERBOSE) {
			return;
		}
		if(summaryFile != null && summaryFile.isFile()) {
			return;
		}
		summaryFile = FileUtils.ensureNewFile(outputDir, "diff-summary-" + DateUtils.dateToWaybackDate(new Date()));
		
		StringBuffer sb = new StringBuffer();
		sb.append("Orig_filename;");
		sb.append("Orig file size;");
		sb.append("Revised_filename;");
		sb.append("Revised file size;");
		sb.append("Number of diffs;");
		sb.append("Orig number of insert (delete in revised) lines;");
		sb.append("Orig insert char count;");
		sb.append("Revised number of insert (delete in orig) lines;");
		sb.append("Revised insert char count;");
		sb.append("Orig number of change lines;");
		sb.append("Revised number of change lines;");
		sb.append("Orig change line diff char count;");
		sb.append("Revised change line diff char count;");
		sb.append("Orig number of change words;");
		sb.append("Revised number of change words;");
		sb.append("Orig change word diff char count;");
		sb.append("Revised change word diff char count;");
		sb.append("Orig change char diff char count;");
		sb.append("Revised change char diff char count;");
		sb.append("\n");
		
		printToFile(sb.toString(), summaryFile);
	}
	
	/**
	 * Prints the string content to the end of the file, e.g. appending, not overriding.
	 * @param content The content to print to the file.
	 * @param f The file to write to.
	 */
	protected void printToFile(String content, File f) {
		try (OutputStream os = new FileOutputStream(f, APPEND_TO_FILE)){
			os.write(content.getBytes(Charset.defaultCharset()));
			os.flush();
		} catch (IOException e) {
			throw new IllegalStateException("The file '" + f.getAbsolutePath() + "' could not be appended with "
					+ "the following: \n" + content, e);
		}
	}
}
