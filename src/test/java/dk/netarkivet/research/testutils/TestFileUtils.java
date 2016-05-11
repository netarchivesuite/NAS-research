package dk.netarkivet.research.testutils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

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
	
	public static int countNumberOfLines(File f) {
		int i = 0;
		try (
			InputStream fis = new FileInputStream(f);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader br = new BufferedReader(isr);
		) {
			while (br.readLine() != null) {
				i++;
			}
		} catch (Exception e) {
			return -1;
		}
		return i;
	}
}
