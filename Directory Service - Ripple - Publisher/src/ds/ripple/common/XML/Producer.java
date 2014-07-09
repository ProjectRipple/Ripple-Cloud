package ds.ripple.common.XML;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public final class Producer {
	private String producerID;
	private ProducerType producerType;
	private String type;
	
	public enum ProducerType {
		PATIENT("Patient"),
		RESPONDER("Responder"),
		CLOUDLET("Cloudlet");
		
		private final String producer;
		
		private ProducerType(final String producer) {
			this.producer = producer;
		}
		
		@Override
		public String toString() {
			return this.producer;
		}
	}
	
	@XmlElement(name="id")
	public String getProducerId() {
		return producerID;
	}
	
	protected void setProducerId(String id) {
		this.producerID = id;
	}
	
	@XmlAttribute(name="type")
	public String getType() {
		return type;
	}
	
	private void setType(String type) {
		this.type = type;
	}
	
	public ProducerType getProducerType() {
		return producerType;
	}
	
	protected void setProducerType(ProducerType type) {
		producerType = type;
		setType(type.toString());
	}
	
	protected boolean hasIdAndType() {
		if (this.producerID == null || this.producerID.equals("")) {
			return false;
		}
		if (this.producerType == null) {
			return false;
		}
		return true;
	}
}
