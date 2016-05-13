package dk.netarkivet.research.utils;

import static org.testng.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.annotations.Test;

public class ListUtilsTest extends ExtendedTestCase {

	private Long smallestValue = -100L;
	private Long largestValue = 100L;
	List<Long> list = Arrays.asList(smallestValue, 0L, 1L, largestValue);
	
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
}
