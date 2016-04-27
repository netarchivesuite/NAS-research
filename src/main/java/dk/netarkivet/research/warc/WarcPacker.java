package dk.netarkivet.research.warc;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Collection;
import java.util.Date;

import org.jwat.common.RandomAccessFileInputStream;
import org.jwat.warc.WarcConstants;
import org.jwat.warc.WarcFileNaming;
import org.jwat.warc.WarcFileNamingDefault;
import org.jwat.warc.WarcFileWriter;
import org.jwat.warc.WarcFileWriterConfig;
import org.jwat.warc.WarcHeader;
import org.jwat.warc.WarcRecord;
import org.jwat.warc.WarcWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.netarkivet.research.cdx.CDXEntry;

/**
 * Packing WARC files by using CDX entries to extract records from the archive.
 */
public class WarcPacker {
	/** The log.*/
	private static Logger logger = LoggerFactory.getLogger(CDXEntry.class);

    /** Default date element for the warc file name. Null for no date element.*/
    private final static Date DEFAULT_WARC_FILENAME_DATE = null;
    /** Default hostname element for the warc file name. Null for no hostname element.*/
    private final static String DEFAULT_WARC_FILENAME_HOSTNAME = null;
    /** Default extension for the warc file. Null for no extension.*/
    private final static String DEFAULT_WARC_FILENAME_EXTENSION = null;
    /** Whether or not to compress the warc file.*/
    public final static Boolean DEFAULT_COMPRESS = false;
    /** Whether or not to overwrite existing files.*/
    private final static Boolean DEFAULT_OVERWRITE = false;
    
	/** The archive extractor for extracting warc-records from the archive.*/
    private final ArchiveExtractor archive;
    /** Whether or not to compress the warc-file.*/
    private final Boolean useCompression;
    
    /**
     * Constructor.
     * Will not use compression. Use the other constructor to apply compression.
     * @param archive The archive to extract the warc-records from.
     */
    public WarcPacker(ArchiveExtractor archive) {
    	this.archive = archive;
    	this.useCompression = DEFAULT_COMPRESS;
    }
    
    /**
     * Constructor.
     * @param archive The archive to extract the warc-records from.
     * @param useCompression Whether or not to compress the warc-file.
     */
    public WarcPacker(ArchiveExtractor archive, boolean useCompression) {
    	this.archive = archive;
    	this.useCompression = useCompression;
    }
    
    /**
     * Extract warc records for given cdx-entries.
     * The warc-records will be extracted from the archive, 
     * and they will be placed in warc-files in the given target-dir.
     * @param entries The CDX entries to have extracted.
     * @param targetDir The directory for placing the resulting warc-files.
     */
    public void extractToWarc(Collection<CDXEntry> entries, File targetDir) {
        String filePrefix = "CDX-EXTRACT";

        try {
            WarcFileNaming warcFileNaming = new WarcFileNamingDefault(filePrefix, DEFAULT_WARC_FILENAME_DATE, 
            		DEFAULT_WARC_FILENAME_HOSTNAME, DEFAULT_WARC_FILENAME_EXTENSION);
            WarcFileWriterConfig warcFileWriterConfig = new WarcFileWriterConfig(targetDir, useCompression, 
            		WarcFileWriterConfig.DEFAULT_MAX_FILE_SIZE, DEFAULT_OVERWRITE);
            WarcFileWriter warcFileWriter = WarcFileWriter.getWarcWriterInstance(warcFileNaming, warcFileWriterConfig);
            
            WarcWriter warcWriter = null;
        	for (CDXEntry cdxEntry : entries) {
        		if (warcFileWriter.nextWriter()) {
        			warcWriter = warcFileWriter.getWriter();
        		}
        		File resultFile = archive.extractWarcRecord(cdxEntry); 
        		writeWarcRecord(warcWriter, resultFile, cdxEntry);
        		resultFile.delete();
        	}
        	warcFileWriter.close();
        } catch (Throwable t) {
        	logger.error("An error occured during retrieval warc records and/or writing of the warc file.", t);
        }
    }
    
    /**
     * Write a given warc-record to the warc-file through the warc-writer.
     * @param warcWriter The warc writer for writing the warc record to the warc file.
     * @param warcRecordFile The file containing the warc-record.
     * @param cdxEntry The CDX entry for the warc-record.
     * @throws IOException If it fails to write the warc-record to the warc-file.
     */
    private void writeWarcRecord(WarcWriter warcWriter, File warcRecordFile, CDXEntry cdxEntry) throws IOException {
    	try (RandomAccessFile payloadRaf = new RandomAccessFile(warcRecordFile, "r");
    			RandomAccessFileInputStream payloadRafin = new RandomAccessFileInputStream(payloadRaf);
    			) {
    		WarcRecord warcRecord = WarcRecord.createRecord(warcWriter);
    		WarcHeader warcHeader = warcRecord.header;
    		warcHeader.addHeader(WarcConstants.FN_CONTENT_LENGTH, warcRecordFile.length(), null);
    		warcHeader.addHeader(WarcConstants.FN_CONTENT_TYPE, cdxEntry.getContentType());
    		warcHeader.addHeader(WarcConstants.FN_WARC_IP_ADDRESS, cdxEntry.getIP());
    		warcHeader.addHeader(WarcConstants.FN_WARC_DATE, new Date(cdxEntry.getDate()), null);
    		warcWriter.writeHeader(warcRecord);
    		warcWriter.streamPayload(payloadRafin);
    	}
    }
}
