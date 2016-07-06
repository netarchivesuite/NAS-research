package dk.netarkivet.research;

import static org.testng.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import java.io.File;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import dk.netarkivet.research.ExtractMetadata.InputFormat;
import dk.netarkivet.research.ExtractMetadata.OutputFormat;
import dk.netarkivet.research.cdx.CDXExtractor;
import dk.netarkivet.research.harvestdb.HarvestJobExtractor;
import dk.netarkivet.research.testutils.TestFileUtils;
import dk.netarkivet.research.utils.FileUtils;

public class ExtractMetadataTest extends ExtendedTestCase {

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
	
	private File csvFile = new File("src/test/resources/duplicates.csv");
	
	@BeforeClass
	public void setup() throws Exception {
		TestFileUtils.removeFile(dir);
		dir.mkdirs();
//		urlFile = TestFileUtils.createTestFile(dir, "X;" + testUrl + ";;;\n");
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
	public void testSuccess() throws Exception {
		
		CDXExtractor cdxExtractor = mock(CDXExtractor.class);
		HarvestJobExtractor jobExtractor = mock(HarvestJobExtractor.class);
		File outFile = new File(outdir, "output-" + Math.random() + ".txt");
		
		ExtractMetadata em = new ExtractMetadata(csvFile, cdxExtractor, jobExtractor, outFile);
		
		em.extractMetadata(InputFormat.INPUT_FORMAT_URL_INTERVAL, OutputFormat.EXPORT_FORMAT_CDX);
	}
	
	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testArgumentFailureTooFewArguments() throws Exception {
		addDescription("Test using too few arguments in the main");
		ExtractMetadata.main("arg1", "arg2");
	}
	
	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testArgumentFailureCsvFile() throws Exception {
		addDescription("Test that it fails when giving an non-existing argument");
		File nonExistingFile = new File(dir, "test+" + Math.random() + ".csv");
		ExtractMetadata.main(nonExistingFile.getAbsolutePath(), "arg2", "arg3", "arg4", "arg5", "arg6");
	}
	
	
	@Test
	public void testExtractOutputFormatWithEmptyArgument() {
		addDescription("Testing extracting the output format, when giving an empty argument");
		ExtractMetadata.OutputFormat outputFormat = ExtractMetadata.extractOutputFormat("");
		assertEquals(outputFormat, ExtractMetadata.OutputFormat.EXPORT_FORMAT_CSV);
	}
	
	@Test
	public void testExtractOutputFormatForCDX() {
		addDescription("Testing extracting the output format, when argument is 'CDX'");
		ExtractMetadata.OutputFormat outputFormat = ExtractMetadata.extractOutputFormat("CDX");
		assertEquals(outputFormat, ExtractMetadata.OutputFormat.EXPORT_FORMAT_CDX);
	}
	
	@Test
	public void testExtractOutputFormatForCSV() {
		addDescription("Testing extracting the output format, when argument is 'CSV'");
		ExtractMetadata.OutputFormat outputFormat = ExtractMetadata.extractOutputFormat("CSV");
		assertEquals(outputFormat, ExtractMetadata.OutputFormat.EXPORT_FORMAT_CSV);
	}
	
	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testExtractOutputFormatWithInvalidArgument() {
		addDescription("Testing extracting the output format, when giving an invalid argument");
		ExtractMetadata.extractOutputFormat("ThisIsNotOneOfTheOutputFormats");
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testExtractInputFormatWithEmptyArgument() {
		addDescription("Testing extracting the input format, when giving an empty argument");
		ExtractMetadata.extractInputFormat("");
	}
	
	@Test
	public void testExtractInputFormatForCDX() {
		addDescription("Testing extracting the input format, when argument is 'WID'");
		ExtractMetadata.InputFormat inputFormat = ExtractMetadata.extractInputFormat("WID");
		assertEquals(inputFormat, ExtractMetadata.InputFormat.INPUT_FORMAT_WID);
	}
	
	@Test
	public void testExtractInputFormatForCSV() {
		addDescription("Testing extracting the input format, when argument is 'URL'");
		ExtractMetadata.InputFormat inputFormat = ExtractMetadata.extractInputFormat("URL");
		assertEquals(inputFormat, ExtractMetadata.InputFormat.INPUT_FORMAT_URL_INTERVAL);
	}
	
	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testExtractInputFormatWithInvalidArgument() {
		addDescription("Testing extracting the input format, when giving an invalid argument");
		ExtractMetadata.extractInputFormat("ThisIsNotOneOfTheOutputFormats");
	}

}
