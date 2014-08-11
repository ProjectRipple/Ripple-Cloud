package ds.ripple.test;

import java.net.UnknownHostException;

import ds.ripple.mongo.SubMongo;
import ds.ripple.mongo.exceptions.IncorrectDBName;

public class MongoSubTest {
	public static void main(String[] args) {
	 try {
		SubMongo mng=new SubMongo("localhost",27017, "tcp://127.0.0.1",5555);
		System.out.println("connected");
	} catch (UnknownHostException | IncorrectDBName e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	 
	}
	 
}
