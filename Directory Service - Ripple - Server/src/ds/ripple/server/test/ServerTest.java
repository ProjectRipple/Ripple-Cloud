package ds.ripple.server.test;

import java.util.Scanner;

import ds.ripple.server.HWServer;

public class ServerTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		HWServer server = new HWServer();
		server.start();
		System.out.println("Press enter to stop...");
		Scanner in = new Scanner(System.in);
		in.nextLine();
		System.out.println("Waiting for server to terminate...");
		server.stop();
		in.close();
	}

}
