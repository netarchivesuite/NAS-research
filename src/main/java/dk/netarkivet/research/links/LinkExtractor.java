package dk.netarkivet.research.links;

import java.util.Collection;

/**
 * Interface for links extractors.
 */
public interface LinkExtractor {
	/**
	 * Extracts the links.
	 * @return The list of URLs extracted from the link extractor.
	 */
	Collection<String> extractLinks();
}
