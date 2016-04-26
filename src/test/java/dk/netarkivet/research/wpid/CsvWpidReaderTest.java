package dk.netarkivet.research.wpid;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.annotations.Test;

public class CsvWpidReaderTest extends ExtendedTestCase {

	File goodCsv = new File("src/test/resources/urls.csv");
	File completeCsv = new File("src/test/resources/urls2.csv");
	
	@Test
	public void testWPIDExtractionFromFileWithOnlyGoodLines() throws Exception {
		addDescription("Test WPID extraction from a file only containing good entries");
		
		assertTrue(goodCsv.isFile());
		
		CsvWpidReader reader = new CsvWpidReader(goodCsv);
    	Collection<WPID> wpids = reader.extractAllWPIDs();

    	assertFalse(wpids.isEmpty());
    	assertEquals(wpids.size(), 3);
    	assertEquals(wpids.size()+1, countNumberOfLines(goodCsv), "Should have 1 line more than file");
	}

	@Test
	public void testWPIDExtractionFromFileWithSomeBadLines() throws Exception {
		addDescription("Test WPID extraction from a file with both good and bad entries");
		
		assertTrue(completeCsv.isFile());
		
		CsvWpidReader reader = new CsvWpidReader(completeCsv);
    	Collection<WPID> wpids = reader.extractAllWPIDs();

    	assertFalse(wpids.isEmpty());
    	assertEquals(wpids.size(), 70);
    	assertEquals(countNumberOfLines(completeCsv), 1199);
	}
	
	private int countNumberOfLines(File f) {
		int i = 0;
		try (
			InputStream fis = new FileInputStream(f);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader br = new BufferedReader(isr);
		) {
			while (br.readLine() != null) {
				i++;
			}
		} catch (Exception e) {
			return -1;
		}
		return i;
	}
}
