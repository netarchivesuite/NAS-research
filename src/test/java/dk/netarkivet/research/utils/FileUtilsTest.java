package dk.netarkivet.research.utils;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertNull;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import dk.netarkivet.research.testutils.TestFileUtils;

public class FileUtilsTest extends ExtendedTestCase {

	private File dir = new File("tempDir");
	private String testFileContent = "This is a test file";
	
	@BeforeClass
	public void setup() throws Exception {
		TestFileUtils.removeFile(dir);
		dir.mkdirs();
	}
	
	@AfterClass
	public void tearDown() throws Exception {
		TestFileUtils.removeFile(dir);
	}
	
	@Test
	public void testInstantiation() {
		addDescription("Test the instantaion of the file utility class");
		new FileUtils();
	}
	
	@Test
	public void testDeprecateFile() throws Exception {
		addDescription("Test deprecationOfAFile");
		File testFile = TestFileUtils.createTestFile(dir, testFileContent);
		
		assertTrue(testFile.exists());
		FileUtils.deprecateFile(testFile);
		assertFalse(testFile.exists());
		assertTrue(new File(testFile.getAbsolutePath() + ".old").isFile());
	}
	
	@Test
	public void testDoubleDeprecateFile() throws Exception {
		addDescription("Test deprecationOfAFile");
		addStep("Create first file and deprecate it", "No file at original location");
		File testFile = TestFileUtils.createTestFile(dir, testFileContent);
		FileUtils.deprecateFile(testFile);
		File depFile = new File(testFile.getAbsolutePath() + ".old");
		assertFalse(testFile.exists());
		assertTrue(depFile.isFile());

		addStep("Create another file at original spot, and deprecate it as well", "Should also depreacted previously deprecated file.");
		testFile = TestFileUtils.createTestFile(dir, testFileContent);
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
		
		File contentFile = TestFileUtils.createTestFile(subDir, testFileContent);
		assertTrue(contentFile.isFile());
		
		FileUtils.deprecateFile(subDir);
		
		assertFalse(subDir.exists());
		File depDir = new File(subDir.getAbsolutePath() + ".old");
		assertTrue(depDir.exists());
		assertTrue(depDir.isDirectory());
		
		assertFalse(contentFile.exists());
		assertTrue(new File(depDir, contentFile.getName()).isFile());
	}
	
	@Test(expectedExceptions = IllegalStateException.class)
	public void testDeprecateFileWhenNotAllowed() throws Exception {
		addDescription("Try to deprecate a file in a directory, where you do not have write access");
		File subDir = new File(dir, "readonlyDirectory");
		subDir.mkdir();
		assertTrue(subDir.isDirectory());
		File f = TestFileUtils.createTestFile(subDir, "This is the content");

		try {
			assertTrue(subDir.setReadOnly());
			assertFalse(subDir.canWrite());
			
			FileUtils.deprecateFile(f);
		} finally {
			subDir.setWritable(true);
			subDir.setExecutable(true);
		}
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
		File f = TestFileUtils.createTestFile(dir, testFileContent);
		
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
		
		File fileSubDir = TestFileUtils.createTestFile(subDir, testFileContent);
		
		assertTrue(fileSubDir.isFile());
		
		FileUtils.ensureNewFile(dir, subDir.getName());
		
		assertTrue(subDir.isFile());
		assertFalse(fileSubDir.exists());
		
		assertTrue(new File(subDir.getAbsolutePath() + ".old").isDirectory(), "Existing directory deprecated");
		assertTrue(new File(subDir.getAbsolutePath() + ".old", fileSubDir.getName()).isFile(), "File in deprecated directory");
	}
	
	@Test(expectedExceptions = IOException.class)
	public void testEnsuringNewFileAtReadOnlyDirectory() throws Exception {
		addDescription("Test ensuring a new file, when the directory is read only.");
		File subDir = new File(dir, "readonlyDirectory");
		subDir.mkdir();
		assertTrue(subDir.isDirectory());
		File f = new File(subDir, "Test" + Math.random() + ".txt");

		try {
			assertTrue(subDir.setReadOnly());
			assertFalse(subDir.canWrite());
			
			FileUtils.ensureNewFile(f);
		} finally {
			subDir.setWritable(true);
			subDir.setExecutable(true);
		}
	}
	
	@Test(expectedExceptions = IOException.class)
	public void testCreateInReadOnlyDirectory() throws Exception {
		addDescription("Tests the creation of a new file in a read-only directory.");
		File subDir = new File(dir, "readonlyDirectory");
		subDir.mkdir();
		assertTrue(subDir.isDirectory());
		
		try {
			assertTrue(subDir.setReadOnly());
			assertFalse(subDir.canWrite());
			
			FileUtils.ensureNewFile(subDir, "TestFile");
		} finally {
			subDir.setWritable(true);
			subDir.setExecutable(true);
		}
	}

	@Test(expectedExceptions = IOException.class)
	public void testCreateDirInReadOnlyDirectory() throws Exception {
		addDescription("Test creating a directory in a directory which is read only.");
		File subDir = new File(dir, "readonlyDirectory");

		try {
			assertTrue(dir.setReadOnly());
			assertFalse(dir.canWrite());
			
			FileUtils.ensureNewFile(subDir);
		} finally {
			dir.setWritable(true);
			dir.setExecutable(true);
		}
	}
	
	@Test
	public void testSortFileInDir() {
		addDescription("Test the sorting of files.");
		File dir = new File("src/test/resources");
		List<String> filenames = FileUtils.getSortedListOfFilenames(dir);
		
		assertFalse(filenames.isEmpty());
		assertTrue(filenames.size() > 1);
		
		for(int i = 1; i < filenames.size(); i++) {
			assertTrue(filenames.get(i-1).compareTo(filenames.get(i)) < 0);
		}
	}
	
	@Test
	public void testSortFileInDirWhenGivenFile() {
		addDescription("Test the sorting, when it is done on a file instead of a directory.");
		File f = new File("src/test/rersouces/jaccept.properties");
		assertNull(FileUtils.getSortedListOfFilenames(f));
	}
	
	@Test
	public void testGetDirectoryNameFromFilenameWithWarc() {
		addDescription("Test getting the directory name from a filename ending on 'WARC'");
		String filename = "test.warc";
		String dirname = FileUtils.getDirectoryNameFromFileName(filename);
		
		assertEquals(dirname, "test");
	}

	@Test
	public void testGetDirectoryNameFromFilenameWithWarcButAlreadyExists() {
		addDescription("Test getting the directory name from a filename ending on 'WARC', but another file with same name already exists.");
		File folder = new File(dir, "test-" + Math.random());
		try {
			folder.mkdirs();
			assertTrue(folder.isDirectory());
			String filename = new File(dir, "test.warc").getAbsolutePath();
			String dirpath = FileUtils.getDirectoryNameFromFileName(filename);
			String dirname = dirpath.replace(dir.getAbsolutePath() + "/", "");

			assertTrue(dirname.startsWith("test"));
			assertFalse(dirname.contains(".warc"));
			assertEquals(dirname.length(), 18);
		} finally {
			folder.delete();
		}
	}
	
	@Test
	public void testGetDirectoryNameFromFilenameWithNonWarc() {
		addDescription("Test getting the directory name from a filename not ending on 'WARC'");
		String filename = new File(dir, "test").getAbsolutePath();
		String dirpath = FileUtils.getDirectoryNameFromFileName(filename);
		String dirname = dirpath.replace(dir.getAbsolutePath() + "/", "");
		
		assertTrue(dirname.startsWith("test"));
		assertEquals(dirname.length(), 18);
	}
}
