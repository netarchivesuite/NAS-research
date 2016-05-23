package dk.netarkivet.research.harvestdb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.netarkivet.harvester.datamodel.HarvestDefinition;
import dk.netarkivet.harvester.datamodel.HarvestDefinitionDAO;
import dk.netarkivet.harvester.datamodel.HarvestDefinitionDBDAO;
import dk.netarkivet.harvester.datamodel.Job;
import dk.netarkivet.harvester.datamodel.JobDAO;
import dk.netarkivet.harvester.datamodel.JobDBDAO;

/**
 * NetarchiveSuite harvest job extractor.
 */
public class NasHarvestJobExtractor implements HarvestJobExtractor {
    /** Logging mechanism. */
    private static Logger logger = LoggerFactory.getLogger(NasHarvestJobExtractor.class);

	/** The harvest job database.*/
	private final JobDAO jobDb;
	/** The harvest definition database.*/
	private final HarvestDefinitionDAO harvestDb;
	
	/**
	 * Constructor.
	 */
	public NasHarvestJobExtractor() {
		jobDb = JobDBDAO.getInstance();
		harvestDb = HarvestDefinitionDBDAO.getInstance();
	}
	
	@Override
	public HarvestJobInfo extractJob(Long jobID) {
		if(jobID == null || jobID < 1) {
			return null;
		}
		try {
			Job job = jobDb.read(jobID);
			HarvestDefinition hd = harvestDb.read(job.getOrigHarvestDefinitionID());
			return new HarvestJobInfo(job, hd);
		} catch (RuntimeException e) {
			logger.warn("Could not extract job info for job: '" + jobID + "'.", e);
			return null;
		}
	}
}
