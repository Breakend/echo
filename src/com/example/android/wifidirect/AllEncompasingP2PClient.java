package com.example.android.wifidirect;

public class AllEncompasingP2PClient {

	private String mac;
	private String name;
	private String groupOwnerMac;
	private String ip;
	private boolean isDirectLink; // are we directly connected to this link?

	/**
	 * Keep track of all the data
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

	public boolean getIsDirectLink() {
		return this.isDirectLink;
	}

	public String getGroupOwnerMac() {
		return groupOwnerMac;
	}

	public void setGroupOwnerMac(String groupOwnerMac) {
		this.groupOwnerMac = groupOwnerMac;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	@Override
	public String toString() {
		return getIp() + "," + getMac() + "," + getName() + "," + getGroupOwnerMac();
	}

	public static AllEncompasingP2PClient fromString(String serialized) {
		String[] divided = serialized.split(",");
		return new AllEncompasingP2PClient(divided[1], divided[0], divided[2], divided[3]);
	}

}
