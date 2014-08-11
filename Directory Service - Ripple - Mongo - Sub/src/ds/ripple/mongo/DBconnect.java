package ds.ripple.mongo;

import java.net.UnknownHostException;
import java.util.List;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

import ds.ripple.mongo.exceptions.IncorrectDBName;



public class DBconnect {
	private DB db;
	private MongoClient mongoClient;

	/**
	 * creates an instance of DBconnect class
	 * 
	 * @param hostaddress,port, Dbname
	 * 
	 * used to connect database and returns object of type DB
	 * @throws IncorrectDBName 
	 * @throws UnknownHostException IncorrectDBName
	 * 
	 */
	public DBconnect(String hostaddress,int port, String Dbname) throws IncorrectDBName, UnknownHostException {	

	 mongoClient = new MongoClient(new ServerAddress(hostaddress , port));
	 if(checkDBname(Dbname)){
		 db = mongoClient.getDB( Dbname );
	 } else throw new IncorrectDBName("Incorrect DBname");
		
	}
	
	/**
	 * getter method for variable DB
	 * 
	 * used to connect database and returns object of type DB
	 * 
	 * @return DB
	 * 
	 */
	public DB getDb() {
		return db;
	}
	
	/**
	 * changes the db that is connected 
	 * 
	 * @param Dbname
	 * 
	 * checks the db name that is requested to connect before 
	 * connecting
	 * @return DB
	 * @throws IncorrectDBName
	 */
	public DB changeDB(String Dbname) throws IncorrectDBName{
		 if(checkDBname(Dbname)){
			 db = mongoClient.getDB( Dbname );
		 }
		 else throw new IncorrectDBName("Incorrect DBname");
		return db;
	}

	/**
	 * gets the list dbs existed in the database
	 * 
	 * @return List<String>
	 * 
	 */
	public List<String> getDBlist(){
		return mongoClient.getDatabaseNames();	
	}
	
	/**
	 * checks for the existence of the mentioned DBname 
	 * in connected DB. 
	 * @param DBname
	 * 
	 * @return boolean
	 * 
	 */
	private boolean checkDBname(String DBname){
		if(mongoClient.getDatabaseNames().contains(DBname)){
			return true;
		}
		else {
			return false;
		}
	}
	

}
