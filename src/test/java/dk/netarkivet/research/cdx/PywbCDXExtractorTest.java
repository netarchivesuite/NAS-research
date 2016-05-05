package dk.netarkivet.research.cdx;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.Collection;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.annotations.Test;

import dk.netarkivet.research.http.HttpRetriever;
import dk.netarkivet.research.utils.DateUtils;
import dk.netarkivet.research.wid.WPID;

public class PywbCDXExtractorTest extends ExtendedTestCase {

	// Use the default PYWB cdx-server.
	String serverUrl = "http://localhost:8080/pywb-cdx/query.html";
	
	@Test
    public void testSimpleCDXExtraction() throws Exception {
		addDescription("Test extraction of a CDX index.");
		String extractURL = "http://example.com/";
		String extractDate = "20140127171200";
		String resultString = extractURL + " " + extractDate + " filename 123 html 200 0fac8bab025fac54495109c9d44b287b";

		WPID wpid = WPID.createNarkWPid(extractURL, DateUtils.waybackDateToDate(extractDate));
		HttpRetriever retriever = mock(HttpRetriever.class);
		when(retriever.retrieveFromUrl(anyString())).thenReturn(resultString);
		
		PywbCDXExtractor extractor = new PywbCDXExtractor(serverUrl, retriever);
		CDXEntry entry = extractor.retrieveCDX(wpid);
		assertNotNull(entry);
		assertEquals(entry.url, extractURL);
	}

	@Test
    public void testCDXExtractionFailure() throws Exception {
		addDescription("Test extracting a element, which does not exist.");
		String extractURL = "http://non-existing-url.in-the-cdx.com/";
		String extractDate = "20130321174128";

		WPID wpid = WPID.createNarkWPid(extractURL, DateUtils.waybackDateToDate(extractDate));
		HttpRetriever retriever = mock(HttpRetriever.class);
		when(retriever.retrieveFromUrl(anyString())).thenReturn(null);
		
		PywbCDXExtractor extractor = new PywbCDXExtractor(serverUrl, retriever);
		CDXEntry entry = extractor.retrieveCDX(wpid);
		assertNull(entry);
	}
	
	@Test
    public void testAllCDXExtractionSuccess() throws Exception {
		addDescription("Test extraction of a element.");
		String extractURL = "http://example.com/";
		String resultString = extractURL + " 20140127171200 filename 123 html 200 0fac8bab025fac54495109c9d44b287b\n"
				+ extractURL + " 20150127171200 filename2 321 html 200 0fac8bab025fac54495109c9d44b287b\n";

		HttpRetriever retriever = mock(HttpRetriever.class);
		when(retriever.retrieveFromUrl(anyString())).thenReturn(resultString);
		
		PywbCDXExtractor extractor = new PywbCDXExtractor(serverUrl, retriever);
		Collection<CDXEntry> entries = extractor.retrieveAllCDX(extractURL);
		assertNotNull(entries);
		assertFalse(entries.isEmpty());
		assertEquals(entries.size(), 2);
		for(CDXEntry entry : entries) {
			assertEquals(entry.url, extractURL);
		}
	}
	
	@Test
    public void testAllCDXExtractionFailure() throws Exception {
		addDescription("Test extraction of a element.");
		String extractURL = "http://example.com/";

		HttpRetriever retriever = mock(HttpRetriever.class);
		when(retriever.retrieveFromUrl(anyString())).thenReturn(null);
		
		PywbCDXExtractor extractor = new PywbCDXExtractor(serverUrl, retriever);
		Collection<CDXEntry> entries = extractor.retrieveAllCDX(extractURL);
		assertNull(entries);
	}
	
	@Test
    public void testAllCDXExtractionEmpyString() throws Exception {
		addDescription("Test extraction of a element.");
		String extractURL = "http://example.com/";

		HttpRetriever retriever = mock(HttpRetriever.class);
		when(retriever.retrieveFromUrl(anyString())).thenReturn(" ");
		
		PywbCDXExtractor extractor = new PywbCDXExtractor(serverUrl, retriever);
		Collection<CDXEntry> entries = extractor.retrieveAllCDX(extractURL);
		assertNotNull(entries);
		assertTrue(entries.isEmpty());
	}
}
