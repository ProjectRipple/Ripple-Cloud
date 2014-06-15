package ds.ripple.common.XML;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {"producer", "timestamp", "location"})
@XmlRootElement
public final class Context {
	private String producer;
	private String timestamp;
	private Location location;

	protected Context() {
		
	}
	
	@XmlElement
	public String getProducer() {
		return producer;
	}

	protected void setProducer(String producer) {
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
