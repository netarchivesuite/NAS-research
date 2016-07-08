package dk.netarkivet.research.links;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.netarkivet.research.exception.ArgumentCheck;
import dk.netarkivet.research.utils.StreamUtils;
import dk.netarkivet.research.utils.UrlUtils;

/**
 * Extracts links from HTML.
 */
public class HtmlLinkExtractor implements LinkExtractor {
	/** Logging mechanism. */
	private static Logger logger = LoggerFactory.getLogger(HtmlLinkExtractor.class);

	/** The HTML mimetype.*/
	public static final String HTML_MIMETYPE = "text/html";

	/**
	 * Constructor.
	 */
	public HtmlLinkExtractor() {}

	@Override
	public Collection<String> extractLinks(InputStream record, URL contentUrl) {
		ArgumentCheck.checkNotNull(record, "InputStream record");
		ArgumentCheck.checkNotNull(contentUrl, "ÚRL contentUrl");

		List<String> links = new ArrayList<String>();
		try {
			String htmlContent = StreamUtils.extractInputStreamAsText(record);
			URL baseUrl = new URL(UrlUtils.getBaseUrl(contentUrl));
			Parser p = new Parser();
			NodeFilter filter = new NodeClassFilter(LinkTag.class);

			p.setInputHTML(htmlContent);
			NodeList list = p.extractAllNodesThatMatch(filter);
			for(int i = 0; i < list.size(); i++) {
				LinkTag n = (LinkTag) list.elementAt(i);
				URL url = extractUrlForLink(n, baseUrl);
				if(url != null) {
					links.add(url.toExternalForm());
				}
			}
		} catch (Exception e) {
			logger.warn("Could not extract links from HTML page.", e);
		}
		logger.trace("Found links for URL '" + contentUrl + "': " + links);
		return links;
	}

	@Override
	public String supportedMimetype() {
		return HTML_MIMETYPE;
	}

	/**
	 * Extracts an URL from a HTML link tag.
	 * @param link The HTML link tag. 
	 * @param baseUrl The baseURL for the extraction.
	 * @return The URL for the link, or null if it does not contain a URL, or the URL is invalid.
	 */
	protected URL extractUrlForLink(LinkTag link, URL baseUrl) {
		String url = link.getAttribute("href");
		if (url == null) {
			url = link.getAttribute("HREF");
		}
		if (url == null) {
			logger.debug("No url found");
			return null;
		}
		try {
			if(url.contains("://")) {
				return new URL(url);
			}
			if(url.startsWith("#")) {
				logger.debug("Ignoring # reference, since it is on same page: " + url);
				return null;
			}
			return new URL(baseUrl, url);
		} catch (MalformedURLException e) {
			logger.info("Issue extracting an link '" + url + "' where the base-url is '" + baseUrl + "'. "
					+ "Returning a null.", e);
		}
		return null;
	}
}
