package dk.netarkivet.research.utils;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class FileUtilsTest extends ExtendedTestCase {


	File dir = new File("tempDir");
	
	@BeforeClass
	public void setup() throws Exception {
		removeFile(dir);
		dir.mkdirs();
		
	}
	
	@Test
	public void testDeprecateFile() throws Exception {
		addDescription("Test deprecationOfAFile");
		File testFile = createTestFile(dir);
		
		assertTrue(testFile.exists());
		FileUtils.deprecateFile(testFile);
		assertFalse(testFile.exists());
		assertTrue(new File(testFile.getAbsolutePath() + ".old").isFile());
	}
	
	@Test
	public void testDoubleDeprecateFile() throws Exception {
		addDescription("Test deprecationOfAFile");
		addStep("Create first file and deprecate it", "No file at original location");
		File testFile = createTestFile(dir);
		FileUtils.deprecateFile(testFile);
		File depFile = new File(testFile.getAbsolutePath() + ".old");
		assertFalse(testFile.exists());
		assertTrue(depFile.isFile());

		addStep("Create another file at original spot, and deprecate it as well", "Should also depreacted previously deprecated file.");
		testFile = createTestFile(dir);
		FileUtils.deprecateFile(testFile);
		assertFalse(testFile.exists());
		assertTrue(depFile.isFile());
		assertTrue(new File(depFile.getAbsolutePath() + ".old").isFile());
	}
	
	@Test
	public void testDeprecateDirectory() throws Exception {
		addDescription("Test deprecation of a directory");
		File subDir = new File(dir, "subDir");
		assertTrue(subDir.mkdir());
		assertTrue(subDir.isDirectory());
		
		File contentFile = createTestFile(subDir);
		assertTrue(contentFile.isFile());
		
		FileUtils.deprecateFile(subDir);
		
		assertFalse(subDir.exists());
		File depDir = new File(subDir.getAbsolutePath() + ".old");
		assertTrue(depDir.exists());
		assertTrue(depDir.isDirectory());
		
		assertFalse(contentFile.exists());
		assertTrue(new File(depDir, contentFile.getName()).isFile());
	}
	
	@Test
	public void testEnsuringNewFileAtNewPosition() throws Exception {
		addDescription("Testing ensuring a new file at a new position");
		File f = new File(dir, "EnsuringTest");
		
		assertFalse(f.isFile());
		
		FileUtils.ensureNewFile(dir, f.getName());
		
		assertTrue(f.isFile());
		assertFalse(new File(f.getAbsolutePath() + ".old").isFile());
	}
	
	@Test
	public void testEnsuringNewFileAtExistingFilePosition() throws Exception {
		addDescription("Testing ensuring a new file at the existing at an existing files position, will deprecate existing file");
		File f = createTestFile(dir);
		
		assertTrue(f.isFile());
		
		FileUtils.ensureNewFile(dir, f.getName());
		
		assertTrue(f.isFile());
		
		assertTrue(new File(f.getAbsolutePath() + ".old").isFile());		
	}

	@Test
	public void testEnsuringNewFileAtExistingDirectoryPosition() throws Exception {
		addDescription("Testing ensuring a new file at the existing at an existing directory position, will deprecate existing file");
		File subDir = new File(dir, "existingSubDir");
		subDir.mkdir();
		assertTrue(subDir.isDirectory());
		
		File fileSubDir = createTestFile(subDir);
		
		assertTrue(fileSubDir.isFile());
		
		FileUtils.ensureNewFile(dir, subDir.getName());
		
		assertTrue(subDir.isFile());
		assertFalse(fileSubDir.exists());
		
		assertTrue(new File(subDir.getAbsolutePath() + ".old").isDirectory(), "Existing directory deprecated");
		assertTrue(new File(subDir.getAbsolutePath() + ".old", fileSubDir.getName()).isFile(), "File in deprecated directory");
	}
	
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
	
	public File createTestFile(File dir) throws Exception {
		File testFile = new File(dir, "test");
		try (FileOutputStream fos = new FileOutputStream(testFile)) {
			fos.write("This is a test file".getBytes());
		}
		return testFile;
	}
}
