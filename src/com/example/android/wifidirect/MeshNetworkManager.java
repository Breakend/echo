package com.example.android.wifidirect;


import java.util.concurrent.ConcurrentHashMap;

public class MeshNetworkManager {
	/**
	 * Your routing table
	 */
	public static ConcurrentHashMap<String, AllEncompasingP2PClient> routingTable =  new ConcurrentHashMap<String, AllEncompasingP2PClient>();
	
	/**
	 * Gotta know yourself
	 */
	private static AllEncompasingP2PClient self;
	
	public static void newClient(AllEncompasingP2PClient c){
		routingTable.put(c.getMac(), c);
	}
	
	public static void clientGone(AllEncompasingP2PClient c){
		routingTable.remove(c.getMac());
	}

	public static AllEncompasingP2PClient getSelf() {
		return self;
	}

	public static void setSelf(AllEncompasingP2PClient self) {
		MeshNetworkManager.self = self;
		newClient(self);
	}
	
	/**
	 * Either returns the IP in the current net if on the same one, or sends to the relevant
	 * Group Owner or sends to all group owners if group owner not in mesh
	 * 
	 * @param c
	 */
	public static String getIPForClient(AllEncompasingP2PClient c){
		
		/*
		 * This is part of the same Group so its okay to use its IP
		 */
		if(self.getGroupOwnerMac() == c.getGroupOwnerMac()){
			// share the same GO then just give its IP
			return c.getIp();
		}
		
		AllEncompasingP2PClient go = routingTable.get(c.getGroupOwnerMac());

		/*
		 * I am the group owner so can propagate
		 */
		if(self.getGroupOwnerMac() == self.getMac()){
			if(self.getGroupOwnerMac() != c.getGroupOwnerMac() && go.getIsDirectLink()){
				//not the same group owner, but we have the group owner as a direct link
				return c.getIp();
			}
			else if(go != null && self.getGroupOwnerMac() != c.getGroupOwnerMac() && !go.getIsDirectLink() ){
				//TODO: need to propagate to all GO available.
				return "0.0.0.0";
			}
		}
		else if(go != null){
			/*
			 * I am not the group owner need to send it to my group owner
			 */
			return Configuration.GO_IP;
		}
		
		return "0.0.0.0";

	}
	
	public static byte[] serializeRoutingTable(){
		//TODO: this to do the hello packet stuff
		StringBuilder serialized = new StringBuilder();
		
		for(AllEncompasingP2PClient v : routingTable.values()){
			serialized.append(v.toString());
			serialized.append("\n");
		}
		
		return serialized.toString().getBytes();
	}
	
	public static void deserializeRoutingTableAndAdd(byte[] rtable){
		String rstring = new String(rtable);
		
		String[] div = rstring.split("\n");
		for(String s : div){
			AllEncompasingP2PClient a = AllEncompasingP2PClient.fromString(s);
			routingTable.put(a.getMac(), a);
		}
	}
	
	/**
	 * Either returns the IP in the current net if on the same one, or sends to the relevant
	 * Group Owner or sends to all group owners if group owner not in mesh
	 * 
	 * @param c
	 */
	public static String getIPForClient(String mac){
		
		AllEncompasingP2PClient c = routingTable.get(mac);
		if(c == null){
			return Configuration.GO_IP;
		}
		
		return getIPForClient(c);

	}
	
	
	
}
