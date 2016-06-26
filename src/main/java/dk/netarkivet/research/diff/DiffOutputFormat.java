package dk.netarkivet.research.diff;

/**
 * The different output formats.
 */
public enum DiffOutputFormat {
	/** 
	 * Writes the complete diff output.
	 * This gives one output file for diff.
	 */
	OUTPUT_FORMAT_VERBOSE,
	/** 
	 * Writes only the summary file in CSV format, where each diff is written as a line.
	 * This only gives one file total.
	 */
	OUTPUT_FORMAT_SUMMARY,
	/**
	 * Writes both a file for each diff performed,
	 * and a summary file for all the diffs.
	 */
	OUTPUT_FORMAT_BOTH;
	
	/**
	 * Extract the output format.
	 * @param outputFormatName The name of the output format.
	 * @return The output format.
	 */
	public static DiffOutputFormat extractOutputFormat(String outputFormatName) {
		if(outputFormatName.equalsIgnoreCase("verbose") || outputFormatName.startsWith("v") 
				|| outputFormatName.startsWith("V")) {
			return DiffOutputFormat.OUTPUT_FORMAT_VERBOSE;
		} else if(outputFormatName.equalsIgnoreCase("summary") || outputFormatName.startsWith("s") 
				|| outputFormatName.startsWith("S")) {
			return DiffOutputFormat.OUTPUT_FORMAT_SUMMARY;
		} else if(outputFormatName.equalsIgnoreCase("both") || outputFormatName.startsWith("b") 
				|| outputFormatName.startsWith("B")) {
			return DiffOutputFormat.OUTPUT_FORMAT_BOTH;
		}

		throw new IllegalArgumentException("Invalid argument for the output format. Must be either: '"
				+ "verbose', 'summary' or 'both'");
	}
}
