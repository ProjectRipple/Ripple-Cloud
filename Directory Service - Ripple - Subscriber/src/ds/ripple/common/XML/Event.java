package ds.ripple.common.XML;

import java.io.StringWriter;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * This class represents content of Ripple XML event message. An example XML 
 * message format, that all event messages will follow is as fallows:<br><br>
 * 
 * &lt;?xml&nbspversion="1.0"&nbspencoding="UTF-8"&nbspstandalone="yes"?&gt;<br>
 * &lt;event&nbspid="EventName@EventIPAddress"&gt;<br>
 * &nbsp&nbsp&nbsp&nbsp&lt;context&gt;<br>
 * &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&lt;producer&gt;ProducerName&lt;/producer&gt;<br>
 * &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&lt;timestamp&gt;Sat&nbspJun&nbsp14&nbsp23:39:31&nbspEDT&nbsp2014&lt;/timestamp&gt;<br>
 * &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&lt;location&nbspdescription="OptionalDescription"&gt;<br>
 * &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&lt;longitude&gt;39.7810121&lt;/longitude&gt;<br>
 * &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&lt;latitude&gt;-84.1179554&lt;/latitude&gt;<br>
 * &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&lt;altitude&gt;240&lt;/altitude&gt;<br>
 * &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&lt;/location&gt;<br>
 * &nbsp&nbsp&nbsp&nbsp&lt;/context&gt;<br>
 * &nbsp&nbsp&nbsp&nbsp&lt;content&gt;<br>
 * &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&lt;Item&nbsptype="temperature"&nbspunit="Celcius"&gt;<br>
 * &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&lt;value&gt;36.6&lt;/value&gt;<br>
 * &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&lt;/Item&gt;<br>
 * &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&lt;Item&nbsptype="bloodPressure"&nbspunit="mm&nbspHg"&gt;<br>
 * &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&lt;value&gt;120/80&lt;/value&gt;<br>
 * &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&lt;/Item&gt;<br>
 * &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&lt;Item&nbsptype="respirationRate"&nbspunit="Breath&nbspper&nbspminute"&gt;<br>
 * &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&lt;value&gt;17.3&lt;/value&gt;<br>
 * &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&lt;/Item&gt;<br>
 * &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&lt;Item&nbsptype="ecg"&nbspunit="200&nbspsamples/sec"&gt;<br>
 * &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&lt;value&gt;value1&lt;/value&gt;<br>
 * &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&lt;value&gt;value2&lt;/value&gt;<br>
 * &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&lt;value&gt;value3&lt;/value&gt;<br>
 * &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&lt;value&gt;value4&lt;/value&gt;<br>
 * &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&lt;value&gt;value5&lt;/value&gt;<br>
 * &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&lt;value&gt;value6&lt;/value&gt;<br>
 * &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&lt;/Item&gt;<br>
 * &nbsp&nbsp&nbsp&nbsp&lt;/content&gt;<br>
 * &lt;/event&gt;<br>
 * 
 * @author pawel
 * 
 */
@XmlType(propOrder = { "context", "content" })
@XmlRootElement
public final class Event {
	private String mId;

	private Context mContext;
	private Content mContent;

	protected Event() {

	}

	/**
	 * Returns ID of event. The convention for Ripple Event ID is: 
	 * eventName@eventIPAddress
	 * 
	 * @return
	 */
	@XmlAttribute
	public String getId() {
		return mId;
	}

	protected void setId(String id) {
		mId = id;
	}

	/**
	 * Returns context of this event. Context of the event contains metadata 
	 * information about the event (answers who?, when?, where? question)
	 * @return
	 */
	@XmlElement
	public Context getContext() {
		return mContext;
	}

	protected void setContext(Context context) {
		mContext = context;
	}

	/**
	 * Returns content of this event (sensor readings, etc)
	 * 
	 * @return
	 */
	@XmlElement
	public Content getContent() {
		return mContent;
	}

	protected void setContent(Content content) {
		mContent = content;
	}
	
	@Override
	public String toString() {
		StringWriter sw = new StringWriter();
		sw.write("<event id=" + this.getId() + ">" + "\n");
		sw.write("\t<context>" + "\n");
		sw.write("\t\t<producer>" + this.mContext.getProducer() + "</producer>"  + "\n");
		sw.write("\t\t<timestamp>" + this.mContext.getTimestamp() + "</timestamp>"  + "\n");
		sw.write("\t\t<location description=" + this.mContext.getLocation().getDescription() + ">"  + "\n");
		sw.write("\t\t\t<longitude>" + this.mContext.getLocation().getLongitude() + "</longitude>"  + "\n");
		sw.write("\t\t\t<latitude>" + this.mContext.getLocation().getLatitude() + "</latitude>"  + "\n");
		sw.write("\t\t\t<altitude>" + this.mContext.getLocation().getAltitude() + "</altitude>"  + "\n");
		sw.write("\t\t</location"  + "\n");
		sw.write("\t</context>"  + "\n");
		sw.write("\t<content>"  + "\n");
		for (FeedItem fi : this.getContent().getList()) {
			sw.write("\t\t<Item type=" + fi.getType() + " unit=" + fi.getUnit() + ">"  + "\n");
			for (String s : fi.getValues()) {
				sw.write("\t\t\t<value>" + s + "</value>"  + "\n");
			}
			sw.write("\t\t</Item>" + "\n");
		}
		sw.write("\t</content>" + "\n");
		sw.write("</event" + "\n");
		return sw.toString();
	}
}
