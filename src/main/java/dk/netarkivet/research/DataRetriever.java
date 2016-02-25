package dk.netarkivet.research;

import org.jwat.warc.WarcRecord;

public interface DataRetriever {
	WarcRecord getWarcRecord(String filename, Long offset);
	
}
