package dk.netarkivet.research.diff;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import difflib.Delta;

/**
 * Results of a diff.
 */
public class DiffResult {
	/** The line number of the original.*/
	protected final int origLineNumber;
	/** The line number of the derived.*/
	protected final int revisedLineNumber;
	
	/** The diffs for the original. Map between type (line, word or char) and the diff list.*/
	protected final Map<DiffResultType, List<String>> origDiff = new HashMap<DiffResultType, List<String>>();
	/** The diffs for the revised. Map between type (line, word or char) and the diff list.*/
	protected final Map<DiffResultType, List<String>> revisedDiff = new HashMap<DiffResultType, List<String>>();
	
	/** Whether it is a change diff. As alternative to insert or delete.*/
	protected final boolean diffTypeIsChange;
	
	
	/**
	 * Constructor.
	 * @param delta The base diff delta for the lines.
	 */
	public DiffResult(Delta<String> delta) {
		diffTypeIsChange = (delta.getType() == Delta.TYPE.CHANGE);
		origDiff.put(DiffResultType.LINE, new ArrayList<String>(delta.getOriginal().getLines()));
		origLineNumber = delta.getOriginal().getPosition();
		revisedDiff.put(DiffResultType.LINE, new ArrayList<String>(delta.getRevised().getLines()));
		revisedLineNumber = delta.getRevised().getPosition();
	}
	
	/**
	 * @return Whether it is a change.
	 */
	public boolean isChange(){
		return diffTypeIsChange;
	}

	/** 
	 * @return The line number of the original.
	 */
	public int getOrigLineNumber() {
		return origLineNumber;
	}
	/** 
	 * @return The line number of the derived.
	 */
	public int getRevisedLineNumber() {
		return revisedLineNumber;
	}

	/**
	 * @return The line diff for the original.
	 */
	public List<String> getOrigLine() {
		return new ArrayList<String>(origDiff.get(DiffResultType.LINE));
	}
	
	/** 
	 * @return The line diff of the derived.
	 */
	public List<String> getRevisedLine() {
		return new ArrayList<String>(revisedDiff.get(DiffResultType.LINE));
	}
	
	/** 
	 * @return Contains the original diff for the words. Is only applicable for the 'CHANGE'.
	 */
	public List<String> getOrigWords() {
		if(origDiff.containsKey(DiffResultType.WORD)) {
			return new ArrayList<String>(origDiff.get(DiffResultType.WORD));
		} 
		return null;
	}
	
	/** 
	 * @return Contains the revised diff for the words. Is only applicable for the 'CHANGE'.
	 */
	public List<String> getRevisedWords() {
		if(revisedDiff.containsKey(DiffResultType.WORD)) {
			return new ArrayList<String>(revisedDiff.get(DiffResultType.WORD));
		} 
		return null;
	}
	
	/** 
	 * @return Contains the original diff for the chars. Is only applicable for the 'CHANGE'.
	 */
	public List<String> getOrigChars() {
		if(origDiff.containsKey(DiffResultType.CHAR)) {
			return new ArrayList<String>(origDiff.get(DiffResultType.CHAR));
		} 
		return null;
	}
	
	/** 
	 * @return Contains the revised diff for the chars. Is only applicable for the 'CHANGE'.
	 */
	public List<String> getRevisedChars() {
		if(revisedDiff.containsKey(DiffResultType.CHAR)) {
			return new ArrayList<String>(revisedDiff.get(DiffResultType.CHAR));
		} 
		return null;
	}

	/**
	 * Inserts delta for words only.
	 * Should only be inserted, if it is a 'CHANGE'.
	 * @param delta The word diff delta.
	 */
	public void insertWords(Delta<String> delta) {
		List<String> origWords = origDiff.get(DiffResultType.WORD);
		if(origWords == null) {
			origWords = new ArrayList<String>(delta.getOriginal().getLines());
		} else {
			origWords.addAll(delta.getOriginal().getLines());
		}
		origDiff.put(DiffResultType.WORD, origWords);		
		
		List<String> revisedWords = revisedDiff.get(DiffResultType.WORD);
		if(revisedWords == null) {
			revisedWords = new ArrayList<String>(delta.getRevised().getLines());
		} else {
			revisedWords.addAll(delta.getRevised().getLines());
		}
		revisedDiff.put(DiffResultType.WORD, revisedWords);
	}

	/**
	 * Inserts delta for chars only.
	 * Should only be inserted if it is a 'CHANGE'.
	 * @param delta The char diff delta.
	 */
	public void insertChars(Delta<String> delta) {
		List<String> origChars = origDiff.get(DiffResultType.CHAR);
		if(origChars == null) {
			origChars = new ArrayList<String>(delta.getOriginal().getLines());
		} else {
			origChars.addAll(delta.getOriginal().getLines());
		}
		origDiff.put(DiffResultType.CHAR, origChars);

		List<String> revisedChars = revisedDiff.get(DiffResultType.CHAR);
		if(revisedChars == null) {
			revisedChars = new ArrayList<String>(delta.getRevised().getLines());
		} else {
			revisedChars.addAll(delta.getRevised().getLines());
		}
		revisedDiff.put(DiffResultType.CHAR, revisedChars);
	}
	
	/**
	 * @param type The type of orig diff result to extract.
	 * @return The number of characters in the diff for the orig of the given type.
	 */
	public int getOrigDiffSize(DiffResultType type) {
		return getLineDiffSize(origDiff.get(type));
	}
	
	/**
	 * @param type The type of revised diff result to extract.
	 * @return The number of characters in the diff for the revised of the given type.
	 */
	public int getRevisedDiffSize(DiffResultType type) {
		return getLineDiffSize(revisedDiff.get(type));
	}
	
	/**
	 * Counts the number of characters in the strings in the list.
	 * @param list The list of strings to count the number of chars in.
	 * @return The number of characters in the strings in the list.
	 */
	protected int getLineDiffSize(List<String> list) {
		if(list == null) {
			return -1;
		}
		int res = 0;
		for(String s : list) {
			res += s.length();
		}
		return res;		
	}

}
