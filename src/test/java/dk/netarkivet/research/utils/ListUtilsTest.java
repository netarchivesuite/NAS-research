package dk.netarkivet.research.utils;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertFalse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.annotations.Test;

public class ListUtilsTest extends ExtendedTestCase {

	private Long smallestValue = -100L;
	private Long largestValue = 100L;
	List<Long> list = Arrays.asList(smallestValue, 0L, largestValue, 1L);
	
	@Test
	public void testInstantiation() {
		addDescription("Test the instantaion of the list utility class");
		new ListUtils();
	}
	
	@Test
	public void testLargestLongValue() throws Exception {
		addDescription("Finds the largest long value in a list of longs.");
		assertEquals(ListUtils.getLargest(list), largestValue);
	}

	@Test
	public void testSmallestLongValue() throws Exception {
		addDescription("Finds the smallest long value in a list of longs.");
		assertEquals(ListUtils.getSmallest(list), smallestValue);
	}
	
	@Test
	public void testSmallestLongValueWithEmptyList() {
		addDescription("Tests the finding of the lowest value in an empty list");
		assertEquals(ListUtils.getSmallest(new ArrayList<Long>()).longValue(), 0L);
	}
	
	@Test
	public void testLargestLongValueWithEmptyList() {
		addDescription("Tests the finding of the largest value in an empty list");
		assertEquals(ListUtils.getLargest(new ArrayList<Long>()).longValue(), 0L);
	}
	
	@Test
	public void testLinesToWordsWithEmptyLine() {
		addDescription("Testing the convertLinesToListOfWords with an empty line");
		List<String> l = ListUtils.convertLinesToListOfWords(Arrays.asList(""));
		assertNotNull(l);
		assertTrue(l.isEmpty());
	}

	@Test
	public void testLinesToWordsWithOnlySpacesInAllLines() {
		addDescription("Testing the convertLinesToListOfWords with an empty line");
		List<String> l = ListUtils.convertLinesToListOfWords(Arrays.asList("   ", "     "));
		assertNotNull(l);
		assertTrue(l.isEmpty());
	}
	
	@Test
	public void testLinesToWordsWithTwoWordsInOneLine() {
		addDescription("Testing the convertLinesToListOfWords with an single line containing two words");
		List<String> l = ListUtils.convertLinesToListOfWords(Arrays.asList("One Two"));
		assertNotNull(l);
		assertFalse(l.isEmpty());
		assertEquals(l.size(), 2);
	}

}
