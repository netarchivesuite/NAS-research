package dk.netarkivet.research.http;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpRetriever {
	/** The log.*/
	private static Logger logger = LoggerFactory.getLogger(HttpRetriever.class);

	/**
	 * Constructor.
	 */
	public HttpRetriever() {}
	
	/**
	 * Makes a HTTP request and delivers the content of the respons.
	 * Will return a null, if an exception is thrown, or if it does not receive a 200 HTTP response.
	 * @param url The url for the HTTP request.
	 * @return The response, or null if it is a bad response or an exception was thrown.
	 */
	public String retrieveFromUrl(String url) {
		try {
			CloseableHttpClient httpClient = HttpClients.createDefault();
			HttpGet httpGet = new HttpGet(url);
			
			HttpResponse response = httpClient.execute(httpGet);
			if(response.getStatusLine().getStatusCode() != 200) {
				logger.warn("Failed to retrieve data. Received response code " + response.getStatusLine().getStatusCode());
				return null;
			}
			
			return EntityUtils.toString(response.getEntity());
		} catch (IOException e) {
			logger.warn("Failed to retrieve data from '" + url + "'. Returning a null", e);
			return null;
		}
	}
}
