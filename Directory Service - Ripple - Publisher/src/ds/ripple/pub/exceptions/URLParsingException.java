package ds.ripple.pub.exceptions;

public class URLParsingException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public final static int ERROR_CODE = -2;
	
	public URLParsingException() {
		super();
	}
	
	public URLParsingException(String message) {
		super(message);
	}
}
