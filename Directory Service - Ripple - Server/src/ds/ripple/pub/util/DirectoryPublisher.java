package ds.ripple.pub.util;

import java.io.IOException;


import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;

import ds.ripple.server.Directory;

/**
 * Directory Publisher class publishes the list of publishers that are registered 
 * with the server. This class uses ZeroMQ libraries to communicate as publisher
 * and publish messages over pub socket.
 * 
 */

public class DirectoryPublisher {
	
	
	private ZMQ.Socket publisher;
	
	/**
	 * Used to create Object type of DirectoryPublisher
	 * 
	 * @param context
	 *  parameter used to specify the context that is used to start
	 *  the publisher
	 *  
	 * @param dir 
	 * Instance of Directory class used to call directory list
	 */
	public DirectoryPublisher(Context context){
	
	
		publisher = context.socket(ZMQ.PUB);
		publisher.bind("tcp://*:5556");
	}

	/**
	 * Used to publish the directory list in server
	 * This function publishes directory list under topic "DIR"
	 * 
	 */
    public void publish() { 
            try {	
            	publisher.sendMore("DIR");
				publisher.send(MessageBuilder.buildMsg(Directory.getDirectoryList()), 0);
			} catch (IOException e) {
				e.printStackTrace();
			}
   	} 
    
    /**
	 * used to close the existing publisher
	 * this function calls close() function in ZeroMQ 
	 * libraries to terminate publisher socket.
	 * 
	 */
    public void closePublisher() {
    	publisher.close();
    }
}
