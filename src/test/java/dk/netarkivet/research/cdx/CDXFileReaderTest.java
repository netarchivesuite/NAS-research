package dk.netarkivet.research.cdx;

import static org.testng.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collection;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.annotations.Test;

import dk.netarkivet.research.cdx.CDXEntry;
import dk.netarkivet.research.cdx.CDXFileReader;
import dk.netarkivet.research.testutils.TestFileUtils;

public class CDXFileReaderTest extends ExtendedTestCase {
	
	String validCdxFilePath = "src/test/resources/cdx/valid.cdx";
	String badLinesCdxFilePath = "src/test/resources/cdx/bad-lines.cdx";
	
    @Test
    public void testReadingFile() throws Exception {
    	addDescription("Test extracting CDX entries from a valid test cdx file.");

    	File cdxFile = new File(validCdxFilePath);
    	assertTrue(cdxFile.isFile());
    	CDXFileReader reader = new CDXFileReader();
    	Collection<CDXEntry> entries = reader.extractCDXFromFile(cdxFile);
    	
    	assertFalse(entries.isEmpty());
    	assertEquals(28, entries.size());
    }

    @Test
    public void testReadingFileWithBadLine() throws Exception {
    	addDescription("Test extracting CDX entries from a test cdx file with bad line.");

    	File cdxFile = new File(badLinesCdxFilePath);
    	assertTrue(cdxFile.isFile());
    	CDXFileReader reader = new CDXFileReader();
    	Collection<CDXEntry> entries = reader.extractCDXFromFile(cdxFile);
    	
    	assertFalse(entries.isEmpty());
    	assertEquals(1, entries.size());
    	assertEquals(4, TestFileUtils.countNumberOfLines(cdxFile));
    }

    @Test(expectedExceptions = FileNotFoundException.class)
    public void testReadingNonExistingFile() throws Exception {
    	addDescription("Test extracting CDX entries from a non-existing file.");

    	File cdxFile = new File("ThisFileDoesNotExist-" + Math.random());
    	CDXFileReader reader = new CDXFileReader();
    	reader.extractCDXFromFile(cdxFile);
    }
    
    @Test
    public void testExtractingCDXFormat() {
    	addDescription("Test extracting a CDX format with no errors");
    	String cdxFormat = "A b a V g m s";
    	CDXFileReader reader = new CDXFileReader();
    	Character[] chars = reader.extractCDXFormat(cdxFormat);
    	
    	assertEquals(chars.length, cdxFormat.replaceAll(" ", "").length());
    }
    
    @Test
    public void testExtractingCDXFormatBadFormat() {
    	addDescription("Test extracting a CDX format ");
    	String cdxFormat = "CDX XDC";
    	CDXFileReader reader = new CDXFileReader();
    	Character[] chars = reader.extractCDXFormat(cdxFormat);
    	
    	assertEquals(chars.length, 0);
    }
}
