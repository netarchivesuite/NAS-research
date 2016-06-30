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
    	assertEquals(drw.getResults().iterator().next().getDeltaType(), DeltaType.CHANGE);
    	assertEquals(drw.getOrigDiffCharCount(DiffResultType.CHAR, DeltaType.CHANGE), 27);
    	assertEquals(drw.getRevisedDiffCharCount(DiffResultType.CHAR, DeltaType.CHANGE), 29);
    	assertEquals(drw.getOrigDiffCharCount(DiffResultType.WORD, DeltaType.CHANGE), 33);
    	assertEquals(drw.getRevisedDiffCharCount(DiffResultType.WORD, DeltaType.CHANGE), 32);
    	assertEquals(drw.getOrigDiffCharCount(DiffResultType.LINE, DeltaType.CHANGE), 47);
    	assertEquals(drw.getRevisedDiffCharCount(DiffResultType.LINE, DeltaType.CHANGE), 49);
    	assertEquals(drw.getOrigGroupCount(DiffResultType.LINE, DeltaType.INSERT_DELETE), 0);
    	assertEquals(drw.getRevisedGroupCount(DiffResultType.LINE, DeltaType.INSERT_DELETE), 0);
    	assertEquals(drw.getOrigGroupCount(DiffResultType.LINE, DeltaType.CHANGE), 1);
    	assertEquals(drw.getRevisedGroupCount(DiffResultType.LINE, DeltaType.CHANGE), 2);
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
    	assertEquals(resultsIterator.next().getDeltaType(), DeltaType.CHANGE);
    	assertFalse(resultsIterator.hasNext());
    	assertEquals(drw.getOrigDiffCharCount(DiffResultType.CHAR, DeltaType.CHANGE), 6);
    	assertEquals(drw.getRevisedDiffCharCount(DiffResultType.CHAR, DeltaType.CHANGE), 5);
    	assertEquals(drw.getOrigDiffCharCount(DiffResultType.WORD, DeltaType.CHANGE), 8);
    	assertEquals(drw.getRevisedDiffCharCount(DiffResultType.WORD, DeltaType.CHANGE), 7);
    	assertEquals(drw.getOrigDiffCharCount(DiffResultType.LINE, DeltaType.CHANGE), 26);
    	assertEquals(drw.getRevisedDiffCharCount(DiffResultType.LINE, DeltaType.CHANGE), 25);
    	assertEquals(drw.getOrigGroupCount(DiffResultType.LINE, DeltaType.INSERT_DELETE), 0);
    	assertEquals(drw.getRevisedGroupCount(DiffResultType.LINE, DeltaType.INSERT_DELETE), 0);
    	assertEquals(drw.getOrigGroupCount(DiffResultType.LINE, DeltaType.CHANGE), 1);
    	assertEquals(drw.getRevisedGroupCount(DiffResultType.LINE, DeltaType.CHANGE), 1);
    }
    
    @Test
    public void testDiffOnFilesWithOneChangedLineEach() throws Exception {
    	addDescription("Test the simple diff between two slightly different files");
    	File orig = new File("src/test/resources/diff/test4-orig.txt");
    	File revised = new File("src/test/resources/diff/test4-revised.txt");
    	SimpleDiffFiles sdf = new SimpleDiffFiles();
    	DiffResultWrapper drw = sdf.diff(new FileInputStream(orig), new FileInputStream(revised));
    	
    	assertEquals(drw.getResults().size(), 2);
    	Iterator<DiffResult> resultsIterator = drw.getResults().iterator();
    	assertEquals(resultsIterator.next().getDeltaType(), DeltaType.INSERT_DELETE);
    	assertTrue(resultsIterator.hasNext());
    	assertEquals(resultsIterator.next().getDeltaType(), DeltaType.INSERT_DELETE);
    	assertFalse(resultsIterator.hasNext());
    	assertEquals(drw.getOrigDiffCharCount(DiffResultType.CHAR, DeltaType.CHANGE), 0);
    	assertEquals(drw.getRevisedDiffCharCount(DiffResultType.CHAR, DeltaType.CHANGE), 0);
    	assertEquals(drw.getOrigDiffCharCount(DiffResultType.WORD, DeltaType.CHANGE), 0);
    	assertEquals(drw.getRevisedDiffCharCount(DiffResultType.WORD, DeltaType.CHANGE), 0);
    	assertEquals(drw.getOrigDiffCharCount(DiffResultType.LINE, DeltaType.CHANGE), 0);
    	assertEquals(drw.getRevisedDiffCharCount(DiffResultType.LINE, DeltaType.CHANGE), 0);
    	assertEquals(drw.getOrigDiffCharCount(DiffResultType.LINE, DeltaType.INSERT_DELETE), 7);
    	assertEquals(drw.getRevisedDiffCharCount(DiffResultType.LINE, DeltaType.INSERT_DELETE), 12);
    	assertEquals(drw.getOrigGroupCount(DiffResultType.LINE, DeltaType.INSERT_DELETE), 1);
    	assertEquals(drw.getRevisedGroupCount(DiffResultType.LINE, DeltaType.INSERT_DELETE), 1);
    	assertEquals(drw.getOrigGroupCount(DiffResultType.LINE, DeltaType.CHANGE), 0);
    	assertEquals(drw.getRevisedGroupCount(DiffResultType.LINE, DeltaType.CHANGE), 0);

    	assertNotNull(drw.toString());
    }
}
