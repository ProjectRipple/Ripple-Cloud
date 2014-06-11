package ds.ripple.sub.exceptions;

public class InvalidURLException extends Exception {
	public final static String ERROR_MESSAGE = "Given URL is invalid";
	
	public InvalidURLException() {
		super();
	}
	
	public InvalidURLException(String message) {
		super(message);
	}

}
