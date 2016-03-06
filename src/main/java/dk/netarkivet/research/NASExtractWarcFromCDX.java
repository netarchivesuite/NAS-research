package dk.netarkivet.research;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import dk.netarkivet.common.distribute.arcrepository.ArcRepositoryClientFactory;
import dk.netarkivet.common.distribute.arcrepository.ViewerArcRepositoryClient;
import dk.netarkivet.research.index.CDXEntry;
import dk.netarkivet.research.index.CDXFileReader;
import dk.netarkivet.research.warc.ArchiveExtractor;
import dk.netarkivet.research.warc.NASArchiveExtractor;
import dk.netarkivet.research.warc.WarcPacker;

/**
 * Extracts a CDX file from a CSV file (in the NAS research format).
 * 
 * It translates the CSV file into WPIDS, which are extracted as CDX indices from a CDXServer.
 */
public class NASExtractWarcFromCDX {
    public static void main( String[] args ) {
    	if(args.length < 1) {
    		System.err.println("Not enough arguments. Requires the following arguments:");
    		System.err.println(" 1. the CDX file");
    		System.err.println(" 2. (OPTIONAL) output directory. If given, then it will extract "
    				+ "the WARC file to the current folder.");
    		System.exit(-1);
    	}
    	
    	File cdxFile = new File(args[0]);
    	if(!cdxFile.isFile()) {
    		System.err.println("The CDX file '" + cdxFile.getAbsolutePath() + "' is not a valid file "
    				+ "(either does not exists or is a directory)");
    		System.exit(-1);
    	}
    	
    	File outDir;
    	if(args.length > 1) {
    		outDir = new File(args[1]);
    	} else {
    		outDir = new File(".");
    	}
    	if(!outDir.isDirectory() && !outDir.mkdir()) {
    		System.err.println("The output directory '" + outDir.getAbsolutePath() + "' is not a valid "
    				+ "directory (either is a file or it cannot be instantiated as a directory)");
    		System.exit(-1);
    	}
    	
    	try {
    		CDXFileReader cdxReader = new CDXFileReader();
    		Collection<CDXEntry> cdxEntries = cdxReader.extractCDXFromFile(cdxFile);
    		
            ViewerArcRepositoryClient arcRepositoryClient = ArcRepositoryClientFactory.getViewerInstance();
            ArchiveExtractor extractor = new NASArchiveExtractor(arcRepositoryClient);
            WarcPacker warcPacker = new WarcPacker(extractor);
            warcPacker.extractToWarc(cdxEntries, outDir);
    	} catch (IOException e) {
    		e.printStackTrace(System.err);
    		System.exit(-1);
    	}
    }
}
