package dk.netarkivet.research.utils;

import java.util.List;

/**
 * Utility method for handling lists.
 */
public class ListUtils {

	/**
	 * Retrieves the largest long value from a list of longs.
	 * @param list The list.
	 * @return The largest value in the list.
	 */
	public static Long getLargest(List<Long> list) {
		if(list.isEmpty()) {
			return 0L;
		}
		Long res = Long.MIN_VALUE;
		for(Long l : list) {
			if(res < l) {
				res = l;
			}
		}
		
		return res;
	}
	
	/**
	 * Retrieves the smallest long value from a list of longs.
	 * @param list The list.
	 * @return The smallest value in the list.
	 */
	public static Long getSmallest(List<Long> list) {
		if(list.isEmpty()) {
			return 0L;
		}
		Long res = Long.MAX_VALUE;
		for(Long l : list) {
			if(res > l) {
				res = l;
			}
		}
		
		return res;
	}

}
