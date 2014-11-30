package com.example.android.wifidirect;

public class Configuration {
	public static final int RECEIVE_PORT = 8888;
	public static final String GO_IP = "192.168.49.1";
	
	/**
	 * This only works on certain devices where multiple simultaneous connections are 
	 * available (infrastructure & ad-hoc) (multiroll)
	 */
	public static final boolean isDeviceBridgingEnabled = false;

}
