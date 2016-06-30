package dk.netarkivet.research.diff;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;
import dk.netarkivet.research.utils.ListUtils;
import dk.netarkivet.research.utils.StreamUtils;

/**
 * Simple diff.
 * Extracts the difference between two simple text input streams.  
 */
public class SimpleDiffFiles implements DiffFiles {

	/**
	 * Constructor.
	 */
	public SimpleDiffFiles() {}
	
	@Override
	public DiffResultWrapper diff(InputStream origIs, InputStream revisedIs) {
		try {
			DiffResultWrapper res = new DiffResultWrapper();
			List<String> origLines = StreamUtils.extractInputStreamAsLines(origIs);
			List<String> revisedLines = StreamUtils.extractInputStreamAsLines(revisedIs);
			Patch<String> linePatch = DiffUtils.diff(origLines, revisedLines);
			
			for(Delta<String> d : linePatch.getDeltas()) {
				res.addResult(extractDiffResult(d));
			}
			
			return res;
		} catch (IOException e) {
			throw new IllegalStateException("Cannot perform diff", e);
		}
	}
	
	/**
	 * Extracts the results for a given delta.
	 * If it is of the type CHANGE, then deltas for words and chars will also be calculated.
	 * @param delta The complete delta.
	 * @return The results of the diff.
	 */
	protected DiffResult extractDiffResult(Delta<String> delta) {
		DiffResult res = new DiffResult(delta);
		if(res.diffDeltaType == DeltaType.CHANGE) {
			List<String> origWords = ListUtils.convertLinesToListOfWords(delta.getOriginal().getLines());
			List<String> revisedWords = ListUtils.convertLinesToListOfWords(delta.getRevised().getLines());
			Patch<String> wordPatch = DiffUtils.diff(origWords, revisedWords);
			for(Delta<String> d : wordPatch.getDeltas()) {
				res.insertWords(d);
			}
			
			List<String> origChars = ListUtils.convertStringsToListOfCharacters(delta.getOriginal().getLines());
			List<String> revisedChars = ListUtils.convertStringsToListOfCharacters(delta.getRevised().getLines());
			Patch<String> charPatch = DiffUtils.diff(origChars, revisedChars);
			for(Delta<String> d : charPatch.getDeltas()) {
				res.insertChars(d);
			}

		}
		
		return res;
	}	
}
