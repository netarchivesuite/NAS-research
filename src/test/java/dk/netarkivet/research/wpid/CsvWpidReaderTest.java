package dk.netarkivet.research.wpid;

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

public class CsvWpidReaderTest extends ExtendedTestCase {

	File goodCsv = new File("src/test/resources/urls.csv");
	File completeCsv = new File("src/test/resources/urls2.csv");
	
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
		
		CsvWpidReader reader = new CsvWpidReader(goodCsv);
    	Collection<WPID> wpids = reader.extractAllWPIDs();

    	assertFalse(wpids.isEmpty());
    	assertEquals(wpids.size(), 3);
    	assertEquals(wpids.size()+1, countNumberOfLines(goodCsv), "Should have 1 line more than file");
	}

	@Test
	public void testWPIDExtractionFromFileWithSomeBadLines() throws Exception {
		addDescription("Test WPID extraction from a file with both good and bad entries");
		
		assertTrue(completeCsv.isFile());
		
		CsvWpidReader reader = new CsvWpidReader(completeCsv);
    	Collection<WPID> wpids = reader.extractAllWPIDs();

    	assertFalse(wpids.isEmpty());
    	assertEquals(wpids.size(), 70);
    	assertEquals(countNumberOfLines(completeCsv), 1199);
	}
	
	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testNullArgument() throws Exception {
		addDescription("Test that it fails, when given a null as argument");
		new CsvWpidReader(null);
	}
	
	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testMissingFileArgument() throws Exception {
		addDescription("Test that it fails, when given a null as argument");
		new CsvWpidReader(new File("thisFileDoesNotExist-" + Math.random() + ".txt"));
	}
	
	@Test
	public void testMissingFileDuringExtraction() throws Exception {
		addDescription("Test failure during parsing file, due to file being deleted prior to extraction");
		File f = TestFileUtils.createTestFile(dir, "This Is Content");

		CsvWpidReader reader = new CsvWpidReader(f);
		
		assertTrue(f.delete());
		
		Collection<WPID> wpids = reader.extractAllWPIDs();
		assertNull(wpids);
	}
	
	@Test
	public void testWPIDExtractionFromGoodXLine() throws Exception {
		addDescription("Test that a good line in the CSV file starting with X will give a proper WPID.");
		CsvWpidReader reader = new CsvWpidReader(goodCsv);
		
		WPID wpid = reader.extractWPID("X;;http://kb.dk/;2004-08-17T10:41:08Z");
		assertNotNull(wpid);
	}
	
	@Test
	public void testWPIDExtractionFromEmptyLine() throws Exception {
		addDescription("Test that an empty line in the CSV file will give a null");
		CsvWpidReader reader = new CsvWpidReader(goodCsv);
		
		WPID wpid = reader.extractWPID("");
		assertNull(wpid);
	}
	
	@Test
	public void testWPIDExtractionFromTextLine() throws Exception {
		addDescription("Test that a line in the CSV file starting with 'T' will give a null");
		CsvWpidReader reader = new CsvWpidReader(goodCsv);
		
		WPID wpid = reader.extractWPID("T;asdf;asdf;asdf;asdf;asdf;Asdf");
		assertNull(wpid);
	}

	@Test
	public void testWPIDExtractionFromLineWithNotEnoughElements() throws Exception {
		addDescription("Test that an empty line in the CSV file will give a null");
		CsvWpidReader reader = new CsvWpidReader(goodCsv);
		
		WPID wpid = reader.extractWPID("x;asdf,fdas");
		assertNull(wpid);
	}

	@Test
	public void testWPIDExtractionFromLineWithEmptyUrl() throws Exception {
		addDescription("Test that an empty line in the CSV file will give a null");
		CsvWpidReader reader = new CsvWpidReader(goodCsv);
		
		WPID wpid = reader.extractWPID("X;;;2004-08-17T10:41:08Z");
		assertNull(wpid);
	}	

	@Test
	public void testWPIDExtractionFromLineWithBadUrl() throws Exception {
		addDescription("Test that an empty line in the CSV file will give a null");
		CsvWpidReader reader = new CsvWpidReader(goodCsv);
		
		WPID wpid = reader.extractWPID("X;;asdfasdfasdf;2004-08-17T10:41:08Z");
		assertNull(wpid);
	}

	@Test
	public void testWPIDExtractionFromLineWithEmptyDate() throws Exception {
		addDescription("Test that an empty line in the CSV file will give a null");
		CsvWpidReader reader = new CsvWpidReader(goodCsv);
		
		WPID wpid = reader.extractWPID("X;;http://kb.dk/;;;;;;XX");
		assertNull(wpid);
	}

	@Test
	public void testWPIDExtractionFromLineWithBadDateFormat() throws Exception {
		addDescription("Test that an empty line in the CSV file will give a null");
		CsvWpidReader reader = new CsvWpidReader(goodCsv);
		
		WPID wpid = reader.extractWPID("X;;http://kb.dk/;Today T10:41:08Z");
		assertNull(wpid);
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
