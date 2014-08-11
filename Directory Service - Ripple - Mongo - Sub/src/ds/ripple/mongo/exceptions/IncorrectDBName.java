package ds.ripple.mongo.exceptions;

public class IncorrectDBName extends Exception {
	private static final long serialVersionUID = 1L;
	
	public final static int ERROR_CODE = -1;
	
	public IncorrectDBName() {
		super();
	}
	public IncorrectDBName(String message) {
		super(message);
	}
}
