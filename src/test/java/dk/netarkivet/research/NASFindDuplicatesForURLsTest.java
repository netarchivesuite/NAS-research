package dk.netarkivet.research;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.util.Collection;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import dk.netarkivet.research.cdx.CDXExtractor;
import dk.netarkivet.research.cdx.DabCDXExtractor;
import dk.netarkivet.research.duplicates.DuplicateExtractor;
import dk.netarkivet.research.harvestdb.HarvestJobExtractor;
import dk.netarkivet.research.harvestdb.HarvestJobInfo;
import dk.netarkivet.research.http.HttpRetriever;
import dk.netarkivet.research.testutils.TestFileUtils;
import dk.netarkivet.research.utils.UrlUtils;

public class NASFindDuplicatesForURLsTest extends ExtendedTestCase {

	String testUrl = "blog.tv2.dk/christiane.vejloe/";
	String cdxServerUrl = "http://localhost:8080/dab/query/";
	
	String cdxReply1 = "blog.tv2.dk/christiane.vejloe/ 20111208102716 http://blog.tv2.dk/christiane.vejloe/ no-type 302 3I42H3S6NNFQ2MSVX7XZKYAYSCX5QBYJ http://blog-dyn.tv2.dk/christiane.vejloe/ 61940000 3235-51-20111208093937-00029-sb-test-har-001.statsbiblioteket.dk.arc\n"
			+ "blog.tv2.dk/christiane.vejloe/ 20120104110941 http://blog.tv2.dk/christiane.vejloe/ no-type 302 3I42H3S6NNFQ2MSVX7XZKYAYSCX5QBYJ http://blog-dyn.tv2.dk/christiane.vejloe/ 30953638 3272-51-20120104104417-00024-kb-test-har-002.kb.dk.arc\n"
			+ "blog.tv2.dk/christiane.vejloe/ 20120104140909 http://blog.tv2.dk/christiane.vejloe/ text/html 200 WPO63F5VQVGIOARVALVMCRC3IXVUM2TH - 56267050 3270-51-20120104132734-00029-sb-test-har-001.statsbiblioteket.dk.arc\n"
			+ "blog.tv2.dk/christiane.vejloe/ 20120105072856 http://blog.tv2.dk/christiane.vejloe/ no-type 302 3I42H3S6NNFQ2MSVX7XZKYAYSCX5QBYJ http://blog-dyn.tv2.dk/christiane.vejloe/ 85965442 3274-51-20120105062123-00022-kb-test-har-001.kb.dk.arc\n";
	
	String cdxReply2 = "blog.tv2.dk/fjellvang.holst.vmd/ 20111205231043 http://blog.tv2.dk/fjellvang.holst.vmd/ text/html 200 IUJMZ42M732PNILUSMCT3DQS3OMZYMEY - 80671393 3234-51-20111205225700-00007-sb-test-har-001.statsbiblioteket.dk.arc\n"
			+ "blog.tv2.dk/fjellvang.holst.vmd/ 20111215031109 http://blog.tv2.dk/fjellvang.holst.vmd/ text/html 200 DLVJS2LUWVAYYMK5RBRP73NDPD3LS7XH - 75118712 3246-51-20111215020148-00008-kb-test-har-001.kb.dk.arc\n"
			+ "blog.tv2.dk/fjellvang.holst.vmd/ 20120101181139 http://blog.tv2.dk/fjellvang.holst.vmd/ no-type 302 3I42H3S6NNFQ2MSVX7XZKYAYSCX5QBYJ http://blog-dyn.tv2.dk/fjellvang.holst.vmd/ 69292814 3250-51-20120101175258-00006-sb-test-har-001.statsbiblioteket.dk.arc\n"
			+ "blog.tv2.dk/fjellvang.holst.vmd/ 20120101211315 http://blog.tv2.dk/fjellvang.holst.vmd/ no-type 302 3I42H3S6NNFQ2MSVX7XZKYAYSCX5QBYJ http://blog-dyn.tv2.dk/fjellvang.holst.vmd/ 75263751 3254-51-20120101205423-00006-sb-test-har-001.statsbiblioteket.dk.arc\n"
			+ "blog.tv2.dk/fjellvang.holst.vmd/ 20120102190203 http://blog.tv2.dk/fjellvang.holst.vmd/ text/html 200 DLVJS2LUWVAYYMK5RBRP73NDPD3LS7XH - 40365564 3258-51-20120102184705-00007-sb-test-har-001.statsbiblioteket.dk.arc\n"
			+ "blog.tv2.dk/fjellvang.holst.vmd/ 20120103005131 http://blog.tv2.dk/fjellvang.holst.vmd/ text/html 200 DLVJS2LUWVAYYMK5RBRP73NDPD3LS7XH - 9168380 3260-51-20120103004840-00007-sb-test-har-001.statsbiblioteket.dk.arc\n"
			+ "blog.tv2.dk/fjellvang.holst.vmd/ 20120104011333 http://blog.tv2.dk/fjellvang.holst.vmd/ no-type 302 3I42H3S6NNFQ2MSVX7XZKYAYSCX5QBYJ http://blog-dyn.tv2.dk/fjellvang.holst.vmd/ 89775004 3270-51-20120104005643-00006-sb-test-har-001.statsbiblioteket.dk.arc\n"
			+ "blog.tv2.dk/fjellvang.holst.vmd/ 20120104012312 http://blog.tv2.dk/fjellvang.holst.vmd/ no-type 302 3I42H3S6NNFQ2MSVX7XZKYAYSCX5QBYJ http://blog-dyn.tv2.dk/fjellvang.holst.vmd/ 59384173 3272-51-20120104002721-00005-kb-test-har-002.kb.dk.arc\n"
			+ "blog.tv2.dk/fjellvang.holst.vmd/ 20120104214046 http://blog.tv2.dk/fjellvang.holst.vmd/ text/html 200 HRCZFC76ZM2HRTKKK4WUUTPNAER4QBBS - 42228079 3274-51-20120104212744-00005-kb-test-har-001.kb.dk.arc";
	
	private File dir = new File("tempDir");
	private File outdir = new File(dir, "outdir");
	private File urlFile;
	
	@BeforeClass
	public void setup() throws Exception {
		TestFileUtils.removeFile(dir);
		dir.mkdirs();
		urlFile = TestFileUtils.createTestFile(dir, "X;" + testUrl + ";;;\n");
	}
	
	@BeforeMethod
	public void methodSetup() throws Exception {
		outdir.mkdir();		
	}
	
	@AfterMethod
	public void methodClean() throws Exception {
		TestFileUtils.removeFile(outdir);
	}
	
	@AfterClass
	public void tearDown() throws Exception {
		TestFileUtils.removeFile(dir);
	}
	
	@Test
	public void testDuplicateFinderMockWithNoReply() throws Exception {
		addDescription("Test the duplicate finder when the cdx-server does not deliver any cdx indices (for the request)");
		
		HttpRetriever httpRetriever = mock(HttpRetriever.class);
		when(httpRetriever.retrieveFromUrl(anyString())).thenReturn("");
		HarvestJobExtractor jobExtractor = mock(HarvestJobExtractor.class);
		
		CDXExtractor cdxExtractor = new DabCDXExtractor(cdxServerUrl, httpRetriever);
		DuplicateExtractor duplicateExtractor = new DuplicateExtractor(cdxExtractor, jobExtractor);
		ExtDuplicateFinder nasDupFinder = new ExtDuplicateFinder(duplicateExtractor, urlFile, outdir);
		nasDupFinder.findDuplicates();
		
		assertEquals(outdir.listFiles().length, 1);
		assertEquals(TestFileUtils.countNumberOfLines(outdir.listFiles()[0]), 1);
	}

	@Test
	public void testDuplicateFinderWithMock() throws Exception {
		addDescription("Test the actual duplicate finder.");
		
		HarvestJobInfo jobInfoTest = new HarvestJobInfo(3250L, "This is a test type", "DONE", "UnitTestJob");
		
		HttpRetriever httpRetriever = mock(HttpRetriever.class);
		when(httpRetriever.retrieveFromUrl(anyString())).thenReturn(cdxReply2);
		HarvestJobExtractor jobExtractor = mock(HarvestJobExtractor.class);
		
		when(jobExtractor.extractJob(eq(3250L))).thenReturn(jobInfoTest);

		CDXExtractor cdxExtractor = new DabCDXExtractor(cdxServerUrl, httpRetriever);
		DuplicateExtractor duplicateExtractor = new DuplicateExtractor(cdxExtractor, jobExtractor);
		ExtDuplicateFinder nasDupFinder = new ExtDuplicateFinder(duplicateExtractor, urlFile, outdir);
		nasDupFinder.findDuplicates();
		assertEquals(outdir.listFiles().length, 1);
		
		addStep("Validate the output format", "");
		File f = new File(outdir, UrlUtils.fileEncodeUrl(testUrl) + ".txt");
		assertTrue(f.isFile()); 

		Collection<String> lines = TestFileUtils.readFile(f);
		assertEquals(lines.size(), 10);
		
		for(String line : lines) {
			if(line.contains(jobInfoTest.getId().toString())) {
				System.err.println(line);
				assertTrue(line.contains(jobInfoTest.getName()));
				assertTrue(line.contains(jobInfoTest.getType()));
			} else {
				assertFalse(line.contains(jobInfoTest.getName()));
				assertFalse(line.contains(jobInfoTest.getType()));
			}
		}
	}
	
	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testMainBadFileArgument() throws Exception {
		addDescription("Test missing file as argument");
		File badTestFile = new File(dir, "ThisIsNotAFile" + Math.random());
		
		ExtDuplicateFinder.main(badTestFile.getAbsolutePath(), cdxServerUrl, outdir.getAbsolutePath());
	}
	
	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testMainCdxServerUrlArgument() throws Exception {
		addDescription("Test cdx-server-url argument, which is not an url");
		String badUrl = "ThisIsNotAnUrl";
		
		ExtDuplicateFinder.main(urlFile.getAbsolutePath(), badUrl, outdir.getAbsolutePath());
	}
	
	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testMainFileAsOutputDirecotryArgument() throws Exception {
		addDescription("Test that it fails, when using an file as output directory");
		
		ExtDuplicateFinder.main(urlFile.getAbsolutePath(), cdxServerUrl, urlFile.getAbsolutePath());
	}
		
	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testMainNotEnoughArguments() throws Exception {
		addDescription("Test that it fails, when not given enough arguments.");
		
		ExtDuplicateFinder.main(urlFile.getAbsolutePath());
	}
	
	@Test
	public void testRealData() throws Exception {
		addDescription("Test the duplicate finder on real data");
		File csvFile = new File("src/test/resources/duplicates.csv");
		
		HttpRetriever httpRetriever = mock(HttpRetriever.class);
		when(httpRetriever.retrieveFromUrl(anyString())).thenReturn("");
		HarvestJobExtractor jobExtractor = mock(HarvestJobExtractor.class);

		CDXExtractor cdxExtractor = new DabCDXExtractor(cdxServerUrl, httpRetriever);
		DuplicateExtractor duplicateExtractor = new DuplicateExtractor(cdxExtractor, jobExtractor);
		ExtDuplicateFinder nasDupFinder = new ExtDuplicateFinder(duplicateExtractor, csvFile, outdir);
		nasDupFinder.findDuplicates();
		
		assertEquals(outdir.listFiles().length, 4);
		for(int i = 0; i < outdir.listFiles().length; i++) {
			assertEquals(TestFileUtils.countNumberOfLines(outdir.listFiles()[i]), 1);
		}
		
		verify(httpRetriever, times(4)).retrieveFromUrl(anyString());
	}

}
