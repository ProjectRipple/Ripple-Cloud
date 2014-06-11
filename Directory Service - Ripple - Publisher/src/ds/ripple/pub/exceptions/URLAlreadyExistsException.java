package ds.ripple.pub.exceptions;

public class URLAlreadyExistsException extends Exception {
	public final static int ERROR_CODE = -1;
	
	public URLAlreadyExistsException() {
		super();
	}
	public URLAlreadyExistsException(String message) {
		super(message);
	}
}
