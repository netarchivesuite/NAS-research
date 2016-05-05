package dk.netarkivet.research.cdx;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.netarkivet.research.http.HttpRetriever;
import dk.netarkivet.research.utils.UrlUtils;
import dk.netarkivet.research.wid.WPID;

/**
 * Extractor for the CDX-server of DAB extractor for Netarkivet.
 * Currently the CDX-server cannot extract based on dates, and therefore we must 
 * go through all the CDX indices for a given URL, and return the one closest to the date.
 * 
 * Example of extraction url:
 * http://localhost:8080/dab/query/?q=netarkivet.dk/%20
 */
public class DabCDXExtractor implements CDXExtractor {
	/** The log.*/
	private static Logger logger = LoggerFactory.getLogger(CDXEntry.class);

	/** The prefix for the URL argument in the HTTP request.*/
	protected static final String QUERY_PREFIX = "?q=";
	/** The slash, which must be in the URL, or it must be appended.*/
	protected static final String QUERY_SLASH = "/";
	/** The suffix for the URL argument (a space) in the HTTP request.*/
	protected static final String QUERY_SUFFIX = "%20";
	
	/**
	 * The CDX format chars.
	 * It must be: A b a m s k r V g
	 */
	public static final List<Character> CDX_FORMAT_CHARS = new ArrayList<Character>();
	static {
		CDX_FORMAT_CHARS.add(CDXConstants.CDX_CHAR_CANONIZED_URL);
		CDX_FORMAT_CHARS.add(CDXConstants.CDX_CHAR_DATE);
		CDX_FORMAT_CHARS.add(CDXConstants.CDX_CHAR_ORIGINAL_URL);
		CDX_FORMAT_CHARS.add(CDXConstants.CDX_CHAR_MIME_TYPE);
		CDX_FORMAT_CHARS.add(CDXConstants.CDX_CHAR_RESPONSE_CODE);
		CDX_FORMAT_CHARS.add(CDXConstants.CDX_CHAR_NEW_STYLE_CHECKSUM);
		CDX_FORMAT_CHARS.add(CDXConstants.CDX_CHAR_REDIRECT);
		CDX_FORMAT_CHARS.add(CDXConstants.CDX_CHAR_COMPRESSED_ARC_FILE_OFFSET);
		CDX_FORMAT_CHARS.add(CDXConstants.CDX_CHAR_FILE_NAME);
	}
	
	/** The prefix for the URL argument in the HTTP request.*/
	private final String cdxUrl;
	/** The http retriever for handling the HTTP requests to the CDX server.*/
	private final HttpRetriever httpRetriever;
	
	/**
	 * Constructor.
	 * @param cdxServerUrl The URL for the CDX server (complete url to query for the right collection).
	 * @param httpRetriever The http retriever for retrieving from the CDX server.
	 */
	public DabCDXExtractor(String cdxServerUrl, HttpRetriever httpRetriever) {
		this.cdxUrl = cdxServerUrl;
		this.httpRetriever = httpRetriever;
	}
	
	@Override
	public CDXEntry retrieveCDX(WPID wpid) {
		Collection<CDXEntry> allCDXforUrl = retrieveAllCDX(wpid.getUrl());
		
		if(allCDXforUrl == null || allCDXforUrl.isEmpty()) {
			return null;
		}
		
		long closestDate = Long.MAX_VALUE;
		CDXEntry res = null;
		
		for(CDXEntry entry : allCDXforUrl) {
			Long timeDiff = Math.abs(entry.getDate() - wpid.getDate().getTime());
			if(timeDiff < closestDate) {
				closestDate = timeDiff;
				res = entry;
			}
		}
		
		return res;
	}

	@Override
	public Collection<CDXEntry> retrieveAllCDX(String url) {
		String requestUrlString = createRequestUrlForURL(url);
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
	 * @param url The URL for the web-resource.
	 * @param date The date when the web-resource was harvested.
	 * @return The request URL for retrieving the CDX from the CDX server.
	 */
	protected String createRequestUrlForURL(String url) {
		String urlWithoutProtocol = UrlUtils.stripProtocol(url);
		StringBuilder res = new StringBuilder();
		res.append(cdxUrl);
		if(!cdxUrl.endsWith(QUERY_PREFIX)) {
			res.append(QUERY_PREFIX);
		}
		res.append(urlWithoutProtocol);
		if(!url.contains(QUERY_SLASH)) {
			res.append(QUERY_SLASH);
		}
		if(!url.endsWith(QUERY_SUFFIX)){
			res.append(QUERY_SUFFIX);
		}
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

		for(int i = 0; i < CDX_FORMAT_CHARS.size(); i++) {
			res.put(CDX_FORMAT_CHARS.get(i), cdxLineSplit[i]);
		}
		return res;
	}
}
