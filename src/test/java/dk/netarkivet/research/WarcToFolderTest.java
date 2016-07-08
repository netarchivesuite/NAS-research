package dk.netarkivet.research;

import static org.testng.Assert.assertEquals;

import java.io.File;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import dk.netarkivet.research.testutils.TestFileUtils;

public class WarcToFolderTest extends ExtendedTestCase {

	private File dir = new File("tempDir");
	private File warcFile = new File("src/test/resources/test.warc");
	
	@BeforeClass
	public void setup() throws Exception {
		TestFileUtils.removeFile(dir);
		dir.mkdirs();
	}
	
	@BeforeMethod
	public void methodSetup() throws Exception {
		dir.mkdir();		
	}
	
	@AfterMethod
	public void tearDown() throws Exception {
		TestFileUtils.removeFile(dir);
	}
	
	@Test
	public void testExtractingAWarcFile() throws Exception {
		ExtWarcUnfolder wtf = new ExtWarcUnfolder(warcFile, dir);
		
		wtf.extract();
		
		assertEquals(dir.list().length, 4);
	}
	
	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testMainTooFewArgumentsFailure() throws Exception {
		addDescription("Test the main function with too few arguments (e.g. no arguments).");
		ExtWarcUnfolder.main();
	}
	
	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testMainBadWarcFileFailure() throws Exception {
		addDescription("Test the main function with a non-existing file as argument.");
		ExtWarcUnfolder.main("TestFile" + Math.random());
	}
	
	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testMainBadOutputDirFailure() throws Exception {
		addDescription("Test the main function with the path to output directory being the path to a file.");
		ExtWarcUnfolder.main(warcFile.getAbsolutePath(), warcFile.getAbsolutePath());
	}
}
