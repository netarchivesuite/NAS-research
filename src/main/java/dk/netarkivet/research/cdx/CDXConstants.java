package dk.netarkivet.research.cdx;

/**
 * Container of constants for the CDX indices.
 */
public class CDXConstants {
	/** A canonized url */
	public static final char CDX_CHAR_CANONIZED_URL = 'A';
	/** B news group - UNUSED! */
	public static final char CDX_CHAR_NEWS_GROUP = 'B';
	/** C rulespace category - UNUSED! */
	public static final char CDX_CHAR_RULESPACE_CATEGORY = 'C';
	/** D compressed dat file offset - UNUSED! */
	public static final char CDX_CHAR_COMPRESSED_DAT_FILE_OFFSET = 'D';
	/** F canonized frame - UNUSED! */
	public static final char CDX_CHAR_CANONIZED_FRAME = 'F';
	/** G multi-columm language description - UNUSED! */
	public static final char CDX_CHAR_MULTI_COLUMM_LANGUAGE_DESCRIPTION = 'G';
	/** H canonized host - UNUSED! */
	public static final char CDX_CHAR_CANONIZED_HOST = 'H';
	/** N massaged url */
	public static final char CDX_CHAR_MASSAGED_URL = 'N';
	/** Q language string - UNUSED! */
	public static final char CDX_CHAR_LANGUAGE_STRING = 'Q';
	/** R canonized redirect - UNUSED! */
	public static final char CDX_CHAR_CANONIZED_REDIRECT = 'R';
	/** U uniqness - UNUSED! */
	public static final char CDX_CHAR_UNIQNESS = 'U';
	/** V compressed arc file offset */
	public static final char CDX_CHAR_COMPRESSED_ARC_FILE_OFFSET = 'V';
	/** a original url */
	public static final char CDX_CHAR_ORIGINAL_URL = 'a';
	/** b date */
	public static final char CDX_CHAR_DATE = 'b';
	/** c old style checksum */
	public static final char CDX_CHAR_OLD_STYLE_CHECKSUM = 'c';
	/** d uncompressed dat file offset - UNUSED! */
	public static final char CDX_CHAR_UNCOMPRESSED_DAT_FILE_OFFSET = 'd';
	/** e IP */
	public static final char CDX_CHAR_IP = 'e';
	/** f frame - UNUSED! */
	public static final char CDX_CHAR_FRAME = 'f';
	/** g file name */
	public static final char CDX_CHAR_FILE_NAME = 'g';
	/** h original host - UNUSED! */
	public static final char CDX_CHAR_ORIGINAL_HOST = 'h';
	/** k new style checksum */
	public static final char CDX_CHAR_NEW_STYLE_CHECKSUM = 'k';
	/** m mime type of original document */
	public static final char CDX_CHAR_MIME_TYPE = 'm';
	/** n arc document length */
	public static final char CDX_CHAR_ARC_DOCUMENT_LENGTH = 'n';
	/** p original path - UNUSED! */
	public static final char CDX_CHAR_ORIGINAL_PATH = 'p';
	/** r redirect */
	public static final char CDX_CHAR_REDIRECT = 'r';
	/** s response code */
	public static final char CDX_CHAR_RESPONSE_CODE = 's';
	/** t title - UNUSED! */
	public static final char CDX_CHAR_TITLE = 't';
	/** v uncompressed arc file offset */
	public static final char CDX_CHAR_UNCOMPRESSED_ARC_FILE_OFFSET = 'v';

	/** The default order of CDX char arguments for a NAS CDX file.*/
	public static final char[] DEFAULT_CDX_CHAR_FORMAT = new char[]{
			CDXConstants.CDX_CHAR_ORIGINAL_URL,
			CDXConstants.CDX_CHAR_DATE,
			CDXConstants.CDX_CHAR_FILE_NAME,
			CDXConstants.CDX_CHAR_COMPRESSED_ARC_FILE_OFFSET,
			CDXConstants.CDX_CHAR_MIME_TYPE,
			CDXConstants.CDX_CHAR_RESPONSE_CODE,
			CDXConstants.CDX_CHAR_NEW_STYLE_CHECKSUM
	};

}
