package dk.netarkivet.research.cdx;

import static org.testng.Assert.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.annotations.Test;

import dk.netarkivet.research.cdx.CDXEntry;
import dk.netarkivet.research.utils.DateUtils;

public class CDXEntryTest extends ExtendedTestCase {
	
	String cdxLineExtract = "0.envato-static.com/ 20130321174128 http://0.envato-static.com/ application/xml 403 VJ3CKK3ZH2FR7V2KM5TSI3TENA7ZSWKM - 83292970 4589-189-20130321161636-00007-sb-test-har-001.statsbiblioteket.dk.arc";
	
	String A = "0.envato-static.com/";
	String b = "20130321174128";
	String a = "http://0.envato-static.com/";
	String m = "application/xml";
	String s = "403";
	String k = "VJ3CKK3ZH2FR7V2KM5TSI3TENA7ZSWKM";
	String r = "-";
	String V = "83292970";
	String g = "4589-189-20130321161636-00007-sb-test-har-001.statsbiblioteket.dk.arc";

	// Extra elements, not the the test cdx extract.
	String e = "127.0.0.1";
	String n = "123";
	
    @Test
    public void testCreatingCDXExtractFromLineSplit() throws Exception {
    	addDescription("Test creating a simple CDX entry from a line and a format");
    	
    	Character[] format = new Character[]{'A', 'b', 'a', 'm', 's', 'k', 'r', 'V', 'g'};
    			
    	CDXEntry entry = CDXEntry.createCDXEntry(cdxLineExtract.split(" "), format);
    	
    	assertEquals(entry.urlNorm, A);
    	assertEquals(DateUtils.dateToWaybackDate(new Date(entry.date)), b);
    	assertEquals(entry.url, a);
    	assertEquals(entry.contentType, m);
    	assertEquals(entry.statusCode.toString(), s);
    	assertEquals(entry.digest, k);
    	assertNull(entry.redirect); // r
    	assertEquals(entry.offset.toString(), V);
    	assertEquals(entry.filename, g);
    	
    	assertNull(entry.ip); // e
    	assertNull(entry.length); // n
    }
    
    @Test
    public void testCreatingCDXExtractFromLineAndFormatDefaultOrder() throws Exception {
    	addDescription("Test creating a simple CDX entry from a line and a format");
    	
    	Character[] format = new Character[]{'A', 'b', 'a', 'm', 's', 'k', 'r', 'V', 'g'};
    	String[] cdxLine = new String[]{A, b, a, m, s, k, r, V, g};
    			
    	CDXEntry entry = CDXEntry.createCDXEntry(cdxLine, format);
    	
    	assertEquals(entry.urlNorm, A);
    	assertEquals(DateUtils.dateToWaybackDate(new Date(entry.date)), b);
    	assertEquals(entry.url, a);
    	assertEquals(entry.contentType, m);
    	assertEquals(entry.statusCode.toString(), s);
    	assertEquals(entry.digest, k);
    	assertNull(entry.redirect); // r
    	assertEquals(entry.offset.toString(), V);
    	assertEquals(entry.filename, g);
    	
    	assertNull(entry.ip); // e
    	assertNull(entry.length); // n
    }
    
    @Test
    public void testCreatingCDXExtractFromLineAndFormatRandomOrder() throws Exception {
    	addDescription("Test creating a simple CDX entry from a line and a format. But in a non-default order");
    	
    	Character[] format = new Character[]{'V', 'A', 's', 'r', 'n', 'm', 'k', 'g', 'e', 'b', 'a'};
    	String[] cdxLine = new String[]{V, A, s, r, n, m, k, g, e, b, a};
    			
    	CDXEntry entry = CDXEntry.createCDXEntry(cdxLine, format);
    	
    	assertEquals(entry.urlNorm, A);
    	assertEquals(DateUtils.dateToWaybackDate(new Date(entry.date)), b);
    	assertEquals(entry.url, a);
    	assertEquals(entry.contentType, m);
    	assertEquals(entry.statusCode.toString(), s);
    	assertEquals(entry.digest, k);
    	assertNull(entry.redirect); // r
    	assertEquals(entry.offset.toString(), V);
    	assertEquals(entry.filename, g);
    	
    	assertEquals(entry.ip, e);
    	assertEquals(entry.length.toString(), n);
    }

    @Test
    public void testCreatingCDXExtractFromMap() throws Exception {
    	addDescription("Test creating a simple CDX entry from a map between the format elements");
    	
    	Map<Character, String> cdxMap = new HashMap<Character, String>();
    	cdxMap.put('A', A);
    	cdxMap.put('b', b);
    	cdxMap.put('a', a);
    	cdxMap.put('m', m);
    	cdxMap.put('s', s);
    	cdxMap.put('k', k);
    	cdxMap.put('r', r);
    	cdxMap.put('V', V);
    	cdxMap.put('g', g);
    			
    	CDXEntry entry = CDXEntry.createCDXEntry(cdxMap);
    	
    	assertEquals(entry.urlNorm, A);
    	assertEquals(DateUtils.dateToWaybackDate(new Date(entry.date)), b);
    	assertEquals(entry.url, a);
    	assertEquals(entry.contentType, m);
    	assertEquals(entry.statusCode.toString(), s);
    	assertEquals(entry.digest, k);
    	assertNull(entry.redirect); // r
    	assertEquals(entry.offset.toString(), V);
    	assertEquals(entry.filename, g);
    }
    
    @Test
    public void testCreatingCDXExtractFailureFewerLineElements() throws Exception {
    	addDescription("Test that creating a simple CDX entry fails, when it has fewer line elements than format elements");
    	
    	Character[] format = new Character[]{'A', 'b', 'a', 'm', 's', 'k', 'r', 'V', 'g'};
    	String[] cdxLine = new String[]{A, b, a, m};
    	
    	CDXEntry entry = CDXEntry.createCDXEntry(cdxLine, format);
    	assertNull(entry);
    }
    
    @Test
    public void testCreatingCDXExtractFailureFewerFormatElements() throws Exception {
    	addDescription("Test that creating a simple CDX entry fails, when it has fewer format elements than line elements");
    	
    	Character[] format = new Character[]{'A', 'b', 'a', 'm'};
    	String[] cdxLine = new String[]{A, b, a, m, s, k, r, V, g};
    	
    	CDXEntry entry = CDXEntry.createCDXEntry(cdxLine, format);
    	assertNull(entry);
    }
}

