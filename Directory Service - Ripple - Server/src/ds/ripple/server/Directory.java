package ds.ripple.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.StringTokenizer;

import ds.ripple.common.PublisherRecord;
import ds.ripple.pub.util.MessageBuilder;

public class Directory {

	/* Map to store unique Id and publisher information */
	private HashMap<Integer, PublisherRecord> pubList = new HashMap<Integer, PublisherRecord>();

	/* Map to store Unique Id and Url of the publishers */
	private HashMap<Integer, String> pubURLs = new HashMap<Integer, String>();
	
	private HashSet<Integer> Pub_active=new HashSet<Integer>();

	protected static final int ERROR_URL_ALREADY_EXISTS = -1;
	protected static final int ERROR_PUBLISHER_PARSING_ERROR = -2;
	protected static final int ERROR_PUBLISHER_URL_NOT_FOUND = -4;
	protected static final int ERROR_PUBLISHER_NOT_UPDATED = -3;
	protected static final int ERROR_PUBLISHER_NOT_IN_DS = -5;
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
			tmpPubRecord = (PublisherRecord) is.readObject();
			if (pubURLs.containsValue(tmpPubRecord.getPub_address())) {
				return ERROR_URL_ALREADY_EXISTS;
			}else if(this.checkAddress(tmpPubRecord.getPub_address())){
				
			}
			return 0;
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
			return ERROR_PUBLISHER_PARSING_ERROR;

		}
	}

	public Integer genUid() {
		int uid;
		boolean nxt = false;
		do {
			uid = rnd.nextInt(1000000);
			if (pubURLs.containsKey(uid)) {
				nxt = true;
			}
		} while (nxt);
		return uid;
	}

	/**
	 * To register a publisher into the directory
	 * 
	 * @param pub
	 * @return
	 */
	public Integer pubisherRegistration(byte[] pub) {

		if (this.pubCheck(pub) == 0) {
			tmpPubRecord.setPub_Id(this.genUid());
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
		try {
			int pubId = Integer.parseInt((String)MessageBuilder.deserialize(ID));
			if (pubList.containsKey(pubId)) {
				pubList.remove(pubId);
				pubURLs.remove(pubId);
				return DEREGISTRATION_OK;
			} else {
				return Integer.toString(ERROR_PUBLISHER_URL_NOT_FOUND);
			}
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Integer.toString(ERROR_PUBLISHER_URL_NOT_FOUND);
		} 
		
	}

	/**
	 * Check if the specified address is a valid numeric TCP/IP address
	 * 
	 * @param pubAddr
	 *   
	 * @return boolean
	 */
	public boolean checkAddress(String pubAddr) {

		// Check if the string is valid
		
		if (pubAddr == null || pubAddr.length() < 7 || pubAddr.length() > 15)
			return false;
		
		StringTokenizer token = new StringTokenizer(pubAddr, ".");
		if (token.countTokens() != 4)
			return false;

		while (token.hasMoreTokens()) {
	// check for values in address are valid
			try {
				int val = Integer.valueOf(token.nextToken()).intValue();
				if (val < 0 || val > 255)
					return false;
			} catch (NumberFormatException ex) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @return HashMap<Integer, PublisherRecord>
	 */
	public HashMap<Integer, PublisherRecord> getDirectoryList() {
		return pubList;
	}

	/**
	 * @return String
	 */
	public String updatePublisherInfo(byte[] msg) {
		try {
			PublisherRecord pubRecord = (PublisherRecord) MessageBuilder
					.deserialize(msg);
			pubList.put(pubRecord.getPub_Id(), pubRecord);
			pubURLs.put(pubRecord.getPub_Id(), pubRecord.getPub_address());
			return "OK";
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
			return Integer.toString(ERROR_PUBLISHER_NOT_UPDATED);
		} 
	}
	
	public int pubAlive(byte[] msg){
		try {
			int pubId = Integer.parseInt((String) MessageBuilder.deserialize(msg));
			if(pubURLs.containsKey(pubId)){
				Pub_active.add(pubId);
				return pubId;
			}
			return ERROR_PUBLISHER_NOT_IN_DS;
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
			return ERROR_PUBLISHER_NOT_IN_DS;
		} 
	}
	
	public boolean  updatePubAlive(){
		Integer val;
		boolean update=false;
			Iterator<Integer> itr=pubURLs.keySet().iterator();
			while (itr.hasNext()){
				val = (Integer) itr.next(); 
				if(!Pub_active.contains(val)){
					pubList.remove(val);
					pubURLs.remove(val);
					update=true;
				}
			}
			Pub_active.clear();
			return update;
	}
}
