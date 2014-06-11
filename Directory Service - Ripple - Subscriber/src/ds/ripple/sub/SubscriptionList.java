package ds.ripple.sub;

import java.util.ArrayList;

import org.zeromq.ZMQ.Context;

import ds.ripple.sub.exceptions.InvalidTopicException;
import ds.ripple.sub.exceptions.InvalidURLException;

public class SubscriptionList {
	private ArrayList<Subscription> mSubscriptions;
	
	protected SubscriptionList() {
		mSubscriptions = new ArrayList<Subscription>();
	}
	
	protected void addSubscription(Context context, String publisherURL, String topic, SubscriptionListener listener) {
		Subscription sub = new Subscription(context, publisherURL, listener);
		mSubscriptions.add(sub);
		sub.subscribe(topic);
	}
	
	protected void addSubscription(Context context, String publisherURL, String[] topics, SubscriptionListener listener) {
		Subscription sub = new Subscription(context, publisherURL, listener);
		mSubscriptions.add(sub);
		sub.subscribe(topics);
	}
	
	protected void removeSubscription(String publisherURL) throws InvalidURLException {
		for (Subscription s : mSubscriptions) {
			if (s.getPublisherURL().equals(publisherURL)) {
				s.stop();
				mSubscriptions.remove(s);
				return;
			}
		}
		// if we ever at this point, that means URL was not found
		throw new InvalidURLException(InvalidURLException.ERROR_MESSAGE);
	}
	
	protected void addTopic(String publisherURL, String topic) {
		for (Subscription s : mSubscriptions) {
			if (s.getPublisherURL().equals(publisherURL)) {
				s.subscribe(topic);
				return;
			}
		}
	}
	
	protected void removeTopic(String publisherURL, String topic) throws InvalidTopicException {
		for (Subscription s : mSubscriptions) {
			if (s.getPublisherURL().equals(publisherURL)) {
				s.unsubscribe(topic);
				return;
			}
		}
	}
}
