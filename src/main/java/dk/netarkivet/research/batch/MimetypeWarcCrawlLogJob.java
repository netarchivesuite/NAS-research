package dk.netarkivet.research.batch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.archive.io.warc.WARCRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.netarkivet.common.exceptions.IOFailure;
import dk.netarkivet.common.utils.warc.WARCBatchJob;

/**
 * BatchJob for extracting mimetypes from the CrawlLog.
 * 
 */
public class MimetypeWarcCrawlLogJob extends WARCBatchJob {
	/** The class log. */
    private static final Logger log = LoggerFactory.getLogger(MimetypeWarcCrawlLogJob.class);

	/**
	 * Serial version uid.
	 */
	private static final long serialVersionUID = 4822111189429743970L;

	@Override
	public void initialize(OutputStream os) {
		// NOTHING!
	}

	@Override
	public void processRecord(WARCRecord record, OutputStream os) {
		String recordUri = (String) record.getHeader().getHeaderValue("WARC-Target-URI");
		if(recordUri == null || !recordUri.contains("metadata://netarkivet.dk/crawl/logs/crawl.log?")) {
			log.debug("Not crawllog: " + recordUri);
			return;
		}
		log.info("Examining crawl log: " + recordUri);
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(record, Charset.defaultCharset()));
			String line;
			while((line = reader.readLine()) != null) {
				List<String> split = removeEmptyElements(line.split("\\s"));
				if(split.size() < 12) {
					log.debug("Bad length : " + split.size());
					continue;
				}
				if(!split.get(1).equals("200")) {
					log.debug("Bad response code : " + split.get(1));
					continue;
				}
				String size = line.split("content-size:")[1];
				String outputLine = split.get(6) + "##" + size + "\n";
				
				os.write(outputLine.getBytes(Charset.defaultCharset()));
			}
		
			os.flush();
		} catch (IOException e) {
			log.warn("Could not perform the batchjob.", e);
			throw new IOFailure("Could not process the record.",e);
		}
	}

	@Override
	public void finish(OutputStream os) {
		// NOTHING!
	}
	
	@Override
	public boolean postProcess(InputStream input, OutputStream output) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(input, Charset.defaultCharset()));
		Map<String, Long> countMap = new HashMap<String, Long>();
		Map<String, Long> sizeMap = new HashMap<String, Long>();
		int totalCount = 0;
		int totalSize = 0;
		String line;
		try {
			while((line = reader.readLine()) != null) {
				String[] split = line.split("##");
				if(split.length != 2) {
					continue;
				}
				Long size = Long.valueOf(split[1]);
				
				insertIntoMap(split[0], 1L, countMap);
				insertIntoMap(split[0], size, sizeMap);
				totalCount += 1;
				totalSize += size;
			}
			
			output.write(("Total records: " + totalCount + "\n").getBytes(Charset.defaultCharset()));
			output.write(("Total size: " + totalSize + "\n").getBytes(Charset.defaultCharset()));
			output.write("\n\nMimetype;count\n".getBytes(Charset.defaultCharset()));
			writeMapToOutputStream(countMap, output);
			output.write("\n\nMimetype;size\n".getBytes(Charset.defaultCharset()));
			writeMapToOutputStream(sizeMap, output);
		} catch (IOException e) {
			throw new IOFailure("Failed post processing", e);
		}
		return true;
	}
	
	/**
	 * Increase the value for the key in the map.
	 * @param key The key for the value in the map to increase.
	 * @param countIncrease The increment value for the key element in the map.
	 * @param map The map to have a value incremented.
	 */
	protected void insertIntoMap(String key, Long countIncrease, Map<String, Long> map) {
		Long value = map.get(key);
		if(value == null) {
			value = 0L;
		}
		value += countIncrease;
		map.put(key, value);
	}
	
	/**
	 * Writes a map to the output stream, one entry per line.
	 * @param map The map to write.
	 * @param os The output stream where it should be written.
	 * @throws IOException If it fails.
	 */
	protected void writeMapToOutputStream(Map<String, Long> map, OutputStream os) throws IOException {
		for(Map.Entry<String, Long> entry : map.entrySet()) {
			String line = entry.getKey() + ";" + entry.getValue() + "\n";
			os.write(line.getBytes(Charset.defaultCharset()));
		}
	}
	
	/**
	 * Converts the array into a list, and removes all empty elements.
	 * @param split The array of strings.
	 * @return The list of string, where the empty ones are removed.
	 */
	protected List<String> removeEmptyElements(String[] split) {
		List<String> res = new ArrayList<String>();
		for(int i = 0; i < split.length; i++) {
			if(split[i].trim().isEmpty()) {
				continue;
			}
			res.add(split[i]);
		}
		return res;
	}
}
