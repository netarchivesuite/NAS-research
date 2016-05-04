package dk.netarkivet.research;

import static org.testng.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

import java.io.File;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import dk.netarkivet.research.cdx.CDXExtractor;
import dk.netarkivet.research.cdx.DabCDXExtractor;
import dk.netarkivet.research.duplicates.DuplicateExtractor;
import dk.netarkivet.research.http.HttpRetriever;
import dk.netarkivet.research.testutils.TestFileUtils;

public class NASFindDuplicatesForURLsTest extends ExtendedTestCase {

	String testUrl = "blog.tv2.dk/christiane.vejloe/";
	String cdxServerUrl = "http://localhost:8080/dab/query/";
	
	String cdxReply = "blog.tv2.dk/christiane.vejloe/ 20111208102716 http://blog.tv2.dk/christiane.vejloe/ no-type 302 3I42H3S6NNFQ2MSVX7XZKYAYSCX5QBYJ http://blog-dyn.tv2.dk/christiane.vejloe/ 61940000 3235-51-20111208093937-00029-sb-test-har-001.statsbiblioteket.dk.arc\n"
			+ "blog.tv2.dk/christiane.vejloe/ 20120104110941 http://blog.tv2.dk/christiane.vejloe/ no-type 302 3I42H3S6NNFQ2MSVX7XZKYAYSCX5QBYJ http://blog-dyn.tv2.dk/christiane.vejloe/ 30953638 3272-51-20120104104417-00024-kb-test-har-002.kb.dk.arc\n"
			+ "blog.tv2.dk/christiane.vejloe/ 20120104140909 http://blog.tv2.dk/christiane.vejloe/ text/html 200 WPO63F5VQVGIOARVALVMCRC3IXVUM2TH - 56267050 3270-51-20120104132734-00029-sb-test-har-001.statsbiblioteket.dk.arc\n"
			+ "blog.tv2.dk/christiane.vejloe/ 20120105072856 http://blog.tv2.dk/christiane.vejloe/ no-type 302 3I42H3S6NNFQ2MSVX7XZKYAYSCX5QBYJ http://blog-dyn.tv2.dk/christiane.vejloe/ 85965442 3274-51-20120105062123-00022-kb-test-har-001.kb.dk.arc\n";
	
	private File dir = new File("tempDir");
	private File urlFile;
	
	@BeforeClass
	public void setup() throws Exception {
		TestFileUtils.removeFile(dir);
		dir.mkdirs();
		urlFile = TestFileUtils.createTestFile(dir, testUrl + "\n");
	}
	
	@AfterClass
	public void tearDown() throws Exception {
		TestFileUtils.removeFile(dir);
	}
	
//	@Test
//	public void testDuplicateFinderMain() throws Exception {
//		addDescription("Test the actual duplicate finder.");
//		
//		NASFindDuplicatesForURLs.main(urlFile.getAbsolutePath(), cdxServerUrl, dir.getAbsolutePath());
//		
//		assertEquals(dir.listFiles().length, 3);
//	}

	@Test
	public void testDuplicateFinderWithMock() throws Exception {
		addDescription("Test the actual duplicate finder.");
		
		HttpRetriever httpRetriever = mock(HttpRetriever.class);
		when(httpRetriever.retrieveFromUrl(anyString())).thenReturn(cdxReply);
		
		CDXExtractor cdxExtractor = new DabCDXExtractor(cdxServerUrl, httpRetriever);
		DuplicateExtractor duplicateExtractor = new DuplicateExtractor(cdxExtractor);
		NASFindDuplicatesForURLs nasDupFinder = new NASFindDuplicatesForURLs(duplicateExtractor, urlFile, dir);
		nasDupFinder.findDuplicates();
		
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
