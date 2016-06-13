package dk.netarkivet.research.warc;

import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import dk.netarkivet.common.distribute.arcrepository.BitarchiveRecord;
import dk.netarkivet.common.distribute.arcrepository.ViewerArcRepositoryClient;
import dk.netarkivet.research.cdx.CDXEntry;

public class NasArchiveExtractorTest extends ExtendedTestCase {
	
	
	CDXEntry entry;
	
	@BeforeMethod
	public void setupMethod() {
		entry = CDXEntry.createCDXEntry(new String[] {"http://netarkivet.dk", "20110101010101", "VJ3CKK3ZH2FR7V2KM5TSI3TENA7ZSWKM"}, new Character[] {'A', 'b', 'k'});
	}
	
	@Test
	public void testExtractionWithEmptyResult() throws Exception {
		addDescription("Test extraction when then archive does not provide a file");
		
		ViewerArcRepositoryClient client = mock(ViewerArcRepositoryClient.class);
		when(client.get(anyString(), anyLong())).thenReturn(null);
		NASArchiveExtractor nae = new NASArchiveExtractor(client);
		File f = nae.extractWarcRecord(entry);
		
		assertNull(f);
	}
	
	@Test
	public void testExtractionWithWarcFile() throws Exception {
		addDescription("Test extraction when then archive does gives a inputstream.");
		String content = "This is the content";
		
		ViewerArcRepositoryClient client = mock(ViewerArcRepositoryClient.class);
		BitarchiveRecord br = mock(BitarchiveRecord.class);
		when(br.getData()).thenReturn(new ByteArrayInputStream(content.getBytes()));
		when(client.get(anyString(), anyLong())).thenReturn(br);
		NASArchiveExtractor nae = new NASArchiveExtractor(client);
		File f = nae.extractWarcRecord(entry);
		
		assertEquals(f.length(), content.length());
	}

	
	@Test(expectedExceptions = IOException.class)
	public void testWarcExtractorWithIncorrectFile() throws Exception {
		addDescription("Test extraction when the archive delivers an error.");
		ViewerArcRepositoryClient client = mock(ViewerArcRepositoryClient.class);
		when(client.get(anyString(), anyLong())).thenThrow(new RuntimeException("This is supposed to fail."));
		NASArchiveExtractor nae = new NASArchiveExtractor(client);
		
		nae.extractWarcRecord(entry);
	}
	
}
