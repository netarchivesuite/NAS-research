package dk.netarkivet.research;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import dk.netarkivet.research.index.CDXEntry;
import dk.netarkivet.research.index.CDXExtractor;
import dk.netarkivet.research.index.PywbCDXExtractor;
import dk.netarkivet.research.wpid.CsvWpidReader;
import dk.netarkivet.research.wpid.WPID;
import dk.netarkivet.research.wpid.WPidReader;

public class NASFindDuplicatesForURLs {

    public static void main( String[] args ) {
    	if(args.length < 2) {
    		System.err.println("Not enough arguments. Requires the following arguments:");
    		System.err.println(" 1. Input file, containing lines where the first element is the URL to search for");
    		System.err.println(" 2. the base URL to the CDX-server.");
    		System.err.println(" 3. (OPTIONAL) output file, otherwise it is printed to std.out.");
    		System.exit(-1);
    	}
    	
    	File inputFile = new File(args[0]);
    	if(!inputFile.isFile()) {
    		System.err.println("The input file '" + inputFile.getAbsolutePath() + "' is not a valid file "
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
//    	
//    	WPidReader reader = new CsvWpidReader(csvFile);
//    	Collection<WPID> wpids = reader.extractAllWPIDs();
//
//    	CDXExtractor cdxExtractor = new PywbCDXExtractor(cdxServerBaseUrl);
//    	List<CDXEntry> cdxEntries = new ArrayList<CDXEntry>(wpids.size());
//    	for(WPID wpid : wpids) {
//    		cdxEntries.add(cdxExtractor.retrieveCDX(wpid));
//    	}
//    	
//    	try {
//    		writeCDXEntriesToFile(cdxEntries, outFile);
//    	} catch (IOException e) {
//    		e.printStackTrace(System.err);
//    	}    	
    }
}
