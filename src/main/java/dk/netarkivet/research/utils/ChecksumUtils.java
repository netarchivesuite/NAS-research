package dk.netarkivet.research.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility class for handling checksums.
 */
public class ChecksumUtils {

	/**
	 * Digests a byte array as SHA1.
	 * @param content The byte array content to digest.
	 * @return The SHA1 digested content.
	 */
	public static byte[] sha1Digest(byte[] content) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA1");
			digest.reset();
			return digest.digest(content);
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException("Cannot instantiate the message digester for the SHA1 checksum algorithm.",
					e);
		}
	}
}
