package ds.ripple.obs.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;

import ds.ripple.common.PublisherRecord;

public class MessageBuilder {
	private final static byte MSG_MAP_REQUEST_CODE = 0x03;
	
	public static byte[] getMapRequestMsg() {
		return buildMsg(MSG_MAP_REQUEST_CODE);
	}
	
	private static byte[] buildMsg(byte msgCode) {
		return new byte[]{msgCode};
	}
	
	public static HashMap<Integer, PublisherRecord> getHashMapFromDSReply(byte[] bytes) throws ClassNotFoundException, IOException {
		try {
			return (HashMap<Integer, PublisherRecord>)deserialize(bytes);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	 
	public static Object deserialize(byte[] bytes) throws ClassNotFoundException, IOException {
		ByteArrayInputStream b= new ByteArrayInputStream(bytes);
		ObjectInputStream o = new ObjectInputStream(b);
		return o.readObject();
	}
}
