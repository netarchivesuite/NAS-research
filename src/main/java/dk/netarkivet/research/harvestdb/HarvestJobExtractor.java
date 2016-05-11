package dk.netarkivet.research.harvestdb;

/**
 * Interface for extracting info about harvested jobs.
 */
public interface HarvestJobExtractor {
	/**
	 * Retrieves the information about a harvested job.
	 * @param jobID The ID of the job.
	 * @return The information of the harvested job.
	 */
	HarvestJobInfo extractJob(Long jobID);
}
