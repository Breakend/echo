package com.ecse414.android.echo.config;

/**
 * Contains configuration settings related to the WiFi Direct implementation
 * @author Peter Henderson
 *
 */
public class Configuration {
	/**
	 * The default ports that all clients receive at
	 */
	public static final int RECEIVE_PORT = 8888;
	
	/**
	 * The default GO IP address for initial connections
	 */
	public static final String GO_IP = "192.168.49.1";

	/**
	 * This only works on certain devices where multiple simultaneous
	 * connections are available (infrastructure & ad-hoc) (multiroll)
	 */
	public static final boolean isDeviceBridgingEnabled = false;

}
