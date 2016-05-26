package dk.netarkivet.research.harvestdb;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertNotNull;

import java.io.File;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class NasHarvestJobExtractorTest extends ExtendedTestCase {

	NasHarvestJobExtractor jobExtractor;
	
	@BeforeClass
	public void setup() {
		String settingsFile = "/home/jolf/quickstart/QUICKSTART/conf/settings_GUIApplication.xml";
		System.setProperty("dk.netarkivet.settings.file", settingsFile);
		if(!(new File(settingsFile)).isFile()) {
			throw new SkipException("File does not exist.");
		}		
		jobExtractor = new NasHarvestJobExtractor();
	}
	
	@Test
	public void testSuccessExtraction() throws Exception {
		addDescription("Test the connection to my local quickstart harvest database.");
		Long jobId = 1L;
		HarvestJobInfo jobinfo = jobExtractor.extractJob(jobId);
		
		assertNotNull(jobinfo);
		assertNotNull(jobinfo.name);
		assertNotNull(jobinfo.type);
		assertNotNull(jobinfo.status);
		assertEquals(jobId, jobinfo.getId());
	}
	
	@Test
	public void testExtractionOfNegativeJobID() throws Exception {
		addDescription("Test how it handles extraction of a negative job-id");
		Long jobId = -1L;
		HarvestJobInfo jobinfo = jobExtractor.extractJob(jobId);
		
		assertNull(jobinfo);
	}
	
	@Test
	public void testExtractionOfNullJobID() throws Exception {
		addDescription("Test how it handles extraction of a null job-id");
		Long jobId = null;
		HarvestJobInfo jobinfo = jobExtractor.extractJob(jobId);
		
		assertNull(jobinfo);
	}
	
	@Test
	public void testExtractionOfMaxJobID() throws Exception {
		addDescription("Test how it handles extraction of the largest possible number as job-id");
		Long jobId = Long.MAX_VALUE;
		HarvestJobInfo jobinfo = jobExtractor.extractJob(jobId);
		
		assertNull(jobinfo);
	}
	
}
