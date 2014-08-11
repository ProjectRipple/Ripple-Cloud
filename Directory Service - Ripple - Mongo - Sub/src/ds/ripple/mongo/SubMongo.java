package ds.ripple.mongo;

import java.net.UnknownHostException;
import java.util.Scanner;

import ds.ripple.common.XML.Event;
import ds.ripple.mongo.exceptions.IncorrectDBName;
import ds.ripple.sub.Publisher;
import ds.ripple.sub.PublisherList;
import ds.ripple.sub.PublisherListListener;
import ds.ripple.sub.RippleEventListener;
import ds.ripple.sub.Subscriber;
import ds.ripple.sub.SubscriptionListener;
import ds.ripple.sub.exceptions.InvalidTopicException;
import ds.ripple.sub.exceptions.InvalidURLException;


public class SubMongo {
	private static DBconnect db;
	private static Subscriber subscriber;
	static PublisherList myPubList = new PublisherList();
	private static DBPersist dbPersist;

	public SubMongo(String hostaddress, int port, String dsURL, int dsPort)
			throws UnknownHostException, IncorrectDBName {
		subscriber = new Subscriber(dsURL, new MyPublisherListListener());
		subscriber.connect();
		db = new DBconnect(hostaddress, port, "ripple");
		dbPersist = new DBPersist(db.getDb());
	}

	private static void subscribe(PublisherList pubList) {

		Publisher[] publst = pubList.getPublishers();
		for (Publisher pub : publst) {
			try {
				for(String topic: pub.getTopics() ){
					if(!topic.toLowerCase().equals("rippleevent")){
						subscriber.subscribe(pub.getURL(),topic ,new MySubscriptionListener());
					}
						
					}		
			} catch (InvalidURLException | InvalidTopicException e) {
				e.printStackTrace();
			}
		}
	}

	static class MyPublisherListListener implements PublisherListListener {

		@Override
		public void publisherListUpdate(PublisherList pl) {
			myPubList = new PublisherList(pl);
			subscribe(myPubList);
		}

	}

	static class MySubscriptionListener implements SubscriptionListener {

		@Override
		public void publishedMessage(String topic, String message) {
			System.out.println("\tReceived message:");
			System.out.println("\t\tTopic " + topic);
			System.out.println("\t\tMessage " + message);
			dbPersist.stringData(topic,message);
		}

	}

}
