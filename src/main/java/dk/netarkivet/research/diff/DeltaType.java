package dk.netarkivet.research.diff;

import difflib.Delta;

/**
 * The type of delta (diff group).
 */
public enum DeltaType {
	/** When it is a diff group, that has been modified in both directions.*/
	CHANGE,
	/** When it is a diff group, that has only been modified in one direction.*/
	INSERT_DELETE;
	
	/**
	 * Extracts the delta type from the delta group.
	 * @param d The delta diff group.
	 * @return The type of delta.
	 */
	public static DeltaType extractFromDelta(Delta<String> d) {
		if(d.getType() == Delta.TYPE.CHANGE) {
			return CHANGE;
		} else {
			return INSERT_DELETE;
		}
	}
	
	/**
	 * Extracts from the boolean for whether or not it is a change.
	 * @param isChange Whether or not it is a change.
	 * @return The type of delta.
	 */
	public static DeltaType extractFromBoolean(boolean isChange) {
		if(isChange) {
			return CHANGE;
		} else {
			return INSERT_DELETE;
		}
	}
}
