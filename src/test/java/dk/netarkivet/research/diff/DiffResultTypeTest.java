package dk.netarkivet.research.diff;

import static org.testng.Assert.assertEquals;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.annotations.Test;

public class DiffResultTypeTest extends ExtendedTestCase {
    @Test
    public void testExtractingFromValues() throws Exception {
    	addDescription("Try extracting the Diff result type from the values");
    	for(DiffResultType dof : DiffResultType.values()) {
    		assertEquals(dof, DiffResultType.valueOf(dof.name()));
    	}
    }
}
