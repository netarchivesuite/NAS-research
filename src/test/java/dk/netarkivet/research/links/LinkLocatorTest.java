package dk.netarkivet.research.links;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertNotNull;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;

import org.jaccept.structure.ExtendedTestCase;
import org.jwat.warc.WarcReader;
import org.jwat.warc.WarcReaderFactory;
import org.jwat.warc.WarcRecord;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import dk.netarkivet.research.cdx.CDXEntry;
import dk.netarkivet.research.cdx.CDXExtractor;
import dk.netarkivet.research.utils.DateUtils;
import dk.netarkivet.research.wid.WaybackWID;

public class LinkLocatorTest extends ExtendedTestCase {
	
	File warcFile = new File("src/test/resources/test.warc");
	String recordUri = "http://netarkivet.dk/";
	WarcRecord record;
	
	@BeforeClass
	public void setupClass() throws Exception {
		WarcReader wr = WarcReaderFactory.getReader(new FileInputStream(warcFile));

		WarcRecord r;
		while((r = wr.getNextRecord()) != null) {
			if(r.header != null && r.header.warcTargetUriStr != null && r.header.warcTargetUriStr.equals(recordUri)) {
				record = r;
				break;
			}
		}
	}
	
	@Test
	public void testExtraction() throws Exception {
		addDescription("Test the extraction of the HTML from a WARC record");
		assertNotNull(record);
		
		String link = "http://netarkivet.dk/link";
		String waybackDate = "20140710050626";
		CDXEntry cdxEntry = CDXEntry.createCDXEntry(new String[]{link, waybackDate}, new Character[]{'a', 'b'});
		
		LinkExtractor linkExtractor = mock(LinkExtractor.class); 
		when(linkExtractor.supportedMimetype()).thenReturn("text/html");
		when(linkExtractor.extractLinks(any(InputStream.class), any(URL.class))).thenReturn(Arrays.asList(link));
		
		CDXExtractor cdxExtractor = mock(CDXExtractor.class);
		when(cdxExtractor.retrieveCDX(any(WaybackWID.class))).thenReturn(cdxEntry);
		
		LinksLocator ll = new LinksLocator(linkExtractor, cdxExtractor);
		Collection<LinkStatus> res = ll.locateLinks(record);
		assertNotNull(res);
		assertEquals(res.size(), 1);
		for(LinkStatus ls : res) {
			assertTrue(ls.found);
			assertEquals(ls.linkUrl, link);
			assertEquals(ls.linkDate.getTime(), DateUtils.waybackDateToDate(waybackDate).getTime());
		}
	}
}
