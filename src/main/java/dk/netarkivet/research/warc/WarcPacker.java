package dk.netarkivet.research.warc;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;

import org.jwat.archive.ManagedPayload;
import org.jwat.common.ANVLRecord;
import org.jwat.common.Base32;
import org.jwat.common.ContentType;
import org.jwat.common.HttpHeader;
import org.jwat.common.Payload;
import org.jwat.common.RandomAccessFileInputStream;
import org.jwat.common.Uri;
import org.jwat.warc.WarcConstants;
import org.jwat.warc.WarcDigest;
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
import dk.netarkivet.research.utils.ChecksumUtils;
import dk.netarkivet.research.utils.UrlUtils;

/**
 * Packing WARC files by using CDX entries to extract records from the archive.
 */
public class WarcPacker {
	/** The log.*/
	private static Logger logger = LoggerFactory.getLogger(WarcPacker.class);

    /** Default date element for the warc file name. Null for no date element.*/
	protected static final Date DEFAULT_WARC_FILENAME_DATE = null;
    /** Default hostname element for the warc file name. Null for no hostname element.*/
    protected static final String DEFAULT_WARC_FILENAME_HOSTNAME = null;
    /** Default extension for the warc file. Null for no extension.*/
    protected static final String DEFAULT_WARC_FILENAME_EXTENSION = null;
    /** Whether or not to compress the warc file.*/
    protected static final Boolean DEFAULT_COMPRESS = false;
    /** Whether or not to overwrite existing files.*/
    protected static final Boolean DEFAULT_OVERWRITE = false;
    
	/** The archive extractor for extracting warc-records from the archive.*/
    protected final ArchiveExtractor archive;
    /** Whether or not to compress the warc-file.*/
    protected final Boolean useCompression;
    /** The URI for the warc info.*/
    protected Uri currentWarcInfoUUID;
    
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
        			writeInfoRecord(warcFileWriter);
        		}
        		File resultFile = archive.extractWarcRecord(cdxEntry); 
        		if(resultFile != null) {
        			writeWarcRecord(warcWriter, resultFile, cdxEntry);
        			if(!resultFile.delete()) {
        				logger.info("Could not cleanup file '" + resultFile.getAbsolutePath() + "'");
        			}
        		}
        	}
        	warcFileWriter.close();
        } catch (Throwable t) {
        	logger.error("An error occured during retrieval warc records and/or writing of the warc file.", t);
        }
    }
    
    /**
     * Write a warc-record to the warc-file through the warc-writer.
     * @param warcWriter The warc writer for writing the warc record to the warc file.
     * @param payloadFile The file containing the payload for the warc-record.
     * @param cdxEntry The CDX entry for the warc-record.
     * @throws IOException If it fails to write the warc-record to the warc-file.
     */
    private void writeWarcRecord(WarcWriter warcWriter, File payloadFile, CDXEntry cdxEntry) throws IOException {
    	try (RandomAccessFile payloadRaf = new RandomAccessFile(payloadFile, "r");
    			RandomAccessFileInputStream payloadRafin = new RandomAccessFileInputStream(payloadRaf);
    			) {
    		WarcRecord warcRecord = WarcRecord.createRecord(warcWriter);
    		WarcHeader warcHeader = warcRecord.header;
        	ManagedPayload managedPayload = ManagedPayload.checkout();
    		Payload payload = Payload.processPayload(payloadRafin, payloadFile.length(), 16384, null);
    		HttpHeader httpHeader = HttpHeader.processPayload(HttpHeader.HT_RESPONSE, payload.getInputStream(), 
    				payload.getRemaining(), null);
    		
    		String contentType;
    		if (httpHeader.isValid()) {
    			payload.setPayloadHeaderWrapped(httpHeader);
    			contentType = "application/http; msgtype=response";
    		} else {
    			contentType = cdxEntry.getContentType();
    		}
			managedPayload.manageRecord(payload, true);

            Uri recordId;
            try {
                recordId = new Uri("urn:uuid:" + UUID.randomUUID().toString());
            } catch (URISyntaxException e) {
                throw new IllegalStateException("Epic fail creating URI from UUID!");
            }
            warcHeader.warcTypeIdx = WarcConstants.RT_IDX_RESPONSE;
            warcHeader.addHeader(WarcConstants.FN_WARC_RECORD_ID, recordId, null);
            warcHeader.addHeader(WarcConstants.FN_WARC_DATE, cdxEntry.getDate(), null);
            warcHeader.addHeader(WarcConstants.FN_WARC_WARCINFO_ID, currentWarcInfoUUID, null);
    		warcHeader.addHeader(WarcConstants.FN_WARC_IP_ADDRESS, cdxEntry.getIP());
            warcHeader.addHeader(WarcConstants.FN_WARC_TARGET_URI, cdxEntry.getUrl());
    		warcHeader.addHeader(WarcConstants.FN_CONTENT_LENGTH, payloadFile.length(), null);
    		warcHeader.addHeader(WarcConstants.FN_CONTENT_TYPE, contentType);
    		
            if (managedPayload.httpHeaderBytes != null) {
            	WarcDigest payloadDigest = WarcDigest.createWarcDigest("SHA1", managedPayload.payloadDigestBytes,
            			"Base32", Base32.encodeArray(managedPayload.payloadDigestBytes));
                warcHeader.addHeader(WarcConstants.FN_WARC_PAYLOAD_DIGEST, payloadDigest, null);
            }
            WarcDigest blockDigest = WarcDigest.createWarcDigest("SHA1", managedPayload.blockDigestBytes, "Base32", 
            		Base32.encodeArray(managedPayload.blockDigestBytes));
            warcHeader.addHeader(WarcConstants.FN_WARC_BLOCK_DIGEST, blockDigest, null);
            warcWriter.writeHeader(warcRecord);
            if (managedPayload.httpHeaderBytes != null) {
                warcWriter.writePayload(managedPayload.httpHeaderBytes);
            }
            InputStream payIn = managedPayload.getPayloadStream();
            warcWriter.streamPayload(payIn);
            payIn.close();
            managedPayload.close();
    	}
    }
    
    /**
     * Writes a info record to the WARC file.
     * @param warcWriter The WARC writer for writing to the WARC file.
     * @throws IOException If an i/o issue occurs.
     * @throws URISyntaxException If an URI is malformed.
     */
    private void writeInfoRecord(WarcFileWriter warcWriter) throws IOException, URISyntaxException {
        ANVLRecord infoPayload = new ANVLRecord();
        infoPayload.addLabelValue("software", "Netarkiv Extract WARC for research projects");
        infoPayload.addLabelValue("ip", UrlUtils.getLocalIP());
        infoPayload.addLabelValue("hostname", UrlUtils.getLocalHostName());
        infoPayload.addLabelValue("conformsTo", "http://bibnum.bnf.fr/WARC/WARC_ISO_28500_version1_latestdraft.pdf");
        
        String filename = warcWriter.getFile().getName();
        if (filename.endsWith(WarcFileWriter.ACTIVE_SUFFIX)) {
        	filename = filename.substring(0, filename.length() - WarcFileWriter.ACTIVE_SUFFIX.length());
        }
        Uri recordId = new Uri("urn:uuid:" + UUID.randomUUID().toString());
        currentWarcInfoUUID = recordId;
        byte[] payloadAsBytes = infoPayload.getUTF8Bytes();
        byte[] digestBytes = ChecksumUtils.sha1Digest(payloadAsBytes);
        WarcDigest blockDigest = WarcDigest.createWarcDigest("SHA1", digestBytes, 
        		"Base32", Base32.encodeArray(digestBytes));
        WarcRecord record = WarcRecord.createRecord(warcWriter.writer);
        WarcHeader header = record.header;
        header.warcTypeIdx = WarcConstants.RT_IDX_WARCINFO;
        header.addHeader(WarcConstants.FN_WARC_RECORD_ID, recordId, null);
        header.addHeader(WarcConstants.FN_WARC_DATE, new Date(), null);
        header.addHeader(WarcConstants.FN_WARC_FILENAME, filename);
        header.addHeader(WarcConstants.FN_CONTENT_TYPE, 
        		ContentType.parseContentType(WarcConstants.CT_APP_WARC_FIELDS), null);
        header.addHeader(WarcConstants.FN_CONTENT_LENGTH, Long.valueOf(payloadAsBytes.length), null);
        header.addHeader(WarcConstants.FN_WARC_BLOCK_DIGEST, blockDigest, null);
        warcWriter.writer.writeHeader(record);
        ByteArrayInputStream bin = new ByteArrayInputStream(payloadAsBytes);
        warcWriter.writer.streamPayload(bin);
        warcWriter.writer.closeRecord();
    }
}
