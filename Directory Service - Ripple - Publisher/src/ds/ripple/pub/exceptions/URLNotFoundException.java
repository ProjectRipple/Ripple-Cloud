package ds.ripple.pub.exceptions;

public class URLNotFoundException extends Exception {
	public final static String ERROR_CODE = "-3";
	
	public URLNotFoundException() {
		super();
	}
	
	public URLNotFoundException(String message) {
		super(message);
	}
}
