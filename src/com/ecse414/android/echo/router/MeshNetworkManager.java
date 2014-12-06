package com.ecse414.android.echo.router;

import java.util.concurrent.ConcurrentHashMap;

import com.ecse414.android.echo.config.Configuration;

/**
 * A manager for keeping track of a mesh and handling routing
 * 
 * @author Peter Henderson
 *
 */
public class MeshNetworkManager {
	/**
	 * Your routing table
	 */
	public static ConcurrentHashMap<String, AllEncompasingP2PClient> routingTable = new ConcurrentHashMap<String, AllEncompasingP2PClient>();

	/**
	 * Need to know yourself
	 */
	private static AllEncompasingP2PClient self;

	/**
	 * Introduce a new client into the routing table
	 * @param c
	 */
	public static void newClient(AllEncompasingP2PClient c) {
		routingTable.put(c.getMac(), c);
	}

	/**
	 * A client has left the routing table
	 * @param c
	 */
	public static void clientGone(AllEncompasingP2PClient c) {
		routingTable.remove(c.getMac());
	}

	/**
	 * Get yourself
	 * @return
	 */
	public static AllEncompasingP2PClient getSelf() {
		return self;
	}

	/**
	 * Set yourself
	 * @param self
	 */
	public static void setSelf(AllEncompasingP2PClient self) {
		MeshNetworkManager.self = self;
		newClient(self);
	}

	/**
	 * Either returns the IP in the current net if on the same one, or sends to
	 * the relevant Group Owner or sends to all group owners if group owner not
	 * in mesh
	 * 
	 * @param c
	 */
	public static String getIPForClient(AllEncompasingP2PClient c) {

		/*
		 * This is part of the same Group so its okay to use its IP
		 */
		if (self.getGroupOwnerMac() == c.getGroupOwnerMac()) {
			// share the same GO then just give its IP
			System.out.println("Have the same group owner, sending to :" + c.getIp());
			return c.getIp();
		}

		AllEncompasingP2PClient go = routingTable.get(c.getGroupOwnerMac());

		
		// I am the group owner so can propagate
		if (self.getGroupOwnerMac() == self.getMac()) {
			if (self.getGroupOwnerMac() != c.getGroupOwnerMac() && go.getIsDirectLink()) {
				// not the same group owner, but we have the group owner as a
				// direct link
				return c.getIp();
			} else if (go != null && self.getGroupOwnerMac() != c.getGroupOwnerMac() && !go.getIsDirectLink()) {
				for(AllEncompasingP2PClient aclient : routingTable.values()){
					if(aclient.getGroupOwnerMac().equals(aclient.getMac())){
						//try sending it to a random group owner
						//can also expand this to all group owners
						return aclient.getIp();
					}
				}
				//no other group owners, don't know who to send it to
				return "0.0.0.0";
			}
		} else if (go != null) { // I am not the group owner - need to sent it to my GO
			return Configuration.GO_IP;
		}

		//Will drop the packet
		return "0.0.0.0";

	}

	/**
	 * Serialize the routing table, one serialized AllEncompasingP2PClient per line
	 * @return
	 */
	public static byte[] serializeRoutingTable() {
		StringBuilder serialized = new StringBuilder();

		for (AllEncompasingP2PClient v : routingTable.values()) {
			serialized.append(v.toString());
			serialized.append("\n");
		}

		return serialized.toString().getBytes();
	}

	/**
	 * De serialize a routing table and populate the existing one with the data
	 * @param rtable
	 */
	public static void deserializeRoutingTableAndAdd(byte[] rtable) {
		String rstring = new String(rtable);

		String[] div = rstring.split("\n");
		for (String s : div) {
			AllEncompasingP2PClient a = AllEncompasingP2PClient.fromString(s);
			routingTable.put(a.getMac(), a);
		}
	}

	/**
	 * Either returns the IP in the current net if on the same one, or sends to
	 * the relevant Group Owner or sends to all group owners if group owner not
	 * in mesh
	 * 
	 * @param c
	 */
	public static String getIPForClient(String mac) {

		AllEncompasingP2PClient c = routingTable.get(mac);
		if (c == null) {
			System.out.println("NULL ENTRY in ROUTING TABLE FOR MAC");
			return Configuration.GO_IP;
		}

		return getIPForClient(c);

	}

}
