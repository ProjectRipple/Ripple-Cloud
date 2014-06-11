package ds.ripple.sub;

import java.io.IOException;
import java.util.ArrayList;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;

import ds.ripple.sub.exceptions.InvalidTopicException;
import ds.ripple.sub.util.MessageBuilder;

public class Subscription {
	private Socket mSocket;
	private Context mContext;
	private SubscriptionListener mListener;
	private ArrayList<String> mTopics = new ArrayList<String>();
	private boolean mIsSubscriptionActive = false;
	private String mPublisherURL;
	
	protected Subscription(Context context, String publisherURL, SubscriptionListener listener) {
		mContext = context;
		mPublisherURL = new String(publisherURL);
		mSocket = mContext.socket(ZMQ.SUB);
		mSocket.connect(publisherURL);
		mListener = listener;
		new Thread(new IncomingMessageListener()).start();
	}
	
	protected void subscribe(String topic) {
		mTopics.add(topic);
		mSocket.subscribe(topic.getBytes());
	}
	
	protected void subscribe(String[] topics) {
		for (String s : topics) {
			mTopics.add(s);
			mSocket.subscribe(s.getBytes());
		}
	}
	
	protected void unsubscribe(String topic) throws InvalidTopicException {
		boolean ret = mTopics.remove(topic);
		if (ret) {
			mSocket.unsubscribe(topic.getBytes());
		} else {
			throw new InvalidTopicException(InvalidTopicException.ERROR_MESSAGE);
		}
	}
	
	protected void unsubscribe(String[] topics) throws InvalidTopicException {
		for (String s : topics) {
			unsubscribe(s);
		}
	}
	
	protected String getPublisherURL() {
		return new String(mPublisherURL);
	}
	
	protected void stop() {
		mListener = null;
		mSocket.close();
		mIsSubscriptionActive = false;
	}
	
	class IncomingMessageListener implements Runnable {
		
		public IncomingMessageListener() {
			
		}
		
		@Override
		public void run() {
			mIsSubscriptionActive = true;
			while(mIsSubscriptionActive) {
				// process header (topic)
				byte[] header = mSocket.recv(0);
				try {
					if (!mTopics.contains(new String(header, "UTF-8"))) {
						// we weren't supposed to receive this message anyway, drop it
						break;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				// process the payload (actual message)
				byte[] payload = mSocket.recv(0);
				try {
					String topic = new String(header, "UTF-8");
					String message = (String)MessageBuilder.deserialize(payload);
					// push the message to the listener
					mListener.publishedMessage(topic, message);
				} catch (ClassNotFoundException | IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
