package ds.ripple.pub.util;

import java.io.IOException;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;

import ds.ripple.server.Directory;

public class DirectoryPublisher {
	private Context context;
	private Directory dir;
	private ZMQ.Socket publisher;
	
	public DirectoryPublisher(Context ctx, Directory dir){
		this.context=ctx;
		this.dir = dir;
		publisher = context.socket(ZMQ.PUB);
		publisher.bind("tcp://*:5556");
	}

    public void publish() { 
            try {	
            	publisher.sendMore("DIR");
				publisher.send(MessageBuilder.buildMsg(dir.getDirectoryList()), 0);
			} catch (IOException e) {
				e.printStackTrace();
			}
   	} 
    
    public void closePublisher() {
    	publisher.close();
    }
}
