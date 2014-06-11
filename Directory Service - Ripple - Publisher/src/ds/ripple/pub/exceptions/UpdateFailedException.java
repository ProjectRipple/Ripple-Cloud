package ds.ripple.pub.exceptions;

public class UpdateFailedException extends Exception {
	public final static int ERROR_CODE = -4;
	
	public UpdateFailedException() {
		super();
	}
	
	public UpdateFailedException(String message) {
		super(message);
	}
}
