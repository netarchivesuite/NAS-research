package dk.netarkivet.research;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import dk.netarkivet.common.distribute.arcrepository.ArcRepositoryClientFactory;
import dk.netarkivet.common.distribute.arcrepository.ViewerArcRepositoryClient;
import dk.netarkivet.research.cdx.CDXEntry;
import dk.netarkivet.research.cdx.CDXExtractor;
import dk.netarkivet.research.cdx.DabCDXExtractor;
import dk.netarkivet.research.http.HttpRetriever;
import dk.netarkivet.research.warc.ArchiveExtractor;
import dk.netarkivet.research.warc.NASArchiveExtractor;
import dk.netarkivet.research.warc.WarcPacker;
import dk.netarkivet.research.wpid.CsvWidReader;
import dk.netarkivet.research.wpid.WID;
import dk.netarkivet.research.wpid.WPID;
import dk.netarkivet.research.wpid.WidReader;

/**
 * Use CDX files to extract WARC files from a NAS archive.
 * 
 * It required, that the NAS settings are properly defined in the properties.
 */
public class NASExtractWarcFromCsv {
    public static void main( String[] args ) {

    	if(args.length < 2) {
    		System.err.println("Not enough arguments. Requires the following arguments:");
    		System.err.println(" 1. the CSV file with the NAS WPID");
    		System.err.println(" 2. the base URL to the CDX-server.");
    		System.err.println(" 3. (OPTIONAL) output directory. If given, then it will extract "
    				+ "the WARC file to the current folder.");
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
    	
    	File outDir;
    	if(args.length > 2) {
    		outDir = new File(args[2]);
    	} else {
    		outDir = new File(".");
    	}
    	if(!outDir.isDirectory() && !outDir.mkdir()) {
    		System.err.println("The output directory '" + outDir.getAbsolutePath() + "' is not a valid "
    				+ "directory (either is a file or it cannot be instantiated as a directory)");
    		System.exit(-1);
    	}
    	
    	WidReader reader = new CsvWidReader(csvFile);
    	Collection<WID> wpids = reader.extractAllWIDs();

    	CDXExtractor cdxExtractor = new DabCDXExtractor(cdxServerBaseUrl, new HttpRetriever());
    	List<CDXEntry> cdxEntries = new ArrayList<CDXEntry>(wpids.size());
    	for(WID wid : wpids) {
    		if(wid instanceof WPID) {
    			cdxEntries.add(cdxExtractor.retrieveCDX((WPID) wid));
    		} else {
    			// TODO print out error.
    		}
    	}
    	
        ViewerArcRepositoryClient arcRepositoryClient = ArcRepositoryClientFactory.getViewerInstance();
        ArchiveExtractor extractor = new NASArchiveExtractor(arcRepositoryClient);
        WarcPacker warcPacker = new WarcPacker(extractor);
        warcPacker.extractToWarc(cdxEntries, outDir);
    }
}
