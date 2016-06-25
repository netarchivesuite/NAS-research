package dk.netarkivet.research.cdx;

import static org.testng.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import dk.netarkivet.research.testutils.TestFileUtils;
import dk.netarkivet.research.utils.FileUtils;

public class CDXFileWriterTest extends ExtendedTestCase {
	
	String dirPath = "test-dir";
	File outDir;
	
	@BeforeMethod
	public void setupMethod() throws Exception {
		outDir = new File(dirPath);
		if(outDir.exists()) {
			TestFileUtils.removeFile(outDir);
		}
		outDir = FileUtils.createDir(dirPath);
	}
	
	@AfterMethod
	public void cleanUpMethod() throws Exception {
		TestFileUtils.removeFile(outDir);
	}
	
    @Test
    public void testWritingFileWithNoCDXindices() throws Exception {
    	addDescription("Test writing a file with no CDX entries");
    	File outputFile = new File(dirPath, "OutputFile-" + Math.random());
    	CDXFileWriter cfw = new CDXFileWriter(outputFile);

    	addStep("Writing an empty list of CDX entries with default CDX format", 
    			"Should have only CDX header line.");
    	cfw.writeCDXEntries(new ArrayList<CDXEntry>(), CDXConstants.getNasDefaultCDXFormat());
    	assertEquals(TestFileUtils.countNumberOfLines(outputFile), 1);
    }
    
    @Test
    public void testWritingOneCDXEntryToTheFile() {
    	addDescription("Writing a single CDX entry to the CDX file.");
    	File outputFile = new File(dirPath, "OutputFile-" + Math.random());
    	CDXFileWriter cfw = new CDXFileWriter(outputFile);
    	CDXEntry entry = CDXEntry.createCDXEntry(new String[] {"http://netarkivet.dk", "20110101010101", "VJ3CKK3ZH2FR7V2KM5TSI3TENA7ZSWKM"}, new Character[] {'A', 'b', 'k'});
    	addStep("Writing an empty list of CDX entries with default CDX format", 
    			"Should have only CDX header line.");
    	cfw.writeCDXEntries(Arrays.asList(entry), CDXConstants.getNasDefaultCDXFormat());    	
    	assertEquals(TestFileUtils.countNumberOfLines(outputFile), 2);

    }
}
