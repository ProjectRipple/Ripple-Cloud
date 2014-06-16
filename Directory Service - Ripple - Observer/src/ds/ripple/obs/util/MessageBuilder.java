package ds.ripple.obs.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;

import ds.ripple.common.PublisherRecord;

public class MessageBuilder {
	private final static byte MSG_MAP_REQUEST_CODE = 0x03;

	/**
	 * Creates a message to request HashMap from the Directory Services.
	 * 
	 * @return
	 */
	public static byte[] getMapRequestMsg() {
		return buildMsg(MSG_MAP_REQUEST_CODE);
	}

	private static byte[] buildMsg(byte msgCode) {
		return new byte[] { msgCode };
	}

	/**
	 * Deserializes HashMap sent by the server to the Observer.
	 * 
	 * @param bytes
	 *            Serialized message
	 * @return HashMap on success, null on failure
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public static HashMap<Integer, PublisherRecord> getHashMapFromDSReply(
			byte[] bytes) throws ClassNotFoundException, IOException {
		try {
			Object obj = deserialize(bytes);
			return (obj instanceof HashMap<?, ?>) ? (HashMap<Integer, PublisherRecord>)obj : null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Object deserialize(byte[] bytes)
			throws ClassNotFoundException, IOException {
		ByteArrayInputStream b = new ByteArrayInputStream(bytes);
		ObjectInputStream o = new ObjectInputStream(b);
		return o.readObject();
	}
}
