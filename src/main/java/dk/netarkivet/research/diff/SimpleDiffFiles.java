package dk.netarkivet.research.diff;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

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
	public DiffResults diff(InputStream origIs, InputStream revisedIs) {
		try {
			List<String> origLines = StreamUtils.extractInputStreamAsLines(origIs);
			List<String> revisedLines = StreamUtils.extractInputStreamAsLines(revisedIs);
			Patch<String> linePatch = DiffUtils.diff(origLines, revisedLines);
			
			List<String> origWords = ListUtils.convertLinesToListOfWords(origLines);
			List<String> revisedWords = ListUtils.convertLinesToListOfWords(revisedLines);
			Patch<String> wordPatch = DiffUtils.diff(origWords, revisedWords);
			
			List<String> origChars = ListUtils.convertStringsToListOfCharacters(origLines);
			List<String> revisedChars = ListUtils.convertStringsToListOfCharacters(revisedLines);
			Patch<String> charPatch = DiffUtils.diff(origChars, revisedChars);
			
			return new DiffResults(linePatch, wordPatch, charPatch);
		} catch (IOException e) {
			throw new IllegalStateException("Cannot perform diff", e);
		}
	}
}
