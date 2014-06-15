package ds.ripple.obs;

import java.io.IOException;
import java.util.HashMap;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;

import ds.ripple.common.PublisherRecord;
import ds.ripple.obs.util.MessageBuilder;

/**
 * Observer class provides an API that allows the user to keep track of
 * publishers' information (such as publisher API, publisher topics, etc) that
 * are registered at the Directory Services server.
 * 
 * @author pawel
 * 
 */
public class Observer {
	// port number at which the Directory Services server listens for requests
	private final static int REQ_PORT = 5555;
	// port number that the Directory Services server uses to publish list
	// of publishers' information
	private final static int SUB_PORT = 5556;

	private Context mContext;
	private Socket mSubSocket, mReqSocket;
	private String mdsURL;
	private HashMap<Integer, PublisherRecord> map;
	private MapListener mListener;

	/**
	 * Constructs Observer object. Class that implements MapListener interface
	 * must be provided.
	 * 
	 * @param dsURL
	 *            URL of the Directory Services server. ex: tcp://192.168.0.1
	 * @param listener
	 *            Class that implements MapListener interface
	 */
	public Observer(String dsURL, MapListener listener) {
		assert listener != null : "MapListener object cannot be null";
		mdsURL = dsURL;
		mListener = listener;
	}

	/**
	 * Call this function to connect to the Directory Services server. The
	 * function will send the request to the Directory Services server to obtain
	 * recent list of publishers' information. It will also start the
	 * subscription for updates of the publishers' information
	 * (publishers/topics list).
	 */
	public void connect() {
		mContext = ZMQ.context(1);
		mReqSocket = mContext.socket(ZMQ.REQ);
		mReqSocket.connect(mdsURL + ":" + REQ_PORT);
		mReqSocket.send(MessageBuilder.getMapRequestMsg(), 0);
		byte[] reply = mReqSocket.recv(0);

		processMapRequestReply(reply);
	}

	/**
	 * Deserializes response from the server. Also starts a separate thread that
	 * will listen for publisher/topic list updates.
	 * 
	 * @param reply
	 */
	private void processMapRequestReply(byte[] reply) {
		try {
			map = MessageBuilder.getHashMapFromDSReply(reply);
			mapUpdate();
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		} finally {
			new Thread(new SubscriptionListener()).start();
		}
	}

	/**
	 * Pushes the map (publisher/topic list) to the listener.
	 */
	private void mapUpdate() {
		mListener.publishedMapUpdate(map);
	}

	/**
	 * This class connects to the Directory Services server, and starts
	 * listening on port (5556) that the Directory Services uses to send updates
	 * about the publisher/topic list.
	 * 
	 * @author pawel
	 * 
	 */
	class SubscriptionListener implements Runnable {

		/**
		 * Creates & opens a subscriber socket.
		 */
		public SubscriptionListener() {
			mSubSocket = mContext.socket(ZMQ.SUB);
			mSubSocket.connect(mdsURL + ":" + SUB_PORT);
			mSubSocket.subscribe("DIR".getBytes());
		}

		/**
		 * Starts the thread to listen for updates.
		 */
		@Override
		public void run() {
			while (true) {
				byte[] bytes = mSubSocket.recv(0);
				try {
					if (!(new String(bytes, "UTF-8").equals("DIR"))) {
						break;
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}

				byte[] payload = mSubSocket.recv(0);
				try {
					map = MessageBuilder.getHashMapFromDSReply(payload);
					
					mapUpdate();
				} catch (ClassNotFoundException | IOException e) {
					System.out
							.println("Error while processing Directory Services reply");
					e.printStackTrace();
				}
			}
		}

	}
}
