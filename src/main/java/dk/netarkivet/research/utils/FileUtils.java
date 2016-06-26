package dk.netarkivet.research.utils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Utility class for dealing with files.
 */
public class FileUtils {

	/**
	 * Deprecates a file, by moving it from 'filepath/filename' to 'filepath/filename.old'.
	 * Does this recursively, if a file with the name already exists.
	 * Also works on directories.
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
	 * @return The new file.
	 * @throws IOException if it fails to create a new file.
	 */
	public static File ensureNewFile(File dir, String name) throws IOException {
		File res = new File(dir, name);
		ensureNewFile(res);
		return res;
	}

	/**
	 * Ensures the given file is created as a new file, and
	 * that any existing file at that location is deprecated.
	 * @param newFile The new file.
	 * @throws IOException If the new file cannot be created.
	 */
	public static void ensureNewFile(File newFile) throws IOException {
		if(newFile.exists()) {
			deprecateFile(newFile);
		}
		if(!newFile.createNewFile()) {
			throw new IllegalStateException("Cannot create the new file '" + newFile.getAbsolutePath() + "'.");
		}
	}

	/**
	 * Creates a directory.
	 * Or makes an appropriate failure.
	 * @param dirPath The path to the directory.
	 * @return The directory.
	 */
	public static File createDir(String dirPath) {
		File outDir = new File(dirPath);
		if(outDir.isFile()) {
			throw new IllegalArgumentException("The location for the output file is not vacent.");
		} else {
			boolean dirSuccess= outDir.mkdirs();
			if(!dirSuccess && !outDir.isDirectory()) {
				throw new IllegalArgumentException("Cannot create the directory '" + dirPath + "'.");
			}
		}
		return outDir;
	}
	
	/**
	 * Extracts a list of files sorted by filename.
	 * @param dir The directory with the files.
	 * @return The sorted list of files.
	 */
	public static List<String> getSortedListOfFilenames(File dir) {
		String[] list = dir.list();
		if(list == null) {
			return null;
		}
		List<String> filenames = Arrays.asList(list);
		Collections.sort(filenames);
		return filenames;
	}
}
