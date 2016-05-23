package dk.netarkivet.research.links;

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
import org.jwat.warc.WarcRecord;
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

	/** The content of the HTML page to extract the links from.*/
    protected final String htmlContent;
	/** 
	 * The base URL for the HTML page.
	 * Used for handling references.
	 */
	protected final URL baseUrl;
	
	/**
	 * Constructor.
	 * @param record The WARC record with the HTML to extract links from.
	 * It must have content-type 'text/html' and contain the URL in the header-uri.
	 */
	public HtmlLinkExtractor(WarcRecord record) {
		ArgumentCheck.checkNotNullOrEmpty(record.getHttpHeader().contentType, "Warc record http field content type");
		ArgumentCheck.checkNotNullOrEmpty(record.header.warcTargetUriStr, "The URL in the WARC header");
		ArgumentCheck.checkIsTrue(record.getHttpHeader().contentType.contains("text/html"), "Header field for content "
				+ "type (" + record.getHttpHeader().contentType + ") should contain 'text/hmtl'");
		
		try {
			htmlContent = StreamUtils.extractInputStreamAsText(record.getPayloadContent());
			URL contentUrl = new URL(record.header.warcTargetUriStr);
			baseUrl = new URL(UrlUtils.getBaseUrl(contentUrl));
		} catch (Exception e) {
			throw new IllegalStateException("Could not instantiate the HTML link extractor.", e);
		}
	}
	
	@Override
	public Collection<String> extractLinks() {
        NodeFilter filter;
        NodeList list;
        List<String> links = new ArrayList<String>();
        Parser p = new Parser();
        filter = new NodeClassFilter(LinkTag.class);
        
        try {
            p.setInputHTML(htmlContent);
            list = p.extractAllNodesThatMatch(filter);
            for(int i = 0; i < list.size(); i++) {
                LinkTag n = (LinkTag) list.elementAt(i);
                URL url = extractUrlForLink(n);
                if(url != null) {
                	links.add(url.toExternalForm());
                }
            }
        } catch (Exception e) {
        	logger.warn("Could not extract links from HTML page.", e);
        }
        return links;
	}
	
	/**
	 * Extracts an URL from a HTML link tag.
	 * @param link The HTML link tag. 
	 * @return The URL for the link, or null if it does not contain a URL, or the URL is invalid.
	 */
	protected URL extractUrlForLink(LinkTag link) {
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
