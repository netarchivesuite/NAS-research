package dk.netarkivet.research.diff;

import java.io.InputStream;

/**
 * Interface for diff implementations.
 */
public interface DiffFiles {
	/**
	 * Calculates the diff between an original data set and a revised instance of the same data set, 
	 * to figure out which data points has changed.
	 * @param orig The original for the first to compare between.
	 * @param revised The revised for the second element in the compare.
	 * @return The diff results.
	 */
	DiffResultWrapper diff(InputStream orig, InputStream revised);
}
