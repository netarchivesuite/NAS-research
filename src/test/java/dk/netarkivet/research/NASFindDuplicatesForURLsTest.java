package dk.netarkivet.research;

import static org.testng.Assert.assertEquals;

import java.io.File;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import dk.netarkivet.research.testutils.TestFileUtils;

public class NASFindDuplicatesForURLsTest extends ExtendedTestCase {

	String testUrl = "blog.tv2.dk/christiane.vejloe/";
	String cdxServerUrl = "http://localhost:8080/dab/query/";
	
	private File dir = new File("tempDir");
	private File urlFile;
	
	@BeforeClass
	public void setup() throws Exception {
		TestFileUtils.removeFile(dir);
		dir.mkdirs();
		urlFile = TestFileUtils.createTestFile(dir, testUrl + "\n");
	}
	
//	@AfterClass
//	public void tearDown() throws Exception {
//		TestFileUtils.removeFile(dir);
//	}
	
	@Test
	public void testDuplicateFinder() throws Exception {
		addDescription("Test the actual duplicate finder.");
		
		NASFindDuplicatesForURLs.main(urlFile.getAbsolutePath(), cdxServerUrl, dir.getAbsolutePath());
		
		assertEquals(dir.listFiles().length, 3);
	}
	
	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testBadFileArgument() throws Exception {
		addDescription("Test missing file as argument");
		File badTestFile = new File(dir, "ThisIsNotAFile" + Math.random());
		
		NASFindDuplicatesForURLs.main(badTestFile.getAbsolutePath(), cdxServerUrl, dir.getAbsolutePath());
	}
	
	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testCdxServerUrlArgument() throws Exception {
		addDescription("Test cdx-server-url argument, which is not an url");
		String badUrl = "ThisIsNotAnUrl";
		
		NASFindDuplicatesForURLs.main(urlFile.getAbsolutePath(), badUrl, dir.getAbsolutePath());
	}
	
	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testFileAsOutputDirecotryArgument() throws Exception {
		addDescription("Test that it fails, when using an file as output directory");
		
		NASFindDuplicatesForURLs.main(urlFile.getAbsolutePath(), cdxServerUrl, urlFile.getAbsolutePath());
	}
		
	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testNotEnoughArguments() throws Exception {
		addDescription("Test that it fails, when not given enough arguments.");
		
		NASFindDuplicatesForURLs.main(urlFile.getAbsolutePath());
	}

}
