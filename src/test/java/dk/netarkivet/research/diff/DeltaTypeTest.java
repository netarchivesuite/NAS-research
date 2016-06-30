package dk.netarkivet.research.diff;

import static org.testng.Assert.assertEquals;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.annotations.Test;

public class DeltaTypeTest extends ExtendedTestCase {
    
    @Test
    public void testExtractingFromValues() throws Exception {
    	addDescription("Try extracting the Delta type from the values");
    	for(DeltaType dt : DeltaType.values()) {
    		assertEquals(dt, DeltaType.valueOf(dt.name()));
    	}
    }
    
    @Test
    public void testExtractFromBooleanTrue() throws Exception {
    	addDescription("Try extraction from boolean value true");
    	assertEquals(DeltaType.CHANGE, DeltaType.extractFromBoolean(true));
    }
    
    @Test
    public void testExtractFromBooleanFalse() throws Exception {
    	addDescription("Try extraction from boolean value false");
    	assertEquals(DeltaType.INSERT_DELETE, DeltaType.extractFromBoolean(false));
    }
}
