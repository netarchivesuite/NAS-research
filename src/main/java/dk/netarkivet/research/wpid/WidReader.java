package dk.netarkivet.research.wpid;

import java.util.Collection;

/**
 * Interface for WID readers.
 */
public interface WidReader {
	/**
	 * Reads its source and extracts all the WIDs.
	 * @return The WIDs.
	 */
	public Collection<WID> extractAllWIDs();
}
