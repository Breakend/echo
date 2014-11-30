package com.example.android.wifidirect;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.widget.Toast;

public class Receiver implements Runnable {

	public static boolean running = false;
	WiFiDirectActivity activity;
	
	public Receiver(WiFiDirectActivity a) {
		this.activity = a;
		running = true;
	}

	public void run() {
		ConcurrentLinkedQueue<Packet> packetQueue = new ConcurrentLinkedQueue<Packet>();

		new Thread(new TcpReciever(Configuration.RECEIVE_PORT, packetQueue)).start();

		Packet p;

		while (true) {
			while (packetQueue.isEmpty()) {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			p = packetQueue.remove();	
			
//			System.out.println("Packet :" + p.toString());
			if(p.getType().equals(Packet.TYPE.HELLO)){
				//Put it in your routing table
				for(AllEncompasingP2PClient c : MeshNetworkManager.routingTable.values()){
					if(c.getMac().equals(MeshNetworkManager.getSelf().getMac())
							|| c.getMac().equals(p.getSenderMac())) continue;
					Packet update = new Packet(Packet.TYPE.UPDATE, Packet.getMacAsBytes(p.getSenderMac()), c.getMac(), MeshNetworkManager.getSelf().getMac());
					Sender.queuePacket(update);
				}

				MeshNetworkManager.routingTable.put(p.getSenderMac(), new AllEncompasingP2PClient(p.getSenderMac(), p.getSenderIP(), p.getSenderMac(), MeshNetworkManager.getSelf().getMac()));
				
				//Send update to all nodes in your routing table
				
				// Update UI
				//PeerListFragment fragment = (PeerListFragment) activity.getFragmentManager().findFragmentById(R.id.frag_peers);
	            //fragment.updatePeerList(new ArrayList<AllEncompasingP2PClient>(MeshNetworkManager.routingTable.values()));
	            
				activity.runOnUiThread(new Runnable(){
			        @Override
			        public void run() {
			            DeviceDetailFragment.updateGroupChatMembersMessage();
			        }
			        
				});
				//Send routing table back as HELLO_ACK
				byte[] rtable = MeshNetworkManager.serializeRoutingTable();

				
				Packet ack = new Packet(Packet.TYPE.HELLO_ACK, rtable, p.getSenderMac(), MeshNetworkManager.getSelf().getMac());
				Sender.queuePacket(ack);
				System.out.println("GOT HELLO");
			}
			else{
			//If you're the intendeded target for a non hello message
			if(p.getMac().equals(MeshNetworkManager.getSelf().getMac())){
					if(p.getType().equals(Packet.TYPE.HELLO_ACK)){
						MeshNetworkManager.deserializeRoutingTableAndAdd(p.getData());
						MeshNetworkManager.getSelf().setGroupOwnerMac(p.getSenderMac());
						activity.runOnUiThread(new Runnable(){
					        @Override
					        public void run() {
					            DeviceDetailFragment.updateGroupChatMembersMessage();
					        }
					        
						});
					}
					else if(p.getType().equals(Packet.TYPE.UPDATE)){
						String emb_mac = Packet.getMacBytesAsString(p.getData(), 0);
						MeshNetworkManager.routingTable.put(emb_mac, new AllEncompasingP2PClient(emb_mac, p.getSenderIP(), p.getMac(), MeshNetworkManager.getSelf().getMac()));
						
						final String message = emb_mac + " joined the conversation";
						final String name = p.getSenderMac();
						activity.runOnUiThread(new Runnable() {

					        @Override
					        public void run() {
					            if (activity.isVisible) {
					            	Toast.makeText(activity,message, Toast.LENGTH_LONG).show();
					            } else {
					            	MessageActivity.addMessage(name, message);
					            }
					        }
					    });
						activity.runOnUiThread(new Runnable(){
					        @Override
					        public void run() {
					            DeviceDetailFragment.updateGroupChatMembersMessage();
					        }
					        
						});
					}
					else if(p.getType().equals(Packet.TYPE.MESSAGE)){
						final String message =  p.getSenderMac() + " says:\n" + new String(p.getData());
						final String msg = new String(p.getData());
						final String name = p.getSenderMac();
						
						if(!MeshNetworkManager.routingTable.contains(p.getSenderMac())){
							/*
							 * Update your routing table if for some reason this guy isn't in it
							 */
							MeshNetworkManager.routingTable.put(p.getSenderMac(), 
									new AllEncompasingP2PClient(p.getSenderMac(),p.getSenderIP(), p.getSenderMac(), MeshNetworkManager.getSelf().getGroupOwnerMac())
									);
						}
						
						activity.runOnUiThread(new Runnable() {

					        @Override
					        public void run() {
					            if (activity.isVisible) {
					            	Toast.makeText(activity,message, Toast.LENGTH_LONG).show();
					            } else {
					            	MessageActivity.addMessage(name, msg);
					            }
					        } 
					    });
						activity.runOnUiThread(new Runnable(){
					        @Override
					        public void run() {
					            DeviceDetailFragment.updateGroupChatMembersMessage();
					        }
					        
						});
					}
				}
				else{
					//otherwise forward it 
					int ttl = p.getTtl();
					//Have a ttl so that they don't bounce around forever
					ttl--;
					if(ttl > 0){
						Sender.queuePacket(p);
						p.setTtl(ttl);
					}
				}
			}



		}
	}

}