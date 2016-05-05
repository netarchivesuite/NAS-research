package dk.netarkivet.research.exception;

public class ArgumentCheck extends RuntimeException {
	
	public ArgumentCheck(String message) {
		super(message);
	}
	
	public ArgumentCheck(String message, Throwable t) {
		super(message, t);
	}
	
	public static void checkNotNull(Object object, String message) {
		if(object == null) {
			throw new ArgumentCheck("Argument may not be null: " + message);
		}
	}
}
