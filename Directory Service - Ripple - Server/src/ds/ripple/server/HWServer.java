package ds.ripple.server;

import java.io.IOException;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;
import org.zeromq.ZMQException;


import ds.ripple.pub.util.DirectoryPublisher;
import ds.ripple.pub.util.MessageBuilder;

public class HWServer {
	
	private String reply;
	private Directory dir = new Directory();
	private DirectoryPublisher dirPub;
	
	private Context context;
	private Socket responder;
	private Timer tmr;
	
	private Thread server;
	
	private boolean isRunning = false;
	
	private static final byte PUBLISHER_REGISTRATION = 0x01;
	private static final byte PUBLISHER_DEREGISTRATION = 0x02;
	private static final byte PUBLISHER_INFO_UPDATE = 0x04;
	private static final byte PUBLISHER_ALIVE = 0x05;
	private static final byte OBSERVER_MAP_REQUEST = 0x03;
	
	/**
	 * creates instance of HWServer class and creates server socket 
	 * binds to local address on port 5555.
	 * Also creates new instance for DirectoryPublisher 
	 * class
	 */
	public HWServer() {
		context = ZMQ.context(1);
		responder = context.socket(ZMQ.REP);
		responder.bind("tcp://*:5555");
		dirPub = new DirectoryPublisher(context);
		 
	}
	
	/**
	 * starts new thread to receive requests
	 * from clients on the port 5555
	 * starts new timer for updating publisher list
	 * checks for the type of request based on the 
	 * first byte.
	 */
	
	public void start() {
		isRunning = true;
		tmr = new Timer();
		tmr.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				if (dir.updatePubAlive()) {
					dirPub.publish();
				}
			}
		}, 60 * 1000, 60 * 1000);

		server = new Thread(new Runnable() {
					
			@Override
			public void run() {
				while (isRunning) {
					byte[] request = null;
					try {
						request = responder.recv(0);
					} catch (ZMQException e) {
						if (isRunning) {
							// unexpected error, nothing we can do, let's die
							e.printStackTrace();
							isRunning = false;
							break;
						} else {
							// this was likely done on purpose, exit silently
							break;
						}
					}
					byte[] requestPayload = Arrays.copyOfRange(request, 1, request.length);
					byte requestHeader = request[0];
					switch (requestHeader) {
						case PUBLISHER_REGISTRATION:
							int pubId = dir.pubisherRegistration(requestPayload);
							try {
								responder.send(MessageBuilder.buildMsg(pubId), 0);
							} catch (IOException e) {
								e.printStackTrace();
							}
							System.out.println("New publisher registered!");
							if (pubId != Directory.ERROR_URL_ALREADY_EXISTS || pubId != Directory.ERROR_PUBLISHER_PARSING_ERROR) {
								dirPub.publish();
							}
							break;
						case PUBLISHER_DEREGISTRATION:
							String deregReplyCode = dir.publisherDeregistration(requestPayload);
							try {
								responder.send(MessageBuilder.buildMsg(reply), 0);
							} catch (IOException e) {
								e.printStackTrace();
							}
							if (deregReplyCode.equals(Directory.DEREGISTRATION_OK)) {
								dirPub.publish();
							}
							System.out.println("A publisher deregistered!");
							break;
						case PUBLISHER_INFO_UPDATE:
							String updateReplyCode = dir.updatePublisherInfo(requestPayload);
							try {
								responder.send(MessageBuilder.buildMsg(updateReplyCode), 0);
								dirPub.publish();
							} catch (IOException e) {
								e.printStackTrace();
							}
							break;
						case PUBLISHER_ALIVE:
							int alive = dir.pubAlive(requestPayload);
							try {
								responder.send(MessageBuilder.buildMsg(alive + ""), 0);
							} catch (IOException e) {
								e.printStackTrace();
							}
							break;
						case OBSERVER_MAP_REQUEST:
							try {
								responder.send(MessageBuilder.buildMsg(Directory.getDirectoryList()), 0);
							} catch (IOException e) {
								e.printStackTrace();
							}
							break;
						default:
							// handle unrecognized commands somehow
							break;
					}
				}			
				context.close();
			}
		});
		server.start();
	}
	
	/**
	 *closes the existing responder socket and
	 *stops timer running for updating publisher list
	 * closes the context.
	 */
	public void stop() {

		responder.close();
		tmr.cancel();

		isRunning = false;
		context.close();
	}
}
