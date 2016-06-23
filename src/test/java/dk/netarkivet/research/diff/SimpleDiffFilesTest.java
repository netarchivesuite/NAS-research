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
	
	@BeforeMethod
	public void setupMethod() throws Exception {
	}
	
	@AfterMethod
	public void cleanUpMethod() throws Exception {
	}
	
    @Test
    public void testDiffOnCompletelyDifferentFiles() throws Exception {
    	addDescription("Test the simple diff between two completely different files");
    	File orig = new File("src/test/resources/diff/test1_orig.txt");
    	File revised = new File("src/test/resources/diff/test1_revised.txt");
    	SimpleDiffFiles sdf = new SimpleDiffFiles();
    	DiffResultWrapper dr = sdf.diff(new FileInputStream(orig), new FileInputStream(revised));
    	System.err.println(dr.toString());
    }

//    @Test
    public void testDiffOnIdenticalFiles() throws Exception {
    	addDescription("Test the simple diff between two identical files");
    	File orig = new File("src/test/resources/diff/test2_orig.txt");
    	File revised = new File("src/test/resources/diff/test2_revised.txt");
    	SimpleDiffFiles sdf = new SimpleDiffFiles();
    	DiffResultWrapper dr = sdf.diff(new FileInputStream(orig), new FileInputStream(revised));
    }
    
//    @Test
    public void testDiffOnSlightlyDifferentFiles() throws Exception {
    	addDescription("Test the simple diff between two slightly different files");
    	File orig = new File("src/test/resources/diff/test4_orig.txt");
    	File revised = new File("src/test/resources/diff/test4_revised.txt");
    	SimpleDiffFiles sdf = new SimpleDiffFiles();
    	DiffResultWrapper dr = sdf.diff(new FileInputStream(orig), new FileInputStream(revised));
    	System.err.println(dr.toString());
    }
}
