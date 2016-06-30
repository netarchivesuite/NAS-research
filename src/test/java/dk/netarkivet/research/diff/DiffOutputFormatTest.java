package dk.netarkivet.research.diff;

import static org.testng.Assert.assertEquals;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.annotations.Test;

public class DiffOutputFormatTest extends ExtendedTestCase {
    @Test
    public void testExtractingVerbose1() throws Exception {
    	addDescription("Try extracting the Verbose Diff output format from 'verbose'");
    	String arg = "verbose";
    	DiffOutputFormat outputFormat = DiffOutputFormat.extractOutputFormat(arg);
    	assertEquals(DiffOutputFormat.OUTPUT_FORMAT_VERBOSE, outputFormat);
    }
    
    @Test
    public void testExtractingVerbose2() throws Exception {
    	addDescription("Try extracting the Verbose Diff output format from 'v'");
    	String arg = "v";
    	DiffOutputFormat outputFormat = DiffOutputFormat.extractOutputFormat(arg);
    	assertEquals(DiffOutputFormat.OUTPUT_FORMAT_VERBOSE, outputFormat);
    }
    
    @Test
    public void testExtractingVerbose3() throws Exception {
    	addDescription("Try extracting the Verbose Diff output format from 'V'");
    	String arg = "V";
    	DiffOutputFormat outputFormat = DiffOutputFormat.extractOutputFormat(arg);
    	assertEquals(DiffOutputFormat.OUTPUT_FORMAT_VERBOSE, outputFormat);
    }
    
    @Test
    public void testExtractingSummary1() throws Exception {
    	addDescription("Try extracting the Summary Diff output format from 'summary'");
    	String arg = "summary";
    	DiffOutputFormat outputFormat = DiffOutputFormat.extractOutputFormat(arg);
    	assertEquals(DiffOutputFormat.OUTPUT_FORMAT_SUMMARY, outputFormat);
    }
    
    @Test
    public void testExtractingSummary2() throws Exception {
    	addDescription("Try extracting the Summary Diff output format from 's'");
    	String arg = "s";
    	DiffOutputFormat outputFormat = DiffOutputFormat.extractOutputFormat(arg);
    	assertEquals(DiffOutputFormat.OUTPUT_FORMAT_SUMMARY, outputFormat);
    }
    
    @Test
    public void testExtractingSummary3() throws Exception {
    	addDescription("Try extracting the Summary Diff output format from 'S'");
    	String arg = "S";
    	DiffOutputFormat outputFormat = DiffOutputFormat.extractOutputFormat(arg);
    	assertEquals(DiffOutputFormat.OUTPUT_FORMAT_SUMMARY, outputFormat);
    }

    @Test
    public void testExtractingBoth1() throws Exception {
    	addDescription("Try extracting the Both Diff output format from 'both'");
    	String arg = "both";
    	DiffOutputFormat outputFormat = DiffOutputFormat.extractOutputFormat(arg);
    	assertEquals(DiffOutputFormat.OUTPUT_FORMAT_BOTH, outputFormat);
    }
    
    @Test
    public void testExtractingBoth2() throws Exception {
    	addDescription("Try extracting the Both Diff output format from 'b'");
    	String arg = "b";
    	DiffOutputFormat outputFormat = DiffOutputFormat.extractOutputFormat(arg);
    	assertEquals(DiffOutputFormat.OUTPUT_FORMAT_BOTH, outputFormat);
    }
    
    @Test
    public void testExtractingBoth3() throws Exception {
    	addDescription("Try extracting the Both Diff output format from 'B'");
    	String arg = "B";
    	DiffOutputFormat outputFormat = DiffOutputFormat.extractOutputFormat(arg);
    	assertEquals(DiffOutputFormat.OUTPUT_FORMAT_BOTH, outputFormat);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testExtractingFailure() throws Exception {
    	addDescription("Try extracting a Diff output format from 'ERROR'");
    	String arg = "ERROR";
    	DiffOutputFormat.extractOutputFormat(arg);
    }
    
    @Test
    public void testExtractingFromValues() throws Exception {
    	addDescription("Try extracting the Diff output format from the values");
    	for(DiffOutputFormat dof : DiffOutputFormat.values()) {
    		assertEquals(dof, DiffOutputFormat.valueOf(dof.name()));
    	}
    }
}
