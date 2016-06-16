package dk.netarkivet.research.interval;

import static org.testng.Assert.assertEquals;

import java.util.Date;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.annotations.Test;

import dk.netarkivet.research.utils.DateUtils;

public class UrlIntervalTest extends ExtendedTestCase {
	
	@Test
	public void testUrlInterval() throws Exception {
		addDescription("Testing creation of an UrlInterval and extraction of the data.");
		String url  = "http://netarkivet.dk";
		Date earliestDate = DateUtils.waybackDateToDate("20110101010101");
		Date latestDate = DateUtils.waybackDateToDate("20120202020202");
		UrlInterval urlInterval = new UrlInterval(url, earliestDate, latestDate);
		
		assertEquals(urlInterval.getUrl(), url);
		assertEquals(urlInterval.getEarliestDate().getTime(), earliestDate.getTime());
		assertEquals(urlInterval.getLatestDate().getTime(), latestDate.getTime());
	}
}

