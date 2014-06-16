package ds.ripple.pub.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import ds.ripple.common.PublisherRecord;


public class MessageBuilder {
	private final static byte MSG_REGISTER_CODE = 0x01;
	private final static byte MSG_DEREGISTER_CODE = 0x02;
	private final static byte MSG_UPDATE_CODE = 0x04;
	private final static byte MSG_KEEP_ALIVE = 0x05;
	
	/**
	 * Constructs registration message.
	 * 
	 * @param pr
	 *            PublisherRecord object that will be sent to the Directory
	 *            Services server.
	 * @return Serialized registration message on success, null on failure
	 */
	public static byte[] getRegisterMsg(PublisherRecord pr) {
		return buildMsg(MSG_REGISTER_CODE, pr);
	}
	
	/**
	 * Constructs deregistration message
	 * 
	 * @param s
	 *            String that contains an ID assigned to the publisher by the
	 *            Directory Services server (id is assigned when a publisher
	 *            registers itself at the Directory Services server)
	 * @return Serialized deregistration message on success, null on failure
	 */
	public static byte[] getDeregisterMsg(String s) {
		return buildMsg(MSG_DEREGISTER_CODE, s);
	}
	
	/**
	 * Constructs update message that will be used to update information about
	 * this publisher.
	 * 
	 * @param pr
	 *            PublisherRecord object must contain ID of the publisher.
	 *            Method will return null if the PublisherRecord object doesn't
	 *            have an ID
	 * @return Serialized update message on success, null on failure
	 */
	public static byte[] getUpdateMsg(PublisherRecord pr) {
		if (pr.getPub_Id() == null)
			return null;
		return buildMsg(MSG_UPDATE_CODE, pr);
	}
	
	private static byte[] buildMsg(byte msgCode, Object msgPayload) {
		try {
			byte[] obj = serialize(msgPayload);
			
			byte[] msg = new byte[1 + obj.length + 1];
			msg[0] = msgCode;
			
			System.arraycopy(obj, 0, msg, 1, obj.length);
			
			return msg;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Constructs keep-alive message
	 * 
	 * @param s
	 *            String that contains an ID assigned to the publisher by the
	 *            Directory Services server (id is assigned when a publisher
	 *            registers itself at the Directory Services server)
	 * @return Serialized keep alive message on success, null on failure
	 */
	public static byte[] getKeepAliveMsg(String publisherID) {
		return buildMsg(MSG_KEEP_ALIVE, publisherID);
	}
	
	/**
	 * Serializes an object
	 * @param obj Object to serialize
	 * @return Serialized object
	 * @throws IOException
	 */
	public static byte[] serialize(Object obj) throws IOException {
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		ObjectOutputStream o = new ObjectOutputStream(b);
	    o.writeObject(obj);
		o.close();
		o.close();
		return b.toByteArray();
	}
	
	/**
	 * Deserializes an object
	 * @param bytes Object to deserialize
	 * @return Deserialized object
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public static Object deserialize(byte[] bytes) throws ClassNotFoundException, IOException {
		ByteArrayInputStream b= new ByteArrayInputStream(bytes);
		ObjectInputStream o = new ObjectInputStream(b);
		return o.readObject();
	}
}
