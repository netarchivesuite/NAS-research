package dk.netarkivet.research.cdx;

import static org.testng.Assert.*;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
    	
    	assertEquals(entry.getUrlNorm(), A);
    	assertEquals(DateUtils.dateToWaybackDate(entry.getDateAsDate()), b);
    	assertEquals(entry.getUrl(), a);
    	assertEquals(entry.getContentType(), m);
    	assertEquals(entry.getStatusCode().toString(), s);
    	assertEquals(entry.getDigest(), k);
    	assertNull(entry.getRedirect()); // r
    	assertEquals(entry.getOffset().toString(), V);
    	assertEquals(entry.getFilename(), g);
    	assertEquals(entry.getIP(), e);
    	assertEquals(entry.getLength().toString(), n);
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

    @Test
    public void testUnmatchedElement() throws Exception {
    	addDescription("Test that it creates the CDX entry, even if it contains unexpected/unhandled CDX elements.");
    	
    	Character[] format = new Character[]{'A', 'b', 'a', 'm', 'Q'};
    	String[] cdxLine = new String[]{A, b, a, m, "Q"};
    	
    	CDXEntry entry = CDXEntry.createCDXEntry(cdxLine, format);
    	assertNotNull(entry);
    }
    
    @Test
    public void testParsingDateFailure() throws Exception {
    	addDescription("Test that it fails when it cannot parse the date");
    	String badDate = "ThisIsNotAnAcceptableDateFormat";
    	Character[] format = new Character[]{'A', 'b', 'a', 's'};
    	String[] cdxLine = new String[]{A, badDate, a, s};
    	
    	CDXEntry entry = CDXEntry.createCDXEntry(cdxLine, format);
    	assertNull(entry);
    }
    
    @Test
    public void testParsingLongFailure() throws Exception {
    	addDescription("Test that it fails when it cannot parse a Long value");
    	String badStatus = "This is not a proper status code";
    	Character[] format = new Character[]{'A', 'b', 'a', 's'};
    	String[] cdxLine = new String[]{A, b, a, badStatus};
    	
    	CDXEntry entry = CDXEntry.createCDXEntry(cdxLine, format);
    	assertNull(entry);
    }
    
    @Test
    public void testExtractingAsLine() throws Exception {
    	addDescription("Test extracting the CDX entry as a string");
    	Character[] format = new Character[]{'V', 'A', 's', 'r', 'n', 'm', 'k', 'g', 'e', 'b', 'a', ' '};
    	String[] cdxLine = new String[]{V, A, s, r, n, m, k, g, e, b, a, " "};
    	CDXEntry entry = CDXEntry.createCDXEntry(cdxLine, format);
    	
    	List<Character> extractFormat = Arrays.asList('V', 'A', 's', 'r', 'n', 'm', 'k', 'g', 'e', 'b', 'a',
    			'N', 'c', 'v', 'Q');
    	
    	String line = entry.extractCDXAsLine(extractFormat);
    	assertTrue(line.contains(V));
    	assertTrue(line.contains(A));
    	assertTrue(line.contains(s));
    	assertTrue(line.contains(r));
    	assertTrue(line.contains(n));
    	assertTrue(line.contains(m));
    	assertTrue(line.contains(k));
    	assertTrue(line.contains(g));
    	assertTrue(line.contains(e));
    	assertTrue(line.contains(b));
    	assertTrue(line.contains(a));
    }
    
    @Test
    public void testExtractingEmptyCDX() throws Exception {
    	addDescription("Test extracting an empty CDX entry.");
    	CDXEntry entry = CDXEntry.createCDXEntry(new String[0], new Character[0]);
    	
    	assertNull(entry.getContentType());
    	assertNull(entry.getDigest());
    	assertNull(entry.getFilename());
    	assertNull(entry.getIP());
    	assertNull(entry.getRedirect());
    	assertNull(entry.getUrl());
    	assertNull(entry.getUrlNorm());
    	
    	assertEquals(0L, entry.getDateAsLong().longValue());
    	assertEquals(0L, entry.getLength().longValue());
    	assertEquals(0L, entry.getOffset().longValue());
    	assertEquals(200, entry.getStatusCode().intValue());
    	
    	String s = entry.toString();
    	assertNotNull(s);
    }
}

