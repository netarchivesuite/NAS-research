package dk.netarkivet.research.utils;

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
		res.replaceAll(" ", "%20");
		res.replaceAll("/", "&2f");
		res.replaceAll("~", "%7e");
		res.replaceAll("`", "%60");
		res.replaceAll("!", "%21");
		res.replaceAll("@", "&40");
		res.replaceAll("#", "%23");
		res.replaceAll("$", "%24");
		res.replaceAll("^", "%5E");
		res.replaceAll("&", "%26");
		res.replaceAll("*", "&2A");
		res.replaceAll("(", "%28");
		res.replaceAll(")", "%29");
		res.replaceAll("\\", "%5C");
		res.replaceAll("|", "%7C");
		res.replaceAll("[", "%5B");
		res.replaceAll("]", "%5D");
		res.replaceAll("{", "%7B");
		res.replaceAll("}", "%7D");
		res.replaceAll(";", "%3B");
		res.replaceAll(":", "%3A");
		res.replaceAll("'", "%27");
		res.replaceAll("\"", "%22");
		res.replaceAll("<", "%3C");
		res.replaceAll(">", "%3E");
		res.replaceAll("/", "&2F");
		res.replaceAll("?", "%3F");
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
	
}
