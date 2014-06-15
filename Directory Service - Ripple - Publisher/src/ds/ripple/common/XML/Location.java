package ds.ripple.common.XML;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {"longitude", "latitude", "altitude"})
@XmlRootElement
public final class Location {
	private String description;
	private String longitude;
	private String latitude;
	private String altitude;
	
	protected Location() {
		
	}
	
	protected Location(String longitude, String latitude, String altitude, String description) {
		this.longitude = longitude;
		this.latitude = latitude;
		this.altitude = altitude;
		this.description = description;
	}
	
	protected Location(String longitude, String latitude, String description) {
		this.longitude = longitude;
		this.latitude = latitude;
		this.description = description;
	}
	
	@XmlAttribute
	public String getDescription() {
		return description;
	}
	
	protected void setDescription(String description) {
		this.description = description;
	}

	@XmlElement
	public String getLongitude() {
		return longitude;
	}

	protected void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	@XmlElement
	public String getLatitude() {
		return latitude;
	}

	protected void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	
	@XmlElement
	public String getAltitude() {
		return altitude;
	}
	
	protected void setAltitude(String alititude) {
		this.altitude = alititude;
	}
	
	protected boolean hasLongLat() {
		if (this.longitude == null || this.longitude.isEmpty()) {
			return false;
		}
		if (this.latitude == null || this.latitude.isEmpty()) {
			return false;
		}
		return true;
	}
}
