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

import dk.netarkivet.research.exception.ArgumentCheck;
import dk.netarkivet.research.testutils.TestFileUtils;
import dk.netarkivet.research.wid.CsvWidReader;
import dk.netarkivet.research.wid.WID;

public class CsvWpidReaderTest extends ExtendedTestCase {

	File goodFulltextCsv = new File("src/test/resources/urls_from_fulltext_only.csv");
	File completeFulltextCsv = new File("src/test/resources/urls_fulltext_and_bad_lines.csv");
	File mixedCsv = new File("src/test/resources/urls_mixed.csv");
	
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

	@Test(expectedExceptions = ArgumentCheck.class)
	public void testNullArgument() throws Exception {
		addDescription("Test that it fails, when given a null as argument");
		new CsvWidReader(null);
	}
	
	@Test(expectedExceptions = ArgumentCheck.class)
	public void testMissingFileArgument() throws Exception {
		addDescription("Test that it fails, when given a null as argument");
		new CsvWidReader(new File("thisFileDoesNotExist-" + Math.random() + ".txt"));
	}
	
	@Test
	public void testWPIDExtractionFromFileWithOnlyGoodLines() throws Exception {
		addDescription("Test WPID extraction from a file only containing good fulltext entries");
		
		assertTrue(goodFulltextCsv.isFile());
		
		CsvWidReader reader = new CsvWidReader(goodFulltextCsv);
    	Collection<WID> wids = reader.extractAllWIDs();

    	assertFalse(wids.isEmpty());
    	assertEquals(wids.size(), 3);
    	assertEquals(wids.size()+1, countNumberOfLines(goodFulltextCsv), "Should have 1 line more than file");
	}

	@Test
	public void testWPIDExtractionFromFileWithSomeBadLines() throws Exception {
		addDescription("Test WPID extraction from a file with both good and bad entries");
		
		assertTrue(completeFulltextCsv.isFile());
		
		CsvWidReader reader = new CsvWidReader(completeFulltextCsv);
    	Collection<WID> wids = reader.extractAllWIDs();

    	assertFalse(wids.isEmpty());
    	assertEquals(wids.size(), 70);
    	assertEquals(countNumberOfLines(completeFulltextCsv), 1199);
	}
	
	@Test
	public void testWPIDExtractionFromFileWithMixedLines() throws Exception {
		addDescription("Test WPID extraction from a file with both good and bad entries");
		
		assertTrue(mixedCsv.isFile());
		
		CsvWidReader reader = new CsvWidReader(mixedCsv);
    	Collection<WID> wids = reader.extractAllWIDs();

    	assertFalse(wids.isEmpty());
    	assertEquals(wids.size(), 5);
    	assertEquals(countNumberOfLines(mixedCsv), 15);
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
		CsvWidReader reader = new CsvWidReader(goodFulltextCsv);
		
		WID wid = reader.extractWID("X;;http://kb.dk/;2004-08-17T10:41:08Z");
		assertNotNull(wid);
		assertTrue(wid instanceof WPID);
	}
	
	@Test
	public void testWPIDExtractionFromEmptyLine() throws Exception {
		addDescription("Test that an empty line in the CSV file will give a null");
		CsvWidReader reader = new CsvWidReader(goodFulltextCsv);
		
		WID wid = reader.extractWID("");
		assertNull(wid);
	}
	
	@Test
	public void testWidExtractionFromTextLine() throws Exception {
		addDescription("Test that a line in the CSV file starting with 'T' will give a null");
		CsvWidReader reader = new CsvWidReader(goodFulltextCsv);
		
		WID wid = reader.extractWID("T;asdf;asdf;asdf;asdf;asdf;Asdf");
		assertNull(wid);
	}

	@Test
	public void testWidExtractionFormWPIDLineWithNotEnoughElements() throws Exception {
		addDescription("Test that a line in the CSV file with too few elements will give a null");
		CsvWidReader reader = new CsvWidReader(goodFulltextCsv);
		
		WID wid = reader.extractWID("x;asdf,fdas");
		assertNull(wid);
	}

	@Test
	public void testWidExtractionFormWPIDLineWithEmptyUrl() throws Exception {
		addDescription("Test that a line in the CSV file with empty urls will give a null");
		CsvWidReader reader = new CsvWidReader(goodFulltextCsv);
		
		WID wid = reader.extractWID("X;;;2004-08-17T10:41:08Z");
		assertNull(wid);
	}

	@Test
	public void testWidExtractionFormWPIDLineWithBadUrl() throws Exception {
		addDescription("Test that a line in the CSV file with a bad url will give a null");
		CsvWidReader reader = new CsvWidReader(goodFulltextCsv);
		
		WID wid = reader.extractWID("X;;asdfasdfasdf;2004-08-17T10:41:08Z");
		assertNull(wid);
	}

	@Test
	public void testWidExtractionFromWPIDLineWithEmptyDate() throws Exception {
		addDescription("Test that a line in the CSV file with an empty date will give a null");
		CsvWidReader reader = new CsvWidReader(goodFulltextCsv);
		
		WID wid = reader.extractWID("X;;http://kb.dk/;;;;;;XX");
		assertNull(wid);
	}

	@Test
	public void testWidExtractionFromWPIDLineWithBadDateFormat() throws Exception {
		addDescription("Test that a line in the CSV file with a bad formatted date will give a null");
		CsvWidReader reader = new CsvWidReader(goodFulltextCsv);
		
		WID wid = reader.extractWID("X;;http://kb.dk/;Today T10:41:08Z");
		assertNull(wid);
	}

	@Test
	public void testWidExtractionFromWaybackWIDLine() throws Exception {
		addDescription("Test that a Wayback line in the CSV file can be extracted as an wayback WID");
		CsvWidReader reader = new CsvWidReader(goodFulltextCsv);
		
		String url = "http://netarkivet.dk/";
		
		WID wid = reader.extractWID("W;;" + url + ";2006-01-02T15:56:58Z;2890-25-20060102155648-00032-sb-prod-har-001.statsbiblioteket.dk.arc/1989229;;;;;;;;;;;;;;;");
		assertNotNull(wid);
		assertTrue(wid instanceof WaybackWID);
		assertEquals(wid.getUrl(), url);
		assertNotNull(wid.getDate());
		assertNotNull(((WaybackWID) wid).getFilename());
	}

	@Test
	public void testWidExtractionFromWaybackWIDLineWithNoDate() throws Exception {
		addDescription("Test that a Wayback line in the CSV file can be extracted as an Wayback WID even if it has a filename but no date");
		CsvWidReader reader = new CsvWidReader(goodFulltextCsv);
		
		String url = "http://netarkivet.dk/";
		String filename = "2890-25-20060102155648-00032-sb-prod-har-001.statsbiblioteket.dk.arc/1989229";
		
		WID wid = reader.extractWID("W;;" + url + ";;" + filename + ";;;;;;;;;;;;;;;");
		assertNotNull(wid);
		assertTrue(wid instanceof WaybackWID);
		assertEquals(wid.getUrl(), url);
		assertEquals(filename, ((WaybackWID) wid).getFilename());
		assertNull(wid.getDate());
	}

	@Test
	public void testWidExtractionFromWaybackWIDLineWithNoFilename() throws Exception {
		addDescription("Test that a Wayback line in the CSV file can be extracted as an Wayback WID even if it has a date but no filename");
		CsvWidReader reader = new CsvWidReader(goodFulltextCsv);
		
		String url = "http://netarkivet.dk/";
		
		WID wid = reader.extractWID("W;;" + url + ";2006-01-02T15:56:58Z;;;;;;;;;;;;;;;;");
		assertNotNull(wid);
		assertTrue(wid instanceof WaybackWID);
		assertEquals(wid.getUrl(), url);
		assertNull(((WaybackWID) wid).getFilename());
		assertNotNull(wid.getDate());
	}
	
	@Test
	public void testWidExtractionFromWaybackWIDLineWithNotEnoughElements() throws Exception {
		addDescription("Test that an empty line in the CSV file will give a null");
		CsvWidReader reader = new CsvWidReader(goodFulltextCsv);
		
		WID wid = reader.extractWID("W;;");
		assertNull(wid);
	}
	
	@Test
	public void testWidExtractionFromWaybackWIDLineWithNoUrl() throws Exception {
		addDescription("Test that an empty line in the CSV file will give a null");
		CsvWidReader reader = new CsvWidReader(goodFulltextCsv);
		
		WID wid = reader.extractWID("W;;;2006-01-02T15:56:58Z;2890-25-20060102155648-00032-sb-prod-har-001.statsbiblioteket.dk.arc/1989229;;;;;;;;;;;;;;;");
		assertNull(wid);
	}
	
	@Test
	public void testWidExtractionFromWaybackWIDLineWithBadUrl() throws Exception {
		addDescription("Test that an empty line in the CSV file will give a null");
		CsvWidReader reader = new CsvWidReader(goodFulltextCsv);
		
		String url = "NotAnUrl";
		
		WID wid = reader.extractWID("W;;" + url + ";2006-01-02T15:56:58Z;2890-25-20060102155648-00032-sb-prod-har-001.statsbiblioteket.dk.arc/1989229;;;;;;;;;;;;;;;");
		assertNull(wid);
	}

	@Test
	public void testWidExtractionFromWaybackWIDLineWithBadDateAndNoFilename() throws Exception {
		addDescription("Test that an empty line in the CSV file will give a null");
		CsvWidReader reader = new CsvWidReader(goodFulltextCsv);
		
		String url = "http://netarkivet.dk/";
		
		WID wid = reader.extractWID("W;;" + url + ";Today T15:56:58Z;;;;;;;;;;;;;;;;");
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
