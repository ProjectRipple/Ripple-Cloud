package ds.ripple.pub.exceptions;

public class UpdateFailedException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public final static int ERROR_CODE = -4;
	
	public UpdateFailedException() {
		super();
	}
	
	public UpdateFailedException(String message) {
		super(message);
	}
}
