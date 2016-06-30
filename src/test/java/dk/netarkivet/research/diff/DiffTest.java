package dk.netarkivet.research.diff;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertFalse;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import dk.netarkivet.research.testutils.TestFileUtils;
import dk.netarkivet.research.utils.FileUtils;

public class DiffTest extends ExtendedTestCase {
	File testDir = new File("tempDir");
	
	@BeforeMethod
	public void setupClass() throws Exception {
		if(testDir.exists()) {
			TestFileUtils.removeFile(testDir);
		}
		testDir = FileUtils.createDir(testDir.getAbsolutePath());
	}
	
	@AfterMethod
	public void tearDownClass() throws Exception {
		TestFileUtils.removeFile(testDir);
	}
	
    @Test
    public void testDiffOnCompletelyDifferentFiles() throws Exception {
    	DiffFiles diffMethod = new SimpleDiffFiles();
    	DiffOutputFormat outputFormat = DiffOutputFormat.OUTPUT_FORMAT_BOTH;
    	
    	File orig = new File("src/test/resources/diff/test1_orig.txt");
    	File revised = new File("src/test/resources/diff/test1_revised.txt");
    	
    	Diff diff = new Diff(diffMethod, outputFormat, testDir);
    	diff.performDiff(orig, revised);
    	assertEquals(testDir.list().length, 2);
    }
    
    @Test
    public void testDiffOnIdenticalFiles() throws Exception {
    	DiffFiles diffMethod = new SimpleDiffFiles();
    	DiffOutputFormat outputFormat = DiffOutputFormat.OUTPUT_FORMAT_SUMMARY;
    	
    	File orig = new File("src/test/resources/diff/test2_orig.txt");
    	File revised = new File("src/test/resources/diff/test2_revised.txt");
    	
    	Diff diff = new Diff(diffMethod, outputFormat, testDir);
    	diff.performDiff(orig, revised);
    	assertEquals(testDir.list().length, 1);
    	assertTrue(testDir.list()[0].startsWith("diff-summary"), "" + Arrays.asList(testDir.list()));
    }
    
    @Test
    public void testVerboseDiffOnIdenticalFiles() throws Exception {
    	DiffFiles diffMethod = new SimpleDiffFiles();
    	DiffOutputFormat outputFormat = DiffOutputFormat.OUTPUT_FORMAT_VERBOSE;
    	
    	File orig = new File("src/test/resources/diff/test4-orig.txt");
    	File revised = new File("src/test/resources/diff/test4-revised.txt");
    	
    	Diff diff = new Diff(diffMethod, outputFormat, testDir);
    	diff.performDiff(orig, revised);
    	assertEquals(testDir.list().length, 1);
    	assertFalse(testDir.list()[0].startsWith("diff-summary"), "" + Arrays.asList(testDir.list()));
    }
    
    @Test(expectedExceptions = IOException.class)
    public void testDiffOnNonExistingOrigFile() throws Exception {
    	DiffFiles diffMethod = new SimpleDiffFiles();
    	DiffOutputFormat outputFormat = DiffOutputFormat.OUTPUT_FORMAT_VERBOSE;
    	
    	File orig = new File("src/test/resources/diff/test3_orig" + Math.random() + ".txt");
    	File revised = new File("src/test/resources/diff/test3_revised.txt");
    	
    	Diff diff = new Diff(diffMethod, outputFormat, testDir);
    	diff.performDiff(orig, revised);
    }

    @Test(expectedExceptions = IOException.class)
    public void testDiffOnNonExistingRevisedFile() throws Exception {
    	DiffFiles diffMethod = new SimpleDiffFiles();
    	DiffOutputFormat outputFormat = DiffOutputFormat.OUTPUT_FORMAT_VERBOSE;
    	
    	File orig = new File("src/test/resources/diff/test3_orig.txt");
    	File revised = new File("src/test/resources/diff/test3_revised" + Math.random() + ".txt");
    	
    	Diff diff = new Diff(diffMethod, outputFormat, testDir);
    	diff.performDiff(orig, revised);
    }
    
    @Test
    public void testInitializingSummaryForSummary() throws Exception {
    	addDescription("Test initializing the summary file, when using output format 'SUMMARY'");
    	DiffFiles diffMethod = new SimpleDiffFiles();
    	DiffOutputFormat outputFormat = DiffOutputFormat.OUTPUT_FORMAT_SUMMARY;
    	
    	Diff diff = new Diff(diffMethod, outputFormat, testDir);
    	diff.initialiseSummaryFile();
    	assertEquals(testDir.list().length, 1);
    	assertTrue(testDir.list()[0].startsWith("diff-summary"), "" + Arrays.asList(testDir.list()));
    }
    
    @Test
    public void testInitializingSummaryForBoth() throws Exception {
    	addDescription("Test initializing the summary file, when using output format 'BOTH'");
    	DiffFiles diffMethod = new SimpleDiffFiles();
    	DiffOutputFormat outputFormat = DiffOutputFormat.OUTPUT_FORMAT_BOTH;
    	
    	Diff diff = new Diff(diffMethod, outputFormat, testDir);
    	diff.initialiseSummaryFile();
    	assertEquals(testDir.list().length, 1);
    	assertTrue(testDir.list()[0].startsWith("diff-summary"), "" + Arrays.asList(testDir.list()));
    }
    
    @Test
    public void testInitializingSummaryForVerbose() throws Exception {
    	addDescription("Test initializing the summary file, when using output format 'VERBOSE'");
    	DiffFiles diffMethod = new SimpleDiffFiles();
    	DiffOutputFormat outputFormat = DiffOutputFormat.OUTPUT_FORMAT_VERBOSE;
    	
    	Diff diff = new Diff(diffMethod, outputFormat, testDir);
    	diff.initialiseSummaryFile();
    	assertEquals(testDir.list().length, 0);
    }
    
    @Test
    public void testInitializingSummaryTwice() throws Exception {
    	addDescription("Test initializing the summary file 2 times, does not create more files.");
    	DiffFiles diffMethod = new SimpleDiffFiles();
    	DiffOutputFormat outputFormat = DiffOutputFormat.OUTPUT_FORMAT_SUMMARY;
    	
    	Diff diff = new Diff(diffMethod, outputFormat, testDir);
    	diff.initialiseSummaryFile();
    	assertEquals(testDir.list().length, 1);
    	
    	diff.initialiseSummaryFile();
    	assertEquals(testDir.list().length, 1);
    	assertTrue(testDir.list()[0].startsWith("diff-summary"), "" + Arrays.asList(testDir.list()));
    }
    
    @Test
    public void testInitializingSummaryAgainAfterRemoval() throws Exception {
    	addDescription("Test initializing the summary file after it has been deleted, should create a new one");
    	DiffFiles diffMethod = new SimpleDiffFiles();
    	DiffOutputFormat outputFormat = DiffOutputFormat.OUTPUT_FORMAT_SUMMARY;
    	
    	Diff diff = new Diff(diffMethod, outputFormat, testDir);
    	diff.initialiseSummaryFile();
    	assertEquals(testDir.list().length, 1);
    	
    	TestFileUtils.removeFile(testDir.listFiles()[0]);
    	assertEquals(testDir.list().length, 0);
    	
    	diff.initialiseSummaryFile();
    	assertEquals(testDir.list().length, 1);
    }
    
    @Test
    public void testPrintingToFileSuccess() throws Exception {
    	addDescription("Test printing something to a file.");
    	DiffFiles diffMethod = new SimpleDiffFiles();
    	DiffOutputFormat outputFormat = DiffOutputFormat.OUTPUT_FORMAT_SUMMARY;
    	
    	Diff diff = new Diff(diffMethod, outputFormat, testDir);
    	diff.printToFile("This is the content", new File(testDir, Math.random() + ".txt"));
    	assertEquals(testDir.list().length, 1);
    }
    
    @Test(expectedExceptions = IllegalStateException.class)
    public void testPrintingToFileNoWritingAllowedFailure() throws Exception {
    	addDescription("Test printing something to a file.");
    	DiffFiles diffMethod = new SimpleDiffFiles();
    	DiffOutputFormat outputFormat = DiffOutputFormat.OUTPUT_FORMAT_SUMMARY;
    	
    	Diff diff = new Diff(diffMethod, outputFormat, testDir);
    	
    	try {
    		testDir.setExecutable(false);
    		testDir.setWritable(false);
    		
        	diff.printToFile("This is the content", new File(testDir, Math.random() + ".txt"));
    	} finally {
    		testDir.setExecutable(true);
    		testDir.setWritable(true);
    	}
    }
    
    @Test(expectedExceptions = IllegalStateException.class)
    public void testPrintingToFileIsDirectoryFailure() throws Exception {
    	addDescription("Test printing something to a file.");
    	DiffFiles diffMethod = new SimpleDiffFiles();
    	DiffOutputFormat outputFormat = DiffOutputFormat.OUTPUT_FORMAT_SUMMARY;
    	
    	Diff diff = new Diff(diffMethod, outputFormat, testDir);
    	
    	File dir = new File(testDir, Math.random() + ".txt");
    	dir.mkdirs();
    	diff.printToFile("This is the content", dir);
    }
    
    @Test
    public void testMakeDiffFilenames1() {
    	DiffFiles diffMethod = new SimpleDiffFiles();
    	DiffOutputFormat outputFormat = DiffOutputFormat.OUTPUT_FORMAT_SUMMARY;
    	String filename1 = "test1-test1";
    	String filename2 = "test2-test2";
    	
    	Diff diff = new Diff(diffMethod, outputFormat, testDir);
    	String filename = diff.makeDiffFilename(filename1, filename2);
    	assertEquals(filename, "diff_test1_test1_test2");
    }
    
    @Test
    public void testMakeDiffFilenames2() {
    	DiffFiles diffMethod = new SimpleDiffFiles();
    	DiffOutputFormat outputFormat = DiffOutputFormat.OUTPUT_FORMAT_SUMMARY;
    	String filename1 = "test1?test1";
    	String filename2 = "test2-test2";
    	
    	Diff diff = new Diff(diffMethod, outputFormat, testDir);
    	String filename = diff.makeDiffFilename(filename1, filename2);
    	assertEquals(filename, "diff_" + filename1 + "_" + filename2);
    }
    
    @Test
    public void testMakeDiffFilenames3() {
    	DiffFiles diffMethod = new SimpleDiffFiles();
    	DiffOutputFormat outputFormat = DiffOutputFormat.OUTPUT_FORMAT_SUMMARY;
    	String filename1 = "test1-test1";
    	String filename2 = "test2?test2";
    	
    	Diff diff = new Diff(diffMethod, outputFormat, testDir);
    	String filename = diff.makeDiffFilename(filename1, filename2);
    	assertEquals(filename, "diff_" + filename1 + "_" + filename2);
    }
}
