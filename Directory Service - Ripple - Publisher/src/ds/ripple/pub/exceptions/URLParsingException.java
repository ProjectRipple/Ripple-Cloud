package ds.ripple.pub.exceptions;

public class URLParsingException extends Exception {
	public final static int ERROR_CODE = -2;
	
	public URLParsingException() {
		super();
	}
	
	public URLParsingException(String message) {
		super(message);
	}
}
