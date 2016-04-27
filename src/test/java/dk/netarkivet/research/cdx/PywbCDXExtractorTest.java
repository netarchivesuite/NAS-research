package dk.netarkivet.research.cdx;

import static org.testng.Assert.*;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.annotations.Test;

import dk.netarkivet.research.cdx.CDXEntry;
import dk.netarkivet.research.cdx.PywbCDXExtractor;
import dk.netarkivet.research.utils.DateUtils;
import dk.netarkivet.research.wpid.WPID;

public class PywbCDXExtractorTest extends ExtendedTestCase {

	// DISABLED - we are using the dab-cdx-server instead
	
	// Use the default PYWB cdx-server.
	String serverUrl = "http://localhost:8080/pywb-cdx/query.html";
	
//	@Test
    public void testSimpleCDXExtraction() throws Exception {
		addDescription("Test extraction of a element.");
		String extractURL = "http://example.com/";
		String extractDate = "20140127171200";

		WPID wpid = WPID.createNarkWPid(extractURL, DateUtils.waybackDateToDate(extractDate));
		
		PywbCDXExtractor extractor = new PywbCDXExtractor(serverUrl);
		CDXEntry entry = extractor.retrieveCDX(wpid);
		assertNotNull(entry);
	}

//	@Test
    public void testCDXExtractionFailure() throws Exception {
		addDescription("Test extracting a element, which does not exist.");
		String extractURL = "http://non-existing-url.in-the-cdx.com/";
		String extractDate = "20130321174128";

		WPID wpid = WPID.createNarkWPid(extractURL, DateUtils.waybackDateToDate(extractDate));
		
		PywbCDXExtractor extractor = new PywbCDXExtractor(serverUrl);
		CDXEntry entry = extractor.retrieveCDX(wpid);
		assertNull(entry);
	}
}
