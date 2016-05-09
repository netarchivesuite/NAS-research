package dk.netarkivet.research.cdx;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.netarkivet.research.http.HttpRetriever;
import dk.netarkivet.research.utils.DateUtils;
import dk.netarkivet.research.wid.WPID;

/**
 * Extractor for the CDX-server of the PYWB.
 * Example of extraction url:
 * http://localhost:8080/pywb-cdx/coll-cdx?url=example.com&closest=20140127171200&limit=1&fl=url,timestamp,filename,offset,length,mime,status,digest
 */
public class PywbCDXExtractor extends AbstractCDXExtractor {
	/** The log.*/
	private static Logger logger = LoggerFactory.getLogger(CDXEntry.class);

	/** The prefix for the URL argument in the HTTP request.*/
	protected static final String URL_ARGUMENT_PREFIX = "url=";
	/** The prefix for the date argument in the HTTP request.*/
	protected static final String DATE_ARGUMENT_PREFIX = "closest=";
	/** The prefix for the URL argument in the HTTP request.*/
	protected static final String LIMIT_1_ARGUMENT = "limit=1";
	/** The prefix for the URL argument in the HTTP request.*/
	protected static final String ARGUMENT_SEPARATOR = "&";
	/** The prefix for the URL argument in the HTTP request.*/
	protected static final String ARGUMENT_INITIALISER = "?";
	/** The element request argument in the HTTP request.*/
	public static final String FL_ARGUMENT_PREFIX = "fl=";
	
	/** Map between CDX format element and their cdx-server fl argument (separated by ,). */
	public static final Map<Character, String> CDX_ARGUMENTS = new LinkedHashMap<Character, String>();
	static {
		CDX_ARGUMENTS.put(CDXConstants.CDX_CHAR_ORIGINAL_URL, "url");
		CDX_ARGUMENTS.put(CDXConstants.CDX_CHAR_DATE, "timestamp");
		CDX_ARGUMENTS.put(CDXConstants.CDX_CHAR_FILE_NAME, "filename");
		CDX_ARGUMENTS.put(CDXConstants.CDX_CHAR_COMPRESSED_ARC_FILE_OFFSET, "offset");
//		CDX_ARGUMENTS.put(CDXEntry.CDX_CHAR_ARC_DOCUMENT_LENGTH, "length");
		CDX_ARGUMENTS.put(CDXConstants.CDX_CHAR_MIME_TYPE, "mime");
		CDX_ARGUMENTS.put(CDXConstants.CDX_CHAR_RESPONSE_CODE, "status");
		CDX_ARGUMENTS.put(CDXConstants.CDX_CHAR_NEW_STYLE_CHECKSUM, "digest");
	}
	
	/** The prefix for the URL argument in the HTTP request.*/
	private final String cdxUrl;
	
	protected final HttpRetriever httpRetriever;
	
	/**
	 * Constructor.
	 * @param cdxServerUrl The URL for the CDX server (complete url to query for the right collection).
	 * @param httpRetriever The http retriever.
	 */
	public PywbCDXExtractor(String cdxServerUrl, HttpRetriever httpRetriever) {
		this.cdxUrl = cdxServerUrl;
		this.httpRetriever = httpRetriever;
	}
	
	@Override
	public CDXEntry retrieveCDX(WPID wpid) {
		String requestUrlString = createRequestUrlForWPID(wpid);
		String response = httpRetriever.retrieveFromUrl(requestUrlString);
		
		if(response == null) {
			logger.warn("Failed to retrieve wpid '" + wpid.toString() + "'. Returning a null");
			return null;
		} else {
			return CDXEntry.createCDXEntry(createCdxMap(response));
		}
	}

	@Override
	public Collection<CDXEntry> retrieveAllCDX(String url) {
		String requestUrlString = createRequestUrlForWID(url);
		String response = httpRetriever.retrieveFromUrl(requestUrlString);
		
		if(response == null || response.isEmpty()) {
			logger.warn("Failed to retrieve CDX indices for URL '" + url + "'. Returning a null");
			return null;
		} else {
			List<CDXEntry> res = new ArrayList<CDXEntry>();
			for(String line : response.split("\n")) {
				CDXEntry entry = CDXEntry.createCDXEntry(createCdxMap(line));
				if(entry != null) {
					res.add(entry);
				}
			}
			return res;
		}
	}

	/**
	 * Creates the request URL for retrieving the CDX entry for a given. 
	 * @param wpid The WPID with the URL and the specific date.
	 * @return The request URL for retrieving the CDX from the CDX server.
	 */
	protected String createRequestUrlForWPID(WPID wpid) {
		StringBuilder res = new StringBuilder();
		res.append(cdxUrl);
		if(!cdxUrl.endsWith(ARGUMENT_INITIALISER)) {
			res.append(ARGUMENT_INITIALISER);
		}
		res.append(URL_ARGUMENT_PREFIX);
		res.append(wpid.getUrl());
		res.append(ARGUMENT_SEPARATOR);
		res.append(DATE_ARGUMENT_PREFIX);
		res.append(DateUtils.dateToWaybackDate(wpid.getDate()));
		res.append(ARGUMENT_SEPARATOR);
		res.append(LIMIT_1_ARGUMENT);
		res.append(ARGUMENT_SEPARATOR);
		res.append(FL_ARGUMENT_PREFIX);
		for(String s : CDX_ARGUMENTS.values()) {
			res.append(s);
			res.append(",");
		}
		res.delete(res.length() - 1, res.length()); // remove last ','
		return res.toString();
	}

	
	/**
	 * Creates the request URL for retrieving the CDX entry for a given. 
	 * @param url The URL for the web-resource.
	 * @param date The date when the web-resource was harvested.
	 * @return The request URL for retrieving the CDX from the CDX server.
	 */
	protected String createRequestUrlForWID(String url) {
		StringBuilder res = new StringBuilder();
		res.append(cdxUrl);
		if(!cdxUrl.endsWith(ARGUMENT_INITIALISER)) {
			res.append(ARGUMENT_INITIALISER);
		}
		res.append(URL_ARGUMENT_PREFIX);
		res.append(url);
		res.append(ARGUMENT_SEPARATOR);
		res.append(FL_ARGUMENT_PREFIX);
		for(String s : CDX_ARGUMENTS.values()) {
			res.append(s);
			res.append(",");
		}
		res.delete(res.length() - 1, res.length()); // remove last ','
		return res.toString();
	}
	
	/**
	 * Creates the CDX map between CDX format element and the string retrieved from the CDX server.
	 * @param cdxLine The cdx line from the server.
	 * @return The map between cdx format element and the value.
	 */
	protected Map<Character, String> createCdxMap(String cdxLine) {
		Map<Character, String> res = new HashMap<Character, String>();
		String[] cdxLineSplit = cdxLine.split(" ");
		Character[] format = CDX_ARGUMENTS.keySet().toArray(new Character[CDX_ARGUMENTS.size()]);
		
		if(cdxLineSplit.length < format.length) {
			logger.warn("Not enough cdx elements. Expected " + format.length + " but only got " + cdxLine.length());
			return null;
		}

		for(int i = 0; i < format.length; i++) {
				res.put(format[i], cdxLineSplit[i]);
		}
		return res;
	}
}
