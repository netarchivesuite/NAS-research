package dk.netarkivet.research.utils;

import java.net.URL;

import dk.netarkivet.research.exception.ArgumentCheck;

/**
 * Utility methods for dealing with URLs.
 */
public class UrlUtils {

	/**
	 * Encoding an URL into a format, which can be used as a file-name.
	 * @param url The URL.
	 * @return The filename encoded URL.
	 */
	public static String fileEncodeUrl(String url) {
		String res = stripProtocol(url);
		res = res.replaceAll(" ", "%20");
		res = res.replaceAll("/", "%2F");
		res = res.replaceAll("~", "%7E");
		res = res.replaceAll("`", "%60");
		res = res.replaceAll("!", "%21");
		res = res.replaceAll("@", "%40");
		res = res.replaceAll("#", "%23");
		res = res.replaceAll("[$]", "%24");
		res = res.replaceAll("[\\^]", "%5E");
		res = res.replaceAll("[&]", "%26");
		res = res.replaceAll("[*]", "%2A");
		res = res.replaceAll("\\(", "%28");
		res = res.replaceAll("\\)", "%29");
		res = res.replaceAll("\\\\", "%5C");
		res = res.replaceAll("[|]", "%7C");
		res = res.replaceAll("\\[", "%5B");
		res = res.replaceAll("\\]", "%5D");
		res = res.replaceAll("\\{", "%7B");
		res = res.replaceAll("\\}", "%7D");
		res = res.replaceAll(";", "%3B");
		res = res.replaceAll(":", "%3A");
		res = res.replaceAll("'", "%27");
		res = res.replaceAll("\"", "%22");
		res = res.replaceAll("<", "%3C");
		res = res.replaceAll(">", "%3E");
		res = res.replaceAll("[?]", "%3F");
		return res;
	}
	
	/**
	 * Strips both the protocol and the wwww from the URL.
	 * Will also handle www with numbers, e.g. http://www1.test.com will become test.com
	 * @param url The URL to strip.
	 * @return The stripped URL.
	 */
	public static String stripProtocolAndWWW(String url) {
		String res = stripProtocol(url);
		if(res.startsWith("www")) {
			res = res.replaceFirst("www[0-9]*[\\.]", "");
		}
		return res;
	}
	
	/**
	 * Strips the protocol from the URL.
	 * E.g. http://example.com would become example.com
	 * @param url The URL to strip the protocol from.
	 * @return The URL without the protocol.
	 */
	public static String stripProtocol(String url) {
		String res = url;
		if(res.contains("://")) {
			res = res.split("://")[1];
		}
		return res;
	}
	
	/**
	 * Extracts the base-url from an url.
	 * The base URL, is the reference point in an HTML page, and used for relative links.
	 * 
	 * 
	 * E.g. the following url  | base reference:
	 *  http://WebReference.com/	http://WebReference.com/
	 *  http://WebReference.com/html/	http://WebReference.com/html/
	 *  http://WebReference.com/html/about.html	http://WebReference.com/html/
	 *  http://WebReference.com/foo/bar.html?baz	http://WebReference.com/foo/
	 *  
	 * @param contentUrl The content URL whose base URL should be extracted.
	 * @return The base URL.
	 */
	public static String getBaseUrl(URL contentUrl) {
		ArgumentCheck.checkNotNull(contentUrl, "URL contentUrl");
		
		String url = contentUrl.toExternalForm();
		if(url.endsWith("/")) {
			return url;
		}
		if(!url.replace("http://", "").contains("/")) {
			return url;
		}
		int lastSlash = url.lastIndexOf("/");
		return url.substring(0, lastSlash+1);
	}
}
