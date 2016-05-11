package dk.netarkivet.research.harvestdb;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.annotations.Test;

public class NasHarvestJobExtractorTest extends ExtendedTestCase {

	@Test
	public void testConnection() throws Exception {
		addDescription("");
		System.setProperty("dk.netarkivet.settings.file", "/home/jolf/quickstart/QUICKSTART/conf/settings_GUIApplication.xml");
		
		NasHarvestJobExtractor jobExtractor = new NasHarvestJobExtractor();
		HarvestJobInfo jobinfo = jobExtractor.extractJob(1L);
		
		System.err.println(jobinfo.getId() + ": " + jobinfo.name + ", " + jobinfo.type + ", " + jobinfo.status);
	}
}
