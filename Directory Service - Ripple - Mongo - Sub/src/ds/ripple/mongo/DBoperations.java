package ds.ripple.mongo;

import java.util.Set;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

public final class DBoperations {

	private DB db;
	private Set<String> collection;
	
	public DBoperations(DB connection){
		db=connection;
	}
	
	public Set<String> getCollections(){
	 collection = db.getCollectionNames();
	
	for (String s : collection) {
	    System.out.println(s);
	}
	return collection;
	}
	
	public void  retrieveAll(String collectionName){
		if(this.checkCollection(collectionName)){
			
		DBCollection col = db.getCollection(collectionName);
	 	BasicDBObject query = new BasicDBObject();
	 	query.put("level", "Warning");
	 	
		DBCursor cursor = col.find(query);    
        while (cursor.hasNext()) { 
         
           System.out.println(cursor.next()); 
         
        }
		}
	}
	
	public BasicDBObject retrieveOne(String collectionName,String key,String value ){
		
		String collection=collectionName, field=key, fieldvalue=value;	
		DBCollection col = db.getCollection(collection);
		BasicDBObject qry = new BasicDBObject();
		qry.put(field, fieldvalue);
		DBCursor cur=col.find(qry);
		
		if(cur.hasNext()){
			qry = (BasicDBObject) cur.next();
			return qry;
		}
		else{
			return null;
		}
	
	}
	
	private boolean checkCollection(String colName){
		String collection=colName;
		
		for (String s : this.getCollections()) {
			 System.out.println(s);
			 // System.out.println(db.getDb().getCollection(s).find());
			 if(collection.equals(s)){
				 return true;
			 }
		}
		return false;
	}	
}
