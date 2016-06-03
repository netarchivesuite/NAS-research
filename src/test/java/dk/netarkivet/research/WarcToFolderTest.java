package dk.netarkivet.research;

import static org.testng.Assert.*;

import java.io.File;

import org.jaccept.structure.ExtendedTestCase;
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
	
	@Test
	public void testExtractingAWarcFile() throws Exception {
		WarcToFolder wtf = new WarcToFolder(warcFile, dir);
		
		wtf.extract();
		
		assertEquals(dir.list().length, 4);
		for(String f : dir.list()) {
			System.err.println(f);
		}
	}
}
