package dk.netarkivet.research.index;

import java.text.ParseException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	/** A canonized url */
	public static final char CDX_CHAR_CANONIZED_URL = 'A';
	/** B news group - UNUSED! */
	public static final char CDX_CHAR_NEWS_GROUP = 'B';
	/** C rulespace category - UNUSED! */
	public static final char CDX_CHAR_RULESPACE_CATEGORY = 'C';
	/** D compressed dat file offset - UNUSED! */
	public static final char CDX_CHAR_COMPRESSED_DAT_FILE_OFFSET = 'D';
	/** F canonized frame - UNUSED! */
	public static final char CDX_CHAR_CANONIZED_FRAME = 'F';
	/** G multi-columm language description - UNUSED! */
	public static final char CDX_CHAR_MULTI_COLUMM_LANGUAGE_DESCRIPTION = 'G';
	/** H canonized host - UNUSED! */
	public static final char CDX_CHAR_CANONIZED_HOST = 'H';
	/** N massaged url */
	public static final char CDX_CHAR_MASSAGED_URL = 'N';
	/** Q language string - UNUSED! */
	public static final char CDX_CHAR_LANGUAGE_STRING = 'Q';
	/** R canonized redirect - UNUSED! */
	public static final char CDX_CHAR_CANONIZED_REDIRECT = 'R';
	/** U uniqness - UNUSED! */
	public static final char CDX_CHAR_UNIQNESS = 'U';
	/** V compressed arc file offset */
	public static final char CDX_CHAR_COMPRESSED_ARC_FILE_OFFSET = 'V';
	/** a original url */
	public static final char CDX_CHAR_ORIGINAL_URL = 'a';
	/** b date */
	public static final char CDX_CHAR_DATE = 'b';
	/** c old style checksum */
	public static final char CDX_CHAR_OLD_STYLE_CHECKSUM = 'c';
	/** d uncompressed dat file offset - UNUSED! */
	public static final char CDX_CHAR_UNCOMPRESSED_DAT_FILE_OFFSET = 'd';
	/** e IP */
	public static final char CDX_CHAR_IP = 'e';
	/** f frame - UNUSED! */
	public static final char CDX_CHAR_FRAME = 'f';
	/** g file name */
	public static final char CDX_CHAR_FILE_NAME = 'g';
	/** h original host - UNUSED! */
	public static final char CDX_CHAR_ORIGINAL_HOST = 'h';
	/** k new style checksum */
	public static final char CDX_CHAR_NEW_STYLE_CHECKSUM = 'k';
	/** m mime type of original document */
	public static final char CDX_CHAR_MIME_TYPE = 'm';
	/** n arc document length */
	public static final char CDX_CHAR_ARC_DOCUMENT_LENGTH = 'n';
	/** p original path - UNUSED! */
	public static final char CDX_CHAR_ORIGINAL_PATH = 'p';
	/** r redirect */
	public static final char CDX_CHAR_REDIRECT = 'r';
	/** s response code */
	public static final char CDX_CHAR_RESPONSE_CODE = 's';
	/** t title - UNUSED! */
	public static final char CDX_CHAR_TITLE = 't';
	/** v uncompressed arc file offset */
	public static final char CDX_CHAR_UNCOMPRESSED_ARC_FILE_OFFSET = 'v';

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
	/** @return CDX element b. */
	public Long getDate() {
		return date;
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
		return statusCode;
	}
	/** @return CDX element c or k. */
	public String getDigest() {
		return digest;
	}
	/** @return CDX element v or V. */
	public Long getOffset() {
		return offset;
	}
	/** @return CDX element n. */
	public Long getLength() {
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
	 * Instantiation method.
	 * @param cdxMapping The mapping between CDX element and value.
	 * @return The CDXEntry 
	 */
	public static CDXEntry createCDXEntry(Map<Character, String> cdxMapping) {
		CDXEntry cdxEntry = new CDXEntry();
		try {
			for(Map.Entry<Character, String> cdxElement : cdxMapping.entrySet()) {
				if("-".equals(cdxElement.getValue())) {
					continue;
				}
				switch (cdxElement.getKey()) {
				case CDX_CHAR_DATE:
					cdxEntry.date = DateUtils.waybackDateToDate(cdxElement.getValue()).getTime();
					break;
				case CDX_CHAR_IP:
					cdxEntry.ip = cdxElement.getValue();
					break;
				case CDX_CHAR_CANONIZED_URL:
				case CDX_CHAR_MASSAGED_URL:
					cdxEntry.urlNorm = cdxElement.getValue();
					break;
				case CDX_CHAR_ORIGINAL_URL:
					cdxEntry.url = cdxElement.getValue();
					break;
				case CDX_CHAR_MIME_TYPE:
					cdxEntry.contentType = cdxElement.getValue();
					break;
				case CDX_CHAR_RESPONSE_CODE:
					cdxEntry.statusCode = Integer.parseInt(cdxElement.getValue());
					break;
				case CDX_CHAR_OLD_STYLE_CHECKSUM:
				case CDX_CHAR_NEW_STYLE_CHECKSUM:
					cdxEntry.digest = cdxElement.getValue();
					break;
				case CDX_CHAR_COMPRESSED_ARC_FILE_OFFSET:
				case CDX_CHAR_UNCOMPRESSED_ARC_FILE_OFFSET:
					cdxEntry.offset = Long.parseLong(cdxElement.getValue());
					break;
				case CDX_CHAR_ARC_DOCUMENT_LENGTH:
					cdxEntry.length = Long.parseLong(cdxElement.getValue());
					break;
				case CDX_CHAR_FILE_NAME:
					cdxEntry.filename = cdxElement.getValue();
					break;
				case CDX_CHAR_REDIRECT:
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

	/**
	 * Instantiation method.
	 * @param cdxLine Array of CDX values.
	 * @param format Array of the format for the CDX value.
	 * @return The CDX entry, or null if it failed to 
	 */
	public static CDXEntry createCDXEntry(String[] cdxLine, char[] format) {
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
}
