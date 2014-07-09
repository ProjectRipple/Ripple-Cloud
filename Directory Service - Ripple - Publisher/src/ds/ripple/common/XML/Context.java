package ds.ripple.common.XML;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {"producer", "timestamp", "location"})
@XmlRootElement
public final class Context {
	private Producer producer;
	private String timestamp;
	private Location location;

	protected Context() {
		
	}
	
	@XmlElement
	public Producer getProducer() {
		return producer;
	}

	protected void setProducer(Producer producer) {
		this.producer = producer;
	}

	@XmlElement
	public String getTimestamp() {
		return timestamp;
	}

	protected void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	
	@XmlElement
	public Location getLocation() {
		return location;
	}
	
	protected void setLocation(Location location) {
		this.location = location;
	}
}
