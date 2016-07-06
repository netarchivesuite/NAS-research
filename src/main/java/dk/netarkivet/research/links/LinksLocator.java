package dk.netarkivet.research.links;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.jwat.common.HeaderLine;
import org.jwat.warc.WarcRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.netarkivet.research.cdx.CDXEntry;
import dk.netarkivet.research.cdx.CDXExtractor;
import dk.netarkivet.research.exception.ArgumentCheck;
import dk.netarkivet.research.utils.DateUtils;
import dk.netarkivet.research.wid.WaybackWID;

/**
 * Link locator.
 * Extracting the links from WARC records using a link extractor, and checking whether the links can be found in 
 * the CDX server.
 */
public class LinksLocator {
    /** Logging mechanism. */
    private static Logger logger = LoggerFactory.getLogger(LinksLocator.class);
	/** The link extractor.*/
	protected final LinkExtractor linkExtractor;
	/** The CDX extractor.*/
	protected final CDXExtractor cdxExtractor;
	
	/**
	 * Constructor.
	 * @param linkExtractor The extractor of the links.
	 * @param cdxExtractor The extractor of CDX entries.
	 */
	public LinksLocator(LinkExtractor linkExtractor, CDXExtractor cdxExtractor) {
		this.linkExtractor = linkExtractor;
		this.cdxExtractor = cdxExtractor;
	}
	
	/**
	 * Extracts the links and their states for the given WARC record.
	 * @param record The WARC record to retrieve links from.
	 * @return The collection of links and their states from the record.
	 */
	public Collection<LinkStatus> locateLinks(WarcRecord record) {
		ArgumentCheck.checkNotNull(record, "WarcRecord record");
		try {
			String mimetype = getMimetype(record);
			// Needs to use 'start with', since the record mimetype can have suffices such as encoding.
			if(!mimetype.startsWith(linkExtractor.supportedMimetype())) {
				logger.debug("Found record with unsupported mimetype '" + mimetype + "'");
				return new ArrayList<LinkStatus>();
			}
			
			List<LinkStatus> linkStates = new ArrayList<LinkStatus>();
			
			Date recordDate = getRecordDate(record);
			URL contentUrl = new URL(record.header.warcTargetUriStr);
			Collection<String> links = linkExtractor.extractLinks(record.getPayloadContent(), contentUrl);
			
			for(String link : links) {
				linkStates.add(getLinkStatus(link, contentUrl.toExternalForm(), recordDate));
			}

			return linkStates;
		} catch (Exception e) {
			logger.warn("Could not extract links.", e);
			return new ArrayList<LinkStatus>();
		}
	}
	
	/**
	 * Extracts the mimetype from the header of the WARC record.
	 * If there is a mimetype in the HTTP header, then it is returned, otherwise
	 * the mimetype of the WARC record is returned.
	 * @param record The WARC record whose mimetype should be retrived.
	 * @return The mimetype.
	 */
	protected String getMimetype(WarcRecord record) {
		if(record.getHttpHeader() == null || record.getHttpHeader().contentType == null) {
			return record.header.contentTypeStr;
		} else {
			return record.getHttpHeader().contentType;
		}
	}
	
	/**
	 * Extracts the date for the WARC record, either from the HTTP response, or from the WARC header field.
	 * @param record The WARC record whose date should be extracted.
	 * @return The date for the WARC record.
	 */
	protected Date getRecordDate(WarcRecord record) {
		Date d;
		HeaderLine hl;
		if(record.getHttpHeader() != null && ((hl = record.getHttpHeader().getHeader("Date")) != null)) {
			d = DateUtils.extractHttpHeaderDate(hl.value);
		} else {
			d = record.header.warcDate;
		}
		if(d == null) {
			d = new Date();
		}
		
		return d;
	}
	
	/**
	 * Extracts the status of a link.
	 * @param link The URL for the link.
	 * @param originalUrl The URL for the resource which located the link.
	 * @param originalDate The date for the resource which located the link.
	 * @return The status of the link from the CDX server.
	 */
	protected LinkStatus getLinkStatus(String link, String originalUrl, Date originalDate) {
		WaybackWID wid = WaybackWID.createNarkWaybackWID(null, link, originalDate);
		CDXEntry entry = cdxExtractor.retrieveCDX(wid);
		if(entry == null) {
			return new LinkStatus(false, link, null, originalUrl, originalDate);
		} else {
			return new LinkStatus(true, link, new Date(entry.getDate()), originalUrl, originalDate);
		}
	}
}
