package dk.netarkivet.research.index;

import static org.testng.Assert.*;

import java.io.File;
import java.util.Collection;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.annotations.Test;

public class CDXFileReaderTest extends ExtendedTestCase {
	
	String cdxFilePath = "src/test/resources/test-cdx.cdx";
	
    @Test
    public void testReadingFile() throws Exception {
    	addDescription("Test loading the test cdx file.");

    	File cdxFile = new File(cdxFilePath);
    	assertTrue(cdxFile.isFile());
    	CDXFileReader reader = new CDXFileReader();
    	Collection<CDXEntry> entries = reader.extractCDXFromFile(cdxFile);
    	
    	assertFalse(entries.isEmpty());
    	assertEquals(28, entries.size());
    }
}
