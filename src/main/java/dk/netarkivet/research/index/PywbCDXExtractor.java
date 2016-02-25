package dk.netarkivet.research.index;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.netarkivet.research.utils.DateUtils;
import dk.netarkivet.research.wpid.WPID;

/**
 * Extractor for the CDX-server of the PYWB.
 * Example of extraction url:
 * http://localhost:8080/pywb-cdx/coll-cdx?url=example.com&closest=20140127171200&limit=1&fl=url,timestamp,filename,offset,length,mime,status,digest
 */
public class PywbCDXExtractor implements CDXExtractor {
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
	public static final Map<Character, String> CDX_ARGUMENTS = new HashMap<Character, String>();
	static {
		CDX_ARGUMENTS.put(CDXEntry.CDX_CHAR_ORIGINAL_URL, "url");
		CDX_ARGUMENTS.put(CDXEntry.CDX_CHAR_DATE, "timestamp");
		CDX_ARGUMENTS.put(CDXEntry.CDX_CHAR_FILE_NAME, "filename");
		CDX_ARGUMENTS.put(CDXEntry.CDX_CHAR_COMPRESSED_ARC_FILE_OFFSET, "offset");
//		CDX_ARGUMENTS.put(CDXEntry.CDX_CHAR_ARC_DOCUMENT_LENGTH, "length");
		CDX_ARGUMENTS.put(CDXEntry.CDX_CHAR_MIME_TYPE, "mime");
		CDX_ARGUMENTS.put(CDXEntry.CDX_CHAR_RESPONSE_CODE, "status");
		CDX_ARGUMENTS.put(CDXEntry.CDX_CHAR_NEW_STYLE_CHECKSUM, "digest");
	}
	
	/** The prefix for the URL argument in the HTTP request.*/
	private final String cdxUrl;
	
	/**
	 * Constructor.
	 * @param cdxServerUrl The URL for the CDX server (complete url to query for the right collection).
	 */
	public PywbCDXExtractor(String cdxServerUrl) {
		this.cdxUrl = cdxServerUrl;
	}
	
	@Override
	public CDXEntry retrieveCDX(WPID wpid) {
		String requestUrlString = createRequestUrl(wpid.getUrl(), wpid.getDate());getClass();
		try {
			CloseableHttpClient httpClient = HttpClients.createDefault();
			HttpGet httpGet = new HttpGet(requestUrlString);
			
			HttpResponse response = httpClient.execute(httpGet);
			if(response.getStatusLine().getStatusCode() != 200) {
				logger.warn("Failed to retrieve data. Received response code " + response.getStatusLine().getStatusCode());
				return null;
			}
			
			String cdxLine = EntityUtils.toString(response.getEntity());
			return CDXEntry.createCDXEntry(createCdxMap(cdxLine));
		} catch (IOException e) {
			logger.warn("Failed to retrieve wpid '" + wpid.toString() + "'. Returning a null", e);
			return null;
		}
	}

	/**
	 * Creates the request URL for retrieving the CDX entry for a given. 
	 * @param url The URL for the web-resource.
	 * @param date The date when the web-resource was harvested.
	 * @return The request URL for retrieving the CDX from the CDX server.
	 */
	protected String createRequestUrl(String url, Date date) {
		StringBuilder res = new StringBuilder();
		res.append(cdxUrl);
		if(!cdxUrl.endsWith(ARGUMENT_INITIALISER)) {
			res.append(ARGUMENT_INITIALISER);
		}
		res.append(URL_ARGUMENT_PREFIX);
		res.append(url);
		res.append(ARGUMENT_SEPARATOR);
		res.append(DATE_ARGUMENT_PREFIX);
		res.append(DateUtils.dateToWaybackDate(date));
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
	 * Creates the CDX map between CDX format element and the string retrieved from the CDX server.
	 * @param cdxLine The cdx line from the server.
	 * @return The map between cdx format element and the value.
	 */
	protected Map<Character, String> createCdxMap(String cdxLine) {
		Map<Character, String> res = new HashMap<Character, String>();
		String[] cdxLineSplit = cdxLine.split(" ");
		Character[] format = CDX_ARGUMENTS.keySet().toArray(new Character[CDX_ARGUMENTS.size()]);

		for(int i = 0; i < format.length; i++) {
			res.put(format[i], cdxLineSplit[i]);
		}
		return res;
	}
}
