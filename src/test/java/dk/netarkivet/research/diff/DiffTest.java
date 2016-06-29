package dk.netarkivet.research.diff;

import static org.testng.Assert.assertEquals;

import java.io.File;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import dk.netarkivet.research.testutils.TestFileUtils;
import dk.netarkivet.research.utils.FileUtils;

public class DiffTest extends ExtendedTestCase {
	File testDir = new File("tempDir");
	
	@BeforeClass
	public void setupClass() throws Exception {
		if(testDir.exists()) {
			TestFileUtils.removeFile(testDir);
		}
		testDir = FileUtils.createDir(testDir.getAbsolutePath());
	}
	
	@AfterClass
	public void tearDownClass() throws Exception {
//		TestFileUtils.removeFile(testDir);
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
}
