package dk.netarkivet.research.diff;

import difflib.Patch;

public class DiffResults {

	protected final Patch<String> diffLines;
	protected final Patch<String> diffWords;
	protected final Patch<String> diffCharacters;
	
	public DiffResults(Patch<String> diffLines, Patch<String> diffWords, Patch<String> diffCharacters) {
		this.diffLines = diffLines;
		this.diffWords = diffWords;
		this.diffCharacters = diffCharacters;
	}
}
