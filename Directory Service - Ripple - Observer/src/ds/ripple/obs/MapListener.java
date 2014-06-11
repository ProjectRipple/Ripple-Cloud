package ds.ripple.obs;

import java.util.HashMap;

import ds.ripple.common.PublisherRecord;

public interface MapListener {
	public void publishedMapUpdate(HashMap<Integer, PublisherRecord> map);
}
