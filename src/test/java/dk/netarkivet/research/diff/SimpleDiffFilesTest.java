package dk.netarkivet.research.diff;

import static org.testng.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import dk.netarkivet.research.cdx.CDXConstants;
import dk.netarkivet.research.cdx.CDXEntry;
import dk.netarkivet.research.cdx.CDXFileWriter;
import dk.netarkivet.research.testutils.TestFileUtils;
import dk.netarkivet.research.utils.FileUtils;

public class SimpleDiffFilesTest extends ExtendedTestCase {
	File orig = new File("src/test/resources/diff/test1_orig.txt");
	File revised = new File("src/test/resources/diff/test1_revised.txt");
	
	@BeforeMethod
	public void setupMethod() throws Exception {
	}
	
	@AfterMethod
	public void cleanUpMethod() throws Exception {
	}
	
    @Test
    public void testDiff() throws Exception {
    	addDescription("Test the simple diff between two completely different files");
    	SimpleDiffFiles sdf = new SimpleDiffFiles();
    	sdf.diff(new FileInputStream(orig), new FileInputStream(revised));

    }
}
