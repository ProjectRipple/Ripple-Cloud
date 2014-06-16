package ds.ripple.sub;

import java.io.IOException;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;

import ds.ripple.common.XML.Event;
import ds.ripple.common.XML.XMLMessage;

public class RippleEventSubscription {
	private static final String PUB_EVENT_TOPIC = "EVENT";
	
	private Socket mSocket;
	private Context mContext;
	private RippleEventListener mListener;
	private String mTopic;
	private boolean mIsSubscriptionActive = false;
	private String mPublisherURL;
	
	protected RippleEventSubscription(Context context, String publisherURL, RippleEventListener listener) {
		mContext = context;
		mPublisherURL = new String(publisherURL);
		mSocket = mContext.socket(ZMQ.SUB);
		mSocket.connect(publisherURL);
		mListener = listener;
		new Thread(new IncomingMessageListener()).start();
	}
	
	protected void subscribe(String topic) {
		mTopic = topic;
	}
	
	protected void stop() {
		mListener = null;
		mSocket.close();
		mIsSubscriptionActive = false;
	}
	
	protected String getPublisherURL() {
		return new String(mPublisherURL);
	}
	
	class IncomingMessageListener implements Runnable {

		public IncomingMessageListener() {
			
		}
		
		@Override
		public void run() {
			mSocket.subscribe(PUB_EVENT_TOPIC.getBytes());
			mIsSubscriptionActive = true;
			while (mIsSubscriptionActive) {
				// process header (topic)
				
				byte[] header = mSocket.recv(0);
				try {
					if (!mTopic.equals(new String(header, "UTF-8"))) {
						// we weren't supposed to receive this message anyway, drop it
						break;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				// process the payload (actual message)
				byte[] payload = mSocket.recv(0);
				try {
					Event e = XMLMessage.unmarshallEventObject(payload);
					mListener.publishedEvent(e);
				} catch (ClassCastException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
}
