package dk.netarkivet.research.diff;

import java.io.InputStream;

public interface DiffFiles {

	DiffResultWrapper diff(InputStream is1, InputStream is2);
}
