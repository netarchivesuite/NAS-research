package dk.netarkivet.research.batch;

import java.io.File;
import java.io.OutputStream;

import org.archive.extract.WATExtractorOutput;
import org.archive.resource.Resource;
import org.archive.resource.ResourceProducer;
import org.archive.resource.producer.WARCFile;

import dk.netarkivet.common.utils.batch.FileBatchJob;

public class WatExtractBatchJob extends FileBatchJob {
	private static final long serialVersionUID = -6188973901869528806L;
	
	WARCFile wf;
	WATExtractorOutput extractorOut;
	
	@Override
	public void initialize(OutputStream os) {
		wf = new WARCFile();
		wf.setStrict(true);
		extractorOut = new WATExtractorOutput(os);
	}

	@Override
	public boolean processFile(File file, OutputStream os) {
		try {
			ResourceProducer producer = wf.getResourceProducer(file,0L);
			
			Resource resource;
			while((resource = producer.getNext()) != null) {
				extractorOut.output(resource);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public void finish(OutputStream os) {
		// Do nothing!
	}
}
