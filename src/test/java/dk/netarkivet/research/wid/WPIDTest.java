package dk.netarkivet.research.wid;

import static org.testng.Assert.*;

import java.util.Date;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.annotations.Test;

import dk.netarkivet.research.wid.WIDConstants;
import dk.netarkivet.research.wid.WPID;

public class WPIDTest extends ExtendedTestCase {

	@Test
	public void testWPID() throws Exception {
		addDescription("Test the WPID created through the constructor.");
		Date date = new Date();
		String url = "http://www.netarkivet.dk";
		String archive = "webarchive.net";
		WPID wpid = WPID.createWPid(archive, url, date);
		
		assertEquals(wpid.getDate(), date);
		assertEquals(wpid.getUrl(), url);
		assertEquals(wpid.getWebarchive(), archive);
	}
	
	@Test
	public void testWPIDForNAS() throws Exception {
		addDescription("Test the WPID created for the danish webarchive.");
		Date date = new Date();
		String url = "http://www.netarkivet.dk";
		WPID wpid = WPID.createNarkWPid(url, date);
		
		assertEquals(wpid.date, date);
		assertEquals(wpid.url, url);
		assertEquals(wpid.webarchive, WIDConstants.NETARCHIVE_DK_WEBARCHIVE);
	}
}
