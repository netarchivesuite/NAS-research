package dk.netarkivet.research.duplicates;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.annotations.Test;

import dk.netarkivet.research.cdx.CDXEntry;
import dk.netarkivet.research.cdx.CDXExtractor;
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
	
	@Test
	public void testDuplicate() throws Exception {
		CDXExtractor extractor = mock(CDXExtractor.class);
		
		when(extractor.retrieveAllCDX(anyString())).thenReturn(testEntries);
		
		DuplicateExtractor finder = new DuplicateExtractor(extractor);
		DuplicateMap map = finder.makeDuplicateMap(testUrl, null, null);
		
		assertEquals(map.getDateToChecksumMap().size(), testEntries.size());
	}
	
	@Test
	public void testNoEntries() throws Exception {
		CDXExtractor extractor = mock(CDXExtractor.class);
		
		when(extractor.retrieveAllCDX(anyString())).thenReturn(new ArrayList<CDXEntry>());
		
		DuplicateExtractor finder = new DuplicateExtractor(extractor);
		DuplicateMap map = finder.makeDuplicateMap(testUrl, null, null);
		
		assertEquals(map.getDateToChecksumMap().size(), 0);		
	}
	
	@Test
	public void testDateInterval() throws Exception {
		addDescription("Test both interval date arguments");
		CDXExtractor extractor = mock(CDXExtractor.class);
		
		when(extractor.retrieveAllCDX(anyString())).thenReturn(testEntries);
		
		DuplicateExtractor finder = new DuplicateExtractor(extractor);
		DuplicateMap map = finder.makeDuplicateMap(testUrl, DateUtils.waybackDateToDate("20120101000000"), DateUtils.waybackDateToDate("20140101000000"));
		
		assertEquals(map.getDateToChecksumMap().size(), 2);
		assertEquals(map.getChecksumToDateListMap().size(), 1);
		assertEquals(map.getChecksumToDateListMap().get("VJ3CKK3ZH2FR7V2KM5TSI3TENA7ZSWKM").size(), 2);
	}

	@Test
	public void testLowerInterval() throws Exception {
		addDescription("Test the earliest date argument for the duplicate finder.");
		CDXExtractor extractor = mock(CDXExtractor.class);
		
		when(extractor.retrieveAllCDX(anyString())).thenReturn(testEntries);
		
		DuplicateExtractor finder = new DuplicateExtractor(extractor);
		DuplicateMap map = finder.makeDuplicateMap(testUrl, DateUtils.waybackDateToDate("20120101000000"), null);
		
		assertEquals(map.getDateToChecksumMap().size(), 5);
		assertEquals(map.getChecksumToDateListMap().size(), 2);
		assertEquals(map.getChecksumToDateListMap().get("VJ3CKK3ZH2FR7V2KM5TSI3TENA7ZSWKM").size(), 3);
		assertEquals(map.getChecksumToDateListMap().get("a9f5f03efdc6d97874959c1e838f1343").size(), 2);
	}
	
	@Test
	public void testUpperInterval() throws Exception {
		addDescription("Test the latest date argument for the duplicate finder.");
		CDXExtractor extractor = mock(CDXExtractor.class);
		
		when(extractor.retrieveAllCDX(anyString())).thenReturn(testEntries);
		
		DuplicateExtractor finder = new DuplicateExtractor(extractor);
		DuplicateMap map = finder.makeDuplicateMap(testUrl, null, DateUtils.waybackDateToDate("20140101000000"));
		
		assertEquals(map.getDateToChecksumMap().size(), 3);
		assertEquals(map.getChecksumToDateListMap().size(), 1);
		assertEquals(map.getChecksumToDateListMap().get("VJ3CKK3ZH2FR7V2KM5TSI3TENA7ZSWKM").size(), 3);
	}
}

