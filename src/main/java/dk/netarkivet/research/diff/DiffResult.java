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
	
	/** The type of delta for this diff result, e.g. is it a change or a insert/delete.*/
	protected final DeltaType diffDeltaType;
	
	
	/**
	 * Constructor.
	 * @param delta The base diff delta for the lines.
	 */
	public DiffResult(Delta<String> delta) {
		this.diffDeltaType = DeltaType.extractFromDelta(delta);
		this.origDiff.put(DiffResultType.LINE, new ArrayList<String>(delta.getOriginal().getLines()));
		this.origLineNumber = delta.getOriginal().getPosition();
		this.revisedDiff.put(DiffResultType.LINE, new ArrayList<String>(delta.getRevised().getLines()));
		this.revisedLineNumber = delta.getRevised().getPosition();
	}
	
	/**
	 * Constructor.
	 * @param isChange Whether it has type CHANGE.
	 * @param origDiffLines The orig lines for the diff.
	 * @param origLineNumber The orig line number for the diff.
	 * @param revisedDiffLines The revised lines for the diff.
	 * @param revisedLineNumber The revised line number for the diff.
	 */
	public DiffResult(boolean isChange, List<String> origDiffLines, int origLineNumber, 
			List<String> revisedDiffLines, int revisedLineNumber) {
		this.diffDeltaType = DeltaType.extractFromBoolean(isChange);
		this.origDiff.put(DiffResultType.LINE, origDiffLines);
		this.origLineNumber = origLineNumber;
		this.revisedDiff.put(DiffResultType.LINE, revisedDiffLines);
		this.revisedLineNumber = revisedLineNumber;
	}
	
	/**
	 * @return Whether it is a change.
	 */
	public DeltaType getDeltaType(){
		return diffDeltaType;
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
	 * @param type The type of the orig results, which is to be retrieved. 
	 * @return Contains the original diff for the given result type. 
	 * Is should only be applicable for 'CHAR' and 'WORD' if 'CHANGE' is true.
	 */
	public List<String> getOrigResultList(DiffResultType type) {
		if(origDiff.containsKey(type)) {
			return new ArrayList<String>(origDiff.get(type));
		} 
		return new ArrayList<String>();
	}
	
	/** 
	 * @param type The type of the revised results, which is to be retrieved. 
	 * @return Contains the revised diff for the given result type. 
	 * Is should only be applicable for 'CHAR' and 'WORD' if 'CHANGE' is true.
	 */
	public List<String> getRevisedResultList(DiffResultType type) {
		if(revisedDiff.containsKey(type)) {
			return new ArrayList<String>(revisedDiff.get(type));
		} 
		return new ArrayList<String>();
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
			return 0;
		}
		int res = 0;
		for(String s : list) {
			res += s.length();
		}
		return res;		
	}
}
