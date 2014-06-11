package ds.ripple.sub.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class MessageBuilder {
	public static Object deserialize(byte[] bytes) throws ClassNotFoundException, IOException {
		ByteArrayInputStream b= new ByteArrayInputStream(bytes);
		ObjectInputStream o = new ObjectInputStream(b);
		return o.readObject();
	}
}
