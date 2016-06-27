package dk.netarkivet.research.harvestdb;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.io.File;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.annotations.Test;

public class ScriptBasedHarvestJobExtractorTest extends ExtendedTestCase {
	@Test
	public void testSuccessExtraction() throws Exception {
		addDescription("Testing the script based extraction, when the script deliveres output in the expected format.");
		File f = new File("src/test/resources/scripts/harvest_script_success.sh");
		assertTrue(f.isFile());
		
		Long id = 123L;
		HarvestJobExtractor jobExtractor = new ScriptBasedHarvestJobExtractor(f);
		HarvestJobInfo jhi = jobExtractor.extractJob(id);
		assertNotNull(jhi);
		assertEquals(jhi.type, HarvestJobInfo.HARVEST_TYPE_SNAPSHOT);
		assertNotNull(jhi.name);
		assertNotNull(jhi.status);
		assertEquals(jhi.id, id);
	}
	
	@Test
	public void testFailureExtraction() throws Exception {
		addDescription("Testing the script based extraction, when the script deliveres output in an unexpected format.");
		File f = new File("src/test/resources/scripts/harvest_script_failure.sh");
		assertTrue(f.isFile());
		
		Long id = 123L;
		HarvestJobExtractor jobExtractor = new ScriptBasedHarvestJobExtractor(f);
		HarvestJobInfo jhi = jobExtractor.extractJob(id);
		assertNull(jhi);
	}
	
	@Test
	public void testExtractingHarvestJobTypeWithTrueArgument() throws Exception {
		addDescription("Testing extrating the harvest job type snapshot when giving values for true.");
		ScriptBasedHarvestJobExtractor jobExtractor = new ScriptBasedHarvestJobExtractor(new File("src/test/resources/jaccept.properties"));
		
		String harvestJobType = jobExtractor.harvestJobType("True");
		assertNotNull(harvestJobType);
		assertEquals(harvestJobType, HarvestJobInfo.HARVEST_TYPE_SNAPSHOT);

		String harvestJobType2 = jobExtractor.harvestJobType("t");
		assertNotNull(harvestJobType2);
		assertEquals(harvestJobType2, HarvestJobInfo.HARVEST_TYPE_SNAPSHOT);
	}
	
	@Test
	public void testExtractingHarvestJobTypeWithFalseArgument() throws Exception {
		addDescription("Testing extrating the harvest job type not-snapshot when giving values for false.");
		ScriptBasedHarvestJobExtractor jobExtractor = new ScriptBasedHarvestJobExtractor(new File("src/test/resources/jaccept.properties"));
		
		String harvestJobType = jobExtractor.harvestJobType("False");
		assertNotNull(harvestJobType);
		assertEquals(harvestJobType, HarvestJobInfo.HARVEST_TYPE_NOT_SNAPSHOT);
		
		String harvestJobType2 = jobExtractor.harvestJobType("f");
		assertNotNull(harvestJobType2);
		assertEquals(harvestJobType2, HarvestJobInfo.HARVEST_TYPE_NOT_SNAPSHOT);
	}
	
	@Test
	public void testExtractingHarvestJobTypeWithNull() throws Exception {
		addDescription("Testing extrating the harvest job type not-snapshot when giving a null.");
		ScriptBasedHarvestJobExtractor jobExtractor = new ScriptBasedHarvestJobExtractor(new File("src/test/resources/jaccept.properties"));
		
		String harvestJobType = jobExtractor.harvestJobType(null);
		assertNull(harvestJobType);
	}
	
	@Test
	public void testExtractingHarvestJobTypeWithEmptyString() throws Exception {
		addDescription("Testing extrating the harvest job type not-snapshot when giving an empty string.");
		ScriptBasedHarvestJobExtractor jobExtractor = new ScriptBasedHarvestJobExtractor(new File("src/test/resources/jaccept.properties"));
		
		String harvestJobType = jobExtractor.harvestJobType("");
		assertNull(harvestJobType);
	}
	
	@Test(expectedExceptions = IllegalStateException.class)
	public void testExtractingHarvestJobTypeWithIncomprehensibleArgument() throws Exception {
		addDescription("Testing extrating the harvest job type not-snapshot when giving a null.");
		ScriptBasedHarvestJobExtractor jobExtractor = new ScriptBasedHarvestJobExtractor(new File("src/test/resources/jaccept.properties"));
		
		jobExtractor.harvestJobType(Math.random() + " is not a valid harvets job type.");
	}
}
