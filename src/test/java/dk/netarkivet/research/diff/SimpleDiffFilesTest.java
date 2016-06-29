package dk.netarkivet.research.diff;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.annotations.Test;

public class SimpleDiffFilesTest extends ExtendedTestCase {
	
    @Test
    public void testDiffOnCompletelyDifferentFiles() throws Exception {
    	addDescription("Test the simple diff between two completely different files");
    	File orig = new File("src/test/resources/diff/test1_orig.txt");
    	File revised = new File("src/test/resources/diff/test1_revised.txt");
    	SimpleDiffFiles sdf = new SimpleDiffFiles();
    	DiffResultWrapper drw = sdf.diff(new FileInputStream(orig), new FileInputStream(revised));

    	assertEquals(drw.getResults().size(), 1);
    	assertTrue(drw.getResults().iterator().next().isChange());
    	assertEquals(drw.getOrigDiffCharCount(DiffResultType.CHAR), 27);
    	assertEquals(drw.getRevisedDiffCharCount(DiffResultType.CHAR), 29);
    	assertEquals(drw.getOrigDiffCharCount(DiffResultType.WORD), 33);
    	assertEquals(drw.getRevisedDiffCharCount(DiffResultType.WORD), 32);
    	assertEquals(drw.getOrigDiffCharCount(DiffResultType.LINE), 47);
    	assertEquals(drw.getRevisedDiffCharCount(DiffResultType.LINE), 49);
    	assertEquals(drw.getOrigLineCount(false), 0);
    	assertEquals(drw.getRevisedLineCount(false), 0);
    	assertEquals(drw.getOrigLineCount(true), 1);
    	assertEquals(drw.getRevisedLineCount(true), 2);
    }

    @Test
    public void testDiffOnIdenticalFiles() throws Exception {
    	addDescription("Test the simple diff between two identical files");
    	File orig = new File("src/test/resources/diff/test2_orig.txt");
    	File revised = new File("src/test/resources/diff/test2_revised.txt");
    	SimpleDiffFiles sdf = new SimpleDiffFiles();
    	DiffResultWrapper drw = sdf.diff(new FileInputStream(orig), new FileInputStream(revised));
    	
    	assertEquals(drw.getResults().size(), 0);
    }

    @Test
    public void testDiffOnFilesWithOneChanged() throws Exception {
    	addDescription("Test the simple diff between two slightly different files");
    	File orig = new File("src/test/resources/diff/test3_orig.txt");
    	File revised = new File("src/test/resources/diff/test3_revised.txt");
    	SimpleDiffFiles sdf = new SimpleDiffFiles();
    	DiffResultWrapper drw = sdf.diff(new FileInputStream(orig), new FileInputStream(revised));
    	
    	assertEquals(drw.getResults().size(), 1);
    	Iterator<DiffResult> resultsIterator = drw.getResults().iterator();
    	assertTrue(resultsIterator.next().isChange());
    	assertEquals(drw.getOrigDiffCharCount(DiffResultType.CHAR), 6);
    	assertEquals(drw.getRevisedDiffCharCount(DiffResultType.CHAR), 5);
    	assertEquals(drw.getOrigDiffCharCount(DiffResultType.WORD), 8);
    	assertEquals(drw.getRevisedDiffCharCount(DiffResultType.WORD), 7);
    	assertEquals(drw.getOrigDiffCharCount(DiffResultType.LINE), 26);
    	assertEquals(drw.getRevisedDiffCharCount(DiffResultType.LINE), 25);
    	assertEquals(drw.getOrigLineCount(false), 0);
    	assertEquals(drw.getRevisedLineCount(false), 0);
    	assertEquals(drw.getOrigLineCount(true), 1);
    	assertEquals(drw.getRevisedLineCount(true), 1);
    }
    
    @Test
    public void testDiffOnFilesWithOneChangedLineEach() throws Exception {
    	addDescription("Test the simple diff between two slightly different files");
    	File orig = new File("src/test/resources/diff/test4_orig.txt");
    	File revised = new File("src/test/resources/diff/test4_revised.txt");
    	SimpleDiffFiles sdf = new SimpleDiffFiles();
    	DiffResultWrapper drw = sdf.diff(new FileInputStream(orig), new FileInputStream(revised));
    	
    	assertEquals(drw.getResults().size(), 2);
    	Iterator<DiffResult> resultsIterator = drw.getResults().iterator();
    	assertFalse(resultsIterator.next().isChange());
    	assertFalse(resultsIterator.next().isChange());
    	assertEquals(drw.getOrigDiffCharCount(DiffResultType.CHAR), 0);
    	assertEquals(drw.getRevisedDiffCharCount(DiffResultType.CHAR), 0);
    	assertEquals(drw.getOrigDiffCharCount(DiffResultType.WORD), 0);
    	assertEquals(drw.getRevisedDiffCharCount(DiffResultType.WORD), 0);
    	assertEquals(drw.getOrigDiffCharCount(DiffResultType.LINE), 7);
    	assertEquals(drw.getRevisedDiffCharCount(DiffResultType.LINE), 12);
    	assertEquals(drw.getOrigLineCount(false), 1);
    	assertEquals(drw.getRevisedLineCount(false), 1);
    	assertEquals(drw.getOrigLineCount(true), 0);
    	assertEquals(drw.getRevisedLineCount(true), 0);
    	
    	assertNotNull(drw.toString());
    }
}
