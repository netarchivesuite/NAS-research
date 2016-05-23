package dk.netarkivet.research.warc;

import java.io.File;
import java.io.IOException;

import dk.netarkivet.research.cdx.CDXEntry;

/**
 * Extracts data from an archive.
 */
public interface ArchiveExtractor {
	/**
     * Extract the WARC-record from archive.
     * @param index The CDX index needed for extracting the warc-record.
     * @return a File object The WARC-record
     * @throws IOException If the extraction fails.
     */
    File extractWarcRecord(CDXEntry index) throws IOException;
}
