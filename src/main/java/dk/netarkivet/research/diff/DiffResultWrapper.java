package dk.netarkivet.research.diff;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Wraps the diff results, and has methods for extraction.
 * 
 */
public class DiffResultWrapper {
	/** The diff results.*/
	protected final List<DiffResult> results;
	
	/**
	 * Constructor.
	 */
	public DiffResultWrapper() {
		this.results = new ArrayList<DiffResult>();
	}
	
	/**
	 * Adds a new result to the list.
	 * @param result The new result.
	 */
	public void addResult(DiffResult result) {
		results.add(result);
	}
	
	/**
	 * @return The results.
	 */
	public Collection<DiffResult> getResults() {
		return results;
	}
	
	/**
	 * @param type The type of diff result to extract the number of chars for.
	 * @return The number of chars in all the orig diff of the given type.
	 */
	public int getOrigDiffCharCount(DiffResultType type) {
		int res = 0;
		for(DiffResult dr : results) {
			res += dr.getOrigDiffSize(type);
		}
		return res;
	}

	/**
	 * @param type The type of diff result to extract the number of chars for.
	 * @return The number of chars in the revised diff for the given type..
	 */
	public int getRevisedDiffCharCount(DiffResultType type) {
		int res = 0;
		for(DiffResult dr : results) {
			res += dr.getRevisedDiffSize(type);
		}
		return res;
	}
	
	/**
	 * @param change Whether or not it should be the change lines or the non-change lines (inserts / deletes).
	 * @return The number of either change lines or non-change lines from the original.
	 */
	public int getOrigLineCount(boolean change) {
		int res = 0;
		for(DiffResult dr : results) {
			if(dr.isChange() == change) {
				res += dr.getOrigLineNumber();
			}
		}
		return res;
	}
	
	/**
	 * @param change Whether or not it should be the change lines or the non-change lines (inserts / deletes).
	 * @return The number of insert/delete lines from the revised.
	 */
	public int getRevisedLineCount(boolean change) {
		int res = 0;
		for(DiffResult dr : results) {
			if(dr.isChange() == change) {
				res += dr.getRevisedLineNumber();
			}
		}
		return res;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for(DiffResult dr : results) {
			sb.append(dr.isChange() ? "Change: " : "Not change: ");
			sb.append("\n");
			for(int i = 0; i < dr.getOrigLine().size(); i++) {
				sb.append((dr.getOrigLineNumber() + i) + " > " + dr.getOrigLine().get(i) + "\n");
			}
			for(int i = 0; i < dr.getRevisedLine().size(); i++) {
				sb.append((dr.getRevisedLineNumber() + i) + " < " + dr.getRevisedLine().get(i) + "\n");
			}
			
			if(dr.isChange()) {
				sb.append("w > " + dr.getOrigWords() + "\n");
				sb.append("w < " + dr.getRevisedWords() + "\n");
				sb.append("c > " + dr.getOrigChars() + "\n");
				sb.append("c < " + dr.getRevisedChars() + "\n");
			}
		}
		return sb.toString();
	}
}
