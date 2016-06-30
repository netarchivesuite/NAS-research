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
	 * @param deltaType The type of diffs to calculate the diffs for.
	 * @return The number of chars in all the orig diff of the given type.
	 */
	public int getOrigDiffCharCount(DiffResultType type, DeltaType deltaType) {
		int res = 0;
		for(DiffResult dr : results) {
			if(dr.getDeltaType() == deltaType) {
				res += dr.getOrigDiffSize(type);
			}
		}
		return res;
	}

	/**
	 * @param type The type of diff result to extract the number of chars for.
	 * @param deltaType The type of diffs to calculate the diffs for.
	 * @return The number of chars in the revised diff for the given type..
	 */
	public int getRevisedDiffCharCount(DiffResultType type, DeltaType deltaType) {
		int res = 0;
		for(DiffResult dr : results) {
			if(dr.getDeltaType() == deltaType) {
				res += dr.getRevisedDiffSize(type);
			}
		}
		return res;
	}
	
	/**
	 * @param diffType the type of diff to calculate its number of groups.
	 * @param deltaType The type of diffs to calculate lines for.
	 * @return The number of elements of the given diff type from the original.
	 */
	public int getOrigGroupCount(DiffResultType diffType, DeltaType deltaType) {
		int res = 0;
		for(DiffResult dr : results) {
			if(dr.getDeltaType() == deltaType) {
				res += dr.getOrigResultList(diffType).size();
			}
		}
		return res;
	}
	
	/**
	 * @param diffType the type of diff to calculate its number of groups.
	 * @param deltaType The type of diffs to calculate lines for.
	 * @return The number of elements of the given diff type from the revised.
	 */
	public int getRevisedGroupCount(DiffResultType diffType, DeltaType deltaType) {
		int res = 0;
		for(DiffResult dr : results) {
			if(dr.getDeltaType() == deltaType) {
				res += dr.getRevisedResultList(diffType).size();
			}
		}
		return res;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for(DiffResult dr : results) {
			sb.append(dr.getDeltaType() == DeltaType.CHANGE ? "Change: " : "Not change: ");
			sb.append("\n");
			for(int i = 0; i < dr.getOrigResultList(DiffResultType.LINE).size(); i++) {
				sb.append((dr.getOrigLineNumber() + i) + " > " + dr.getOrigResultList(DiffResultType.LINE).get(i) 
						+ "\n");
			}
			for(int i = 0; i < dr.getRevisedResultList(DiffResultType.LINE).size(); i++) {
				sb.append((dr.getRevisedLineNumber() + i) + " < " 
						+ dr.getRevisedResultList(DiffResultType.LINE).get(i) + "\n");
			}
			
			if(dr.getDeltaType() == DeltaType.CHANGE) {
				sb.append("w > " + dr.getOrigResultList(DiffResultType.WORD) + "\n");
				sb.append("w < " + dr.getRevisedResultList(DiffResultType.WORD)+ "\n");
				sb.append("c > " + dr.getOrigResultList(DiffResultType.CHAR) + "\n");
				sb.append("c < " + dr.getRevisedResultList(DiffResultType.CHAR) + "\n");
			}
		}
		return sb.toString();
	}
}
