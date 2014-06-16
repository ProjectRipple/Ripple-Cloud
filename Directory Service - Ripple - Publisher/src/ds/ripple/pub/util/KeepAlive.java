package ds.ripple.pub.util;

import java.io.IOException;

import org.zeromq.ZMQ.Socket;

import ds.ripple.pub.Publisher;
import ds.ripple.pub.exceptions.PublisherNotRegisteredException;
import ds.ripple.pub.exceptions.URLAlreadyExistsException;
import ds.ripple.pub.exceptions.URLParsingException;

public class KeepAlive implements Runnable {
	private Socket mSocket;
	private int mPubId;
	private Publisher mPub;
	private final byte[] mKeepALiveMsg;
	
	public KeepAlive(Publisher publisher, Socket requestSocket, int pubisherID) {
		mSocket = requestSocket;
		mPubId = pubisherID;
		mKeepALiveMsg = MessageBuilder.getKeepAliveMsg(pubisherID + "");
		mPub = publisher;
	}
	
	@Override
	public void run() {
		try {
			mSocket.send(mKeepALiveMsg, 0);
			String response = (String)MessageBuilder.deserialize(mSocket.recv(0));
			if (response.equals(mPubId + "")) {
				// everything is good, exit silently
				return;
			}
			if (response.equals(PublisherNotRegisteredException.ERROR_CODE)) {
				// directory service dropped this publisher from its list
				// let's try to re-register it again
				mPub.register();
			}
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		} catch (URLAlreadyExistsException | URLParsingException e1) {
			// if we are here, that means the connection to the directory services
			// has been lost
			e1.printStackTrace();
			mPub.terminate();
		}
	} 

}
