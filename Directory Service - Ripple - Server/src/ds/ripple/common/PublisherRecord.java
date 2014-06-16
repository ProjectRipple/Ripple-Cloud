package ds.ripple.common;

import java.io.Serializable;

public class PublisherRecord implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Integer pub_Id;
	private String pub_address;
	private String pub_name;
	private String[] topics;

	public String[] getTopics() {
		return topics;
	}

	public void setTopics(String[] topics) {
		this.topics = topics;
	}

	public Integer getPub_Id() {
		return pub_Id;
	}

	public String getPub_address() {
		return pub_address;
	}

	public void setPub_address(String pub_address) {
		this.pub_address = pub_address;
	}

	public void setPub_Id(Integer pub_Id) {
		this.pub_Id = pub_Id;
	}

	public String getPub_name() {
		return pub_name;
	}

	public void setPub_name(String pub_name) {
		this.pub_name = pub_name;
	}

}
