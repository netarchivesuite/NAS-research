package dk.netarkivet.research.batch;

import static org.testng.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;

import org.archive.extract.ExtractorOutput;
import org.archive.extract.WATExtractorOutput;
import org.archive.resource.Resource;
import org.archive.resource.ResourceProducer;
import org.archive.resource.producer.WARCFile;
import org.jaccept.structure.ExtendedTestCase;
import org.testng.SkipException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import dk.netarkivet.research.testutils.TestFileUtils;

public class WatBatchJobTest extends ExtendedTestCase {
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
	public void testStuff() throws Exception {
		addDescription("Testing stuff");
		
		File outFile = new File(dir, "WAT-output-" + Math.random() + ".wat.gz");
		outFile.createNewFile();
		
		WARCFile wf = new WARCFile();
	    wf.setStrict(true);
	    ResourceProducer producer = wf.getResourceProducer(warcFile,0L);
	    ExtractorOutput out = new WATExtractorOutput(new FileOutputStream(outFile)); //, outFile.getAbsolutePath());
	    Resource resource;
	    while((resource = producer.getNext()) != null) {
	    	out.output(resource);
	    }
	    
	    assertTrue(outFile.isFile());
//	    assertEquals(outFile.length(), 2427);
	}
}
