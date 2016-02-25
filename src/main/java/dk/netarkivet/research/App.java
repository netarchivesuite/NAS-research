package dk.netarkivet.research;

import dk.netarkivet.archive.tools.GetRecord;
import dk.netarkivet.common.distribute.arcrepository.ArcRepositoryClientFactory;
import dk.netarkivet.common.distribute.arcrepository.BitarchiveRecord;
import dk.netarkivet.common.distribute.arcrepository.ViewerArcRepositoryClient;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) {
    	
        
        ViewerArcRepositoryClient arcRepositoryClient = ArcRepositoryClientFactory.getViewerInstance();
        BitarchiveRecord br = arcRepositoryClient.get("file", 0L);
//        br.
    }
}
