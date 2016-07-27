package dk.netarkivet.research.links;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import dk.netarkivet.research.http.HttpRetriever;

/**
 * CDX Link locator.
 * Extracting the links from WARC records using a link extractor, and checking whether the links can be found in 
 * the CDX server.
 * 
 * Optimized to only look for each link once.
 */
public class LiveLinksLocator extends LinksLocator {
	/** The retriever of the HTTP data.*/
	protected final HttpRetriever httpRetriever;
	
	/** Map over which links have been found to be existing.*/
	protected Map<String, Boolean> linkMap = new HashMap<String, Boolean>();
	
	/**
	 * Constructor.
	 * @param httpRetriever For locating the links on the live net.
	 */
	public LiveLinksLocator(LinkExtractor linkExtractor, HttpRetriever httpRetriever) {
		super(linkExtractor);
		this.httpRetriever = httpRetriever;
	}

	@Override
	protected LinkStatus getLinkStatus(String link, String originalUrl, Date originalDate) {
		if(checkUrl(link)) {
			return new LinkStatus(true, link, new Date(), originalUrl, originalDate, "web");
		} else {
			return new LinkStatus(false, link, null, originalUrl, originalDate, "web");
		}
	}
	
	/**
	 * Checks whether an URL gives a 'OK' http response.
	 * Will only check the live web, if the link has not yet been found.
	 * If it has been found before, then the previous answer will suffice.
	 * @param url The link to check whether it exists.
	 * @return Whether the URL exists.
	 */
	protected boolean checkUrl(String url) {
		Boolean res = linkMap.get(url);
		if(res == null) {
			res = httpRetriever.exists(url);
			linkMap.put(url, res);
		}
		return res;
	}
}
