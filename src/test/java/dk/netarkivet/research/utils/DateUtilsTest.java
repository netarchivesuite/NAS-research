package dk.netarkivet.research.utils;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertEquals;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.annotations.Test;

public class DateUtilsTest extends ExtendedTestCase {
	
	@Test
	public void testCsvDateFormat() throws Exception {
		addDescription("Test extracting the expected CSV format");
		Date d = DateUtils.extractCsvDate("2005-06-22T20:37:27Z");
		assertNotNull(d);
	}
	
	@Test
	public void testExtractCsvNull() throws Exception {
		addDescription("Test extracting a date from a null.");
		Date d = DateUtils.extractCsvDate(null);
		assertNull(d);
	}
	
	@Test
	public void testExtractCsvEmptyString() throws Exception {
		addDescription("Test extracting a date from an empty string.");
		Date d = DateUtils.extractCsvDate("");
		assertNull(d);
	}
	
	@Test
	public void testExtractCsvSpacesString() throws Exception {
		addDescription("Test extracting a date from a string only containing spaces.");
		Date d = DateUtils.extractCsvDate("               ");
		assertNull(d);
	}
	
	@Test
	public void testNonCsvSystemDefaultDateFormat() throws Exception {
		addDescription("Tests the system default format, which is not corresponding to the csv-format.");
		
		String d1 = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.ENGLISH).format(new Date(0));
		Date d = DateUtils.extractCsvDate(d1);
		assertNotNull(d);
		assertEquals(d.getTime(), 0);
	}
	
	@Test
	public void testGarbageFormat() throws Exception {
		addDescription("Test a garbage date input");
		Date d = DateUtils.extractCsvDate("Whenever");
		assertNull(d);
	}
}
