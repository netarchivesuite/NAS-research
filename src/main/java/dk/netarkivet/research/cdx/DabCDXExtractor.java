package dk.netarkivet.research.cdx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
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
public class DabCDXExtractor extends AbstractCDXExtractor {
	/** The log.*/
	private static Logger logger = LoggerFactory.getLogger(DabCDXExtractor.class);

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
	protected static final List<Character> CDX_FORMAT_CHARS = 
			Collections.unmodifiableList(Arrays.asList(
					CDXConstants.CDX_CHAR_CANONIZED_URL,
					CDXConstants.CDX_CHAR_DATE,
					CDXConstants.CDX_CHAR_ORIGINAL_URL,
					CDXConstants.CDX_CHAR_MIME_TYPE,
					CDXConstants.CDX_CHAR_RESPONSE_CODE,
					CDXConstants.CDX_CHAR_NEW_STYLE_CHECKSUM,
					CDXConstants.CDX_CHAR_REDIRECT,
					CDXConstants.CDX_CHAR_COMPRESSED_ARC_FILE_OFFSET,
					CDXConstants.CDX_CHAR_FILE_NAME));
	
	/**
	 * Retrieves the default CDX format for this extractor.
	 * @return The default CDX format.
	 */
	public static Collection<Character> getDefaultCDXFormat() {
		return Collections.unmodifiableList(CDX_FORMAT_CHARS);
	}

	/** The prefix for the URL argument in the HTTP request.*/
	protected final String cdxUrl;
	/** The http retriever for handling the HTTP requests to the CDX server.*/
	protected final HttpRetriever httpRetriever;
	
	/** Map to keep track of the already extracted CDX entries, so we don't have to extract them several times.*/
	protected final Map<String, List<CDXEntry>> cdxExtractMap = new HashMap<String, List<CDXEntry>>();

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
		return retrieveCDXclosestToDate(allCDXforUrl, wpid.getDate());
	}

	@Override
	public Collection<CDXEntry> retrieveAllCDX(String url) {
		List<CDXEntry> res = cdxExtractMap.get(url);

		if(res != null) {
			logger.debug("Using already extracted CDX entries for the URL '" + url + "'.");
		} else {
			logger.debug("Extracting CDX entries for URL '" + url + "'.");
			String requestUrlString = createRequestUrlForURL(url);
			String response = httpRetriever.retrieveFromUrl(requestUrlString);
			if(response == null || response.isEmpty()) {
				logger.warn("Failed to retrieve CDX indices for the URL '" + url + "'. Returning a null");
				res = Arrays.asList();
			} else {
				res = new ArrayList<CDXEntry>();
				for(String line : response.split("\n")) {
					CDXEntry entry = CDXEntry.createCDXEntry(createCdxMap(line));
					if(entry != null) {
						res.add(entry);
					}
				}
			}
			cdxExtractMap.put(url, res);
		}
		
		return res;
	}

	/**
	 * Creates the request URL for retrieving the CDX entry for a given. 
	 * @param url The URL for the web-resource.
	 * @param date The date when the web-resource was harvested.
	 * @return The request URL for retrieving the CDX from the CDX server.
	 */
	protected String createRequestUrlForURL(String url) {
		String urlWithoutProtocol = UrlUtils.stripProtocolAndWWWAndLowerCase(url);
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
