package dk.netarkivet.research.harvestdb;

import dk.netarkivet.harvester.datamodel.Job;

/**
 * Container of information about a harvest job. 
 */
public class HarvestJobInfo {
	/** The id of the job.*/
	protected final Long id;
	/** The type of job (e.g. snapshot or selective/event).*/
	protected final String type;
	/** The status of the job.*/
	protected final String status;
	/** The name of the job.*/
	protected final String name;
	
	/**
	 * Constructor.
	 * @param id The id of the job.
	 * @param type The type of job (e.g. snapshot or selective/event).
	 * @param status The status of the job.
	 * @param name The name of the job.
	 */
	public HarvestJobInfo(Long id, String type, String status, String name) {
		this.id = id;
		this.type = type;
		this.status = status;
		this.name = name;
	}
	
	/**
	 * Constructor.
	 * @param nasJob A NAS job from the harvest database.
	 */
	public HarvestJobInfo(Job nasJob) {
		this.id = nasJob.getJobID();
		this.type = nasJob.isSnapshot() ? "snapshot" : "selective/event";
		this.status = nasJob.getStatus().name();
		this.name = nasJob.getOrderXMLName();
	}
	
	/**
	 * @return The ID.
	 */
	public Long getId() {
		return id;
	}
	
	/**
	 * @return The type (e.g. snapshot, selective, event)
	 */
	public String getType() {
		return type;
	}
	
	/**
	 * @return The status.
	 */
	public String getStatus() {
		return status;
	}
}
