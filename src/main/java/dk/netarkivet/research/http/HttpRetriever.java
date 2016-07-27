package dk.netarkivet.research.http;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for encapsulating HTTP methods.
 */
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
		try (CloseableHttpClient httpClient = HttpClients.createDefault();){
			HttpGet httpGet = new HttpGet(url);

			HttpResponse response = httpClient.execute(httpGet);
			if(response.getStatusLine().getStatusCode() != 200) {
				logger.warn("Failed to retrieve data. Received response code " 
						+ response.getStatusLine().getStatusCode());
				return null;
			}

			return EntityUtils.toString(response.getEntity());
		} catch (IOException e) {
			logger.warn("Failed to retrieve data from '" + url + "'. Returning a null", e);
			return null;
		}
	}

	/**
	 * Check whether a link exists on the live net.
	 * @param link The link to validate whether it exists.
	 * @return Whether or not the link refers to an existing URL.
	 */
	public boolean exists(String link) {
		try {
			URL url = new URL(link);
			HttpURLConnection.setFollowRedirects(false);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("HEAD");
			
			return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
		} catch (Exception e) {
			logger.debug("Could validate the existing of the link '" + link + "'", e);
			return false;
		}
	}
}
