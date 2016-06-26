package dk.netarkivet.research.diff;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import dk.netarkivet.research.exception.ArgumentCheck;
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
	
	/**
	 * Constructor.
	 * @param diffMethod The method.
	 * @param diffPrinter The output printer.
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
	 */
	public void performDiff(File orig, File revised) throws IOException {
		DiffResultWrapper results = diffMethod.diff(new FileInputStream(orig), new FileInputStream(revised));
		
		if(outputFormat == DiffOutputFormat.OUTPUT_FORMAT_VERBOSE) {
			writeFileOutput(results, orig.getName(), revised.getName());
		} else if(outputFormat == DiffOutputFormat.OUTPUT_FORMAT_SUMMARY) {
			writeSummaryOutput(results, orig.getName(), revised.getName());
		} else {
			writeFileOutput(results, orig.getName(), revised.getName());
			writeSummaryOutput(results, orig.getName(), revised.getName());
		}
	}
	
	/**
	 * Writes the complete diff result to a file.
	 * @param results The results to write.
	 * @param origFilename The name of the original file.
	 * @param revisedFilename The name of the revised file.
	 * @throws IOException If something goes wrong.
	 */
	protected void writeFileOutput(DiffResultWrapper results, String origFilename, String revisedFilename) throws IOException {
		String filename = makeDiffFilename(origFilename, revisedFilename);
		File outputFile = FileUtils.ensureNewFile(outputDir, filename);
		try (OutputStream os = new FileOutputStream(outputFile)) {
			os.write(results.toString().getBytes());
			os.flush();
		}
	}
	
	/**
	 * Writes the summary output for give results to the summary file.
	 * @param results The results to write the summary of.
	 * @param origFilename The name of the original file.
	 * @param revisedFilename The name of the revised file.
	 * @throws IOException If something goes wrong with the writing.
	 */
	protected void writeSummaryOutput(DiffResultWrapper results, String origFilename, String revisedFilename) throws IOException {
		ArgumentCheck.checkIsFile(summaryFile, "Summary file has not yet been instantiated.");
		try (OutputStream os = new FileOutputStream(summaryFile)) {
			StringBuffer sb = new StringBuffer();
			sb.append(origFilename + ";");
			sb.append(revisedFilename + ";");
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

			os.write(sb.toString().getBytes());
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
		return "diff_" + origSplit[0] + "_" + origSplit[1] + "-" + revisedSplit[1];
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
		
		try (OutputStream os = new FileOutputStream(summaryFile)) {
			StringBuffer sb = new StringBuffer();
			sb.append("Orig_filename;");
			sb.append("Revised_filename;");
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

			os.write(sb.toString().getBytes());
			os.flush();
		}
	}
}
