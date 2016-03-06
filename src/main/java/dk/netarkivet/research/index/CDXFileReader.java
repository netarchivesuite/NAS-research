package dk.netarkivet.research.index;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.netarkivet.research.wpid.CsvWpidReader;

/**
 * CDX file reader.
 */
public class CDXFileReader {
    /** Logging mechanism. */
    private static Logger logger = LoggerFactory.getLogger(CsvWpidReader.class);

	/**
	 * Constructor.
	 */
	public CDXFileReader() { }
	
	/**
	 * Extracts the CDXs from a file.
	 * @param cdxFile The file to extract from.
	 * @return The list of CDXs.
	 * @throws IOException If an issue occurs while reading CDXs from the file.
	 */
    public Collection<CDXEntry> extractCDXFromFile(File cdxFile) throws IOException {
    	List<CDXEntry> res = new ArrayList<CDXEntry>();
    	String line;
    	try (
    	    InputStream fis = new FileInputStream(cdxFile);
    	    InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
    	    BufferedReader br = new BufferedReader(isr);
    	) {
    		Character[] cdxFormat = extractCDXFormat(br.readLine());
    	    while ((line = br.readLine()) != null) {
    	    	CDXEntry entry = CDXEntry.createCDXEntry(line.split(" "), cdxFormat);
    	    	if(entry != null) {
    	    		res.add(entry);
    	    	}
    	    }
    	}
    	
    	return res;
    }
    
    /**
     * Extract the CDX format argument characters from the first line of the CDX file.
     * @param cdxChars The first line in the CDX file.
     * @return The CDX format characters.
     */
    protected Character[] extractCDXFormat(String cdxChars) {
    	List<Character> res = new ArrayList<Character>();
    	
    	for(String c : cdxChars.split(" ")) {
    		if(c.isEmpty() || c.equals("CDX")) {
    			// ignore first 
    			logger.trace("ignore CDX format substring '" + c + "'");
    			continue;
    		} else if(c.length() != 1) {
    			// ignore, if it is not 1 digit
    			logger.info("Ignoring element which is not 1 character: '" + c + "'.");
    			continue;
    		}
    		res.add(c.charAt(0));
    	}
    	
    	return res.toArray(new Character[res.size()]);
    }
}
