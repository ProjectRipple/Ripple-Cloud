package ds.ripple.common.XML;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public final class Content {
	private ArrayList<FeedItem> list;

    protected Content() {
		list = new ArrayList<FeedItem>();
	}
	
	protected void addItem(FeedItem item) {
		list.add(item);
	}
		
	@XmlElement(name="Item")
	public ArrayList<FeedItem> getList() {
		return new ArrayList<FeedItem>(list);
	}

	protected void setList(ArrayList<FeedItem> list) {
		this.list = list;
	}
}
