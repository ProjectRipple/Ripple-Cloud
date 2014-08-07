package ds.ripple.MQTTSub;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import ds.ripple.common.XML.FeedItem;
import ds.ripple.common.XML.Producer.ProducerType;
import ds.ripple.common.XML.XMLMessage;
import ds.ripple.common.XML.XMLMessage.XMLMessageBuilder;
import ds.ripple.pub.Publisher;
import ds.ripple.pub.exceptions.TopicNotRegisteredException;
import ds.ripple.pub.exceptions.URLAlreadyExistsException;
import ds.ripple.pub.exceptions.URLParsingException;

public class ZMQPub extends MqttSubcriber {
	Publisher pub;
	XMLMessage xmlMsg;
	Date date;
	SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy h:mm:ss a");
	boolean registered = false;

	public ZMQPub(String brokerUrl, String publisherURL, String dsURL,
			String topic, String publisherName) {
		pub = new Publisher(publisherURL, dsURL, topic, publisherName, true);

		BROKER_URL = brokerUrl;
	}

	/**
	 * 
	 * To start publisher, register in directory service and start mqtt client
	 */
	public void startPub() throws URLAlreadyExistsException,
			URLParsingException {
		pub.register();
		registered = true;
		runClient();
	}

	/**
	 * 
	 * To stop publisher, de-register from directory service and stop mqtt
	 * client
	 */
	public void stopPub() throws Exception {
		pub.deregister();
		stopClient();
	}

	// generating random values for bp
	public String rand(int value) {
		Random rand = new Random();
		int randomNum = rand.nextInt((20) + 1) + value - 10;
		return Integer.toString(randomNum);
	}

	/**
	 * 
	 * method to check for object is not null and if not null converting it to
	 * string
	 * 
	 * @param obj
	 * 
	 * 
	 */
	public String checkNotNull(Object obj) {
		if (obj == null) {
			return "";
		}
		return obj.toString();
	}

	/**
	 * 
	 * messageArrived This callback is invoked when a message is received on a
	 * subscribed topic.
	 * 
	 * @param topic
	 *            , message topic on which message is published and MqttMessage
	 *            in json format for parsing into required xml format
	 * 
	 */
	@Override
	public void messageArrived(String topic, MqttMessage message) {

		// System.out.println(message);
		if (registered) {
			publishJSON(message);
			if (topic.toLowerCase().contains("p_stats")
					&& topic.toLowerCase().contains("vitalcast")) {
				parseVitalCast(message);
			} else if (topic.toLowerCase().contains("p_stats")
					&& topic.toLowerCase().contains("info")) {
				parsePatientInfo(message);
			} else if (topic.toLowerCase().contains("p_stream")) {
				parseStream(topic, message);
			} else if (topic.toLowerCase().contains("c_status")) {
				parseCloudletMsg(message);
			} else if (topic.toLowerCase().contains("r_status")) {
				// parseCloudletMsg(message);
			} else if (topic.toLowerCase().contains("p_action")) {
				parseAction(message);
			}

		}
	}

	public void publishJSON(MqttMessage message) {
		try {
			pub.publish(pub.getTopics()[0], new String(message.getPayload()));
		} catch (TopicNotRegisteredException | IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Constructs XMLMessage from mqtt message received from Mqtt broker
	 * published on topic “P_Stats/src/vitalcast”
	 * 
	 * @param message
	 *            MqttMessage in json format for parsing into required xml
	 *            format(Vital cast)
	 * 
	 */
	public void parseVitalCast(MqttMessage message) {

		try {
			date = new Date();
			
			String obj = new String(message.getPayload());
			json = (JSONObject) new JSONParser().parse(obj);
			XMLMessageBuilder builder = new XMLMessageBuilder(
					checkNotNull(json.get("ip")))
					.producer(json.get("src").toString(), ProducerType.PATIENT)
					.location(0, 0, 0)
					.timestamp(sdf.format(date))
					.addContentMultiValue(FeedItem.ItemType.BLOOD_PRESSURE,
							"mm Hg", new String[] { rand(120), rand(80) })
					.addContentSingleValue(FeedItem.ItemType.HEART_RATE, "bpm",
							checkNotNull(json.get("hr")))
					.addContentSingleValue(FeedItem.ItemType.RESPIRATION_RATE,
							"breaths/min", checkNotNull(json.get("resp")))
					.addContentSingleValue(FeedItem.ItemType.TEMPERATURE,
							"fahrenheit", checkNotNull(json.get("temp")))
					.addContentSingleValue(FeedItem.ItemType.O2_SATURATION,
							"mg/l", checkNotNull(json.get("sp02")));
			// // System.out.println("published:" + xmlMsg);
			xmlMsg = builder.build();
			pub.publish(xmlMsg);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Constructs XMLMessage from mqtt message received from Mqtt broker
	 * published on topic C_Status/#
	 * 
	 * @param message
	 *            MqttMessage in json format for parsing into required xml
	 *            format(cloudlet ping)
	 * 
	 */
	public void parseCloudletMsg(MqttMessage message) {

		try {

			String obj = new String(message.getPayload());
			json = (JSONObject) new JSONParser().parse(obj);
			XMLMessageBuilder builder = new XMLMessageBuilder(
					checkNotNull(json.get("date"))).producer(
					checkNotNull(json.get("cid")), ProducerType.CLOUDLET)
					.timestamp(checkNotNull(json.get("date")));
			if (json.containsKey("patients")) {
				JSONArray patientList = (JSONArray) json.get("patients");
				Iterator patientItr = patientList.iterator();
				ArrayList<String> ptnlst = new ArrayList<String>();
				while (patientItr.hasNext()) {
					JSONObject patient = (JSONObject) patientItr.next();
					ptnlst.add(checkNotNull(patient.get("id")));
				}
				builder.addContentMultiValue(
						FeedItem.ItemType.CLOUDLET_PATIENTID_LIST, "N/A",
						ptnlst);
			}

			// builder.addContentMultiValue(FeedItem.ItemType.ECG, " ",
			// getECG());
			JSONObject locat = (JSONObject) json.get("location");
			builder.location(
					Double.parseDouble(checkNotNull(locat.get("lat"))),
					Double.parseDouble(checkNotNull(locat.get("lng"))),
					Double.parseDouble(checkNotNull(locat.get("alt"))));
			// // System.out.println("published:" + xmlMsg);
			xmlMsg = builder.build();
			pub.publish(xmlMsg);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Constructs XMLMessage from mqtt message received from Mqtt broker
	 * published on topic P_Action/#
	 * 
	 * @param message
	 *            MqttMessage in json format for parsing into required xml
	 *            format(patient note )
	 * 
	 */
	public void parseAction(MqttMessage message) {

		HashMap<String, FeedItem.ItemType> bodyPartsTxt = new HashMap<String, FeedItem.ItemType>();
		bodyPartsTxt.put("NONE", FeedItem.ItemType.NOTE_TEXT_GENERAL);
		bodyPartsTxt.put("FRONT_HEAD", FeedItem.ItemType.NOTE_TEXT_FRONT_HEAD);
		bodyPartsTxt
				.put("FRONT_TORSO", FeedItem.ItemType.NOTE_TEXT_FRONT_TORSO);
		bodyPartsTxt.put("FRONT_RIGHT_ARM",
				FeedItem.ItemType.NOTE_TEXT_FRONT_RIGHT_ARM);
		bodyPartsTxt.put("FRONT_LEFT_ARM",
				FeedItem.ItemType.NOTE_TEXT_FRONT_LEFT_ARM);
		bodyPartsTxt.put("FRONT_RIGHT_LEG",
				FeedItem.ItemType.NOTE_TEXT_FRONT_RIGHT_LEG);
		bodyPartsTxt.put("FRONT_LEFT_LEG",
				FeedItem.ItemType.NOTE_TEXT_FRONT_LEFT_LEG);

		HashMap<String, FeedItem.ItemType> bodyPartsImg = new HashMap<String, FeedItem.ItemType>();
		bodyPartsImg.put("NONE", FeedItem.ItemType.NOTE_IMG_GENERAL);
		bodyPartsImg.put("FRONT_HEAD", FeedItem.ItemType.NOTE_IMG_FRONT_HEAD);
		bodyPartsImg.put("FRONT_TORSO", FeedItem.ItemType.NOTE_IMG_FRONT_TORSO);
		bodyPartsImg.put("FRONT_RIGHT_ARM",
				FeedItem.ItemType.NOTE_IMG_FRONT_RIGHT_ARM);
		bodyPartsImg.put("FRONT_LEFT_ARM",
				FeedItem.ItemType.NOTE_IMG_FRONT_LEFT_ARM);
		bodyPartsImg.put("FRONT_RIGHT_LEG",
				FeedItem.ItemType.NOTE_IMG_FRONT_RIGHT_LEG);
		bodyPartsImg.put("FRONT_LEFT_LEG",
				FeedItem.ItemType.NOTE_IMG_FRONT_LEFT_LEG);

		try {
			String obj = new String(message.getPayload());
			json = (JSONObject) new JSONParser().parse(obj);
			XMLMessageBuilder builder = new XMLMessageBuilder(
					checkNotNull(json.get("nid")))
					.producer(checkNotNull(json.get("rid")),
							ProducerType.RESPONDER)
					.timestamp(checkNotNull(json.get("date")))
					.addContentSingleValue(FeedItem.ItemType.PATIENT_ID, "id",
							checkNotNull(json.get("pid")));

			if (json.containsKey("contents")) {
				JSONArray content = (JSONArray) json.get("contents");
				Iterator contItr = content.iterator();
				if (json.get("body_part") != null) {
					while (contItr.hasNext()) {
						JSONObject note = (JSONObject) contItr.next();
						if (checkNotNull(note.get("type")).equalsIgnoreCase(
								"text")) {
							builder.addContentSingleValue(
									bodyPartsTxt.get(json.get("body_part")),
									"N/A", checkNotNull(note.get("msg")));
						} else if (checkNotNull(note.get("type"))
								.equalsIgnoreCase("image")) {
							builder.addContentSingleValue(
									bodyPartsImg.get(json.get("body_part")),
									".Jpg", checkNotNull(note.get("img")));
						}

					}
				}
			}

			JSONObject locat = (JSONObject) json.get("location");
			builder.location(
					Double.parseDouble(checkNotNull(locat.get("lat"))),
					Double.parseDouble(checkNotNull(locat.get("lng"))),
					Double.parseDouble(checkNotNull(locat.get("alt"))));

			xmlMsg = builder.build();
			// System.out.println("published:" + xmlMsg);
			pub.publish(xmlMsg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Constructs XMLMessage from mqtt message received from Mqtt broker
	 * published on topic P_Stats/[PID]/info
	 * 
	 * @param message
	 *            MqttMessage in json format for parsing into required xml
	 *            format(patient info )
	 * 
	 */
	public void parsePatientInfo(MqttMessage message) {

		try {

			String obj = new String(message.getPayload());
			json = (JSONObject) new JSONParser().parse(obj);
			String[] name = json.get("name").toString().split(" ");
			//System.out.println("name :" + Arrays.toString(name));
			XMLMessageBuilder builder = new XMLMessageBuilder(
					checkNotNull(json.get("date")))
					.producer(checkNotNull(json.get("rid")),
							ProducerType.RESPONDER)
					.timestamp(checkNotNull(json.get("date")))

					.addContentSingleValue(FeedItem.ItemType.PATIENT_ID, "N/A",
							checkNotNull(json.get("pid")))
					.addContentSingleValue(FeedItem.ItemType.PAITENT_LAST_NAME,
							"N/A", checkNotNull(name[0]))
					.addContentSingleValue(FeedItem.ItemType.AGE, "N/A",
							checkNotNull(json.get("age")))
					.addContentSingleValue(FeedItem.ItemType.PATIENT_SEX,
							"N/A", checkNotNull(json.get("sex")))
					.addContentSingleValue(
							FeedItem.ItemType.PATIENT_NBC_STATUS, "N/A",
							checkNotNull(json.get("nbc")))
					.addContentSingleValue(
							FeedItem.ItemType.PATIENT_TRIAGE_COLOR, "N/A",
							checkNotNull(json.get("triage")))
					.addContentSingleValue(
							FeedItem.ItemType.PATIENT_STATUS_DESCRIPTION,
							"N/A", checkNotNull(json.get("status")));
			if (name.length == 2) {
				builder.addContentSingleValue(
						FeedItem.ItemType.PATIENT_FIRST_NAME, "N/A",
						checkNotNull(name[1]));
			}

			builder.location(0, 0, 0);
		
			xmlMsg = builder.build();
			//System.out.println("published:" + xmlMsg);
			pub.publish(xmlMsg);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Constructs XMLMessage from mqtt message received from Mqtt broker
	 * published on topic p_stream/#
	 * 
	 * @param message
	 *            MqttMessage in json format for parsing into required xml
	 *            format(ecg message)
	 * 
	 */
	public void parseStream(String topic, MqttMessage message) {

		String[] topicParts = topic.split("/");
		try {
			XMLMessageBuilder builder = new XMLMessageBuilder("Ecg data")
					.producer(checkNotNull(topicParts[1]), ProducerType.PATIENT);

			String stream = new String(message.getPayload());
			byte[] streamByte = hexStringToByteArray(stream);
			for (int i = 0; i < streamByte.length; i++) {

				builder.addContentSingleValue(
						FeedItem.ItemType.ECG,
						" ",
						Integer.toString(convert2BytesToUInt(new byte[] {
								streamByte[i], streamByte[++i] })));
			}
			// System.out.println("published:" + xmlMsg);
			builder.timestamp(sdf.format(date));
			builder.location(0, 0, 0);

			xmlMsg = builder.build();
			pub.publish(xmlMsg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * converts byte array to unsigned int values
	 * 
	 * @param bytes
	 *            byte array for converting 2 bytes to unsigned int
	 * 
	 */
	public int convert2BytesToUInt(byte[] bytes) {

		return ((bytes[0] & 0xFF) << 8 | (bytes[1] & 0xFF));
	}

	/**
	 * converts hex string to byte array
	 * 
	 * @param str
	 *            byte array for converting 2 bytes to unsigned int
	 * 
	 */
	public byte[] hexStringToByteArray(String str) {
		int len = str.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(str.charAt(i), 16) << 4) + Character
					.digit(str.charAt(i + 1), 16));
		}

		return data;
	}

}
