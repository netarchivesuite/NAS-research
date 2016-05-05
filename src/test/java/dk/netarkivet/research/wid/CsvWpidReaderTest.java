package dk.netarkivet.research.wid;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import dk.netarkivet.research.testutils.TestFileUtils;
import dk.netarkivet.research.wid.CsvWidReader;
import dk.netarkivet.research.wid.WID;

public class CsvWpidReaderTest extends ExtendedTestCase {

	File goodCsv = new File("src/test/resources/urls_from_fulltext_only.csv");
	File completeCsv = new File("src/test/resources/urls_fulltext_and_bad_lines.csv");
	
	private File dir = new File("tempDir");
	
	@BeforeClass
	public void setup() throws Exception {
		TestFileUtils.removeFile(dir);
		dir.mkdirs();
	}
	
	@AfterClass
	public void tearDown() throws Exception {
		TestFileUtils.removeFile(dir);
	}
	
	@Test
	public void testWPIDExtractionFromFileWithOnlyGoodLines() throws Exception {
		addDescription("Test WPID extraction from a file only containing good entries");
		
		assertTrue(goodCsv.isFile());
		
		CsvWidReader reader = new CsvWidReader(goodCsv);
    	Collection<WID> wids = reader.extractAllWIDs();

    	assertFalse(wids.isEmpty());
    	assertEquals(wids.size(), 3);
    	assertEquals(wids.size()+1, countNumberOfLines(goodCsv), "Should have 1 line more than file");
	}

	@Test
	public void testWPIDExtractionFromFileWithSomeBadLines() throws Exception {
		addDescription("Test WPID extraction from a file with both good and bad entries");
		
		assertTrue(completeCsv.isFile());
		
		CsvWidReader reader = new CsvWidReader(completeCsv);
    	Collection<WID> wids = reader.extractAllWIDs();

    	assertFalse(wids.isEmpty());
    	assertEquals(wids.size(), 70);
    	assertEquals(countNumberOfLines(completeCsv), 1199);
	}
	
	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testNullArgument() throws Exception {
		addDescription("Test that it fails, when given a null as argument");
		new CsvWidReader(null);
	}
	
	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testMissingFileArgument() throws Exception {
		addDescription("Test that it fails, when given a null as argument");
		new CsvWidReader(new File("thisFileDoesNotExist-" + Math.random() + ".txt"));
	}
	
	@Test
	public void testMissingFileDuringExtraction() throws Exception {
		addDescription("Test failure during parsing file, due to file being deleted prior to extraction");
		File f = TestFileUtils.createTestFile(dir, "This Is Content");

		CsvWidReader reader = new CsvWidReader(f);
		
		assertTrue(f.delete());
		
		Collection<WID> wids = reader.extractAllWIDs();
		assertNull(wids);
	}
	
	@Test
	public void testWPIDExtractionFromGoodXLine() throws Exception {
		addDescription("Test that a good line in the CSV file starting with X will give a proper WPID.");
		CsvWidReader reader = new CsvWidReader(goodCsv);
		
		WID wid = reader.extractWID("X;;http://kb.dk/;2004-08-17T10:41:08Z");
		assertNotNull(wid);
	}
	
	@Test
	public void testWPIDExtractionFromEmptyLine() throws Exception {
		addDescription("Test that an empty line in the CSV file will give a null");
		CsvWidReader reader = new CsvWidReader(goodCsv);
		
		WID wid = reader.extractWID("");
		assertNull(wid);
	}
	
	@Test
	public void testwidExtractionFromTextLine() throws Exception {
		addDescription("Test that a line in the CSV file starting with 'T' will give a null");
		CsvWidReader reader = new CsvWidReader(goodCsv);
		
		WID wid = reader.extractWID("T;asdf;asdf;asdf;asdf;asdf;Asdf");
		assertNull(wid);
	}

	@Test
	public void testwidExtractionFromLineWithNotEnoughElements() throws Exception {
		addDescription("Test that an empty line in the CSV file will give a null");
		CsvWidReader reader = new CsvWidReader(goodCsv);
		
		WID wid = reader.extractWID("x;asdf,fdas");
		assertNull(wid);
	}

	@Test
	public void testwidExtractionFromLineWithEmptyUrl() throws Exception {
		addDescription("Test that an empty line in the CSV file will give a null");
		CsvWidReader reader = new CsvWidReader(goodCsv);
		
		WID wid = reader.extractWID("X;;;2004-08-17T10:41:08Z");
		assertNull(wid);
	}	

	@Test
	public void testwidExtractionFromLineWithBadUrl() throws Exception {
		addDescription("Test that an empty line in the CSV file will give a null");
		CsvWidReader reader = new CsvWidReader(goodCsv);
		
		WID wid = reader.extractWID("X;;asdfasdfasdf;2004-08-17T10:41:08Z");
		assertNull(wid);
	}

	@Test
	public void testwidExtractionFromLineWithEmptyDate() throws Exception {
		addDescription("Test that an empty line in the CSV file will give a null");
		CsvWidReader reader = new CsvWidReader(goodCsv);
		
		WID wid = reader.extractWID("X;;http://kb.dk/;;;;;;XX");
		assertNull(wid);
	}

	@Test
	public void testwidExtractionFromLineWithBadDateFormat() throws Exception {
		addDescription("Test that an empty line in the CSV file will give a null");
		CsvWidReader reader = new CsvWidReader(goodCsv);
		
		WID wid = reader.extractWID("X;;http://kb.dk/;Today T10:41:08Z");
		assertNull(wid);
	}
	
	private int countNumberOfLines(File f) {
		int i = 0;
		try (
			InputStream fis = new FileInputStream(f);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader br = new BufferedReader(isr);
		) {
			while (br.readLine() != null) {
				i++;
			}
		} catch (Exception e) {
			return -1;
		}
		return i;
	}
}
