package dk.netarkivet.research.exception;

import java.io.File;
import java.util.Arrays;

/**
 * Exception for invalid arguments. 
 * Contains validating methods which throws this exception, if they fail.
 */
public class ArgumentCheck extends RuntimeException {
	
	/**
	 * Constructor.
	 * @param message The message of the exception.
	 */
	public ArgumentCheck(String message) {
		super(message);
	}
	
	/**
	 * Constructor.
	 * @param message The message of the exception.
	 * @param t The causing throwable to embed.
	 */
	public ArgumentCheck(String message, Throwable t) {
		super(message, t);
	}
	
	/**
	 * Throws an exception if it is null.
	 * @param object The object to check whether it is null.
	 * @param message The message if it is null.
	 */
	public static void checkNotNull(Object object, String message) {
		if(object == null) {
			throw new ArgumentCheck("Argument may not be null: " + message);
		}
	}
	
	/**
	 * Throws an exception if the string is null or empty.
	 * @param object The object to check whether it is null or empty.
	 * @param message The message if it is null or empty.
	 */
	public static void checkNotNullOrEmpty(String object, String message) {
		checkNotNull(object, message);
		if(object.isEmpty()) {
			throw new ArgumentCheck("String may not be empty: " + message);
		}
	}
	
	/**
	 * Throws an exception if the array only contains nulls.
	 * The array may contain nulls, but it should contain non-null objects.
	 * @param objects The array of objects. 
	 */
	public static void checkNotAllNull(Object ... objects) {
		for(Object o : objects) {
			if(o != null) {
				return;
			}
		}
		throw new ArgumentCheck("Argument array may not only contain nulls.");
	}

	/**
	 * Throws an exception if the array contains a null.
	 * @param objects The array of objects.
	 */
	public static void checkNotAnyNull(Object ...objects) {
		for(Object o : objects) {
			if(o == null) {
				throw new ArgumentCheck("Argument array must not contain any nulls: '" 
						+ Arrays.asList(objects).toString() + "'");
			}
		}
	}

	/**
	 * Throws an exception if the file does not exist or it is not a file.
	 * @param file The file to validate.
	 * @param message The message for the exception.
	 */
	public static void checkIsFile(File file, String message) {
		checkNotNull(file, message);
		if(!file.isFile()) {
			throw new ArgumentCheck("File '" + file + "' is not a file: " + message);
		}
	}
	
	/**
	 * Check whether a boolean is true.
	 * @param b The boolean to check.
	 * @param message The message for the exception.
	 */
	public static void checkIsTrue(boolean b, String message) {
		if(!b) {
			throw new ArgumentCheck("Expected true, but was false: " + message);
		}
	}
}
