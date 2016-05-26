package dk.netarkivet.research.cdx;

import static org.testng.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.annotations.Test;

public class CDXConstantsTest extends ExtendedTestCase {

	@Test
	public void testConstructor() {
		addDescription("Test the instantiation of the CDX constants class.");
		new CDXConstants();
	}
	
	@Test
	public void testDefaultCDXFormat() {
		addDescription("Test the elements in the default CDX format");
		List<Character> defaultFormat = Arrays.asList(CDXConstants.DEFAULT_CDX_CHAR_FORMAT);
		
		assertTrue(defaultFormat.contains(CDXConstants.CDX_CHAR_ORIGINAL_URL));
		assertTrue(defaultFormat.contains(CDXConstants.CDX_CHAR_DATE));
		assertTrue(defaultFormat.contains(CDXConstants.CDX_CHAR_FILE_NAME));
		assertTrue(defaultFormat.contains(CDXConstants.CDX_CHAR_COMPRESSED_ARC_FILE_OFFSET));
		assertTrue(defaultFormat.contains(CDXConstants.CDX_CHAR_MIME_TYPE));
		assertTrue(defaultFormat.contains(CDXConstants.CDX_CHAR_RESPONSE_CODE));
		assertTrue(defaultFormat.contains(CDXConstants.CDX_CHAR_NEW_STYLE_CHECKSUM));
	}
}
