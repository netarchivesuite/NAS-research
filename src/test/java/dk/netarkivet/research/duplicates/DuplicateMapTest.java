package dk.netarkivet.research.duplicates;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.eq;
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
import dk.netarkivet.research.utils.DateUtils;

public class DuplicateMapTest extends ExtendedTestCase {
	
	List<CDXEntry> testEntries = Arrays.asList(
			CDXEntry.createCDXEntry(new String[] {"http://netarkivet.dk", "20110101010101", "VJ3CKK3ZH2FR7V2KM5TSI3TENA7ZSWKM"}, new Character[] {'A', 'b', 'k'}),
			CDXEntry.createCDXEntry(new String[] {"https://netarkivet.dk", "20120202020202", "VJ3CKK3ZH2FR7V2KM5TSI3TENA7ZSWKM"}, new Character[] {'A', 'b', 'k'}),
			CDXEntry.createCDXEntry(new String[] {"http://netarkivet.dk", "20130303030303", "VJ3CKK3ZH2FR7V2KM5TSI3TENA7ZSWKM"}, new Character[] {'A', 'b', 'k'}),
			CDXEntry.createCDXEntry(new String[] {"https://netarkivet.dk", "20140404040404", "VJ3CKK3ZH2FR7V2KM5TSI3TENA7ZSWKM"}, new Character[] {'A', 'b', 'k'}),
			CDXEntry.createCDXEntry(new String[] {"http://netarkivet.dk", "20150505050505", "a9f5f03efdc6d97874959c1e838f1343"}, new Character[] {'A', 'b', 'k'}),
			CDXEntry.createCDXEntry(new String[] {"https://netarkivet.dk", "20160606060606", "a9f5f03efdc6d97874959c1e838f1343"}, new Character[] {'A', 'b', 'k'})
			);
	
	@Test
	public void testDuplicateMapWithNoEntries() throws Exception {
		DuplicateMap dm = new DuplicateMap();
		
		assertEquals(dm.dateMap.size(), 0);
		assertEquals(dm.jobMap.size(), 0);
		assertEquals(dm.getChecksumToDateListMap().size(), 0);
		assertEquals(dm.getDateToChecksumMap().size(), 0);
		assertEquals(dm.getMap().size(), 0);
	}
	
	
}

