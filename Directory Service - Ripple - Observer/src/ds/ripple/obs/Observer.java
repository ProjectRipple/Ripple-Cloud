package ds.ripple.obs;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;

import ds.ripple.common.PublisherRecord;
import ds.ripple.obs.util.MessageBuilder;

public class Observer {
	private final static int REQ_PORT = 5555;
	private final static int SUB_PORT = 5556;

	private Context mContext;
	private Socket mSubSocket, mReqSocket;
	private String mdsURL;
	private HashMap<Integer, PublisherRecord> map;
	private MapListener mListener;

	public Observer(String dsURL, MapListener listener) {
		mdsURL = dsURL;
		mListener = listener;
	}

	public void connect() {
		mContext = ZMQ.context(1);
		mReqSocket = mContext.socket(ZMQ.REQ);
		mReqSocket.connect(mdsURL + ":" + REQ_PORT);
		mReqSocket.send(MessageBuilder.getMapRequestMsg(), 0);
		byte[] reply = mReqSocket.recv(0);

		processMapRequestReply(reply);
	}

	private void processMapRequestReply(byte[] reply) {
		try {
			map = MessageBuilder.getHashMapFromDSReply(reply);
			mapUpdate();
		} catch (ClassNotFoundException | IOException e) {
			System.out
					.println("Error while processing Directory Services reply");
			e.printStackTrace();
		} finally {
			
			new Thread(new SubscriptionListener()).start();
		}
	}

	private void mapUpdate() {
		mListener.publishedMapUpdate(map);
	}

	class SubscriptionListener implements Runnable {

		public SubscriptionListener() {
			mSubSocket = mContext.socket(ZMQ.SUB);
			mSubSocket.connect(mdsURL + ":" + SUB_PORT);
			mSubSocket.subscribe("DIR".getBytes());
		}

		@Override
		public void run() {
			while (true) {
				byte[] bytes = mSubSocket.recv(0);
				try {
					if (!(new String(bytes,"UTF-8").equals("DIR"))) {
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
					System.out.println("Error while processing Directory Services reply");
					e.printStackTrace();
				}
			}
		}

	}
}
