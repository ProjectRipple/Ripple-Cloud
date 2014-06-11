package ds.ripple.sub;

import java.util.ArrayList;

public class PublisherList {
	private ArrayList<Publisher> mList;
	
	public PublisherList() {
		mList = new ArrayList<Publisher>();
	}
	
	public PublisherList(PublisherList pb) {
		this.mList = new ArrayList<Publisher>();
		for (Publisher p : pb.getPublishers()) {
			this.mList.add(new Publisher(p));
		}
	}
	
	protected void addPublisher(Publisher p) {
		mList.add(p);
	}
	
	protected void removePublisher(Publisher p) {
		mList.remove(p);
	}
	
	protected void clearPublisherList() {
		mList.clear();
	}
	
	protected int containsURLAndTopic(String url, String topic) {
		for (Publisher p : mList) {
			if (p.getURL().equals(url)) {
				for (String s : p.getTopics()) {
					if (s.equals(topic)) {
						return 0;
					}
				}
				return -1;
			}
		}
		return -2;
	}
	
	public void displayPublishers() {
		if (mList.isEmpty()) {
			System.out.println("Publisher list is empty.");
		}
		for (Publisher p : mList) {
			System.out.println("Publisher @ " + p.getURL() + " has following topics:");
			for (String s : p.getTopics()) {
				System.out.println("\t" + s);
			}
		}
	}
	
	public Publisher[] getPublishers() {
		if (mList.isEmpty()) {
			return null;
		}
		return mList.toArray(new Publisher[mList.size()]);
	}
	
}
