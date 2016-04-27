package dk.netarkivet.research.warc;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.netarkivet.common.distribute.arcrepository.BitarchiveRecord;
import dk.netarkivet.common.distribute.arcrepository.ViewerArcRepositoryClient;
import dk.netarkivet.common.utils.FileUtils;
import dk.netarkivet.research.cdx.CDXEntry;
import dk.netarkivet.research.wpid.CsvWpidReader;

public class NASArchiveExtractor implements ArchiveExtractor {
    /** Logging mechanism. */
    private static Logger logger = LoggerFactory.getLogger(CsvWpidReader.class);

    /** The NAS ArcRepositoryClient for retrieving the Arc/Warc record.*/
    ViewerArcRepositoryClient client;

    /**
     * Constructor.
     */
    public NASArchiveExtractor(ViewerArcRepositoryClient client) {
        this.client = client;
    }
    
	@Override
	public File extractWarcRecord(CDXEntry index) throws IOException {
		BitarchiveRecord payload = null; 
		File f = null;
		InputStream in = null;
		try {
			payload = client.get(index.getFilename(), index.getOffset());
			if (payload != null) {
				f = File.createTempFile("payload", "tmp");
				in = payload.getData();
				FileUtils.writeStreamToFile(in, f);
				logger.info("Data extracted for url '" + index.getUrl() + "' fetched from (filename,offset)=(" 
						+ index.getFilename() + "," + index.getOffset() + ") ."); 
			} else {
				logger.warn("Unable to extract data for (filename,offset)=(" 
						+ index.getFilename() + "," + index.getOffset() + "). Probable reason: not found");
			}
		} catch (Throwable e) {
			throw new IOException("Unable to extract (filename,offset)=(" 
					+ index.getFilename() + "," + index.getOffset() + "). The reason: " + e);
		} finally {
			IOUtils.closeQuietly(in);
		}
		return f;
	}
}
