package ds.ripple.sub;

public class Publisher {
	private String mURL;
	private String[] mTopics;

	public Publisher(String URL, String[] topics) {
		setURL(URL);
		setTopics(topics);
	}
	
	public Publisher(Publisher publisher) {
		this.setURL(publisher.getURL());
		this.setTopics(publisher.getTopics());
	}
	
	public String getURL() {
		return new String(mURL);
	}

	protected void setURL(String mURL) {
		this.mURL = new String(mURL);
	}

	public String[] getTopics() {
		String[] topics = new String[mTopics.length];
		System.arraycopy(mTopics, 0, topics, 0, mTopics.length);
		return topics;
	}

	protected void setTopics(String[] topics) {
		mTopics = new String[topics.length];
		System.arraycopy(topics, 0, mTopics, 0, topics.length);
	}

	
}
