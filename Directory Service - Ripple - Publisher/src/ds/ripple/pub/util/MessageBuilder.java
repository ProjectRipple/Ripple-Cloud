package ds.ripple.pub.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;

import ds.ripple.common.PublisherRecord;


public class MessageBuilder {
	private final static byte MSG_REGISTER_CODE = 0x01;
	private final static byte MSG_DEREGISTER_CODE = 0x02;
	private final static byte MSG_UPDATE_CODE = 0x04;
	
	public static byte[] getRegisterMsg(PublisherRecord pr) {
		return buildMsg(MSG_REGISTER_CODE, pr);
	}
	
	public static byte[] getDeregisterMsg(String s) {
		return buildMsg(MSG_DEREGISTER_CODE, s);
	}
	
	public static byte[] getUpdateMsg(PublisherRecord pr) {
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
	
	public static byte[] serialize(Object obj) throws IOException {
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		ObjectOutputStream o = new ObjectOutputStream(b);
	    o.writeObject(obj);
		o.close();
		o.close();
		return b.toByteArray();
	}
	
	public static Object deserialize(byte[] bytes) throws ClassNotFoundException, IOException {
		ByteArrayInputStream b= new ByteArrayInputStream(bytes);
		ObjectInputStream o = new ObjectInputStream(b);
		return o.readObject();
	}
}
