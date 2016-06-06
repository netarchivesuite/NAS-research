package dk.netarkivet.research;

import static org.testng.Assert.*;

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
//	private File warcFile = new File("/home/jolf/research-output/CDX-EXTRACT-20160531153934-00000-kb-prod-acs-02.warc");
//	private File warcFile = new File("/home/jolf/research-output/CDX-EXTRACT2.warc");
	
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
		WarcToFolder wtf = new WarcToFolder(warcFile, dir);
		
		wtf.extract();
		
		assertEquals(dir.list().length, 4);
	}
}
