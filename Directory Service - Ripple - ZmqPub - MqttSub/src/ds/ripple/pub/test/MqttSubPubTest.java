package ds.ripple.pub.test;

import java.util.Scanner;


import ds.ripple.MQTTSub.ZMQPub;



public class MqttSubPubTest {
	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		System.out.println("Enter the Mqtt broker url");	
		String MqttUrl =in.nextLine();
		System.out.println("Enter publisher URL (ex: tcp://192.168.0.10):");
		String pubURL = in.nextLine();
		System.out.println("Enter Directory Services URL (ex: tcp://192.168.0.11):");
		String dsURL = in.nextLine();
		System.out.println("Enter publisher name:");
		String pubName = in.nextLine();
		ZMQPub mqttSubPub = new ZMQPub(MqttUrl,pubURL, dsURL,pubName);
			try {
				mqttSubPub.startPub();
				System.out.println("Press enter to stop...");
				in.close();
			//	mqttSubPub.stopPub();
			//	in.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}	
	}
