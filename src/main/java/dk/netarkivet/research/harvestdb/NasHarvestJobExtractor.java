package dk.netarkivet.research.harvestdb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.netarkivet.harvester.datamodel.Job;
import dk.netarkivet.harvester.datamodel.JobDAO;
import dk.netarkivet.harvester.datamodel.JobDBDAO;
import dk.netarkivet.research.wid.CsvWidReader;

/**
 * NetarchiveSuite harvest job extractor.
 */
public class NasHarvestJobExtractor implements HarvestJobExtractor {
    /** Logging mechanism. */
    private static Logger logger = LoggerFactory.getLogger(CsvWidReader.class);

	/** The harvest job database.*/
	private final JobDAO jobDb;
	
	/**
	 * Constructor.
	 */
	public NasHarvestJobExtractor() {
		jobDb = JobDBDAO.getInstance();
	}
	
	@Override
	public HarvestJobInfo extractJob(Long jobID) {
		try {
			Job job = jobDb.read(jobID);
			return new HarvestJobInfo(job);
		} catch (Exception e) {
			logger.warn("Could not extract job info for job: '" + jobID + "'.", e);
			return null;
		}
	}
}
