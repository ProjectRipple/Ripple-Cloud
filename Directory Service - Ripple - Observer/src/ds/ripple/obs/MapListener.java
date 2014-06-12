package ds.ripple.obs;

import java.util.HashMap;

import ds.ripple.common.PublisherRecord;

/**
 * This interface has to be implemented by a class that wants to receive updates
 * from the Directory Services server. publishedMapUpdate method will be called
 * every time the Directory Services server publishes an new list of available
 * publishers.
 * 
 * @author pawel
 * 
 */
public interface MapListener {
	/**
	 * Called when a new list of publishers has been received from the Directory
	 * Services server.
	 * 
	 * @param map
	 *            HashMap that contains a list of publishers' information that
	 *            is provided by the Directory Services server. HashMapKey - a
	 *            unique integer that identifies each of the publishers.
	 *            PublisherRecord - see PublisherRecord class
	 */
	public void publishedMapUpdate(HashMap<Integer, PublisherRecord> map);
}
