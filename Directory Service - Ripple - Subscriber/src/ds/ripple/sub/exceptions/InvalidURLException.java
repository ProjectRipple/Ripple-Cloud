package ds.ripple.sub.exceptions;

public class InvalidURLException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public final static String ERROR_MESSAGE = "Given URL is invalid";
	
	public InvalidURLException() {
		super();
	}
	
	public InvalidURLException(String message) {
		super(message);
	}

}
