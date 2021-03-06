package dk.netarkivet.research.duplicates;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.annotations.Test;

import dk.netarkivet.research.cdx.CDXConstants;
import dk.netarkivet.research.cdx.CDXEntry;
import dk.netarkivet.research.cdx.CDXExtractor;
import dk.netarkivet.research.harvestdb.HarvestJobExtractor;
import dk.netarkivet.research.harvestdb.HarvestJobInfo;
import dk.netarkivet.research.interval.UrlInterval;
import dk.netarkivet.research.utils.DateUtils;

public class DuplicateExtractorTest extends ExtendedTestCase {
	
	String testUrl = "netarkivet.dk";
	List<CDXEntry> testEntries = Arrays.asList(
			CDXEntry.createCDXEntry(new String[] {"http://netarkivet.dk", "20110101010101", "VJ3CKK3ZH2FR7V2KM5TSI3TENA7ZSWKM"}, new Character[] {'A', 'b', 'k'}),
			CDXEntry.createCDXEntry(new String[] {"https://netarkivet.dk", "20120202020202", "VJ3CKK3ZH2FR7V2KM5TSI3TENA7ZSWKM"}, new Character[] {'A', 'b', 'k'}),
			CDXEntry.createCDXEntry(new String[] {"http://netarkivet.dk", "20130303030303", "VJ3CKK3ZH2FR7V2KM5TSI3TENA7ZSWKM"}, new Character[] {'A', 'b', 'k'}),
			CDXEntry.createCDXEntry(new String[] {"https://netarkivet.dk", "20140404040404", "VJ3CKK3ZH2FR7V2KM5TSI3TENA7ZSWKM"}, new Character[] {'A', 'b', 'k'}),
			CDXEntry.createCDXEntry(new String[] {"http://netarkivet.dk", "20150505050505", "a9f5f03efdc6d97874959c1e838f1343"}, new Character[] {'A', 'b', 'k'}),
			CDXEntry.createCDXEntry(new String[] {"https://netarkivet.dk", "20160606060606", "a9f5f03efdc6d97874959c1e838f1343"}, new Character[] {'A', 'b', 'k'})
			);
	
	HarvestJobInfo testHarvestJobInfo = new HarvestJobInfo(3715L, "type", "status", "name");
	
	@Test
	public void testDuplicate() throws Exception {
		CDXExtractor extractor = mock(CDXExtractor.class);
		HarvestJobExtractor jobExtractor = mock(HarvestJobExtractor.class);
		
		when(extractor.retrieveAllCDX(anyString())).thenReturn(testEntries);
		
		DuplicateExtractor finder = new DuplicateExtractor(extractor, jobExtractor);
		DuplicateMap map = finder.makeDuplicateMap(new UrlInterval(testUrl, null, null));
		
		assertEquals(map.getDateToChecksumMap().size(), testEntries.size());
		
		verify(extractor).retrieveAllCDX(eq(testUrl));
		verifyZeroInteractions(jobExtractor);
	}
	
	@Test
	public void testJobInfoExtractorSuccess() {
		addDescription("Testing the extraction of a JOB id from a CDX when the filename is properly formatted.");
		CDXEntry entry = CDXEntry.createCDXEntry(new String[] {"https://netarkivet.dk", "20160606060606", "a9f5f03efdc6d97874959c1e838f1343", "3715-167-20120811062241-00003-sb-test-har-001.statsbiblioteket.dk.arc"}, 
				new Character[] {'A', 'b', 'k', CDXConstants.CDX_CHAR_FILE_NAME});

		CDXExtractor extractor = mock(CDXExtractor.class);
		HarvestJobExtractor jobExtractor = mock(HarvestJobExtractor.class);
		when(jobExtractor.extractJob(eq(3715L))).thenReturn(testHarvestJobInfo);

		DuplicateExtractor finder = new DuplicateExtractor(extractor, jobExtractor);
		HarvestJobInfo hji = finder.extractJobInfo(entry);
		assertEquals(3715L, hji.getId().longValue());
	}

	@Test
	public void testJobInfoExtractorNoFilenameFailure() {
		addDescription("Testing the extraction of a JOB id from a CDX, when the filename is missing");
		CDXEntry entry = CDXEntry.createCDXEntry(new String[] {"https://netarkivet.dk", "20160606060606", "a9f5f03efdc6d97874959c1e838f1343"}, 
				new Character[] {'A', 'b', 'k'});

		CDXExtractor extractor = mock(CDXExtractor.class);
		HarvestJobExtractor jobExtractor = mock(HarvestJobExtractor.class);
		when(jobExtractor.extractJob(eq(3715L))).thenReturn(testHarvestJobInfo);

		DuplicateExtractor finder = new DuplicateExtractor(extractor, jobExtractor);
		HarvestJobInfo hji = finder.extractJobInfo(entry);
		assertNull(hji);
	}

	@Test
	public void testJobInfoExtractorBadFilenameFormatFailure() {
		addDescription("Testing the extraction of a JOB id from a CDX, when the filename does not have a default format");
		CDXEntry entry = CDXEntry.createCDXEntry(new String[] {"https://netarkivet.dk", "20160606060606", "a9f5f03efdc6d97874959c1e838f1343", "ThisIsASillyFilename.arc"}, 
				new Character[] {'A', 'b', 'k', CDXConstants.CDX_CHAR_FILE_NAME});

		CDXExtractor extractor = mock(CDXExtractor.class);
		HarvestJobExtractor jobExtractor = mock(HarvestJobExtractor.class);
		when(jobExtractor.extractJob(eq(3715L))).thenReturn(testHarvestJobInfo);

		DuplicateExtractor finder = new DuplicateExtractor(extractor, jobExtractor);
		HarvestJobInfo hji = finder.extractJobInfo(entry);
		assertNull(hji);
	}

	@Test
	public void testJobInfoExtractorEmptyFilenameFormatFailure() {
		addDescription("Testing the extraction of a JOB id from a CDX, when the filename is empty");
		CDXEntry entry = CDXEntry.createCDXEntry(new String[] {"https://netarkivet.dk", "20160606060606", "a9f5f03efdc6d97874959c1e838f1343", ""}, 
				new Character[] {'A', 'b', 'k', CDXConstants.CDX_CHAR_FILE_NAME});

		CDXExtractor extractor = mock(CDXExtractor.class);
		HarvestJobExtractor jobExtractor = mock(HarvestJobExtractor.class);
		when(jobExtractor.extractJob(eq(3715L))).thenReturn(testHarvestJobInfo);

		DuplicateExtractor finder = new DuplicateExtractor(extractor, jobExtractor);
		HarvestJobInfo hji = finder.extractJobInfo(entry);
		assertNull(hji);
	}

	@Test
	public void testJobInfoExtractorNull() {
		addDescription("Testing the extraction of a JOB id from a CDX, when the filename is empty");
		CDXEntry entry = CDXEntry.createCDXEntry(new String[] {"https://netarkivet.dk", "20160606060606", "a9f5f03efdc6d97874959c1e838f1343", ""}, 
				new Character[] {'A', 'b', 'k', CDXConstants.CDX_CHAR_FILE_NAME});

		CDXExtractor extractor = mock(CDXExtractor.class);
		HarvestJobExtractor jobExtractor = null;

		DuplicateExtractor finder = new DuplicateExtractor(extractor, jobExtractor);
		HarvestJobInfo hji = finder.extractJobInfo(entry);
		assertNull(hji);
	}
	
	@Test
	public void testNoEntries() throws Exception {
		CDXExtractor extractor = mock(CDXExtractor.class);
		HarvestJobExtractor jobExtractor = mock(HarvestJobExtractor.class);

		when(extractor.retrieveAllCDX(anyString())).thenReturn(new ArrayList<CDXEntry>());
		
		DuplicateExtractor finder = new DuplicateExtractor(extractor, jobExtractor);
		DuplicateMap map = finder.makeDuplicateMap(new UrlInterval(testUrl, null, null));
		
		assertEquals(map.getDateToChecksumMap().size(), 0);		
	}
	
	@Test
	public void testDateInterval() throws Exception {
		addDescription("Test both interval date arguments");
		CDXExtractor extractor = mock(CDXExtractor.class);
		HarvestJobExtractor jobExtractor = mock(HarvestJobExtractor.class);

		when(extractor.retrieveAllCDX(anyString())).thenReturn(testEntries);
		
		DuplicateExtractor finder = new DuplicateExtractor(extractor, jobExtractor);
		DuplicateMap map = finder.makeDuplicateMap(new UrlInterval(testUrl, DateUtils.waybackDateToDate("20120101000000"), DateUtils.waybackDateToDate("20140101000000")));
		
		assertEquals(map.getDateToChecksumMap().size(), 2);
		assertEquals(map.getChecksumToDateListMap().size(), 1);
		assertEquals(map.getChecksumToDateListMap().get("VJ3CKK3ZH2FR7V2KM5TSI3TENA7ZSWKM").size(), 2);
	}

	@Test
	public void testLowerInterval() throws Exception {
		addDescription("Test the earliest date argument for the duplicate finder.");
		CDXExtractor extractor = mock(CDXExtractor.class);
		HarvestJobExtractor jobExtractor = mock(HarvestJobExtractor.class);

		when(extractor.retrieveAllCDX(anyString())).thenReturn(testEntries);
		
		DuplicateExtractor finder = new DuplicateExtractor(extractor, jobExtractor);
		DuplicateMap map = finder.makeDuplicateMap(new UrlInterval(testUrl, DateUtils.waybackDateToDate("20120101000000"), null));
		
		assertEquals(map.getDateToChecksumMap().size(), 5);
		assertEquals(map.getChecksumToDateListMap().size(), 2);
		assertEquals(map.getChecksumToDateListMap().get("VJ3CKK3ZH2FR7V2KM5TSI3TENA7ZSWKM").size(), 3);
		assertEquals(map.getChecksumToDateListMap().get("a9f5f03efdc6d97874959c1e838f1343").size(), 2);
	}
	
	@Test
	public void testUpperInterval() throws Exception {
		addDescription("Test the latest date argument for the duplicate finder.");
		CDXExtractor extractor = mock(CDXExtractor.class);
		HarvestJobExtractor jobExtractor = mock(HarvestJobExtractor.class);

		when(extractor.retrieveAllCDX(anyString())).thenReturn(testEntries);
		
		DuplicateExtractor finder = new DuplicateExtractor(extractor, jobExtractor);
		DuplicateMap map = finder.makeDuplicateMap(new UrlInterval(testUrl, null, DateUtils.waybackDateToDate("20140101000000")));
		
		assertEquals(map.getDateToChecksumMap().size(), 3);
		assertEquals(map.getChecksumToDateListMap().size(), 1);
		assertEquals(map.getChecksumToDateListMap().get("VJ3CKK3ZH2FR7V2KM5TSI3TENA7ZSWKM").size(), 3);
	}
}

