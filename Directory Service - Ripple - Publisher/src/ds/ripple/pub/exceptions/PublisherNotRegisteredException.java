package ds.ripple.pub.exceptions;

public class PublisherNotRegisteredException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public final static String ERROR_CODE = "-5";
	
	public PublisherNotRegisteredException() {
		super();
	}
	
	public PublisherNotRegisteredException(String message) {
		super(message);
	}
}
