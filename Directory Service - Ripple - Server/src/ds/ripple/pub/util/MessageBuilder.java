package ds.ripple.pub.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class MessageBuilder {
	public static byte[] buildMsg(Object obj) throws IOException {	
		return serialize(obj);
	}
	
	public static byte[] buildMsg(byte msgCode, Object msgPayload) {
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
	
	private static byte[] serialize(Object obj) throws IOException {
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
