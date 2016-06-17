package dk.netarkivet.research.diff;

import java.io.InputStream;

public interface DiffFiles {

	DiffResults diff(InputStream is1, InputStream is2);
}
