package ds.ripple.sub;

public interface SubscriptionListener {
	public void publishedMessage(String topic, String message);
}
