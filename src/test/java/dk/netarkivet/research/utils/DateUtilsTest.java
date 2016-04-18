package dk.netarkivet.research.utils;

import static org.testng.Assert.*;

import java.io.File;
import java.util.Collection;
import java.util.Date;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.annotations.Test;

public class DateUtilsTest extends ExtendedTestCase {

	File extractedCsv = new File("src/test/resources/urls.csv");
	
	@Test
	public void testCsvDateFormat() throws Exception {
		addDescription("Test ");
		Date d = DateUtils.extractCsvDate("2005-06-22T20:37:27Z");
		assertNotNull(d);
	}
}
