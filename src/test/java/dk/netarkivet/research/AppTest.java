package dk.netarkivet.research;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.Assert;
import org.testng.annotations.Test;

public class AppTest extends ExtendedTestCase {
    @Test
    public void testUnitTestingFramework() throws Exception {
        addDescription("This is supposed to test the testing framework");
        Assert.assertTrue(2 + 2 == 4);
        Assert.assertFalse(2 + 2 == 5);
    }
}
