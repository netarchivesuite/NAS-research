package dk.netarkivet.research.index;

import static org.testng.Assert.*;

import java.util.Collection;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.annotations.Test;

import dk.netarkivet.research.utils.DateUtils;
import dk.netarkivet.research.wpid.WPID;

public class DabCDXExtractorTest extends ExtendedTestCase {

	// Use the default dab cdx-server
	String serverUrl = "http://localhost:8080/dab/query/";
	
	@Test
    public void testSimpleCDXExtraction() throws Exception {
		addDescription("Test extraction of a element.");
		String extractURL = "http://example.com/";
		String extractDate = "20140127171200";

		WPID wpid = WPID.createNarkWPid(extractURL, DateUtils.waybackDateToDate(extractDate));
		CDXExtractor extractor = new DabCDXExtractor(serverUrl);
		CDXEntry entry = extractor.retrieveCDX(wpid);
		assertNotNull(entry);
	}

	@Test
    public void testCDXExtractionFailure() throws Exception {
		addDescription("Test extracting a element, which does not exist.");
		String extractURL = "http://non-existing-url.in-the-cdx.com/";
		String extractDate = "20130321174128";

		WPID wpid = WPID.createNarkWPid(extractURL, DateUtils.waybackDateToDate(extractDate));
		
		CDXExtractor extractor = new DabCDXExtractor(serverUrl);
		CDXEntry entry = extractor.retrieveCDX(wpid);
		assertNull(entry);
	}
	
	@Test
	public void testAllCDXExtraction() throws Exception {
		addDescription("Test extraction of a element.");
		String extractURL = "http://example.com/";

		CDXExtractor extractor = new DabCDXExtractor(serverUrl);
		Collection<CDXEntry> entries = extractor.retrieveAllCDX(extractURL);
		assertNotNull(entries);
		assertEquals(entries.size(), 44);
	}
}
