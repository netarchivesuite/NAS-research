package dk.netarkivet.research;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import dk.netarkivet.research.cdx.CDXEntry;
import dk.netarkivet.research.cdx.CDXExtractor;
import dk.netarkivet.research.cdx.DabCDXExtractor;
import dk.netarkivet.research.cdx.PywbCDXExtractor;
import dk.netarkivet.research.http.HttpRetriever;
import dk.netarkivet.research.wid.CsvWidReader;
import dk.netarkivet.research.wid.WID;
import dk.netarkivet.research.wid.WPID;
import dk.netarkivet.research.wid.WidReader;

/**
 * Does the full extraction from CSV file to WARC files.
 * 
 * It translates the CSV file into WPIDS, which are extracted as CDX indices from a CDXServer.
 * The WARC records can be extracted from another tool.
 * 
 */
public class NASExtractCDXFromCSV {
	
    public static void main( String[] args ) {
    	if(args.length < 2) {
    		System.err.println("Not enough arguments. Requires the following arguments:");
    		System.err.println(" 1. the CSV file with the NAS WPID");
    		System.err.println(" 2. the base URL to the CDX-server.");
    		System.err.println(" 3. (OPTIONAL) the location for the output CDX file.");
    		System.exit(-1);
    	}
    	
    	File csvFile = new File(args[0]);
    	if(!csvFile.isFile()) {
    		System.err.println("The CSV file '" + csvFile.getAbsolutePath() + "' is not a valid file "
    				+ "(either does not exists or is a directory)");
    		System.exit(-1);
    	}
    	
    	String cdxServerBaseUrl = args[1];
    	try {
    		new URL(cdxServerBaseUrl);
    	} catch (IOException e) {
    		System.err.println("The CSX Server url '" + cdxServerBaseUrl + "' is invalid.");
    		e.printStackTrace(System.err);
    		System.exit(-1);
    	}
    	
    	File outFile;
    	if(args.length > 2) {
    		outFile = new File(args[2]);
    	} else {
    		outFile = new File(".");
    	}
    	if(outFile.exists()) {
    		System.err.println("The location for the output file is not vacent.");
    		System.exit(-1);
    	}
    	
    	WidReader reader = new CsvWidReader(csvFile);
    	Collection<WID> wpids = reader.extractAllWIDs();

    	CDXExtractor cdxExtractor = new DabCDXExtractor(cdxServerBaseUrl, new HttpRetriever());
    	List<CDXEntry> cdxEntries = new ArrayList<CDXEntry>(wpids.size());
    	for(WID wpid : wpids) {
    		if(wpid instanceof WPID) {
    			cdxEntries.add(cdxExtractor.retrieveCDX((WPID) wpid));
    		} else {
    			// ??
    		}
    	}
    	
    	try {
    		writeCDXEntriesToFile(cdxEntries, outFile);
    	} catch (IOException e) {
    		e.printStackTrace(System.err);
    	}    	
    }
    
    /**
     * Write the CDX entries to a file.
     * 
     * @param cdxEntries The CDX entries to write to the file.
     * @param outFile The file to write the CDX entries to.
     * @throws IOException If there is an issue with writing the file. 
     */
    protected static void writeCDXEntriesToFile(Collection<CDXEntry> cdxEntries, File outFile) throws IOException {
    	Collection<Character> cdxChars = PywbCDXExtractor.CDX_ARGUMENTS.keySet();
    	
    	try (OutputStream outStream = new FileOutputStream(outFile);) {
    		// Write first line
    		outStream.write(" CDX ".getBytes());
    		for(Character c : cdxChars) {
    			outStream.write(new String(c + " ").getBytes());
    		}
    		outStream.write("\n".getBytes());

    		// Write all the cdx lines
    		for(CDXEntry cdxEntry : cdxEntries) {
        		outStream.write(cdxEntry.extractCDXAsLine(cdxChars).getBytes());
        		outStream.write("\n".getBytes());
    		}
    		outStream.flush();
    	}
    }
}
