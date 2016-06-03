package dk.netarkivet.research.warc;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.jwat.warc.WarcReader;
import org.jwat.warc.WarcReaderFactory;
import org.jwat.warc.WarcRecord;

/**
 * Extracts WARC records from a WARC file.
 */
public class WarcExtractor {
	/** The WARC reader.*/
	protected final WarcReader reader;
	
	/**
	 * Constructor.
	 * @param warcFile The WARC file to extract.
	 */
	public WarcExtractor(File warcFile) {
		try {
			reader = WarcReaderFactory.getReader( new FileInputStream(warcFile));
		} catch (IOException e) {
			throw new IllegalStateException("Could not instantiate a Warc File reader for the file '"
					+ warcFile + "'", e);
		}
	}
	
	/**
	 * Retrieves the next WARC record from the WARC file.
	 * @return The next WARC record, or null if there are more WARC records.
	 * @throws IOException If something goes wrong.
	 */
	public WarcRecord getNext() throws IOException {
		return reader.getNextRecord();
	}
}
