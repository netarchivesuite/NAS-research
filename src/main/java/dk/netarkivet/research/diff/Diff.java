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
	protected final Boolean APPEND_TO_FILE = true;
	
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
		try (OutputStream os = new FileOutputStream(outputFile)) {
			os.write(results.toString().getBytes(Charset.defaultCharset()));
			os.flush();
		}
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
		if(summaryFile == null || !summaryFile.isFile()) {
			initialiseSummaryFile();
		}
		try (OutputStream os = new FileOutputStream(summaryFile, APPEND_TO_FILE)) {
			StringBuffer sb = new StringBuffer();
			sb.append(origFile.getName() + ";");
			sb.append(origFile.length() + ";");
			sb.append(revisedFile.getName() + ";");
			sb.append(revisedFile.length() + ";");
			sb.append(results.getResults().size() + ";");
			sb.append(results.getOrigLineCount(false) + ";");
			sb.append(results.getRevisedLineCount(false) + ";");
			sb.append(results.getOrigLineCount(true) + ";");
			sb.append(results.getRevisedLineCount(true) + ";");
			sb.append(results.getOrigDiffCharCount(DiffResultType.LINE) + ";");
			sb.append(results.getRevisedDiffCharCount(DiffResultType.LINE) + ";");
			sb.append(results.getOrigDiffCharCount(DiffResultType.WORD) + ";");
			sb.append(results.getRevisedDiffCharCount(DiffResultType.WORD) + ";");
			sb.append(results.getOrigDiffCharCount(DiffResultType.CHAR) + ";");
			sb.append(results.getRevisedDiffCharCount(DiffResultType.CHAR) + ";");

			sb.append("\n");

			os.write(sb.toString().getBytes(Charset.defaultCharset()));
			os.flush();
		}
	}
	
	/**
	 * Creates the name for the diff file.
	 * @param origFilename The name of the original file.
	 * @param revisedFilename The name of the revised file.
	 * @return The diff file name.
	 */
	protected String makeDiffFilename(String origFilename, String revisedFilename) {
		String[] origSplit = origFilename.split("-");
		String[] revisedSplit = origFilename.split("-");
		if(origSplit.length < 2 || revisedSplit.length < 2) {
			return "diff_" + origSplit[0] + "_" + revisedSplit[0];
		} else {
			return "diff_" + origSplit[0] + "_" + origSplit[1] + "-" + revisedSplit[1];
		}
	}
	
	/**
	 * Initializes the summary file.
	 * Though only, if it is a output format, which uses the summary file.
	 */
	protected void initialiseSummaryFile() throws IOException {
		if(outputFormat == DiffOutputFormat.OUTPUT_FORMAT_VERBOSE) {
			return;
		}
		summaryFile = FileUtils.ensureNewFile(outputDir, "diff-Summary-" + DateUtils.dateToWaybackDate(new Date()));
		
		try (OutputStream os = new FileOutputStream(summaryFile, APPEND_TO_FILE)) {
			StringBuffer sb = new StringBuffer();
			sb.append("Orig_filename;");
			sb.append("Orig file size;");
			sb.append("Revised_filename;");
			sb.append("Revised file size;");
			sb.append("Number of diffs;");
			sb.append("Orig number of insert/delete lines;");
			sb.append("Revised number of insert/delete lines;");
			sb.append("Orig number of change lines;");
			sb.append("Revised number of change lines;");
			sb.append("Orig change line diff char count;");
			sb.append("Revised change line diff char count;");
			sb.append("Orig change word diff char count;");
			sb.append("Revised change word diff char count;");
			sb.append("Orig change char diff char count;");
			sb.append("Revised change char diff char count;");

			sb.append("\n");

			os.write(sb.toString().getBytes(Charset.defaultCharset()));
			os.flush();
		}
	}
}
