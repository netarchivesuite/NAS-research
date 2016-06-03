package dk.netarkivet.research.warc;

import static org.testng.Assert.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import dk.netarkivet.research.cdx.CDXEntry;
import dk.netarkivet.research.testutils.TestFileUtils;
import dk.netarkivet.research.utils.FileUtils;
import dk.netarkivet.research.wid.WIDConstants;
import dk.netarkivet.research.wid.WPID;

public class WarcPackerTest extends ExtendedTestCase {

	String baseDirPath = "test-dir";
	String outputDirPath = "outputDir";
	File baseDir;
	File outputDir;
	
	File contentFile;
	
	@BeforeMethod
	public void setupMethod() throws Exception {
		baseDir = new File(baseDirPath);
		if(baseDir.exists()) {
			TestFileUtils.removeFile(baseDir);
		}
		baseDir = FileUtils.createDir(baseDirPath);
		
		contentFile = TestFileUtils.createTestFile(baseDir, "test" + Math.random(), "This is the content of the file.");
		
		outputDir = FileUtils.createDir(baseDir.getAbsolutePath() + "/" + outputDirPath);
		assertEquals(outputDir.list().length, 0);
		assertEquals(baseDir.list().length, 2, Arrays.asList(baseDir.list()).toString());
	}
	
	@AfterMethod
	public void cleanUpMethod() throws Exception {
		TestFileUtils.removeFile(baseDir);
	}
	
	@Test
	public void testWarcPackerWithEmptyCDXList() throws Exception {
		addDescription("Test extraction of an empty list of CDX entries.");
		ArchiveExtractor ae = mock(ArchiveExtractor.class);
		WarcPacker wp = new WarcPacker(ae);
		wp.extractToWarc(new ArrayList<CDXEntry>(), outputDir);
		
		assertEquals(outputDir.list().length, 0);
		assertEquals(baseDir.list().length, 2, Arrays.asList(baseDir.list()).toString());
	}
	
	@Test
	public void testWarcPackerSuccessExtract() throws Exception {
		addDescription("Test successfull extraction from the ");
		
		ArchiveExtractor ae = mock(ArchiveExtractor.class);
		when(ae.extractWarcRecord(any(CDXEntry.class))).thenReturn(contentFile);

		CDXEntry cdxEntry = CDXEntry.createCDXEntry(new String[] {"http://netarkivet.dk", "20110101010101", "VJ3CKK3ZH2FR7V2KM5TSI3TENA7ZSWKM"}, new Character[] {'A', 'b', 'k'});
		
		WarcPacker wp = new WarcPacker(ae);
		wp.extractToWarc(Arrays.asList(cdxEntry), outputDir);
		
		assertEquals(outputDir.list().length, 1);
	}
}
