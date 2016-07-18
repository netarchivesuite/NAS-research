package dk.netarkivet.research.testutils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.netarkivet.research.utils.StreamUtils;

public class ProcessTestingUtils {
	private static Logger logger = LoggerFactory.getLogger(ProcessTestingUtils.class);

	public static boolean isProcessRunning(String processName) {
		try {
			String command = "ps auxwww";
			Process p = Runtime.getRuntime().exec(command);
			int success = p.waitFor();
			if(success != 0) {
				logger.warn("Oddly could not terminate properly: " + success + ". Gave error: \n"
						+ StreamUtils.extractInputStreamAsText(p.getErrorStream()));
			}

			String processes = StreamUtils.extractInputStreamAsText(p.getInputStream());
			return processes.contains(processName);
		} catch (Exception e) {
			logger.warn("Issue occured when checking for a running process.", e);
			return false;
		}
	}
}
