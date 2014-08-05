package ds.ripple.MQTTSub;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

import org.eclipse.paho.client.mqttv3.MqttException;

import org.eclipse.paho.client.mqttv3.MqttSecurityException;

import org.json.simple.JSONObject;

public abstract class MqttSubcriber implements MqttCallback {

	MqttClient myClient;
	MqttConnectOptions connOpt;
	JSONObject json;

	protected String BROKER_URL = "tcp://10.0.3.70:1883";

	static final String CLIENT_ID = "MQTTsub1";
	//String Topic = "P_Stats/#";
	String Topic = "#";

	/**
	 * 
	 * This callback is invoked upon losing the MQTT connection.
	 * 
	 */
	@Override
	public void connectionLost(Throwable t) {
		try {
			myClient.connect(connOpt);
		} catch (MqttSecurityException e) {

			e.printStackTrace();
		} catch (MqttException e) {

			e.printStackTrace();
		}
	}

	/**
	 * 
	 * start mqtt client for connecting to broker
	 * 
	 */
	public void runClient() {
		// setup MQTT Client
		String clientID = CLIENT_ID;
		connOpt = new MqttConnectOptions();
		connOpt.setCleanSession(false);
		connOpt.setKeepAliveInterval(60);
		// connOpt.setUserName(USERNAME);
		// connOpt.setPassword(PASSWORD_MD5.toCharArray());

		// Connect to Broker
		try {
			myClient = new MqttClient(BROKER_URL, clientID);
			myClient.setCallback(this);
			myClient.connect(connOpt);
		} catch (MqttException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		System.out.println("Connected to " + BROKER_URL);

		// subscribe to topic
		try {
			int subQoS = 0;
			myClient.subscribe(Topic, subQoS);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		// TODO Auto-generated method stub

	}
	
	
	  public void stopClient() throws Exception {	    
	    // Cleanly stop the Mqtt client connection
	    myClient.disconnect();
	  }
}
