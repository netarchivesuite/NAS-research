package dk.netarkivet.research.cdx;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.netarkivet.research.exception.ArgumentCheck;

/**
 * Class for writing CDX entries to a file.
 */
public class CDXFileWriter {
    /** Logging mechanism. */
    private static Logger logger = LoggerFactory.getLogger(CDXFileWriter.class);

    /** The file where the output should be written.*/
    protected final File outputFile;
    
	/**
	 * Constructor.
	 * @param outputFile The file to write the CDX entries to.
	 */
	public CDXFileWriter(File outputFile) {
		ArgumentCheck.checkNotNull(outputFile, "File outputFile");
		this.outputFile = outputFile;
	}
	
	public void writeCDXEntries(Collection<CDXEntry> entries, Collection<Character> cdxFormat) {
		logger.debug("Writing CDX indices to file '" + outputFile.getName() + "'.");
		try(FileOutputStream fos = new FileOutputStream(outputFile)) {
			String firstLine = createFirstLine(cdxFormat);
			fos.write(firstLine.getBytes());
			
			for(CDXEntry entry : entries) {
				String line = entry.extractCDXAsLine(cdxFormat);
				fos.write(line.getBytes());
			}
		} catch(IOException e) {
			String errMsg = "Issue writing the CDX indices to file '" + outputFile.getAbsolutePath() + "'";
			logger.error(errMsg, e);
			throw new IllegalStateException(errMsg, e);
		}
	}
	
	protected String createFirstLine(Collection<Character> cdxFormat) {
		StringBuilder res = new StringBuilder();
		res.append(" CDX");
		for(Character c : cdxFormat) {
			res.append(" " + c);
		}
		res.append("\n");
		return res.toString();
	}
}
