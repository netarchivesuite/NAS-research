package dk.netarkivet.research.batch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.archive.io.arc.ARCRecord;

import dk.netarkivet.common.exceptions.IOFailure;
import dk.netarkivet.common.utils.arc.ARCBatchJob;

/**
 * BatchJob for extracting mimetypes.
 * 
 */
public class MimetypeBatchJob extends ARCBatchJob {

	/**
	 * Serial version uid.
	 */
	private static final long serialVersionUID = -300628049610754983L;

	@Override
	public void initialize(OutputStream os) {
		// NOTHING!
	}

	@Override
	public void processRecord(ARCRecord record, OutputStream os) {
		String line = record.getHeader().getMimetype() + "##" + record.getHeaderString().length() + "\n";
		
		try {
			os.write(line.getBytes(Charset.defaultCharset()));
		} catch (IOException e) {
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
			output.write("Mimetype;count\n".getBytes(Charset.defaultCharset()));
			writeMapToOutputStream(countMap, output);
			output.write("\n\nMimetype;size\n".getBytes(Charset.defaultCharset()));
			writeMapToOutputStream(sizeMap, output);
		} catch (IOException e) {
			throw new IOFailure("", e);
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
}
