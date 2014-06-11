package ds.ripple.sub;

import java.util.HashMap;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;

import ds.ripple.common.PublisherRecord;
import ds.ripple.obs.MapListener;
import ds.ripple.obs.Observer;
import ds.ripple.sub.exceptions.InvalidTopicException;
import ds.ripple.sub.exceptions.InvalidURLException;

public class Subscriber {
	private final static int DS_PORT = 5555;
	
	private Observer mObserver;
	private Context mContext;
	
	private PublisherList mPubList = new PublisherList();
	private boolean isPubListenerRegistered = false;
	private PublisherListListener mPubListListener = null;
	
	private SubscriptionList mSubList = new SubscriptionList();
	
	class PublisherMapListener implements MapListener {

		@Override
		public void publishedMapUpdate(HashMap<Integer, PublisherRecord> map) {
			mPubList.clearPublisherList();
			for (PublisherRecord pb : map.values()) {
				mPubList.addPublisher(new Publisher(pb.getPub_address(), pb.getTopics()));
			}
			if (isPubListenerRegistered) {
				mPubListListener.publisherListUpdate(new PublisherList(mPubList));
			}
		}
		
	}
	
	public Subscriber(String dsURL) {
		mContext = ZMQ.context(1);
		mObserver = new Observer(dsURL, new PublisherMapListener()); 
		mObserver.connect();
	}
	
	public Subscriber(String dsURL, PublisherListListener pubListListener) {
		isPubListenerRegistered = true;
		mPubListListener = pubListListener;
		mContext = ZMQ.context(1);
		mObserver = new Observer(dsURL, new PublisherMapListener());
		mObserver.connect();
	}
	
	public Publisher[] getPublishersList() {
		return mPubList.getPublishers();
	}
	
	public void displayPublishersList() {
		mPubList.displayPublishers();
	}
	
	public void subscribe(String pubURL, String pubTopic, SubscriptionListener listener) throws InvalidURLException, InvalidTopicException{
		validateURLAndTopic(pubURL, pubTopic);
		mSubList.addSubscription(mContext, pubURL, pubTopic, listener);
	}
	
	public void subscribe(String pubURL, String[] pubTopics, SubscriptionListener listener) throws InvalidURLException, InvalidTopicException {
		for (String s : pubTopics) {
			validateURLAndTopic(pubURL, s);
		}
		mSubList.addSubscription(mContext, pubURL, pubTopics, listener);
	}
	
	public void unsubscribeFromTopic(String pubURL, String pubTopic) throws InvalidTopicException {
		mSubList.removeTopic(pubURL, pubTopic);
	}
	
	public void unsubscribeFromTopics(String pubURL, String[] pubTopics) throws InvalidTopicException {
		for (String s : pubTopics) {
			mSubList.removeTopic(pubURL, s);
		}
	}
	
	public void stopSubscription(String pubURL) throws InvalidURLException {
		mSubList.removeSubscription(pubURL);
	}
	
	private void validateURLAndTopic(String url, String topic) throws InvalidURLException, InvalidTopicException{
		switch (mPubList.containsURLAndTopic(url, topic)) {
			case 0:
				return;
			case -1:
				throw new InvalidTopicException(InvalidTopicException.ERROR_MESSAGE);
			case -2:
				throw new InvalidURLException(InvalidURLException.ERROR_MESSAGE);
		}
	}
}
