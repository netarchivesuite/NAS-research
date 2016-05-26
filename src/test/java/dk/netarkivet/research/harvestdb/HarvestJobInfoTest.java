package dk.netarkivet.research.harvestdb;

import static org.testng.Assert.assertEquals;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.annotations.Test;

public class HarvestJobInfoTest extends ExtendedTestCase {
	
	@Test
	public void testHarvestJobInfo() {
		addDescription("Testing the creation of and extraction from a harvest job info.");
		
		Long jobId = Long.MAX_VALUE;
		String jobType = "Type of job";
		String jobStatus = "Status for the job";
		String jobName = "The name of the job";
		HarvestJobInfo jobInfo = new HarvestJobInfo(jobId, jobType, jobStatus, jobName);
		
		assertEquals(jobId, jobInfo.getId());
		assertEquals(jobType, jobInfo.getType());
		assertEquals(jobStatus, jobInfo.getStatus());
		assertEquals(jobName, jobInfo.getName());
	}
}
