package dk.netarkivet.research.testutils;

import java.io.File;
import java.io.FileOutputStream;

public class TestFileUtils {

	public static void removeFile(File f) throws Exception {
		if(f.isDirectory()) {
			for(File subFile : f.listFiles())
			removeFile(subFile);
		}
		f.delete();
		
		if(f.exists()) {
			throw new IllegalStateException("The file '" + f.getAbsolutePath() + "' should have been deleted.");
		}
	}
	
	public static File createTestFile(File dir, String content) throws Exception {
		File testFile = new File(dir, "test");
		try (FileOutputStream fos = new FileOutputStream(testFile)) {
			fos.write(content.getBytes());
		}
		return testFile;
	}
}
