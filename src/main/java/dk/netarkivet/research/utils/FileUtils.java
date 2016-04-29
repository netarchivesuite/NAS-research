package dk.netarkivet.research.utils;

import java.io.File;
import java.io.IOException;

/**
 * Utility class for dealing with files.
 */
public class FileUtils {

	/**
	 * Deprecates a file, by moving it from 'filepath/filename' to 'filepath/filename.old'.
	 * Does this recursively, if a file with the name already exists.
	 * @param file The file to deprecate.
	 */
	public static void deprecateFile(File file) {
		File newDest = new File(file.getParentFile(), file.getName() + ".old");
		if(newDest.exists()) {
			deprecateFile(newDest);
		}
		boolean success = file.renameTo(newDest);
		
		if(!success) {
			throw new IllegalStateException("Could not deprecate file '" + file.getAbsolutePath() + "'.");
		}
	}
	
	/**
	 * Ensures, that a new file is created for the given location.
	 * If a file or directory already exists, then it is deprecated.
	 * 
	 * @param dir The directory where the file should be created. 
	 * @param name The name of the file.
	 */
	public static File ensureNewFile(File dir, String name) throws IOException {
		File res = new File(dir, name);
		if(res.exists()) {
			deprecateFile(res);
		}
		if(!res.createNewFile()) {
			throw new IllegalStateException("Cannot create a new file at '" + res.getAbsolutePath() + "'");
		}
		return res;
	}
}
