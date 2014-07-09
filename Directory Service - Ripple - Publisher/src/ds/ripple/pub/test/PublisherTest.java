package ds.ripple.pub.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

import ds.ripple.common.XML.XMLMessage;
import ds.ripple.common.XML.FeedItem.ItemType;
import ds.ripple.common.XML.Producer.ProducerType;
import ds.ripple.common.XML.XMLMessage.XMLMessageBuilder;
import ds.ripple.pub.Publisher;
import ds.ripple.pub.exceptions.TopicNotRegisteredException;
import ds.ripple.pub.exceptions.URLAlreadyExistsException;
import ds.ripple.pub.exceptions.URLNotFoundException;
import ds.ripple.pub.exceptions.URLParsingException;
import ds.ripple.pub.exceptions.UpdateFailedException;
import ds.ripple.pub.exceptions.XMLMissingArgumentException;

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
		String pubURL = in.nextLine();
		System.out.println("Enter Directory Services URL (ex: tcp://192.168.0.11):");
		String dsURL = in.nextLine();
		System.out.println("Enter a topic:");
		topic = in.nextLine();
		System.out.println("Enter publisher name:");
		String pubName = in.nextLine();
		
		Publisher pub = new Publisher(pubURL, dsURL, topic, pubName, true);
	
		try {
			pub.register();
		} catch (URLAlreadyExistsException | URLParsingException e) {
			e.printStackTrace();
		}
		
		String cmd;
		do {
			usage();
			cmd = in.nextLine();
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
			} else if (cmd.equals("E")) {
				publishE(pub, in);
			}
		} while (!cmd.equals("quit"));
	}
	
	public static void usage() {
		System.out.println("This is a publisher test program:");
		System.out.println("1) To publish under the topic \"" + topic + "\" press [P]");
		System.out.println("2) To publish under another topics press [X]");
		System.out.println("3) To publish Ripple Event message press [E]");
		System.out.println("4) To modify topic list press [T]");
		System.out.println("5) To append topic list press [A]");
		System.out.println("6) To deregister & exit press [D]");
		System.out.println("7) To force quit type [quit]");
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
		String topic = in.nextLine();
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
	
	public static void publishE(Publisher pub, Scanner in) {
		System.out.println("Enter Event ID");
		String eventId = in.nextLine();
		String date = new Date().toString();
		System.out.println("Enter the ID of producer of the event:");
		String producerId = in.nextLine();
		System.out.println("The producer type was hardcoded to <" + ProducerType.PATIENT.toString() + ">");
		System.out.println("Location of the event:");
		System.out.println("\tlongitude:");
		String longitude = in.nextLine();
		System.out.println("\tlatitude:");
		String latitude = in.nextLine();
		System.out.println("\taltitude:");
		String altitude = in.nextLine();
		System.out.println("\tlocation description:");
		String description = in.nextLine();
		System.out.println("Enter feed items, once done type \"done\" for feed item type");
		ArrayList<String> type = new ArrayList<String>();
		ArrayList<String> unit = new ArrayList<String>();
		ArrayList<String> value = new ArrayList<String>();
		String input;
		boolean stop = false;
		do {
			System.out.println("\tFeed item type (temperature, bloodPressure, or ecg)");
			input = in.nextLine();
			if (input.equals("done")) {
				stop = true;
				break;
			} else {
				type.add(input);
			}
			System.out.println("\tFeed item unit");
			unit.add(in.nextLine());
			System.out.println("\tFeed item value:");
			value.add(in.nextLine());
		} while (!stop);
		XMLMessageBuilder builder = new XMLMessageBuilder(eventId)
			.producer(producerId, ProducerType.PATIENT)
			.timestamp(date)
			.location(longitude, latitude, altitude, description);
		for (int i=0 ; i < type.size() ; i++) {
			if (type.get(i).equals(ItemType.TEMPERATURE.toString()))
				builder.addContentSingleValue(ItemType.TEMPERATURE, unit.get(i), value.get(i));
			else if (type.get(i).equals(ItemType.BLOOD_PRESSURE.toString()))
				builder.addContentSingleValue(ItemType.BLOOD_PRESSURE, unit.get(i), value.get(i));
			else if (type.get(i).equals(ItemType.ECG.toString()))
				builder.addContentSingleValue(ItemType.ECG, unit.get(i), value.get(i));
			else {
				System.out.println("Unrecongized item type, aborting...");
				return;
			}
		}
		XMLMessage msg = null;
		try {
			msg = builder.build();
		} catch (XMLMissingArgumentException e) {
			e.printStackTrace();
		}
		
		pub.publish(msg);
		
		System.out.println("Ripple event message sent!");
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
		String input = in.nextLine();
		while (!input.equals("done")) {
			topics.add(input);
			input = in.nextLine();
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
		String input = in.nextLine();
		while (!input.equals("done")) {
			topics.add(input);
			input = in.nextLine();
		}
		
		try {
			pub.updateTopicList(topics.toArray(new String[topics.size()]));
			System.out.println("Topics updated.");
		} catch (UpdateFailedException e) {
			e.printStackTrace();
		}
	}
}
