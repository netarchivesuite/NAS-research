package dk.netarkivet.research.links;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.Collection;

import org.jaccept.structure.ExtendedTestCase;
import org.jwat.warc.WarcReader;
import org.jwat.warc.WarcReaderFactory;
import org.jwat.warc.WarcRecord;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class HtmlLinkExtractorTest extends ExtendedTestCase {
	
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
		HtmlLinkExtractor extractor = new HtmlLinkExtractor();
		Collection<String> urls = extractor.extractLinks(record.getPayloadContent(), new URL(record.header.warcTargetUriStr));
		assertFalse(urls.isEmpty());
		assertEquals(21, urls.size());
	}
}
