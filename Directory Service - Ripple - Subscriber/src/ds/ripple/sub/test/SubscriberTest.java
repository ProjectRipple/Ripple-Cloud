package ds.ripple.sub.test;

import java.util.Scanner;

import ds.ripple.common.XML.Event;
import ds.ripple.sub.PublisherList;
import ds.ripple.sub.PublisherListListener;
import ds.ripple.sub.RippleEventListener;
import ds.ripple.sub.Subscriber;
import ds.ripple.sub.SubscriptionListener;
import ds.ripple.sub.exceptions.InvalidTopicException;
import ds.ripple.sub.exceptions.InvalidURLException;

public class SubscriberTest {
	static PublisherList myPubList = new PublisherList();
	
	public static void main(String[] args) {
		System.out.println("\t----Subscriber v1.0 API test---\n");
		
		Scanner in = new Scanner(System.in);
		System.out.println("Enter Directory Services URL (ex: tcp://192.168.0.1)");
		String dsURL = in.nextLine();
		
		
		Subscriber subscriber = new Subscriber(dsURL, new MyPublisherListListener());
		
		
		String cmd;
		do {
			usage();
			cmd = in.nextLine();
			if (cmd.equals("P")) {
				displayPublishers();
			} else if (cmd.equals("S")) {
				subscribe(subscriber, in);
			} else if (cmd.equals("U")) {
				unsubscribe(subscriber, in);
			} else if (cmd.equals("R")) {
				subscribeForRippleEvents(subscriber, in);
			}
		} while (!cmd.equals("quit"));
	}
	
	private static void subscribeForRippleEvents(Subscriber sub, Scanner in) {
		System.out.println("Enter URL of publisher (ex. tcp://192.168.0.1:32142)");
		String url = in.nextLine();
		sub.subscriberForRippleEvents(url, new MyRipplEventListener());
	}

	private static void unsubscribe(Subscriber subscriber, Scanner in) {
		System.out.println("Enter publisher URL (ex: tcp://192.168.0.1:5555)");
		String url = in.nextLine();
		System.out.println("Enter topic to unsubscribe:");
		String topic = in.nextLine();
		try {
			subscriber.unsubscribeFromTopic(url, topic);
		} catch (InvalidTopicException e) {
			e.printStackTrace();
		}
		System.out.println("Unsubscribed!");
	}

	private static void subscribe(Subscriber subscriber, Scanner in) {
		System.out.println("Enter publisher URL (ex: tcp://192.168.0.1:5555)");
		String url = in.nextLine();
		System.out.println("Enter topic to subscribe:");
		String topic = in.nextLine();
		try {
			subscriber.subscribe(url, topic, new MySubscriptionListener());
		} catch (InvalidURLException | InvalidTopicException e) {
			e.printStackTrace();
		}
		System.out.println("Subscribed!");
	}

	private static void displayPublishers() {
		myPubList.displayPublishers();
		
	}

	public static void usage() {
		System.out.println("\nUsage:");
		System.out.println("When subscribe to a publisher and its topic(s), you will start receiving published " +
				"messages automatically. The below menu will still be functional. ");
		System.out.println("1) To disply list of available publisher/topics press [P]");
		System.out.println("2) To subscribe press [S]");
		System.out.println("3) To subscribe for Ripple events press [R]");
		System.out.println("4) To unsubscribe press [U]");
		System.out.println("5) To quit type [quit]");
		System.out.println("Press enter to confirm");
	}
	
	static class MyPublisherListListener implements PublisherListListener {

		@Override
		public void publisherListUpdate(PublisherList pl) {
			myPubList = new PublisherList(pl);
		}
		
	}
	
	static class MySubscriptionListener implements SubscriptionListener {
		
		@Override
		public void publishedMessage(String topic, String message) {
			System.out.println("\tReceived message:");
			System.out.println("\t\tTopic " + topic);
			System.out.println("\t\tMessage " + message);
		}
		
	}
	
	static class MyRipplEventListener implements RippleEventListener {

		@Override
		public void publishedEvent(Event event) {
			System.out.println("Received Ripple Event in XML format:\n" + event);
		}
		
	}
	
	
}
