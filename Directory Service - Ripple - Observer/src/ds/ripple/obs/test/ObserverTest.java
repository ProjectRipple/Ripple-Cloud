package ds.ripple.obs.test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import ds.ripple.common.PublisherRecord;
import ds.ripple.obs.MapListener;
import ds.ripple.obs.Observer;

/**
 * This a sample class that demonstrates how to use the Observer API. Code is
 * self-explanatory.
 * 
 * @author pawel
 * 
 */
public class ObserverTest {

	public static void main(String[] args) throws MalformedURLException {
		System.out.println("\t----Observer v1.0 API test---");
		usage();

		Scanner in = new Scanner(System.in);
		System.out.println("Enter URL of D.S. server (i.e. tcp://192.168.0.1): ");
		String url = in.next();
		in.close();

		// creates the Observer object
		Observer obs = new Observer(url, new MapUpdates());
		// connects to the Directory Services server
		obs.connect();

		while (true)
			;
	}

	public static void usage() {
		System.out.println("Usage: once the URL of D.S. server is entered, "
				+ "the observer runs on its own until killed.\n");
	}

	static class MapUpdates implements MapListener {

		@Override
		public void publishedMapUpdate(HashMap<Integer, PublisherRecord> map) {
			System.out.println("Available publishers' URL and topic:");

			if (map.isEmpty()) {
				System.out.println("\tNo publishers are available. The observer will"
								+ " notify you once the publishers become available.");
				return;
			}
			for (Map.Entry<Integer, PublisherRecord> entry : map.entrySet()) {
				System.out.println("\tPublisher ID: " + entry.getKey() + " @ "
						+ entry.getValue().getPub_address());
				System.out.println("\tList of available topics on the publisher:");
				for (String topics : entry.getValue().getTopics()) {
					System.out.println("\t\t" + topics);
				}
			}
		}
	}

}
