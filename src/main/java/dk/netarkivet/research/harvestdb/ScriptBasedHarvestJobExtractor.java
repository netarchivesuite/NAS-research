package dk.netarkivet.research.harvestdb;

import java.io.File;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.netarkivet.research.exception.ArgumentCheck;
import dk.netarkivet.research.utils.StreamUtils;

/**
 * Extracts harvest job info using a script. 
 * 
 * The script must take 1 argument: the job id. - e.g. script db_extract.sh
 * 
 * The script must then deliver 2 lines.
 * The first line must deliver whether or not it is a snapshot harvest (true/false). 
 * The second line must deliver the name of the harvest.
 * 
 * 
 * E.g. If it is a NAS database, then you could have the following two commands in your script:
 ** psql -h $DB_HOST -A -t -c "select snapshot from jobs WHERE job_id = $JOB_ID;" -U $USER $DB_NAME
 ** psql -h $DB_HOST -A -t -c "select name FROM harvestdefinitions WHERE harvest_id = \(select harvest_id from jobs WHERE job_id = $JOB_ID);" -U $USER $DB_NAME
 * 
 * Where:
 * $DB_HOST is the ip for the machine, where you are running the harvest postgresql.
 * $USER is the username on the machine
 * $DB_NAME is the name of the database on the postgresql server.
 * $JOB_ID is the argument for the script. 
 */
public class ScriptBasedHarvestJobExtractor implements HarvestJobExtractor {
	/** Logging mechanism. */
	private static Logger logger = LoggerFactory.getLogger(ScriptBasedHarvestJobExtractor.class);

	/** The file with the script.*/
	protected final File scriptFile;
	
	/**
	 * Constructor.
	 * @param scriptFile The file with the script.
	 */
	public ScriptBasedHarvestJobExtractor(File scriptFile) {
		ArgumentCheck.checkIsFile(scriptFile, "File scriptFile");
		this.scriptFile = scriptFile;
	}
	
	@Override
	public HarvestJobInfo extractJob(Long jobID) {
		logger.debug("Extracting harvest job info for job '" + jobID + "'.");
		
		try {
			String command = "bash " + scriptFile.getAbsolutePath() + " " + jobID;
			Process p = Runtime.getRuntime().exec(command);
			int success = p.waitFor();
			if(success != 0) {
				logger.warn("Script did not terminate properly. Gave return code '" + success 
						+ "' when running command:\n" + command + "\nTrying to continue");
			}

			List<String> lines = StreamUtils.extractInputStreamAsLines(p.getInputStream());
			if(lines.size() < 2) {
				logger.warn("Received incomprehensible output from running the script: " + lines
						+ ". Returning a null.");
				return null;
			}
			
			return new HarvestJobInfo(jobID, harvestJobType(lines.get(0)), "unknown", lines.get(1));
		} catch (Exception e) {
			logger.warn("Issue occured when extracting the harvest job info. Null returned.", e);
			return null;
		}
	}

	/**
	 * Extracts the harvest job type from the script output.
	 * @param jobTypeOutputLine The line from the script output with the job type.
	 * @return The name for the harvest job type.
	 */
	protected String harvestJobType(String jobTypeOutputLine) {
		if(jobTypeOutputLine == null || jobTypeOutputLine.isEmpty()) {
			return null;
		}
		if(jobTypeOutputLine.startsWith("t") || jobTypeOutputLine.startsWith("T")) {
			return HarvestJobInfo.HARVEST_TYPE_SNAPSHOT;
		} else if(jobTypeOutputLine.startsWith("f") || jobTypeOutputLine.startsWith("F")) {
			return HarvestJobInfo.HARVEST_TYPE_NOT_SNAPSHOT;
		}
		
		throw new IllegalStateException("Cannot extract harvest job type.");
  	}
}
