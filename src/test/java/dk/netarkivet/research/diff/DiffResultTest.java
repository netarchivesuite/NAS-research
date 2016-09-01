package dk.netarkivet.research.diff;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.annotations.Test;

public class DiffResultTest extends ExtendedTestCase {
	
    @Test
    public void testDiffOnCompletelyDifferentFiles() throws Exception {
    	Boolean isChange = false;
    	List<String> origLines = Arrays.asList("Line 1", "line 2", "line 3");
    	int origLineNumber = 1;
    	List<String> revisedLines = Arrays.asList("Not a line", "Another revised line");
    	int revisedLineNumber = 23;
    	
    	DiffResult dr = new DiffResult(isChange, origLines, origLineNumber, revisedLines, revisedLineNumber);
    	
    	assertTrue(dr.getOrigResultList(DiffResultType.CHAR).isEmpty());
    	assertTrue(dr.getRevisedResultList(DiffResultType.CHAR).isEmpty());
    	assertTrue(dr.getOrigResultList(DiffResultType.WORD).isEmpty());
    	assertTrue(dr.getRevisedResultList(DiffResultType.WORD).isEmpty());
    	
    	assertEquals(dr.getDeltaType(), DeltaType.INSERT_DELETE);
    	
    	assertNotNull(dr.getOrigResultList(DiffResultType.LINE));
    	assertFalse(dr.getOrigResultList(DiffResultType.LINE).isEmpty());
    	assertNotNull(dr.getRevisedResultList(DiffResultType.LINE));
    	assertFalse(dr.getRevisedResultList(DiffResultType.LINE).isEmpty());
    	
    	assertEquals(dr.getOrigResultList(DiffResultType.LINE), origLines);
    	assertEquals(dr.getRevisedResultList(DiffResultType.LINE), revisedLines);
    	assertEquals(dr.getOrigLineNumber(), origLineNumber);
    	assertEquals(dr.getRevisedLineNumber(), revisedLineNumber);
    	
    	assertEquals(dr.getOrigDiffSize(DiffResultType.CHAR), 0);
    	assertEquals(dr.getRevisedDiffSize(DiffResultType.CHAR), 0);
    	assertEquals(dr.getOrigDiffSize(DiffResultType.WORD), 0);
    	assertEquals(dr.getRevisedDiffSize(DiffResultType.WORD), 0);
    	assertEquals(dr.getOrigDiffSize(DiffResultType.LINE), 18);
    	assertEquals(dr.getRevisedDiffSize(DiffResultType.LINE), 30);
    }
}
