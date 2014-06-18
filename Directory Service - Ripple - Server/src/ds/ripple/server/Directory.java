package ds.ripple.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

import ds.ripple.common.PublisherRecord;
import ds.ripple.pub.util.MessageBuilder;

public class Directory {

	/* Map to store unique Id and publisher information */
	private static HashMap<Integer, PublisherRecord> pubList = new HashMap<Integer, PublisherRecord>();

	/* Map to store Unique Id and Url of the publishers */
	private ConcurrentHashMap<Integer, String> pubURLs = new ConcurrentHashMap<Integer, String>();
	
	/* Map to store Unique Id of publishers which are alive */
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
	 * To check and validate the information of publisher before its entry into
	 * directory service
	 * 
	 * @param pub
	 * publisher record object in byte code is passed to check legibility.
	 * 
	 * @return integer
	 * 
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

	/**
	 * To generate unique id in Integer for new publisher
	 * 
	 * @return Integer
	 * 
	 */
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
	 * 	 Publisher record in byte code for publisher registration
	 * @return Integer
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
	 * To de-register a publisher from the directory service
	 * before the publisher stops.
	 * 
	 * @param ID
	 * 	Unique Id of publisher to de-register from directory service 
	 * 
	 * @return String
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
			
			e.printStackTrace();
			return Integer.toString(ERROR_PUBLISHER_URL_NOT_FOUND);
		} 
		
	}

	/**
	 * Check if the specified address is a valid numeric TCP/IP address
	 * 
	 * @param pubAddr
	 *  Tcp address of Publisher that starts as new publisher. 
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
	 * This method is used to get list of publishers stored in directory service
	 * 
	 * @return HashMap<Integer, PublisherRecord>
	 * 	list of publishers  in Hash Map with unique Id as keys and
	 * Publisher Record as values
	 * 
	 */
	public static HashMap<Integer, PublisherRecord> getDirectoryList() {
		return pubList;
	}

	/**
	 * updates the publisher information that is already stored in directory service
	 * 
	 * @param pub
	 * publisher record in byte code for the update of the information
	 * 
	 * @return String
	 * OK or error code
	 */
	public String updatePublisherInfo(byte[] pub) {
		try {
			PublisherRecord pubRecord = (PublisherRecord) MessageBuilder
					.deserialize(pub);
			pubList.put(pubRecord.getPub_Id(), pubRecord);
			pubURLs.put(pubRecord.getPub_Id(), pubRecord.getPub_address());
			return "OK";
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
			return Integer.toString(ERROR_PUBLISHER_NOT_UPDATED);
		} 
	}
	
	/**
	 * updates the list of active publishers with the publisher Id 
	 * provided
	 * 
	 * @param msg
	 * publisher record in byte code for which is active
	 * 
	 * @return int
	 * 	publisher Id or error code
	 */
	
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
	
	/**
	 * updates the directory service list with list of active publishers 
	 * provided
	
	 * @return boolean
	 * 	true if any updates or false
	 * 
	 */
	
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
