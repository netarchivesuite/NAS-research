package dk.netarkivet.research.utils;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.net.URL;

import static org.testng.Assert.assertFalse;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.annotations.Test;

import dk.netarkivet.research.exception.ArgumentCheck;

public class UrlUtilsTest extends ExtendedTestCase {

	private final String endUrl = "netarkivet.dk";
	
	@Test
	public void testInstantiation() {
		addDescription("Test the instantaion of the url utility class");
		new UrlUtils();
	}
	
	@Test
	public void testStrippingNoProtocol() throws Exception {
		addDescription("Test stripping the http protocol");
		String url = endUrl;
		assertEquals(endUrl, UrlUtils.stripProtocol(url));
	}
	
	@Test
	public void testStrippingHttpProtocol() throws Exception {
		addDescription("Test stripping the http protocol");
		String url = "http://" + endUrl;
		assertEquals(endUrl, UrlUtils.stripProtocol(url));
	}

	@Test
	public void testStrippingHttpsProtocol() throws Exception {
		addDescription("Test stripping the http protocol");
		String url = "https://" + endUrl;
		assertEquals(endUrl, UrlUtils.stripProtocol(url));
	}
	
	@Test
	public void testStrippingFtpProtocol() throws Exception {
		addDescription("Test stripping the http protocol");
		String url = "Ftp://" + endUrl;
		assertEquals(endUrl, UrlUtils.stripProtocol(url));
	}

	@Test
	public void testFilenameEncoding() throws Exception {
		addDescription("Tests that all bad filename characters from an url are encoded.");
		String badCharacters = " ~`!@#$^&*()\\|[]{};:'<>/?";
		String url = "asdfghjklæøqwertyuiopåzxcvbnm" + badCharacters
				+ "0987654321ABCDEFGHIJKLMNOPQRSTUVWXYZÆØÅ";
		String filename = UrlUtils.fileEncodeUrl(url);
		
		for(int i = 0; i < badCharacters.length(); i++) {
			String c = badCharacters.substring(i, i+1);
			assertTrue(url.contains(c), c);
			assertFalse(filename.contains(c), "'" + c + "' in '" + filename + "'");
		}
	}
	
	@Test
	public void testBaseUrlForDomain() throws Exception {
		addDescription("Test extraction of the base URL from a domain url.");
		String url = "http://netarkivet.dk";
		String baseUrl = UrlUtils.getBaseUrl(new URL(url));
		assertEquals(baseUrl, url);
	}
	
	@Test
	public void testBaseUrlForDomainWithSlash() throws Exception {
		addDescription("Test extraction of the base URL from a domain url ending with an slash.");
		String url = "http://netarkivet.dk/";
		String baseUrl = UrlUtils.getBaseUrl(new URL(url));
		assertEquals(baseUrl, url);
	}
	
	@Test
	public void testBaseUrlForPathWithSlash() throws Exception {
		addDescription("Test extraction of the base URL from an url with a path and ending on a slash.");
		String url = "http://netarkivet.dk/test/";
		String baseUrl = UrlUtils.getBaseUrl(new URL(url));
		assertEquals(baseUrl, url);
	}
	
	@Test
	public void testBaseUrlForPathNoSlashEnd() throws Exception {
		addDescription("Test extraction of the base URL from an url with a path, but not ending on a slash.");
		String expectedBaseUrl = "http://netarkivet.dk/test/";
		String url = expectedBaseUrl + "test.html";
		String baseUrl = UrlUtils.getBaseUrl(new URL(url));
		assertEquals(baseUrl, expectedBaseUrl);
	}
	
	@Test(expectedExceptions = ArgumentCheck.class)
	public void testBaseUrlWithNullArgument() throws Exception {
		addDescription("Test extraction of the base URL from a null. Should throw exception");
		UrlUtils.getBaseUrl(null);
	}
}
