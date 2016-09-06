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

import dk.netarkivet.research.exception.ArgumentCheck;
import dk.netarkivet.research.utils.DateUtils;

/**
 * Abstract link locator.
 * Extracting the links from WARC records using a link extractor.
 * Requires the sub-classes to implement the way for checking whether the link exists.
 */
public abstract class LinksLocator {
    /** Logging mechanism. */
    private static Logger logger = LoggerFactory.getLogger(LinksLocator.class);
	/** The link extractor.*/
	protected final LinkExtractor linkExtractor;
	
	/**
	 * Constructor.
	 * @param linkExtractor The extractor of the links.
	 */
	public LinksLocator(LinkExtractor linkExtractor) {
		this.linkExtractor = linkExtractor;
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
			
			Date recordDate =  getRecordDate(record); //elzi change from getRecordDate(record);
			URL contentUrl = new URL(record.header.warcTargetUriStr);
			Collection<String> links = linkExtractor.extractLinks(record.getPayloadContent(), contentUrl);
			
			for(String link : links) {
				String extractLink = link.contains("#") ? link.split("#")[0] : link;
				linkStates.add(getLinkStatus(extractLink, contentUrl.toExternalForm(), recordDate));
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
		//if(record.getHttpHeader() != null && ((hl = record.getHttpHeader().getHeader("Date")) != null)) {
		//	d = DateUtils.extractHttpHeaderDate(hl.value);
		// else {
			d = record.header.warcDate;
		//}
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
	protected abstract LinkStatus getLinkStatus(String link, String originalUrl, Date originalDate);
}
