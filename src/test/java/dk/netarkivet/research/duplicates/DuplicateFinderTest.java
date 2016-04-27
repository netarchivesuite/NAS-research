package dk.netarkivet.research.duplicates;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.annotations.Test;

import dk.netarkivet.research.cdx.CDXEntry;
import dk.netarkivet.research.cdx.CDXExtractor;

public class DuplicateFinderTest extends ExtendedTestCase {
	
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
		
		DuplicateFinder finder = new DuplicateFinder(extractor);
		Map<String, List<Long>> map = finder.makeDuplicateMap(testUrl);
		
		assertEquals(map.size(), 2);
		assertEquals(map.get("VJ3CKK3ZH2FR7V2KM5TSI3TENA7ZSWKM").size(), 4);
		assertEquals(map.get("a9f5f03efdc6d97874959c1e838f1343").size(), 2);
	}
	
	@Test
	public void testNoEntries() throws Exception {
		CDXExtractor extractor = mock(CDXExtractor.class);
		
		when(extractor.retrieveAllCDX(anyString())).thenReturn(new ArrayList<CDXEntry>());
		
		DuplicateFinder finder = new DuplicateFinder(extractor);
		Map<String, List<Long>> map = finder.makeDuplicateMap(testUrl);
		
		assertEquals(map.size(), 0);		
	}
}

