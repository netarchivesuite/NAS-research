package dk.netarkivet.research.batch;

import java.io.IOException;
import java.io.OutputStream;

import org.archive.io.arc.ARCRecord;

import dk.netarkivet.common.exceptions.IOFailure;
import dk.netarkivet.common.utils.arc.ARCBatchJob;

/**
 * BatchJob 
 */
public class MimetypeBatchJob extends ARCBatchJob {

	/**
	 * Serial version uid.
	 */
	private static final long serialVersionUID = -300628049610754983L;

	@Override
	public void initialize(OutputStream os) {
		// NOTHING!
	}

	@Override
	public void processRecord(ARCRecord record, OutputStream os) {
		String line = record.getHeader().getMimetype() + " ## " + record.getHeaderString().length();
		
		try {
			os.write(line.getBytes());
		} catch (IOException e) {
			throw new IOFailure("Could not process the record.",e);
		}
	}

	@Override
	public void finish(OutputStream os) {
		// NOTHING!
	}
}
