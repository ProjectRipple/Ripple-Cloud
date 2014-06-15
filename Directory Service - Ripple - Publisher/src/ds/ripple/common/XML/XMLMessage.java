package ds.ripple.common.XML;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import ds.ripple.pub.exceptions.XMLMissingArgumentException;

/**
 * XMLMessage class converts Event object from/to XML format. The Event object is 
 * a read-only object, it can be created through XMLMessageBuilder class. XML 
 * message that this class can create/serialize/deserialize has the following 
 * format:<br><br>
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
public class XMLMessage {	
	private final Event event;
	
	/**
	 * XMLMessage constructor. We want it to be private to have better control
	 * of XML message.
	 * 
	 * @param builder
	 */
	private XMLMessage(XMLMessageBuilder builder) {
		Event e = new Event();
		e.setId(builder.event.getId());
		e.setContent(builder.content);
		e.setContext(builder.context);
		this.event = e;
	}

	/**
	 * Returns the Event object. Note: Event is meant to be a ready-only object.
	 * Events are created by XMLMessageBuilder object.
	 * 
	 * @return Event object
	 */
	public Event getEventObject() {
		return this.event;		
	}
	
	/**
	 * Prints the Event object in XML format.
	 */
	@Override
	public String toString() {
		JAXBContext jaxbContext;
		try {
			StringWriter stringwriter = new StringWriter();
			
			jaxbContext = JAXBContext.newInstance(Event.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			jaxbMarshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.FALSE);
			jaxbMarshaller.marshal(event, stringwriter);
			
			return stringwriter.toString();
		} catch (JAXBException e) {
			return null;
		}
		
	}
	
	/**
	 * Returns serialized XML message that represents the Event object.
	 * 
	 * @return serialized XML message
	 */
	public byte[] getBytes() {
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Event.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			jaxbMarshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.FALSE);
				
			ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
			ObjectOutputStream outputStream = new ObjectOutputStream(byteStream);
			jaxbMarshaller.marshal(event, outputStream);
			outputStream.close();
			return byteStream.toByteArray();
		} catch (JAXBException | IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Deserializes XML message that represents the Event object
	 * 
	 * @param bytes
	 *            Serialized Event object
	 * @return Event object
	 */
	public static Event unmarshallEventObject(byte[] bytes) {
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Event.class);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			
			ByteArrayInputStream byteInputStream = new ByteArrayInputStream(bytes);
		    ObjectInputStream objectStream = new ObjectInputStream(byteInputStream);
		    return (Event)unmarshaller.unmarshal(objectStream);
		} catch (JAXBException | IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * XMLMessageBuilder is a helper class that will construct XMLMessage object
	 * that will contain an Event object. XMLMessageBuilder follows Java builder
	 * pattern. An example usage of XMLMessageBuilder object:<br><br>
	 * 
	 * XMLMessageBuilder builder = new XMLMessageBuilder("EventName@EventIPAddress")<br>
	 * &nbsp&nbsp&nbsp&nbsp.producer("ProducerName")<br>
	 * &nbsp&nbsp&nbsp&nbsp.timestamp(new Date().toString())<br>
	 * &nbsp&nbsp&nbsp&nbsp.location("39.7810121", "-84.1179554","240", "OptionalDescription")<br>
	 * &nbsp&nbsp&nbsp&nbsp.addContentSingleValue("temperature", "Celcius", "36.6")<br>
	 * &nbsp&nbsp&nbsp&nbsp.addContentSingleValue("bloodPressure", "mm Hg", "120/80")<br> 
	 * &nbsp&nbsp&nbsp&nbsp.addContentSingleValue("respirationRate", "Breath per minute", "17.3")<br>
	 * &nbsp&nbsp&nbsp&nbsp.addContentMultiValue("ecg", "200 samples/sec", list);<br><br>
	 * 
	 * You have to call the following methods to create a valid Event object: producer(), 
	 * timestamp(), location(). Event message must have a content (at least one value).<br><br>
	 * 
	 * To create XMLMessge:<br><br>
	 * 
	 * XMLMessage msg = builder.build();<br><br>
	 * 
	 * @author pawel
	 * 
	 */
	public static class XMLMessageBuilder {
		private Event event;
		private Content content;
		private Context context;
		private Location location;

		/**
		 * Creates XMLMessageBuilder, takes one argument: ID of the event. The
		 * convention for event ID is as follows:
		 * 
		 * eventName@eventIPAddress
		 * 
		 * Event ID will not be validated, it is not mandatory to follow the
		 * above convention.
		 * 
		 * @param eventID
		 *            ID of Event
		 */
		public XMLMessageBuilder(String eventID) {
			this.event = new Event();
			this.event.setId(eventID);
			this.content = new Content();
			this.context = new Context();
			this.location = new Location();
		}
		
		/**
		 * Sets producer name of Event.
		 * 
		 * @param producer
		 *            Name of the producer of Event
		 * @return XMLMessageBuilder object
		 */
		public XMLMessageBuilder producer(String producer) {
			this.context.setProducer(producer);
			return this;
		}

		/**
		 * Sets timestamp of Event
		 * 
		 * @param timestamp
		 *            Timestamp of Event
		 * @return XMLMessageBuilder object
		 */
		public XMLMessageBuilder timestamp(String timestamp) {
			this.context.setTimestamp(timestamp);
			return this;
		}
		
		/**
		 * Sets location of Event
		 * 
		 * @param longitude
		 *            Longitude of Event
		 * @param latitude
		 *            Latitude of Event
		 * @return XMLMessageBuilder object
		 */
		public XMLMessageBuilder location(String longitude, String latitude) {
			this.location.setLongitude(longitude);
			this.location.setLatitude(latitude);
			this.context.setLocation(this.location);
			return this;
		}
		
		/**
		 * Sets location of Event
		 * 
		 * @param longitude
		 *            Longitude of Event
		 * @param latitude
		 *            Latitude of Event
		 * @param description
		 *            Location description of Event
		 * @return XMLMessageBuilder object
		 */
        public XMLMessageBuilder location(String longitude, String latitude, String description) {
			this.location.setLongitude(longitude);
			this.location.setLatitude(latitude);
			this.location.setDescription(description);
			this.context.setLocation(this.location);
			return this;
		}
		

		/**
		 * Sets location of Event
		 * 
		 * @param longitude
		 *            Longitude of Event
		 * @param latitude
		 *            Latitude of Event
		 * @param altitude
		 *            Altitude of Event
		 * @param description
		 *            Location description of Event
		 * @return XMLMessageBuilder object
		 */
		public XMLMessageBuilder location(String longitude, String latitude, String altitude, String description) {
			this.location.setLongitude(longitude);
			this.location.setLatitude(latitude);
			this.location.setAltitude(altitude);
			this.location.setDescription(description);
			this.context.setLocation(this.location);
			return this;
		}
		
		/**
		 * Adds one feed item to Event. Supply *null* for missing parameters.
		 * 
		 * @param itemType
		 *            Type of feed item (ex. temperature)
		 * @param itemUnit
		 *            Unit of feed item (ex. Celcius)
		 * @param itemValue
		 *            Value of feed item (ex. 36.6)
		 * @return XMLMessageBuilder object
		 */
		public XMLMessageBuilder addContentSingleValue(String itemType, String itemUnit, String itemValue) {
			this.content.addItem(new FeedItem(itemType, itemValue, itemUnit));
			return this;
		}

		/**
		 * Adds one feed item to Event with multiple values. Supply *null* for
		 * missing parameters.
		 * 
		 * @param itemType
		 *            Type of feed item (ex. ecg)
		 * @param itemUnit
		 *            Unit of feed item (ex. 200samples/sec)
		 * @param itemValues
		 *            Values of feed item (ex. {value1}, {value2}, ...
		 * @return XMLMessageBuilder object
		 */
		public XMLMessageBuilder addContentMultiValue(String itemType, String itemUnit, String[] itemValues) {
			FeedItem feedItem = new FeedItem();
			feedItem.setType(itemType);
			feedItem.setUnit(itemUnit);
			for (String s : itemValues) {
				feedItem.addValue(s);
			}
			this.content.addItem(feedItem);
			return this;
		}
		
		/**
		 * Adds one feed item to Event with multiple values. Supply *null* for
		 * missing parameters.
		 * 
		 * @param itemType
		 *            Type of feed item (ex. ecg)
		 * @param itemUnit
		 *            Unit of feed item (ex. 200samples/sec)
		 * @param itemValues
		 *            Values of feed item (ex. {value1}, {value2}, ...
		 * @return XMLMessageBuilder object
		 */
		public XMLMessageBuilder addContentMultiValue(String itemType, String itemUnit, Iterable<? extends String> itemValues) {
			FeedItem feedItem = new FeedItem();
			feedItem.setType(itemType);
			feedItem.setUnit(itemUnit);
			for (String s : itemValues) {
				feedItem.addValue(s);
			}
			this.content.addItem(feedItem);
			return this;
		}
		
		/**
		 * Constructs and returns XMLMessage object. The XMLMessage will be
		 * validated, XMLMissingArgumentException will be thrown if the message
		 * fails validation.
		 * 
		 * @return XMLMessage
		 * @throws XMLMissingArgumentException
		 */
		public XMLMessage build() throws XMLMissingArgumentException {
			this.event.setContent(this.content);
			this.event.setContext(this.context);
			XMLMessage xmlMessage = new XMLMessage(this);
			validateMessage();
			return xmlMessage;
		}
		
		/**
		 * Validates XMLMessage object. Checks if the supplied arguments
		 * (Strings) that makes an Event object are not null or empty. Elements
		 * checks are: EventID, producer name, timestam, location (long, lat at
		 * minimum), feed item (Event must have at least one)
		 * 
		 * @throws XMLMissingArgumentException
		 */
		private void validateMessage() throws XMLMissingArgumentException {
			if (this.event.getId() == null || this.event.getId().equals("")) {
				throw new XMLMissingArgumentException("Event ID cannot be empty");
			}
			if (this.event.getContext().getProducer() == null || this.event.getContext().getProducer().equals("")) {
				throw new XMLMissingArgumentException("Event must have a producer name");
			}
			if (this.event.getContext().getTimestamp() == null || this.event.getContext().getProducer().equals("")) {
				throw new XMLMissingArgumentException("Event must have a timestamp");
			}
			if (!this.event.getContext().getLocation().hasLongLat()) {
				throw new XMLMissingArgumentException("Event must have a location (latitude and longitude at minimum");
			}
			if (this.event.getContent().getList().isEmpty()) {
				throw new XMLMissingArgumentException("Event must have a content");
			}
		}
	}
}
