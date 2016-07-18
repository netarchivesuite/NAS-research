package dk.netarkivet.research.cdx;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.SkipException;
import org.testng.annotations.Test;

import dk.netarkivet.research.http.HttpRetriever;
import dk.netarkivet.research.interval.UrlInterval;
import dk.netarkivet.research.testutils.ProcessTestingUtils;
import dk.netarkivet.research.utils.DateUtils;
import dk.netarkivet.research.wid.WID;
import dk.netarkivet.research.wid.WPID;
import dk.netarkivet.research.wid.WaybackWID;

public class DabCDXExtractorTest extends ExtendedTestCase {

	// Use the default dab cdx-server
	String serverUrl = "http://localhost:8080/dab/query/";
	
	String dabResultString = "netarkivet.dk/eurovision-song-contest-2014/ 20140623134425 http://netarkivet.dk/eurovision-song-contest-2014/ text/html 200 EVSVCFF7UCXF6AT2VQ7OABH4A6Q4X4D5 - 12840518 209790-135-20140623134154-00000-sb-prod-har-004.statsbiblioteket.dk.warc\n"
			+ "netarkivet.dk/eurovision-song-contest-2014/ 20140623134542 http://netarkivet.dk/eurovision-song-contest-2014/ text/html 200 EVSVCFF7UCXF6AT2VQ7OABH4A6Q4X4D5 - 24458484 209784-135-20140623134303-00000-kb-prod-har-002.kb.dk.warc\n"
			+ "netarkivet.dk/eurovision-song-contest-2014/ 20140623141323 http://netarkivet.dk/eurovision-song-contest-2014/ text/html 200 EVSVCFF7UCXF6AT2VQ7OABH4A6Q4X4D5 - 14130596 209791-135-20140623141051-00000-sb-prod-har-004.statsbiblioteket.dk.warc\n"
			+ "netarkivet.dk/eurovision-song-contest-2014/ 20140623141903 http://netarkivet.dk/eurovision-song-contest-2014/ text/html 200 EVSVCFF7UCXF6AT2VQ7OABH4A6Q4X4D5 - 24458464 209792-135-20140623141635-00000-kb-prod-har-003.kb.dk.warc\n"
			+ "netarkivet.dk/eurovision-song-contest-2014/ 20141006145452 http://netarkivet.dk/eurovision-song-contest-2014/ text/html 200 DSJ4NEEOW5S5GVFVK7AZFKQSG63QNYDP - 310019820 215896-217-20141006141736-00003-kb-prod-har-004.kb.dk.warc\n"
			+ "netarkivet.dk/eurovision-song-contest-2014/ 20150910100240 http://netarkivet.dk/eurovision-song-contest-2014/ text/html 200 AB66XB3DFYS3M2OYQ4EWR54X4NA45HTI - 16223658 239240-135-20150910100037-00000-kb-prod-har-024.kb.dk.warc\n"
			+ "netarkivet.dk/eurovision-song-contest-2014/ 20151112080726 http://netarkivet.dk/eurovision-song-contest-2014/ text/html 200 AUSGLUEQIXCL4WT2UF74LTNN7CCFRZY3 - 6960131 244253-135-20151112080508-00000-kb-prod-har-011.kb.dk.warc\n"
			+ "netarkivet.dk/eurovision-song-contest-2014/ 20151112091559 http://netarkivet.dk/eurovision-song-contest-2014/ text/html 200 AUSGLUEQIXCL4WT2UF74LTNN7CCFRZY3 - 1515645 244257-135-20151112091335-00000-kb-prod-har-013.kb.dk.warc\n"
			+ "netarkivet.dk/eurovision-song-contest-2014/ 20151112112546 http://netarkivet.dk/eurovision-song-contest-2014/ text/html 200 AUSGLUEQIXCL4WT2UF74LTNN7CCFRZY3 - 1515640 244260-135-20151112112315-00000-kb-prod-har-007.kb.dk.warc\n"
			+ "netarkivet.dk/eurovision-song-contest-2014/ 20151211101606 http://netarkivet.dk/eurovision-song-contest-2014/ text/html 200 AUSGLUEQIXCL4WT2UF74LTNN7CCFRZY3 - 190083548 245851-241-20151211095913-00001-sb-prod-har-001.statsbiblioteket.dk.warc\n"
			+ "netarkivet.dk/eurovision-song-contest-2014/ 20160118122240 http://netarkivet.dk/eurovision-song-contest-2014/ text/html 200 AHHABWQZSCY6GGQCQCLTR4XTEDP4GNSW - 7184247 250506-135-20160118122037-00000-kb-prod-har-008.kb.dk.warc\n"
			+ "netarkivet.dk/eurovision-song-contest-2014/ 20160214135612 http://netarkivet.dk/eurovision-song-contest-2014/ text/html 200 BCQSTEJA76UQGNA2FB3VDWF4GBSMW5UL - 131028640 251953-244-20160214133913-00000-sb-prod-har-003.statsbiblioteket.dk.warc\n"
			+ "netarkivet.dk/eurovision-song-contest-2014/ 20160621121541 http://netarkivet.dk/eurovision-song-contest-2014/ text/html 200 NELZEB54CWQLCDY6GIEAC6I7EHWLZNGH - 53449465 261174-252-20160621121532812-00016-sb-prod-har-004.statsbiblioteket.dk.warc\n";
	
	@Test
    public void testMockedSimpleCDXExtraction() throws Exception {
		addDescription("Test extraction of a element.");
		String extractURL = "http://example.com/";
		String extractDate = "20140127171200";

		WPID wpid = WPID.createNarkWPid(extractURL, DateUtils.waybackDateToDate(extractDate));
		HttpRetriever retriever = mock(HttpRetriever.class);
		when(retriever.retrieveFromUrl(anyString())).thenReturn(dabResultString);
		
		CDXExtractor extractor = new DabCDXExtractor(serverUrl, retriever);
		CDXEntry entry = extractor.retrieveCDX(wpid);
		assertNotNull(entry);
	}

	@Test
    public void testMockedCDXExtractionFailure() throws Exception {
		addDescription("Test extracting a element, which does not exist.");
		String extractURL = "http://non-existing-url.in-the-cdx.com/";
		String extractDate = "20130321174128";

		WPID wpid = WPID.createNarkWPid(extractURL, DateUtils.waybackDateToDate(extractDate));
		HttpRetriever retriever = mock(HttpRetriever.class);
		when(retriever.retrieveFromUrl(anyString())).thenReturn("");
		
		CDXExtractor extractor = new DabCDXExtractor(serverUrl, retriever);
		CDXEntry entry = extractor.retrieveCDX(wpid);
		assertNull(entry);
	}
	
	@Test
	public void testMockedCDXExtractionForAllCDXEntries() throws Exception {
		addDescription("Test extraction of a element.");
		String extractURL = "http://example.com/";

		HttpRetriever retriever = mock(HttpRetriever.class);
		when(retriever.retrieveFromUrl(anyString())).thenReturn(dabResultString);
		
		CDXExtractor extractor = new DabCDXExtractor(serverUrl, retriever);
		Collection<CDXEntry> entries = extractor.retrieveAllCDX(extractURL);
		assertNotNull(entries);
		assertEquals(entries.size(), 13);
	}
	
	@Test
	public void testMockedCDXExtractionForWaybackWidWithFilename() throws Exception {
		addDescription("Test extraction of a CDX entry on the filename. And validate, that it has that file-name");
		String extractURL = "http://example.com/";
		String extractDate = "20140127171200";
		String extractFilename = "251953-244-20160214133913-00000-sb-prod-har-003.statsbiblioteket.dk.warc";

		WaybackWID wid = WaybackWID.createNarkWaybackWID(extractFilename, extractURL, DateUtils.waybackDateToDate(extractDate));
		HttpRetriever retriever = mock(HttpRetriever.class);
		when(retriever.retrieveFromUrl(anyString())).thenReturn(dabResultString);
		
		CDXExtractor extractor = new DabCDXExtractor(serverUrl, retriever);
		CDXEntry entry = extractor.retrieveCDX(wid);
		assertNotNull(entry);
		assertEquals(entry.filename, extractFilename);
	}

	@Test
	public void testMockedCDXExtractionForWaybackWidWithUnknownFilename() throws Exception {
		addDescription("Test extraction of a CDX entry using a filename, which is not existing.");
		String extractURL = "http://example.com/";
		String extractDate = "20140127171200";
		String extractFilename = "ThisIsNotAProperFilename";

		WaybackWID wid = WaybackWID.createNarkWaybackWID(extractFilename, extractURL, DateUtils.waybackDateToDate(extractDate));
		HttpRetriever retriever = mock(HttpRetriever.class);
		when(retriever.retrieveFromUrl(anyString())).thenReturn(dabResultString);
		
		CDXExtractor extractor = new DabCDXExtractor(serverUrl, retriever);
		CDXEntry entry = extractor.retrieveCDX(wid);
		assertNotNull(entry);
		assertFalse(entry.filename.equals(extractFilename));
	}

	@Test
	public void testMockedCDXExtractionForAllWIDEntries() throws Exception {
		addDescription("Test extraction of CDX entries for WIDs.");
		String extractURL = "http://example.com/";
		String extractDate = "20140127171200";
		String extractFilename = "ThisIsNotAProperFilename";

		WaybackWID wid1 = WaybackWID.createNarkWaybackWID(extractFilename, extractURL, DateUtils.waybackDateToDate(extractDate));
		WPID wid2 = WPID.createNarkWPid(extractURL, DateUtils.waybackDateToDate(extractDate));
		HttpRetriever retriever = mock(HttpRetriever.class);
		when(retriever.retrieveFromUrl(anyString())).thenReturn(dabResultString);
		
		CDXExtractor extractor = new DabCDXExtractor(serverUrl, retriever);
		Collection<CDXEntry> entry = extractor.retrieveCDXentries(Arrays.asList((WID) wid1, (WID) wid2));
		assertNotNull(entry);
		assertFalse(entry.isEmpty());
		assertEquals(entry.size(), 2);
	}

	@Test
	public void testMockedCDXExtractionOfURLInterval() throws Exception {
		addDescription("Test extraction of CDX entries for a URL interval.");
		String extractURL = "http://example.com/";
		Date extractEarliestDate = DateUtils.waybackDateToDate("20150101000000");
		Date extractLatestDate = DateUtils.waybackDateToDate("20160101000000");

		UrlInterval urlInterval = new UrlInterval(extractURL, extractEarliestDate, extractLatestDate); 
		HttpRetriever retriever = mock(HttpRetriever.class);
		when(retriever.retrieveFromUrl(anyString())).thenReturn(dabResultString);
		
		CDXExtractor extractor = new DabCDXExtractor(serverUrl, retriever);
		Collection<CDXEntry> entry = extractor.retrieveCDXForInterval(urlInterval);
		assertNotNull(entry);
		assertFalse(entry.isEmpty());
		assertEquals(entry.size(), 5);
	}

	
	@Test
    public void testSimpleCDXExtraction() throws Exception {
		if(!ProcessTestingUtils.isProcessRunning("tomcat")) {
			throw new SkipException("No local tomcat for DAB. Skipping test.");
		}
		addDescription("Test extraction of a element.");
		String extractURL = "http://example.com/";
		String extractDate = "20140127171200";

		WPID wpid = WPID.createNarkWPid(extractURL, DateUtils.waybackDateToDate(extractDate));
		CDXExtractor extractor = new DabCDXExtractor(serverUrl, new HttpRetriever());
		CDXEntry entry = extractor.retrieveCDX(wpid);
		assertNotNull(entry);
	}

	@Test
    public void testCDXExtractionFailure() throws Exception {
		if(!ProcessTestingUtils.isProcessRunning("tomcat")) {
			throw new SkipException("No local tomcat for DAB. Skipping test.");
		}
		addDescription("Test extracting a element, which does not exist.");
		String extractURL = "http://non-existing-url.in-the-cdx.com/";
		String extractDate = "20130321174128";

		WPID wpid = WPID.createNarkWPid(extractURL, DateUtils.waybackDateToDate(extractDate));
		CDXExtractor extractor = new DabCDXExtractor(serverUrl, new HttpRetriever());
		CDXEntry entry = extractor.retrieveCDX(wpid);
		assertNull(entry);
	}
	
	@Test
	public void testAllCDXExtraction() throws Exception {
		if(!ProcessTestingUtils.isProcessRunning("tomcat")) {
			throw new SkipException("No local tomcat for DAB. Skipping test.");
		}
		addDescription("Test extraction of a element.");
		String extractURL = "http://example.com/";

		CDXExtractor extractor = new DabCDXExtractor(serverUrl, new HttpRetriever());
		Collection<CDXEntry> entries = extractor.retrieveAllCDX(extractURL);
		assertNotNull(entries);
		assertEquals(entries.size(), 44);
	}
}
