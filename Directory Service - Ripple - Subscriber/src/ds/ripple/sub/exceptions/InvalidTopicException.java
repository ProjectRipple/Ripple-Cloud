package ds.ripple.sub.exceptions;

public class InvalidTopicException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public final static String ERROR_MESSAGE = "Given topic is invalid";
	
	public InvalidTopicException() {
		super();
	}
	
	public InvalidTopicException(String message) {
		super(message);
	}
}
