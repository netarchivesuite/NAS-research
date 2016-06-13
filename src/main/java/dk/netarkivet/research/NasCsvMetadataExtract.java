package dk.netarkivet.research;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.netarkivet.research.cdx.CDXEntry;
import dk.netarkivet.research.cdx.CDXExtractor;
import dk.netarkivet.research.cdx.DabCDXExtractor;
import dk.netarkivet.research.exception.ArgumentCheck;
import dk.netarkivet.research.harvestdb.HarvestJobExtractor;
import dk.netarkivet.research.harvestdb.HarvestJobInfo;
import dk.netarkivet.research.harvestdb.NasHarvestJobExtractor;
import dk.netarkivet.research.http.HttpRetriever;
import dk.netarkivet.research.utils.CDXUtils;
import dk.netarkivet.research.utils.DateUtils;
import dk.netarkivet.research.wid.CsvWidReader;
import dk.netarkivet.research.wid.WID;
import dk.netarkivet.research.wid.WidReader;

/**
 * Extracts a metadata CSV file from a CSV file extracted from a search-extract Excel document by ELZI.
 * 
 * The extracted CSV file contains WIDs in the format of WPIDs and WaybackWIDs.
 * 
 * The metadata CSV file will basically contain the CDX entry for each WID, along with the metadata 
 * for the harvest job for the resource of the WID.
 */
public class NasCsvMetadataExtract {
	/** The log.*/
	private static Logger logger = LoggerFactory.getLogger(NasCsvMetadataExtract.class);
	
	/**
	 * Main method.
	 * @param args The list of arguments.
	 */
    public static void main( String ... args ) {
    	if(args.length < 3) {
    		System.err.println("Not enough arguments. Requires the following arguments:");
    		System.err.println(" 1. the CSV file with the NAS WIDs");
    		System.err.println(" 2. the base URL to the CDX-server.");
    		System.err.println(" 3. Whether or not to use the havest database, either 'y'/'yes' or 'n'/'no'.");
    		System.err.println(" - If this option is set to true, then it requires the parameter: '"
    				+ "dk.netarkivet.settings.file', which will be set by the script using the environment "
    				+ "variable NAS_SETTINGS.");
    		System.err.println(" 4. (OPTIONAL) the location for the output CSV metadata file.");
    		
    		System.exit(-1);
    	}
    	
    	File csvFile = new File(args[0]);
    	if(!csvFile.isFile()) {
    		throw new IllegalArgumentException("The CSV file '" + csvFile.getAbsolutePath() + "' is not a valid file "
    				+ "(either does not exists or is a directory)");
    	}
    	
    	String cdxServerBaseUrl = args[1];
    	try {
    		new URL(cdxServerBaseUrl);
    	} catch (IOException e) {
    		throw new IllegalArgumentException("The CSX Server url '" + cdxServerBaseUrl + "' is invalid.", e);
    	}
    	DabCDXExtractor cdxExtractor = new DabCDXExtractor(cdxServerBaseUrl, new HttpRetriever());
    	
    	HarvestJobExtractor jobExtractor = null;
    	if(extractWhetherToUseHarvestDb(args[2])) {
    		logger.debug("Using NAS harvest job database for extracting job info");
    		if(System.getProperty("dk.netarkivet.settings.file") == null) {
    			throw new IllegalArgumentException("No system property for the NAS settings file defined. "
    					+ "Must be defined in 'dk.netarkivet.settings.file'.");
    		}
    		if(!(new File(System.getProperty("dk.netarkivet.settings.file")).isFile())) {
    			throw new IllegalArgumentException("The NAS settings file is no a valid file "
    					+ "(either not existing, or a directory)");
    		}
    		jobExtractor = new NasHarvestJobExtractor();
    	}
    	
    	File outFile;
    	if(args.length > 3) {
    		outFile = new File(args[3]);
    	} else {
    		outFile = new File(".");
    	}
    	if(outFile.exists()) {
    		System.err.println("The location for the output file is not vacent.");
    		System.exit(-1);
    	}
    	
    	NasCsvMetadataExtract extractor = new NasCsvMetadataExtract(csvFile, cdxExtractor, jobExtractor, outFile);
    	try {
    		extractor.extractMetadata();
    	} catch (IOException e) {
    		e.printStackTrace(System.err);
    		throw new IllegalStateException("Failed to extract the metadata", e);
    	}
    	
    	System.out.println("Finished");
    	System.exit(0);
    }
    
    /**
     * Extracts the argument for whether or not to extract the job info from the NAS HarvestDb.
     * Throws an exception, if it is not a valid argument.
     * @param arg The argument. Must be 'y'/'yes' or 'n'/'no'.
     * @return True if it starts with 'y', false if it starts with 'n'.
     */
    protected static boolean extractWhetherToUseHarvestDb(String arg) {
    	ArgumentCheck.checkNotNullOrEmpty(arg, "String arg");
    	if(arg.equalsIgnoreCase("y") || arg.equalsIgnoreCase("yes")) {
    		return true;
    	} else if(arg.equalsIgnoreCase("n") || arg.equalsIgnoreCase("no")) {
    		return false;
    	}
    	logger.warn("Not default value for whether or not to extract the job info from the NAS harvest database. "
    			+ "Trying prefix 'y' or 'n'");
    	if(arg.startsWith("y")) {
    		return true;
    	} else if(arg.startsWith("n")) {
    		return false;
    	}
    	
    	throw new IllegalArgumentException("Cannot decipher argument for whether or not to extract the harvest job. "
    			+ "Must be either 'yes' or 'no'.");
    }
    
    /** The reader of WIDs from the CSV file.*/
    protected final WidReader reader;
    /** The base URL for the CDX server.*/
    protected final CDXExtractor cdxExtractor;
    /** The extractor of the harvest job database.*/
    protected final HarvestJobExtractor jobExtractor;
    /** The file where the output is written.*/
    protected final File outFile;
    /** The constants for appending output data to the file.*/
    public static final Boolean APPEND_TO_FILE = true;
    /** The constants for not appending output data to the file.*/
    public static final Boolean NO_APPEND_TO_FILE = false;
    
    /**
     * Constructor.
     * @param csvFile The input file in the NAS CSV format.
     * @param cdxServer The base url for the CDX server.
     * @param jobExtractor The extractor of harvest job information.
     * @param outFile The output file.
     */
    public NasCsvMetadataExtract(File csvFile, CDXExtractor cdxExtractor, HarvestJobExtractor jobExtractor, File outFile) {
    	this.reader = new CsvWidReader(csvFile);
    	this.cdxExtractor = cdxExtractor;
    	this.jobExtractor = jobExtractor;
    	this.outFile = outFile;
    }
    
    /**
     * Extracts the metadata for the WIDs from the CSV file, then extract all the CDX entries for the WIDS,
     * then extract the job data, and finally print.
     */
    public void extractMetadata() throws IOException {
    	Collection<WID> wids = reader.extractAllWIDs();

    	Collection<CDXEntry> cdxEntries = cdxExtractor.retrieveCDXentries(wids);
    	
    	for(CDXEntry entry : cdxEntries) {
    		HarvestJobInfo jobInfo = extractJobInfo(entry);
    		writeEntryToFile(entry, jobInfo);
    	}
    }
    
    /**
     * Writes the first line to the output file, e.g. the output format.
     * @throws IOException If it fails to write to the output file.
     */
    protected void writeFirstLineToFile() throws IOException {
    	try (OutputStream outStream = new FileOutputStream(outFile, NO_APPEND_TO_FILE)) {
    		StringBuilder line = new StringBuilder();
    		
    		line.append("URL;");
    		line.append("Normalized URL;");
    		line.append("Date;");
    		line.append("Content type;");
    		line.append("HTTP Status;");
    		line.append("Checksum;");
    		line.append("Redirect URL;");
    		line.append("Filename;");
    		line.append("File offset;");
    		line.append("Job ID;");
    		line.append("Job Type;");
    		line.append("Job name;");
    		
    		outStream.write(line.toString().getBytes(Charset.defaultCharset()));
    		outStream.flush();
    	}
    }
    
    /**
     * Write the CDX entry and harvest job info to the file.
     * @param entry The CDX entry.
     * @param jobInfo The harvest job info.
     * @throws IOException If there is an issue with writing the file. 
     */
    protected void writeEntryToFile(CDXEntry entry, HarvestJobInfo jobInfo) throws IOException {
    	try (OutputStream outStream = new FileOutputStream(outFile, APPEND_TO_FILE)) {
    		StringBuilder line = new StringBuilder();
    		
    		CDXUtils.addCDXElementToStringBuffer(entry.getUrl(), line);
    		line.append(";");
    		CDXUtils.addCDXElementToStringBuffer(entry.getUrlNorm(), line);
    		line.append(";");
    		CDXUtils.addCDXElementToStringBuffer(DateUtils.dateToWaybackDate(entry.getDate()), line);
    		line.append(";");
    		CDXUtils.addCDXElementToStringBuffer(entry.getContentType(), line);
    		line.append(";");
    		CDXUtils.addCDXElementToStringBuffer(entry.getStatusCode(), line);
    		line.append(";");
    		CDXUtils.addCDXElementToStringBuffer(entry.getDigest(), line);
    		line.append(";");
    		CDXUtils.addCDXElementToStringBuffer(entry.getRedirect(), line);
    		line.append(";");
    		CDXUtils.addCDXElementToStringBuffer(entry.getFilename(), line);
    		line.append(";");
    		CDXUtils.addCDXElementToStringBuffer(entry.getOffset(), line);
    		line.append(";");
    		if(jobInfo != null) {
    			CDXUtils.addCDXElementToStringBuffer(jobInfo.getId(), line);
    			line.append(";");
    			CDXUtils.addCDXElementToStringBuffer(jobInfo.getType(), line);
    			line.append(";");
    			CDXUtils.addCDXElementToStringBuffer(jobInfo.getName(), line);
    			line.append(";");
    		}
    		
    		outStream.write(line.toString().getBytes(Charset.defaultCharset()));
    		outStream.flush();
    		outStream.flush();
    	}
    }
    
	/**
	 * Extracts the harvest job info for the harvest job id in the filename in the CDX entry.
	 * @param entry The CDX entry.
	 * @return The harvest job info. Or null if something goes wrong, e.g. malformed filename or missing extractor.
	 */
    protected HarvestJobInfo extractJobInfo(CDXEntry entry) {
		Long jobId = CDXUtils.extractJobID(entry);
		if(jobExtractor == null || jobId == null) {
			logger.debug("Cannot extract harvest job info due to missing jobExtractor or jobId.");
			return null;
		}
		logger.debug("Extracting harvest job info for job '" + jobId + "'.");
		try {
			return jobExtractor.extractJob(jobId);
		} catch (RuntimeException e) {
			logger.warn("Could not extract harvest job info for job '" + jobId + "'", e);
			return null;
		}
    }
}
