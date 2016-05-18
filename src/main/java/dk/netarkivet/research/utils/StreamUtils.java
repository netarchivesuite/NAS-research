package dk.netarkivet.research.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Utility class for handling input/output streams.
 */
public class StreamUtils {

	/**
	 * Extracts the content of an input stream as text.
	 * @param is The input stream.
	 * @return The text content of the input stream.
	 * @throws IOException If it fails.
	 */
	public static String extractInputStreamAsText(InputStream is) throws IOException {
		StringBuilder res = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
			String line;
			while((line = br.readLine()) != null) {
				res.append(line);
			}
		}
		return res.toString();
	}
}
