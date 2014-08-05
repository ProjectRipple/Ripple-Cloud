package ds.ripple.pub.test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import ds.ripple.common.XML.FeedItem;
import ds.ripple.common.XML.XMLMessage;
import ds.ripple.common.XML.Producer.ProducerType;
import ds.ripple.common.XML.XMLMessage.XMLMessageBuilder;
import ds.ripple.pub.Publisher;
import ds.ripple.pub.exceptions.URLAlreadyExistsException;
import ds.ripple.pub.exceptions.URLParsingException;

public class PubTestRippleEvent {
	Publisher pub;
	XMLMessage xmlMsg;
	Date date;
	SimpleDateFormat sdf;
	boolean publish = true;

	public void setPublish(boolean publish) {
		this.publish = publish;
	}

	// generating random values for bp
	public String rand(int value) {
		Random rand = new Random();
		if (value != 0) {
			int randomNum = rand.nextInt((20) + 1) + value - 10;
			return Integer.toString(randomNum);
		} else {
			int randomNum = rand.nextInt();
			return Integer.toString(randomNum);
		}
	}

	public ArrayList<String> getECG() {
		FileInputStream fstream;
		ArrayList<String> ecg = new ArrayList<String>();
		try {

			// file location for ecg
			fstream = new FileInputStream("/home/rahul/workspace/ecg");
			BufferedReader br = new BufferedReader(new InputStreamReader(
					fstream));
			String strLine;
			while ((strLine = br.readLine()) != null) {
				ecg.add(strLine);
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ecg;

	}

	public PubTestRippleEvent(String publisherURL, String dsURL, String topic,
			String publisherName) {
		pub = new Publisher(publisherURL, dsURL, topic, publisherName, true);

	}

	public void startPub() throws URLAlreadyExistsException,
			URLParsingException {
		pub.register();
		
		while (publish) {
		
				try {
					
					date = new Date();
					sdf = new SimpleDateFormat("MM/dd/yyyy h:mm:ss a");

				//	System.out.println(sdf.format(date));
					XMLMessageBuilder builder = new XMLMessageBuilder(rand(0))
							.producer("pat_138813", ProducerType.PATIENT)
							.timestamp(sdf.format(date))
							.location("10", "20", "1000", "field")
							.addContentMultiValue(
									FeedItem.ItemType.BLOOD_PRESSURE, "mm Hg",
									new String[] { rand(120), rand(80) })
							.addContentSingleValue(
									FeedItem.ItemType.HEART_RATE, "bpm",
									rand(80))
							.addContentSingleValue(
									FeedItem.ItemType.RESPIRATION_RATE,
									"breaths/min", rand(18))
							.addContentSingleValue(
									FeedItem.ItemType.TEMPERATURE,
									"fahrenheit", rand(90))
							.addContentSingleValue(
									FeedItem.ItemType.O2_SATURATION, "percent",
									rand(85));
					builder.addContentMultiValue(FeedItem.ItemType.ECG, " ",
							getECG());
					xmlMsg = builder.build();
					// System.out.println("published:"+xmlMsg);

					pub.publish(xmlMsg);
					Thread.sleep(2000);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		
	}
}
