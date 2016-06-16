package dk.netarkivet.research.interval;

import static org.testng.Assert.assertEquals;

import java.io.File;
import java.util.Collection;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import dk.netarkivet.research.testutils.TestFileUtils;
import dk.netarkivet.research.utils.FileUtils;

public class CsvUrlIntervalReaderTest extends ExtendedTestCase {
	
	File csvUrlIntervalFile = new File("src/test/resources/duplicates.csv");
	File testDir = new File("tempDir");
	
	@BeforeClass
	public void setupClass() {
		testDir = FileUtils.createDir(testDir.getAbsolutePath());
	}
	
	@AfterClass
	public void tearDownClass() throws Exception {
		TestFileUtils.removeFile(testDir);
	}
	
	@Test
	public void testExtractionOfProperFile() throws Exception {
		addDescription("Test extracting a file from ");
		CsvUrlIntervalReader reader = new CsvUrlIntervalReader(csvUrlIntervalFile);
		Collection<UrlInterval> urlIntervals = reader.extractAllUrlIntervals();
		
		assertEquals(urlIntervals.size(), 4);
	}
	
	@Test(expectedExceptions = IllegalStateException.class)
	public void testExtractionOfNonExistingFile() throws Exception {
		addDescription("Test the CsvUrlIntervalReader of a file, which is removed before extraction.");
		File testFile = TestFileUtils.createTestFile(testDir, "This is apparently content");
		CsvUrlIntervalReader reader = new CsvUrlIntervalReader(testFile);
		TestFileUtils.removeFile(testFile);
		reader.extractAllUrlIntervals();
	}
}

