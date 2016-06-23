package dk.netarkivet.research.utils;

import java.util.ArrayList;
import java.util.Collection;
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

	/**
	 * Extracts all the words from a collection of lines.
	 * By word is meant any collection of non-space characters.
	 * @param lines The lines to collect the words from.
	 * @return The words in the lines.
	 */
	public static List<String> convertLinesToListOfWords(Collection<String> lines) {
		List<String> res = new ArrayList<String>();
		for(String line : lines) {
			String[] split = line.split("\\s");
			for(String w : split) {
				if(!w.isEmpty()) {
					res.add(w);
				}
			}
		}
		return res;
	}

	/**
	 * Extracts all the characters from a collection of strings.
	 * @param strings The collection of strings to extract the characters from.
	 * @return The characters in the strings.
	 */
	public static List<String> convertStringsToListOfCharacters(Collection<String> strings) {
		List<String> res = new ArrayList<String>();
		for(String s : strings) {
			for(char c : s.toCharArray()) {
				res.add(String.valueOf(c));
			}
		}
		return res;
	}
}
