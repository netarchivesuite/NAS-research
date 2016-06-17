package dk.netarkivet.research.diff;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;

import difflib.Delta;
import difflib.DiffAlgorithm;
import difflib.DiffUtils;
import difflib.Patch;
import dk.netarkivet.research.utils.StreamUtils;

public class SimpleDiffFiles implements DiffFiles {

	/**
	 * Constructor.
	 */
	public SimpleDiffFiles() {}
	
	@Override
	public DiffResults diff(InputStream origIs, InputStream revisedIs) {
		try {
			List<String> orig = StreamUtils.extractInputStreamAsLines(origIs);
			List<String> revised = StreamUtils.extractInputStreamAsLines(revisedIs);
			Patch<String> patch = DiffUtils.diff(orig, revised);
			for(Delta<String> d : patch.getDeltas()) {
				System.out.println(d.getType() + " -> " + d.getOriginal().getLines());
				System.out.println(d.getType() + " -> " + d.getRevised().getLines());
			}
		} catch (IOException e) {
			throw new IllegalStateException("Cannot perform diff", e);
		}
		return null;
	}
}
