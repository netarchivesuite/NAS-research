package dk.netarkivet.research.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;

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
		try (BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.defaultCharset()))) {
			String line;
			while((line = br.readLine()) != null) {
				res.append(line);
			}
		}
		return res.toString();
	}
	
	/**
	 * Copies an input stream to an output stream.
	 * @param is The input stream where the data comes from.
	 * @param os The output stream where the date goes to.
	 * @throws IOException If something goes wrong.
	 */
	public static void printInputStreamToOutputStream(InputStream is, OutputStream os) throws IOException {
		byte[] b = new byte[32*1024];
		try {
			while(is.read(b) > 0) {
				os.write(b);
			}
		} finally {
			os.flush();
			is.close();
			is.close();
		}
	}
}
