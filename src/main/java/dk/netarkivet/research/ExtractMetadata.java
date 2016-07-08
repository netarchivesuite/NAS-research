package dk.netarkivet.research;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.netarkivet.research.cdx.CDXEntry;
import dk.netarkivet.research.cdx.CDXExtractor;
import dk.netarkivet.research.cdx.CDXFileWriter;
import dk.netarkivet.research.cdx.DabCDXExtractor;
import dk.netarkivet.research.exception.ArgumentCheck;
import dk.netarkivet.research.harvestdb.HarvestJobExtractor;
import dk.netarkivet.research.harvestdb.HarvestJobInfo;
import dk.netarkivet.research.harvestdb.NasHarvestJobExtractor;
import dk.netarkivet.research.harvestdb.ScriptBasedHarvestJobExtractor;
import dk.netarkivet.research.http.HttpRetriever;
import dk.netarkivet.research.interval.CsvUrlIntervalReader;
import dk.netarkivet.research.interval.UrlInterval;
import dk.netarkivet.research.utils.CDXUtils;
import dk.netarkivet.research.utils.DateUtils;
import dk.netarkivet.research.utils.FileUtils;
import dk.netarkivet.research.wid.CsvWidReader;
import dk.netarkivet.research.wid.WID;

/**
 * Extracts metadata for the entries of a NAS WID file.
 * The NAS WID file is a CSV file converted from the search-extract Excel document by ELZI.
 * 
 * The input CSV file must contain WIDs in the format of WPIDs and WaybackWIDs.
 * 
 * This tool can either extract the CDX indices for the WIDs, 
 * or a metadata CSV file which will contain both the information from the CDX entry for each WID, 
 * along with the metadata for the harvest job for the resource of the WID.
 */
public class ExtractMetadata {
	/** The log.*/
	private static Logger logger = LoggerFactory.getLogger(ExtractMetadata.class);
	
	/**
	 * Main method.
	 * @param args The list of arguments.
	 */
    public static void main(String ... args) {
    	if(args.length < 4) {
    		System.err.println("Not enough arguments. Requires the following arguments:");
    		System.err.println(" 1. the CSV file in either the NAS WID format, or the URL interval format.");
    		System.err.println("  - NAS WID format has coloumns: 'W/X';#;url;date;location;filename");
    		System.err.println("  - URL interval format has coloumns: 'W';url;earliest date;latest date");
    		System.err.println(" 2. Format for CSV file: either 'WID' or 'URL'");
    		System.err.println(" 3. the base URL to the CDX-server.");
    		System.err.println(" 4. Whether or not to extract harvest job info, either 'y'/'yes' or 'n'/'no'.");
    		System.err.println(" - If this option is set to true, then it requires one of the following "
    				+ "environemnt variables: ");
    		System.err.println("   * 'dk.netarkivet.settings.file', for the settings file for the NetarchiveSuite "
    				+ "instance which contains the harvest database. This can be set by the script using the "
    				+ "environment variable NAS_SETTINGS.");
    		System.err.println("   * 'dk.netarkivet.research.script', for the script for extracting the job infos "
    				+ "without going through NetarchiveSuite harvest db setup.");
    		System.err.println(" 5. (OPTIONAL) Whether to extract in CSV format or CDX format, either 'cdx' or 'csv'.");
    		System.err.println(" - This cannot be set to 'cdx' and still extract the harvest job info.");
    		System.err.println(" - The CDX format will be a classical NAS CDX file.");
    		System.err.println(" - Default is 'CSV'.");
    		System.err.println(" 6. (OPTIONAL) the location for the output metadata file.");
    		
    		throw new IllegalArgumentException("Not enough arguments");
    	}
    	
    	File csvFile = new File(args[0]);
    	if(!csvFile.isFile()) {
    		throw new IllegalArgumentException("The CSV file '" + csvFile.getAbsolutePath() + "' is not a valid file "
    				+ "(either does not exists or is a directory)");
    	}
    	
    	InputFormat inputFormat = extractInputFormat(args[1]);
    	
    	String cdxServerBaseUrl = args[2];
    	try {
    		new URL(cdxServerBaseUrl);
    	} catch (IOException e) {
    		throw new IllegalArgumentException("The CSX Server url '" + cdxServerBaseUrl + "' is invalid.", e);
    	}
    	DabCDXExtractor cdxExtractor = new DabCDXExtractor(cdxServerBaseUrl, new HttpRetriever());
    	
    	HarvestJobExtractor jobExtractor = null;
    	if(extractWhetherToUseHarvestDb(args[3])) {
    		logger.debug("Using NAS harvest job database for extracting job info");
    		if(System.getProperty("dk.netarkivet.settings.file") != null) {
        		if(!(new File(System.getProperty("dk.netarkivet.settings.file")).isFile())) {
        			throw new IllegalArgumentException("The NAS settings file is no a valid file "
        					+ "(either not existing, or a directory)");
        		}
        		
        		logger.info("Using the NAS harvest job extractor to extract directly from the database.");
        		jobExtractor = new NasHarvestJobExtractor();
    		} else if(System.getProperty("dk.netarkivet.research.script") != null) {
    			File scriptFile = new File(System.getProperty("dk.netarkivet.research.script"));
        		if(!(scriptFile.isFile())) {
        			throw new IllegalArgumentException("The harvest job extraction script is no a valid file "
        					+ "(either not existing, or a directory)");
        		}
        		
        		logger.info("Using harvest job extractor script to extract harvest job infos.");
        		jobExtractor = new ScriptBasedHarvestJobExtractor(scriptFile);
    		} else {
    			throw new IllegalArgumentException("No system property for neither the NAS settings file defined "
    					+ "nor the extraction script has been set. Must be defined in either environment variables:"
    					+ "+'dk.netarkivet.settings.file' or 'dk.netarkivet.research.script'.");
    		}
    	}
    	
    	OutputFormat outputFormat = OutputFormat.EXPORT_FORMAT_CSV;
    	if(args.length > 4) {
    		outputFormat = extractOutputFormat(args[4]);
    	}
    	if(outputFormat == OutputFormat.EXPORT_FORMAT_CDX && jobExtractor != null) {
    		throw new IllegalArgumentException("Cannot export in CDX format when also extracting the harvest job info."
    				+ "\nEither turn off the harvest job extraction or change output format.");
    	}
    	
    	File outFile;
    	if(args.length > 5) {
    		outFile = new File(args[5]);
    	} else {
    		outFile = new File(csvFile.getParentFile(), csvFile.getName() + ".out");
    		if(outFile.exists()) {
    			FileUtils.deprecateFile(outFile);
    		}
    	}
    	if(outFile.exists()) {
    		System.err.println("The location for the output file is not vacent.");
    		System.exit(-1);
    	}
    	
    	logger.debug("Using input file '" + csvFile.getAbsolutePath() + "'");
    	logger.debug("Input file is in type '" + inputFormat.name() + "'");
    	logger.debug("CDX server has url '" + cdxServerBaseUrl + "'");
    	logger.debug("Use job extractor '" + (jobExtractor == null) + "'");
    	logger.debug("Output format '" + csvFile.getAbsolutePath() + "'");
    	logger.debug("Output file '" + outFile + "'");

    	ExtractMetadata extractor = new ExtractMetadata(csvFile, cdxExtractor, jobExtractor, outFile);
    	try {
    		extractor.extractMetadata(inputFormat, outputFormat);
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
     * @param useHarvestDb The argument. Must be 'y'/'yes' or 'n'/'no'.
     * @return True if it starts with 'y', false if it starts with 'n'.
     */
    protected static boolean extractWhetherToUseHarvestDb(String useHarvestDb) {
    	ArgumentCheck.checkNotNullOrEmpty(useHarvestDb, "String arg");
    	if(useHarvestDb.equalsIgnoreCase("y") || useHarvestDb.equalsIgnoreCase("yes")) {
    		return true;
    	} else if(useHarvestDb.equalsIgnoreCase("n") || useHarvestDb.equalsIgnoreCase("no")) {
    		return false;
    	}
    	logger.warn("Not default value for whether or not to extract the job info from the NAS harvest database. "
    			+ "Trying prefix 'y' or 'n'");
    	if(useHarvestDb.startsWith("y")) {
    		return true;
    	} else if(useHarvestDb.startsWith("n")) {
    		return false;
    	}
    	
    	throw new IllegalArgumentException("Cannot decipher argument for whether or not to extract the harvest job. "
    			+ "Must be either 'yes' or 'no'.");
    }
    
    /**
     * Extracts the argument for which output format to use.
     * Must be either CDX or CSV. 
     * @param exportFormatName The commandline argument for the export format.
     * @return Either CDX or CSV.
     */
    protected static OutputFormat extractOutputFormat(String exportFormatName) {
    	if(exportFormatName.isEmpty()) {
    		return OutputFormat.EXPORT_FORMAT_CSV;
    	}
    	if(exportFormatName.equalsIgnoreCase(EXPORT_FORMAT_CSV)) {
    		return OutputFormat.EXPORT_FORMAT_CSV;
    	} else if(exportFormatName.equalsIgnoreCase(EXPORT_FORMAT_CDX)) {
    		return OutputFormat.EXPORT_FORMAT_CDX;
    	} 
    	throw new IllegalArgumentException("Output format must be either 'CDX' or 'CSV'");
    }
    
    /**
     * Extracts the argument for which input format to use.
     * Must be either URL or WID. 
     * @param inputFormatName The commandline argument for the input format.
     * @return Either CDX or CSV.
     */
    protected static InputFormat extractInputFormat(String inputFormatName) {
    	if(inputFormatName.equalsIgnoreCase(INPUT_FORMAT_WID)) {
    		return InputFormat.INPUT_FORMAT_WID;
    	} else if(inputFormatName.equalsIgnoreCase(INPUT_FORMAT_URL_INTERVAL)) {
    		return InputFormat.INPUT_FORMAT_URL_INTERVAL;
    	} 
    	throw new IllegalArgumentException("Output format must be either '"
    			+ INPUT_FORMAT_URL_INTERVAL + "' or '" + INPUT_FORMAT_WID + "'");
    }
    
    /** The constants for appending output data to the file.*/
    public static final Boolean APPEND_TO_FILE = true;
    /** The constants for not appending output data to the file.*/
    public static final Boolean NO_APPEND_TO_FILE = false;
    /** Constant for the CDX export format.*/
    public static final String EXPORT_FORMAT_CDX = "CDX";
    /** Constant for the CSV export format.*/
    public static final String EXPORT_FORMAT_CSV = "CSV";
    /** Constant for the URL interval input format.*/
    public static final String INPUT_FORMAT_URL_INTERVAL = "URL";
    /** Constant for the WID input format.*/
    public static final String INPUT_FORMAT_WID = "WID";

    /** The reader of WIDs from the CSV file.*/
    protected final File inputFile;
    /** The base URL for the CDX server.*/
    protected final CDXExtractor cdxExtractor;
    /** The extractor of the harvest job database.*/
    protected final HarvestJobExtractor jobExtractor;
    /** The file where the output is written.*/
    protected final File outFile;
    
    /**
     * Constructor.
     * @param csvFile The input file in the NAS CSV format.
     * @param cdxExtractor The extractor of CDX entries.
     * @param jobExtractor The extractor of harvest job information.
     * @param outFile The output file.
     */
    public ExtractMetadata(File csvFile, CDXExtractor cdxExtractor, HarvestJobExtractor jobExtractor, File outFile) {
    	this.inputFile = csvFile;
    	this.cdxExtractor = cdxExtractor;
    	this.jobExtractor = jobExtractor;
    	this.outFile = outFile;
    }
    
    /**
     * Extracts the metadata for the entries in the CSV file, then extract all the CDX entries for the WIDS,
     * then extract the job data, and finally print.
     * @param inputFormat The input format, either 'WID' or 'URL interval'.
     * @param outputFormat The output format, either 'CDX' or 'CSV'.
     * @throws IOException If it fails to write to file.
     */
    public void extractMetadata(InputFormat inputFormat, OutputFormat outputFormat) throws IOException {
    	Collection<CDXEntry> cdxEntries = extractCdxForFileEntries(inputFormat);
    	
    	if(outputFormat == OutputFormat.EXPORT_FORMAT_CSV) {
    		logger.info("Printing CDX");
    		extractToCsvFormat(cdxEntries);
    	} else {
    		logger.info("Printing all metadata to CSV");
    		CDXFileWriter outputWriter = new CDXFileWriter(outFile);
    		outputWriter.writeCDXEntries(cdxEntries, DabCDXExtractor.getDefaultCDXFormat());
    	}
    }
    
    /**
     * Extracts the CDX entries for the file of the given type.
     * @param inputFormat The type of file. Either WID or URL interval.
     * @return The CDX entries for the file.
     */
    protected Collection<CDXEntry> extractCdxForFileEntries(InputFormat inputFormat) {
    	if(inputFormat == InputFormat.INPUT_FORMAT_WID) {
    		CsvWidReader reader = new CsvWidReader(inputFile);
    		Collection<WID> wids = reader.extractAllWIDs();
    		return cdxExtractor.retrieveCDXentries(wids);
    	} else {
    		CsvUrlIntervalReader reader = new CsvUrlIntervalReader(inputFile);
    		Collection<UrlInterval> intervals = reader.extractAllUrlIntervals();
    		List<CDXEntry> res = new ArrayList<CDXEntry>(intervals.size());
    		for(UrlInterval ui : intervals) {
        		res.addAll(cdxExtractor.retrieveCDXForInterval(ui));
    		}
    		return res;
    	}
    }
    
    /**
     * Extracts the CDX entries to the CSV metadata format, including extracting the job info.
     * @param cdxEntries The CDX entries.
     * @throws IOException If it fails to print to output file.
     */
    protected void extractToCsvFormat(Collection<CDXEntry> cdxEntries) throws IOException {
    	writeFirstLineToFile();
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
    		line.append("\n");
    		
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
    		line.append("\'");
    		CDXUtils.addCDXElementToStringBuffer(DateUtils.dateToWaybackDate(entry.getDate()), line);
    		line.append("\'");
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
    		} else {
    			line.append(CDXUtils.extractJobID(entry));
    			line.append(";");
    			line.append("N/A;");
    			line.append("N/A;");
    		}
    		line.append("\n");
    		
    		outStream.write(line.toString().getBytes(Charset.defaultCharset()));
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
    
    /**
     * The types of input formats supported.
     */
    protected enum InputFormat {
    	/** The WID input format.*/
    	INPUT_FORMAT_WID,
    	/** The URL interval input format.*/
    	INPUT_FORMAT_URL_INTERVAL;
    };
    /**
     * The type of output formats supported.
     */
    protected enum OutputFormat {
    	/** The CDX output format.*/
    	EXPORT_FORMAT_CDX,
    	/** The CSV output format.*/
    	EXPORT_FORMAT_CSV;
    };
}
