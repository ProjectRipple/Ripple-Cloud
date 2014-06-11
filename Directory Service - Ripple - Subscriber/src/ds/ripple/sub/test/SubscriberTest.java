package ds.ripple.sub.test;

import java.util.Scanner;

import ds.ripple.sub.Publisher;
import ds.ripple.sub.PublisherList;
import ds.ripple.sub.PublisherListListener;
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
		String dsURL = in.next();
		
		Subscriber subscriber = new Subscriber(dsURL, new MyPublisherListListener());
		
		
		usage();
		String cmd;
		do {
			cmd = in.next();
			if (cmd.equals("P")) {
				displayPublishers();
			} else if (cmd.equals("S")) {
				subscribe(subscriber, in);
			} else if (cmd.equals("U")) {
				unsubscribe(subscriber, in);
			}
		} while (!cmd.equals("quit"));
	}
	
	private static void unsubscribe(Subscriber subscriber, Scanner in) {
		System.out.println("Enter publisher URL (ex: tcp://192.168.0.1:5555)");
		String url = in.next();
		System.out.println("Enter topic to unsubscribe:");
		String topic = in.next();
		try {
			subscriber.unsubscribeFromTopic(url, topic);
		} catch (InvalidTopicException e) {
			e.printStackTrace();
		}
		System.out.println("Unsubscribed!");
	}

	private static void subscribe(Subscriber subscriber, Scanner in) {
		System.out.println("Enter publisher URL (ex: tcp://192.168.0.1:5555)");
		String url = in.next();
		System.out.println("Enter topic to subscribe:");
		String topic = in.next();
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
		System.out.println("3) To unsubscribe press [U]");
		System.out.println("4) To quit type [quit]");
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
}
