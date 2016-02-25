package dk.netarkivet.research.wpid;

import java.util.Collection;

/**
 * Interface for WPID readers.
 */
public interface WPidReader {
	/**
	 * Reads its source and extracts all the WPIDS.
	 * @return The WPIDs.
	 */
	public Collection<WPID> extractAllWPIDs();
}
