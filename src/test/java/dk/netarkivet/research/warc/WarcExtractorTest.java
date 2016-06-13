package dk.netarkivet.research.warc;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import java.io.File;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.annotations.Test;

public class WarcExtractorTest extends ExtendedTestCase {
	
	@Test
	public void testWarcExtractorWithProperWarcFile() throws Exception {
		addDescription("Test extraction of proper warc file with 4 records.");
		File warcFile = new File("src/test/resources/test.warc");
		WarcExtractor we = new WarcExtractor(warcFile);
		assertNotNull(we.getNext());
		assertNotNull(we.getNext());
		assertNotNull(we.getNext());
		assertNotNull(we.getNext());
		assertNull(we.getNext());
	}
	
	@Test(expectedExceptions = IllegalStateException.class)
	public void testWarcExtractorWithIncorrectFile() throws Exception {
		addDescription("Test WARC extraction when a bad file is given.");
		File warcFile = new File(".");
		new WarcExtractor(warcFile);
	}
	
}
