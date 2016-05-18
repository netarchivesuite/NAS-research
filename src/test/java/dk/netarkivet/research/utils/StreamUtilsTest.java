package dk.netarkivet.research.utils;

import static org.testng.Assert.assertEquals;

import java.io.ByteArrayInputStream;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.annotations.Test;

public class StreamUtilsTest extends ExtendedTestCase {

	
	@Test
	public void testInstantiation() {
		addDescription("Test the instantaion of the stream utility class");
		new StreamUtils();
	}
	
	@Test
	public void testExtractingInputStreamAsString() throws Exception {
		addDescription("Test extracting a buffered text input stream as a string.");
		String in = "This is the expected input stream";
		String out = StreamUtils.extractInputStreamAsText(new ByteArrayInputStream(in.getBytes()));
		
		assertEquals(in, out);
	}
}
