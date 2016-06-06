package dk.netarkivet.research.exception;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import java.io.File;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.annotations.Test;

public class ArgumentCheckTest extends ExtendedTestCase {
	
	public final File testFile = new File("src/test/resources/test.warc");
	
	@Test
	public void testConstructorWithMessageOnly() throws Exception {
		addDescription("Test the constructor with only the message argument.");
		String message = "This is the exception message";
		Exception e = new ArgumentCheck(message);
		assertEquals(e.getMessage(), message);
		assertNull(e.getCause());
	}
	
	@Test
	public void testConstructorWithMessageAndException() throws Exception {
		addDescription("Test the constructor with both the message and the exception arguments.");
		String message = "This is the exception message";
		Exception embeddedException = new IllegalArgumentException();
		Exception e = new ArgumentCheck(message, embeddedException);
		assertEquals(e.getMessage(), message);
		assertNotNull(e.getCause());
		assertEquals(e.getCause(), embeddedException);
	}

	@Test
	public void testCheckNotNullWithNotNull() {
		addDescription("Test the checkNotNull with a non-null element");
		ArgumentCheck.checkNotNull(new String(), "Should not throw exception");
	}
	
	@Test(expectedExceptions = ArgumentCheck.class)
	public void testCheckNotNullWithNull() {
		addDescription("Test the checkNotNull with a null element");
		ArgumentCheck.checkNotNull(null, "Should throw exception");
	}
	
	@Test
	public void testIsFileWithFile() {
		addDescription("Test the checkIsFile with a file");
		ArgumentCheck.checkIsFile(testFile, "Should not throw an exception");
	}
	
	@Test(expectedExceptions = ArgumentCheck.class)
	public void testIsFileWithANonExistingFile() {
		addDescription("Test the checkIsFile with a file");
		File f = new File(testFile.getParentFile(), "ThisIsNotAFile-" + Math.random() + ".txt");
		ArgumentCheck.checkIsFile(f, "Should not throw an exception");
	}

	@Test
	public void testCheckNotAllNullWithNoNull() {
		addDescription("Test the checkNotAllNull method with an array with no nulls");
		ArgumentCheck.checkNotAllNull(new String("test"), new Integer(1));
	}

	@Test
	public void testCheckNotAllNullWithOneNullAndOneNotNull() {
		addDescription("Test the checkNotAllNull method with an array with one null and one not-null");
		ArgumentCheck.checkNotAllNull(new String("test"), null);
	}
	
	@Test(expectedExceptions = ArgumentCheck.class)
	public void testCheckNotAllNullWithOnlyNulls() {
		addDescription("Test the checkNotAllNull method with an array with only nulls");
		ArgumentCheck.checkNotAllNull(null, null);
	}

	@Test
	public void testCheckNotAnyNullWithNoNull() {
		addDescription("Test the checkNotAnyNull method with an array with no nulls");
		ArgumentCheck.checkNotAnyNull(new String("test"), new Integer(1));
	}

	@Test(expectedExceptions = ArgumentCheck.class)
	public void testCheckNotAnyNullWithOneNullAndOneNotNull() {
		addDescription("Test the checkNotAnyNull method with an array with one null and one not-null");
		ArgumentCheck.checkNotAnyNull(new String("test"), null);
	}
	
	@Test(expectedExceptions = ArgumentCheck.class)
	public void testCheckNotAnyNullWithOnlyNulls() {
		addDescription("Test the checkNotAnyNull method with an array with only nulls");
		ArgumentCheck.checkNotAnyNull(null, null);
	}

}
