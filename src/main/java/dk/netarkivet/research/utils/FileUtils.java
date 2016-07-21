package dk.netarkivet.research.utils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for dealing with files.
 */
public class FileUtils {
	/** Logging mechanism. */
	private static Logger logger = LoggerFactory.getLogger(FileUtils.class);

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
		logger.debug("Ensuring new file at '" + newFile.getAbsolutePath() + "'.");
		if(newFile.exists()) {
			deprecateFile(newFile);
		}
		boolean success = newFile.createNewFile();
		logger.debug("Ensured new file at '" + newFile.getAbsolutePath() + "': " + success);
	}

	/**
	 * Creates a directory.
	 * Or makes an appropriate failure.
	 * @param dirPath The path to the directory.
	 * @return The directory.
	 */
	public static File createDir(String dirPath) {
		File dir = new File(dirPath);
		if(dir.isDirectory()) {
			logger.debug("The directory '" + dir.getAbsolutePath() + "' already exists.");
		} else if(dir.isFile()) {
			throw new IllegalArgumentException("The location for the output file is not vacent.");
		} else {
			boolean success = dir.mkdirs();
			logger.debug("Ensured new file at '" + dir.getAbsolutePath() + "': " + success);
		}
		return dir;
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
	
	/**
	 * Finds an appropriate directory name for a WARC-filename.
	 * E.g. removes the extension, or adds the current date.
	 * @param filename The name of the file, who we should 
	 * @return The name of the directory for the file-name.
	 */
	public static String getDirectoryNameFromFileName(String filename) {
		String res = "";
		if(filename.endsWith(".warc")) {
			res = filename.substring(0, filename.length()-".warc".length());
			if(!(new File(res).exists())) {
				return res;
			}
		} else {
			res = filename;
		}

		return res + DateUtils.dateToWaybackDate(new Date());
	}
}
