package dk.netarkivet.research.links;

import java.io.InputStream;
import java.net.URL;
import java.util.Collection;

/**
 * Interface for links extractors.
 */
public interface LinkExtractor {
	/**
	 * Extracts the links.
	 * @param record The inputstream with the HTML record.
	 * @param contentUrl The URL for the HTML record.
	 * @return The list of URLs extracted from the link extractor.
	 */
	Collection<String> extractLinks(InputStream record, URL contentUrl);
	
	/**
	 * @return The mimetype which is supported by this extractor.
	 */
	String supportedMimetype();
}
