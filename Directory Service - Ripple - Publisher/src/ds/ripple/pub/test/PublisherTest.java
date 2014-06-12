package ds.ripple.pub.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import ds.ripple.pub.Publisher;
import ds.ripple.pub.exceptions.TopicNotRegisteredException;
import ds.ripple.pub.exceptions.URLAlreadyExistsException;
import ds.ripple.pub.exceptions.URLNotFoundException;
import ds.ripple.pub.exceptions.URLParsingException;
import ds.ripple.pub.exceptions.UpdateFailedException;

/**
 * This a sample class that demonstrates how to use the Publisher API. Code is
 * self-explanatory.
 * 
 * @author pawel
 *
 */
public class PublisherTest {
	static String topic;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("\t----Publisher v1.0 API test---\n");
		
		Scanner in = new Scanner(System.in);
		System.out.println("Enter publisher URL (ex: tcp://192.168.0.10):");
		String pubURL = in.next();
		System.out.println("Enter Directory Services URL (ex: tcp://192.168.0.11):");
		String dsURL = in.next();
		System.out.println("Enter a topic:");
		topic = in.next();
		System.out.println("Enter publisher name:");
		String pubName = in.next();
		
		Publisher pub = new Publisher(pubURL, dsURL, topic, pubName);
		
		try {
			pub.register();
		} catch (URLAlreadyExistsException | URLParsingException e) {
			e.printStackTrace();
		}
		
		usage();
		String cmd;
		do {
			cmd = in.next();
			if (cmd.equals("P")) {
				publish(pub, in);
			} else if (cmd.equals("D")) {
				deregister(pub);
				System.exit(0);
			} else if (cmd.equals("T")) {
				updateTopics(pub, in);
			} else if (cmd.equals("A")) {
				appendTopics(pub, in);
			} else if (cmd.equals("X")) {
				publishX(pub, in);
			}
		} while (!cmd.equals("quit"));
	}
	
	public static void usage() {
		System.out.println("This is a publisher test program:");
		System.out.println("1) To publish under the " + topic + " press [P]");
		System.out.println("2) To publish under another topics press [X]");
		System.out.println("3) To modify topic list press [T]");
		System.out.println("4) To append topic list press [A]");
		System.out.println("5) To deregister & exit press [D]");
		System.out.println("6) To force quit type [quit]");
		System.out.println("Press enter to confirm.");
	}
	
	public static void publish(Publisher pub, Scanner in) {
		System.out.println("Enter the message: ");
		String msg = in.nextLine();	
		try {
			pub.publish(topic, msg);
		} catch (TopicNotRegisteredException | IOException e) {
			e.printStackTrace();
			return;
		}
		System.out.println("Message sent!");
	}
	
	public static void publishX(Publisher pub, Scanner in) {
		System.out.println("Enter the topic: ");
		String topic = in.next();
		System.out.println("Enter the message: ");
		String message = in.nextLine();
		try {
			pub.publish(topic, message);
		} catch (TopicNotRegisteredException | IOException e) {
			e.printStackTrace();
			return;
		}
		System.out.println("Message sent!");
	}

	public static void deregister(Publisher pub) {
		try {
			pub.deregister();
		} catch (URLNotFoundException e) {
			e.printStackTrace();
			return;
		}
		System.out.println("Publisher deregistered!");
	}
	
	public static void updateTopics(Publisher pub, Scanner in) {
		System.out.println("Current topic list:");
		for (String topic : pub.getTopics()) {
			System.out.println("\t" + topic);
		}
		
		System.out.println("Enter new topics. Once done, type \"done\":");
		ArrayList<String> topics = new ArrayList<String>();
		String input = in.next();
		while (!input.equals("done")) {
			topics.add(input);
			input = in.next();
		}
		
		try {
			pub.updateTopicList(topics.toArray(new String[topics.size()]));
			System.out.println("Topics updated.");
		} catch (UpdateFailedException e) {
			e.printStackTrace();
		}
	}
	
	public static void appendTopics(Publisher pub, Scanner in) {
		ArrayList<String> topics = new ArrayList<String>();
		
		System.out.println("Current topic list:");
		for (String topic : pub.getTopics()) {
			System.out.println("\t" + topic);
			topics.add(topic);
		}
		
		System.out.println("Enter new topics. Once done, type \"done\":");
		String input = in.next();
		while (!input.equals("done")) {
			topics.add(input);
			input = in.next();
		}
		
		try {
			pub.updateTopicList(topics.toArray(new String[topics.size()]));
			System.out.println("Topics updated.");
		} catch (UpdateFailedException e) {
			e.printStackTrace();
		}
	}
}
