package ds.ripple.pub.exceptions;

public class TopicNotRegisteredException extends Exception {
	private static final long serialVersionUID = 1L;

	public TopicNotRegisteredException() {
		super();
	}
	
	public TopicNotRegisteredException(String message) {
		super(message);
	}
}
