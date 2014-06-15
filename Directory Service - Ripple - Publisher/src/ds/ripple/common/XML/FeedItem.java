package ds.ripple.common.XML;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public final class FeedItem {
	private String type;
	private ArrayList<String> values;
	private String unit;

	protected FeedItem() {
		this.values = new ArrayList<String>();
	}
	
	protected FeedItem(String type, String value) {
		this.type = type;
		this.values = new ArrayList<String>();
		this.values.add(value);
	}
	
	protected FeedItem(String type, String value, String unit) {
		this.type = type;
		this.values = new ArrayList<String>();
		this.values.add(value);
		this.unit = unit;
	}
	
	protected FeedItem(String type, ArrayList<String> values, String unit) {
		this.type = type;
		this.values = new ArrayList<String>(values);
		this.unit = unit;
	}
	
	@XmlAttribute
	public String getType() {
		return type;
	}

	protected void setType(String type) {
		this.type = type;
	}
	
	@XmlElement(name="value")
	public ArrayList<String> getValues() {
		return this.values;
	}
	
	protected void setValues(ArrayList<String> values) {
		this.values = new ArrayList<String>(values);
	}
	
	protected void setValue(String value) {
		this.values = new ArrayList<String>();
		this.values.add(value);
	}
	
	protected void addValue(String value) {
		this.values.add(value);
	}
	
	@XmlAttribute
	public String getUnit() {
		return unit;
	}

	protected void setUnit(String unit) {
		this.unit = unit;
	}
}
