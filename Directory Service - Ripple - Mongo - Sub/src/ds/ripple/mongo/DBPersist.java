package ds.ripple.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;


public class DBPersist {
	DBObject dbObject;
	
	private DB dbobj;

	public DBPersist(DB db) {
		dbobj = db;
	}

	/**
	 * checks for the existence of the mentioned DBname 
	 * in connected DB. 
	 * @param topic,data
	 * 
	 * 
	 */
	public void stringData(String topic, String data) {
		/*
		 * Map<String, Object> msgDoc = new HashMap<String, Object>();
		 * msgDoc.put("topic", topic); msgDoc.put("message", data);
		 */
		String collection = "unknown";
		try {
			if(isJson(data)){
				dbObject = (DBObject) JSON.parse(data);
			}
			else{
				dbObject = new BasicDBObject();
				dbObject.put("message", data);
				//dbobj.getCollection(collection).insert(msg);
			}
			switch (topic.toLowerCase()) {
			case "p_stats_vitalcast":
				collection = "p_stats_vitalcast";
				break;
			case "p_stats_info":
				collection = "p_stats_info";
				break;
			case "p_stream":
				collection = "p_stream";
				break;
			case "c_status":
				collection = "c_status";
				break;
			case "r_status":
				collection = "r_status";
				break;
			case "p_action":
				collection = "p_action";
				break;
			default:
				collection = "unknown";
			}
			dbobj.getCollection(collection).insert(dbObject);
		} catch (Exception e) {
			e.printStackTrace();
			
		}

	}
	/**
	 * checks for the string if it is JSON
	 * @param str
	 * 
	 * @return boolean
	 * 
	 */
	private boolean isJson(String str){
		try {
	        JSON.parse(str);
	    } catch (Exception e) {
	        return false;
	    }
	    return true;
	}
	

}
