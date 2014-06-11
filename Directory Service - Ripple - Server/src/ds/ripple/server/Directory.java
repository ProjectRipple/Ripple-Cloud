package ds.ripple.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Random;

import ds.ripple.common.PublisherRecord;
import ds.ripple.pub.util.MessageBuilder;

public class Directory {

	/* Map to store unique Id and publisher information */
	private HashMap<Integer, PublisherRecord> pubList = new HashMap<Integer, PublisherRecord>();

	/* Map to store Unique Id and Url of the publishers */
	private HashMap<Integer, String> pubURLs = new HashMap<Integer, String>();

	protected static final int ERROR_URL_ALREADY_EXISTS = -1;
	protected static final int ERROR_PUBLISHER_PARSING_ERROR = -2;
	protected static final int ERROR_PUBLISHER_URL_NOT_FOUND = -3;
	protected static final String DEREGISTRATION_OK = "Ok";
	

	private Random rnd = new Random();

	private PublisherRecord tmpPubRecord;

	/**
	 * To check and validate the information of publisher before entry into
	 * directory
	 * 
	 * @param pub
	 */
	private int pubCheck(byte[] pub) {
		ByteArrayInputStream in = new ByteArrayInputStream(pub);
		try {
			ObjectInputStream is = new ObjectInputStream(in);
			// if (is.readObject() instanceof PublisherRecord) {
			tmpPubRecord = (PublisherRecord) is.readObject();
			if (pubURLs.containsValue(tmpPubRecord.getPub_address())) {
				return ERROR_URL_ALREADY_EXISTS;
			}
			// LOGGER.info("data recieved for registration is not in format");
			return 0;
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
			return ERROR_PUBLISHER_PARSING_ERROR;

		}
	}

	/**
	 * To register a publisher into the directory
	 * 
	 * @param pub
	 * @return
	 */
	public Integer pubisherRegistration(byte[] pub) {
		if (this.pubCheck(pub) == 0) {
			tmpPubRecord.setPub_Id(rnd.nextInt(100000));
			pubList.put(tmpPubRecord.getPub_Id(), tmpPubRecord);
			pubURLs.put(tmpPubRecord.getPub_Id(), tmpPubRecord.getPub_address());
			return tmpPubRecord.getPub_Id();
		} else {
			return this.pubCheck(pub);
		}
	}

	/**
	 * To de-register a publisher from the directory
	 * 
	 * @param ID
	 * @return
	 */
	public String publisherDeregistration(byte[] ID) {
		if (pubList.containsKey(ID)) {
			pubList.remove(ID);
			pubURLs.remove(ID);
			return DEREGISTRATION_OK;
		} else {
			return Integer.toString(ERROR_PUBLISHER_URL_NOT_FOUND);
		}
	}

	/**
	 * @return
	 */
	public HashMap<Integer, PublisherRecord> getDirectoryList() {
		return pubList;
	}
	
	public String updatePublisherInfo(byte[] msg) {
		try {
			PublisherRecord pubRecord = (PublisherRecord)MessageBuilder.deserialize(msg);
			pubList.put(pubRecord.getPub_Id(), pubRecord);
			pubURLs.put(pubRecord.getPub_Id(), pubRecord.getPub_address());
			return "0";
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
			return "-1";
		} catch (Exception e) {
			e.printStackTrace();
			return "-1";
		}
	}
}
