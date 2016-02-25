package dk.netarkivet.research.warc;

import java.io.File;
import java.io.IOException;

import dk.netarkivet.research.index.CDXEntry;

public interface ArchiveExtractor {
	/**
     * Extract the WARC-record from archive.
     * @param index The CDX index needed for extracting the warc-record.
     * @return a File object The WARC-record
     * @throws IOException If the extraction fails.
     */
    public File extractWarcRecord(CDXEntry index) throws IOException;
}
