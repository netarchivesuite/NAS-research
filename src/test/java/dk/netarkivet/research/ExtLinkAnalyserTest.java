package dk.netarkivet.research;

import static org.testng.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import dk.netarkivet.research.cdx.CDXExtractor;
import dk.netarkivet.research.cdx.DabCDXExtractor;
import dk.netarkivet.research.harvestdb.HarvestJobExtractor;
import dk.netarkivet.research.http.HttpRetriever;
import dk.netarkivet.research.testutils.TestFileUtils;
import dk.netarkivet.research.utils.FileUtils;

public class ExtLinkAnalyserTest extends ExtendedTestCase {

	private File dir = new File("tempDir");
//	private File warcFile = new File("src/test/resources/CDX-EXTRACT-20160531153934-00000-kb-prod-acs-02.warc");
	private File warcFile = new File("src/test/resources/test.warc");
	
	String cdxServerUrl = "http://localhost:8080/dab/query/";
	
	String cdxReply1 = "blog.tv2.dk/christiane.vejloe/ 20111208102716 http://blog.tv2.dk/christiane.vejloe/ no-type 302 3I42H3S6NNFQ2MSVX7XZKYAYSCX5QBYJ http://blog-dyn.tv2.dk/christiane.vejloe/ 61940000 3235-51-20111208093937-00029-sb-test-har-001.statsbiblioteket.dk.arc\n"
			+ "blog.tv2.dk/christiane.vejloe/ 20120104110941 http://blog.tv2.dk/christiane.vejloe/ no-type 302 3I42H3S6NNFQ2MSVX7XZKYAYSCX5QBYJ http://blog-dyn.tv2.dk/christiane.vejloe/ 30953638 3272-51-20120104104417-00024-kb-test-har-002.kb.dk.arc\n"
			+ "blog.tv2.dk/christiane.vejloe/ 20120104140909 http://blog.tv2.dk/christiane.vejloe/ text/html 200 WPO63F5VQVGIOARVALVMCRC3IXVUM2TH - 56267050 3270-51-20120104132734-00029-sb-test-har-001.statsbiblioteket.dk.arc\n"
			+ "blog.tv2.dk/christiane.vejloe/ 20120105072856 http://blog.tv2.dk/christiane.vejloe/ no-type 302 3I42H3S6NNFQ2MSVX7XZKYAYSCX5QBYJ http://blog-dyn.tv2.dk/christiane.vejloe/ 85965442 3274-51-20120105062123-00022-kb-test-har-001.kb.dk.arc\n";

	
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
	public void testExtractingLinksFromFile() throws Exception {
		File outFile = new File(dir, "out" + Math.random() + ".txt");
		
		//next two lines are mockup of HttpRetriever
		HttpRetriever httpRetriever = mock(HttpRetriever.class);
		when(httpRetriever.retrieveFromUrl(anyString())).thenReturn(cdxReply1);

		CDXExtractor cdxExtractor = new DabCDXExtractor(cdxServerUrl, httpRetriever);

		ExtLinkAnalyser analyser = new ExtLinkAnalyser(cdxExtractor);

		analyser.analyseWarcFile(warcFile, outFile);
		
		assertEquals(dir.list().length, 1);
		assertEquals(TestFileUtils.countNumberOfLines(dir.listFiles()[0]), 22);
	}
	
}
