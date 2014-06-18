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
	

	/**
	 * Constructs reply message from msgCode and msgPayload.
	 * 
	 * @param msgCode ,msgPayload
	 *            builds message in byte code adding message code with the 
	 *            the body of message
	 *           
	 * @return Serialized message on success, null on failure
	 */
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
	
	/**
	 * Serializes message from the object provided
	 * 
	 * @param obj 
	 *            builds message in byte code object provided with the 
	 *            the body of message
	 *           
	 * @return Serialized message on success, null on failure
	 */
	private static byte[] serialize(Object obj) throws IOException {
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		ObjectOutputStream o = new ObjectOutputStream(b);
	    o.writeObject(obj);
		o.close();
	
		return b.toByteArray();
	}
	
	/**
	 * De-serializes the byte code received and constructs 
	 * 
	 * @param obj 
	 *            builds message in byte code object provided with the 
	 *            the body of message
	 *           
	 * @return Serialized message on success, null on failure
	 */
	public static Object deserialize(byte[] bytes) throws ClassNotFoundException, IOException {
		ByteArrayInputStream b= new ByteArrayInputStream(bytes);
		ObjectInputStream o = new ObjectInputStream(b);
		return o.readObject();
	}
}
