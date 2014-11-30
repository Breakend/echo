package com.example.android.wifidirect;

import java.util.concurrent.ConcurrentLinkedQueue;


/**
 * Responsible for sending all packets that appear in the queue
 * @author mverte
 */
public class Sender implements Runnable {
	
	private static ConcurrentLinkedQueue<Packet> ccl;
	
	public Sender() {
		if(ccl == null)
			ccl = new ConcurrentLinkedQueue<Packet>();
	}
	
	public static boolean queuePacket(Packet p) {
		if(ccl == null)
			ccl = new ConcurrentLinkedQueue<Packet>();
		return ccl.add(p);
	}
	
	@Override
	public void run() {
		TcpSender packetSender = new TcpSender();
		// Begin queuing hello packets to be sent
		//this.enqueueHelloPackets();
		while (true) {
			// A spin wait is super resource heavy
			// TODO: not this
			while (ccl.isEmpty()) {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			

			
			Packet p = ccl.remove();
			String ip = MeshNetworkManager.getIPForClient(p.getMac());
			packetSender.sendPacket(ip, Configuration.RECEIVE_PORT, p);

		}
	}
	

}
