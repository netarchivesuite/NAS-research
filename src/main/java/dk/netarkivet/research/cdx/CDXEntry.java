package dk.netarkivet.research.cdx;

import java.text.ParseException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.netarkivet.research.utils.CDXUtils;
import dk.netarkivet.research.utils.DateUtils;

/**
 * CDX index entry.
 * 
 * Does not support all CDX elements.
 * 
 * Inspired by the dk.netarkivet.dab.restservice.CDXEntry from the DAB project.
 */
public class CDXEntry {
	/** The log.*/
	private static Logger logger = LoggerFactory.getLogger(CDXEntry.class);

	/**
	 * Instantiation method.
	 * @param cdxLine Array of CDX values.
	 * @param format Array of the format for the CDX value.
	 * @return The CDX entry, or null if it failed to 
	 */
	public static CDXEntry createCDXEntry(String[] cdxLine, Character[] format) {
		if (cdxLine.length != format.length) {
			logger.warn("CDX line ('" + cdxLine.length + "') and CDX format ('" + format.length + "') does not have "
					+ "same size.");
			return null;
		}
		
		Map<Character, String> cdxMapping = new HashMap<Character, String>();
		for(int i = 0; i < cdxLine.length; i++) {
			if(format[i] != ' ') {
				cdxMapping.put(format[i], cdxLine[i]);
			}
		}
		
		return createCDXEntry(cdxMapping);
	}
	
	/** CDX element A or N. */
	protected String urlNorm;
	/** CDX element b. */
	protected Long date;
	/** CDX element e. */
	protected String ip;
	/** CDX element a. */
	protected String url;
	/** CDX element m. */
	protected String contentType;
	/** CDX element s. */
	protected Integer statusCode;
	/** CDX element c or k. */
	protected String digest;
	/** CDX element v or V. */
	protected Long offset;
	/** CDX element n. */
	protected Long length;
	/** CDX element g. */
	protected String filename;
	/** CDX element r. */
	protected String redirect;

	/**
	 * Constructor.
	 * Private, since the entry must be instantiated by the 'getInstance' method.
	 */
	protected CDXEntry() {}

	/** @return CDX element A or N. */
	public String getUrlNorm() {
		return urlNorm;
	}
	/** @return CDX element b; the date - as a long. */
	public Long getDateAsLong() {
		if(date == null) {
			return 0L;
		}
		return date;
	}
	/** @return CDX element b; the date - as a Date object. */
	public Date getDateAsDate() {
		if(date == null) {
			return null;
		}
		return new Date(date);
	}
	/** @return CDX element e. */
	public String getIP() {
		return ip;
	}
	/** @return CDX element a. */
	public String getUrl() {
		return url;
	}
	/** @return CDX element m. */
	public String getContentType() {
		return contentType;
	}
	/** @return CDX element s. */
	public Integer getStatusCode() {
		if(statusCode == null) {
			return 200;
		}
		return statusCode;
	}
	/** @return CDX element c or k. */
	public String getDigest() {
		return digest;
	}
	/** @return CDX element v or V. */
	public Long getOffset() {
		if(offset == null) {
			return 0L;
		}
		return offset;
	}
	/** @return CDX element n. */
	public Long getLength() {
		if(length == null) {
			return 0L;
		}
		return length;
	}
	/** @return CDX element g. */
	public String getFilename() {
		return filename;
	}
	/** @return CDX element r. */
	public String getRedirect() {
		return redirect;
	}

	/**
	 * Extract this CDXEntry as a line for a CDX file.
	 * @param charKeys The CDX char keys for extracting in the wanted order.
	 * @return The line for the CDXEntry.
	 */
	public String extractCDXAsLine(Collection<Character> charKeys) {
		StringBuilder res = new StringBuilder();
		for(Character c : charKeys) {
			switch(c) {
			case CDXConstants.CDX_CHAR_DATE:
				CDXUtils.addCDXElementToStringBuffer(DateUtils.dateToWaybackDate(getDateAsLong()), res);
				break;
			case CDXConstants.CDX_CHAR_IP:
				CDXUtils.addCDXElementToStringBuffer(ip, res);
				break;
			case CDXConstants.CDX_CHAR_CANONIZED_URL:
			case CDXConstants.CDX_CHAR_MASSAGED_URL:
				CDXUtils.addCDXElementToStringBuffer(urlNorm, res);
				break;
			case CDXConstants.CDX_CHAR_ORIGINAL_URL:
				CDXUtils.addCDXElementToStringBuffer(url, res);
				break;
			case CDXConstants.CDX_CHAR_MIME_TYPE:
				CDXUtils.addCDXElementToStringBuffer(contentType, res);
				break;
			case CDXConstants.CDX_CHAR_RESPONSE_CODE:
				CDXUtils.addCDXElementToStringBuffer(statusCode, res);
				break;
			case CDXConstants.CDX_CHAR_OLD_STYLE_CHECKSUM:
			case CDXConstants.CDX_CHAR_NEW_STYLE_CHECKSUM:
				CDXUtils.addCDXElementToStringBuffer(digest, res);
				break;
			case CDXConstants.CDX_CHAR_COMPRESSED_ARC_FILE_OFFSET:
			case CDXConstants.CDX_CHAR_UNCOMPRESSED_ARC_FILE_OFFSET:
				CDXUtils.addCDXElementToStringBuffer(offset, res);
				break;
			case CDXConstants.CDX_CHAR_ARC_DOCUMENT_LENGTH:
				CDXUtils.addCDXElementToStringBuffer(length, res);
				break;
			case CDXConstants.CDX_CHAR_FILE_NAME:
				CDXUtils.addCDXElementToStringBuffer(filename, res);
				break;
			case CDXConstants.CDX_CHAR_REDIRECT:
				CDXUtils.addCDXElementToStringBuffer(redirect, res);
				break;
			default:
				logger.warn("Cannot handle cdx element '" + c + "'.");
				break;
			}
			
			res.append(" ");
		}
		res.append("\n");
		return res.toString();
	}
	
	/**
	 * Instantiation method.
	 * @param cdxMapping The mapping between CDX element and value.
	 * @return The CDXEntry, or null if bad argument.
	 */
	public static CDXEntry createCDXEntry(Map<Character, String> cdxMapping) {
		if(cdxMapping == null) {
			return null;
		}
		CDXEntry cdxEntry = new CDXEntry();
		try {
			for(Map.Entry<Character, String> cdxElement : cdxMapping.entrySet()) {
				if("-".equals(cdxElement.getValue())) {
					continue;
				}
				switch (cdxElement.getKey()) {
				case CDXConstants.CDX_CHAR_DATE:
					cdxEntry.date = DateUtils.waybackDateToDate(cdxElement.getValue()).getTime();
					break;
				case CDXConstants.CDX_CHAR_IP:
					cdxEntry.ip = cdxElement.getValue();
					break;
				case CDXConstants.CDX_CHAR_CANONIZED_URL:
				case CDXConstants.CDX_CHAR_MASSAGED_URL:
					cdxEntry.urlNorm = cdxElement.getValue();
					break;
				case CDXConstants.CDX_CHAR_ORIGINAL_URL:
					cdxEntry.url = cdxElement.getValue();
					break;
				case CDXConstants.CDX_CHAR_MIME_TYPE:
					cdxEntry.contentType = cdxElement.getValue();
					break;
				case CDXConstants.CDX_CHAR_RESPONSE_CODE:
					cdxEntry.statusCode = Integer.parseInt(cdxElement.getValue());
					break;
				case CDXConstants.CDX_CHAR_OLD_STYLE_CHECKSUM:
				case CDXConstants.CDX_CHAR_NEW_STYLE_CHECKSUM:
					cdxEntry.digest = cdxElement.getValue();
					break;
				case CDXConstants.CDX_CHAR_COMPRESSED_ARC_FILE_OFFSET:
				case CDXConstants.CDX_CHAR_UNCOMPRESSED_ARC_FILE_OFFSET:
					cdxEntry.offset = Long.parseLong(cdxElement.getValue());
					break;
				case CDXConstants.CDX_CHAR_ARC_DOCUMENT_LENGTH:
					cdxEntry.length = Long.parseLong(cdxElement.getValue());
					break;
				case CDXConstants.CDX_CHAR_FILE_NAME:
					cdxEntry.filename = cdxElement.getValue();
					break;
				case CDXConstants.CDX_CHAR_REDIRECT:
					cdxEntry.redirect = cdxElement.getValue();
					break;
				default:
					logger.debug("Unmatched CDX element. Key '" + cdxElement.getKey() + "' with value '"
							+ cdxElement.getValue() + "'.");
					break;
				}
			}
		} catch (NumberFormatException e) {
			logger.warn("Issue extracting the number from a string.", e);
			return null;
		} catch (ParseException e) {
			logger.warn("Issue parsing data", e);
			return null;
		}

		return cdxEntry;
	}
	
	@Override
	public String toString() {
		return extractCDXAsLine(CDXConstants.getNasDefaultCDXFormat());
	}
}
