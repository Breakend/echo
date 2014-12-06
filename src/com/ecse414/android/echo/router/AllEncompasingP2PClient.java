package com.ecse414.android.echo.router;

/**
 * This is an alternative representation the Android P2P library's WiFiP2PDevice class
 * it contains information about any client connected to the mesh and is stored in
 * the routing table
 * 
 * @author Peter Henderson
 *
 */
public class AllEncompasingP2PClient {

	/**
	 * The client's mac address
	 */
	private String mac;
	
	/**
	 * The client's name (i.e. Joe)
	 */
	private String name;
	
	/**
	 * The client's GO mac address, for routing
	 */
	private String groupOwnerMac;
	
	/**
	 * The client's IP address
	 */
	private String ip;
	
	/**
	 * Whether it is a direct link or not, this could help with making routing more efficient
	 */
	private boolean isDirectLink;

	/**
	 * Constructor
	 */
	public AllEncompasingP2PClient(String mac_address, String ip, String name, String groupOwner) {
		this.setMac(mac_address);
		this.setName(name);
		this.setIp(ip);
		this.setGroupOwnerMac(groupOwner);
		this.isDirectLink = true;
	}

	/**
	 * Change this if we don't have a direct link
	 * 
	 * @param d
	 */
	public void setIsDirectLink(boolean d) {
		this.isDirectLink = d;
	}

	/**
	 * Get if have a direct link to this client from the current running client
	 * @return
	 */
	public boolean getIsDirectLink() {
		return this.isDirectLink;
	}

	/**
	 * Get GO MAC
	 * @return
	 */
	public String getGroupOwnerMac() {
		return groupOwnerMac;
	}

	/**
	 * Set GO MAC
	 * @param groupOwnerMac
	 */
	public void setGroupOwnerMac(String groupOwnerMac) {
		this.groupOwnerMac = groupOwnerMac;
	}

	/**
	 * Get the client's name
	 * Note: here we don't currently use this so much, and just refer to client's as MAC addresses
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set a client's name (like a nickname or something for future use)
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get the client's mac address
	 * @return
	 */
	public String getMac() {
		return mac;
	}

	/**
	 * Set the client's mac address
	 * @param mac
	 */
	public void setMac(String mac) {
		this.mac = mac;
	}

	/**
	 * Get the client's IP as a string
	 * (i.e. 124.12.124.15)
	 * @return
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * Set the client's IP as a string
	 * @param ip
	 */
	public void setIp(String ip) {
		this.ip = ip;
	}

	/**
	 * Serialize this client's information into a comma delimited form
	 */
	@Override
	public String toString() {
		return getIp() + "," + getMac() + "," + getName() + "," + getGroupOwnerMac();
	}

	/**
	 * Generate a client object from a serializerd string
	 * @param serialized
	 * @return
	 */
	public static AllEncompasingP2PClient fromString(String serialized) {
		String[] divided = serialized.split(",");
		return new AllEncompasingP2PClient(divided[1], divided[0], divided[2], divided[3]);
	}

}
