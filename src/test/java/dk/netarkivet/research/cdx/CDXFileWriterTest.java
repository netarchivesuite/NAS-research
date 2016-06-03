package dk.netarkivet.research.cdx;

import static org.mockito.Mockito.mock;
import static org.testng.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import dk.netarkivet.research.cdx.CDXEntry;
import dk.netarkivet.research.cdx.CDXFileReader;
import dk.netarkivet.research.testutils.TestFileUtils;
import dk.netarkivet.research.utils.FileUtils;
import dk.netarkivet.research.warc.ArchiveExtractor;
import dk.netarkivet.research.warc.WarcPacker;

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
    	addDescription("Test loading the test cdx file.");

    	File outputFile = new File(dirPath, "OutputFile-" + Math.random());
    	CDXFileWriter cfw = new CDXFileWriter(outputFile);
    	cfw.writeCDXEntries(new ArrayList<CDXEntry>(), Arrays.asList(CDXConstants.DEFAULT_CDX_CHAR_FORMAT));
    	
    	assertEquals(TestFileUtils.countNumberOfLines(outputFile), 1);
    }
    
    @Test
    public void testStuff() {
    	
    }
}
