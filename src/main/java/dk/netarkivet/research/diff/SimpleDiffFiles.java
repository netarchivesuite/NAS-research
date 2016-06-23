package dk.netarkivet.research.diff;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;
import dk.netarkivet.research.utils.ListUtils;
import dk.netarkivet.research.utils.StreamUtils;

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
				res.addResult(extractDiffResult(d, origLines, revisedLines));
			}
			
			return res;
		} catch (IOException e) {
			throw new IllegalStateException("Cannot perform diff", e);
		}
	}
	
	protected DiffResult extractDiffResult(Delta<String> delta, List<String> origLines, List<String> revisedLines) {
		DiffResult res = new DiffResult(delta);
		if(res.diffTypeIsChange) {
			List<String> origWords = ListUtils.convertLinesToListOfWords(origLines);
			List<String> revisedWords = ListUtils.convertLinesToListOfWords(revisedLines);
			Patch<String> wordPatch = DiffUtils.diff(origWords, revisedWords);
			for(Delta<String> d : wordPatch.getDeltas()) {
				res.insertWords(d);
			}
			
			List<String> origChars = ListUtils.convertStringsToListOfCharacters(origLines);
			List<String> revisedChars = ListUtils.convertStringsToListOfCharacters(revisedLines);
			Patch<String> charPatch = DiffUtils.diff(origChars, revisedChars);
			for(Delta<String> d : charPatch.getDeltas()) {
				res.insertChars(d);
			}

		}
		
		return res;
	}	
}
