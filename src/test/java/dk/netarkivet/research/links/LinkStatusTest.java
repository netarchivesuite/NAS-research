package dk.netarkivet.research.links;

import static org.testng.Assert.assertEquals;

import java.util.Date;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.annotations.Test;

import dk.netarkivet.research.utils.DateUtils;

public class LinkStatusTest extends ExtendedTestCase {
	
	@Test
	public void testLinkStatus() throws Exception {
		addDescription("Test the link status");
		boolean found = true;
		String url = "http://netarkivet.dk";
		Date date = DateUtils.waybackDateToDate("20140710050626");
		String referralUrl = "http://netarkivet.dk/link";
		Date referralDate = new Date(1234567890);
		LinkStatus ls = new LinkStatus(found, url, date, referralUrl, referralDate, "test");
		
		assertEquals(found, ls.isFound());
		assertEquals(url, ls.getLinkUrl());
		assertEquals(date.getTime(), ls.getLinkDate().getTime());
		assertEquals(referralUrl, ls.getReferralUrl());
		assertEquals(referralDate.getTime(), ls.getReferralDate().getTime());
	}
}
