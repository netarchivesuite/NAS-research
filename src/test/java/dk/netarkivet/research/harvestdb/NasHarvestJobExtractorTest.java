package dk.netarkivet.research.harvestdb;

import java.io.File;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.SkipException;
import org.testng.annotations.Test;

public class NasHarvestJobExtractorTest extends ExtendedTestCase {

	@Test
	public void testConnection() throws Exception {
		addDescription("Test the connection to my local ");
		String settingsFile = "/home/jolf/quickstart/QUICKSTART/conf/settings_GUIApplication.xml";
		System.setProperty("dk.netarkivet.settings.file", settingsFile);
		if(!(new File(settingsFile)).isFile()) {
			throw new SkipException("File does not exist.");
		}
		
		NasHarvestJobExtractor jobExtractor = new NasHarvestJobExtractor();
		HarvestJobInfo jobinfo = jobExtractor.extractJob(1L);
		
		System.err.println(jobinfo.getId() + ": " + jobinfo.name + ", " + jobinfo.type + ", " + jobinfo.status);
	}
}
