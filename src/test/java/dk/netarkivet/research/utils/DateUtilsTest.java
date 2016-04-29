package dk.netarkivet.research.utils;

import static org.testng.Assert.assertNotNull;

import java.util.Date;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.annotations.Test;

public class DateUtilsTest extends ExtendedTestCase {
	
	@Test
	public void testCsvDateFormat() throws Exception {
		addDescription("Test ");
		Date d = DateUtils.extractCsvDate("2005-06-22T20:37:27Z");
		assertNotNull(d);
	}
}
