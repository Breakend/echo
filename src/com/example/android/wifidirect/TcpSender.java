package com.example.android.wifidirect;

import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class TcpSender {

	Socket tcpSocket = null;

	public boolean sendPacket(String ip, int port, Packet data) {

		try {
			System.out.println("IP: " + ip);
			InetAddress serverAddr = InetAddress.getByName(ip);
			tcpSocket = new Socket();
			tcpSocket.bind(null);
			tcpSocket.connect(new InetSocketAddress(serverAddr, port), 5000);

		} catch (Exception e) {
			/*
			 * If can't connect assume that they left the chat and remove them
			 */
			System.out.println("eXception removing: " + data.getMac());
			MeshNetworkManager.routingTable.remove(data.getMac());
			Receiver.somebodyLeft(data.getMac());
			Receiver.updatePeerList();
			e.printStackTrace();
			return false;
		}

		OutputStream os = null;

		try {
			os = tcpSocket.getOutputStream();
			os.write(data.serialize());
			os.close();
			tcpSocket.close();

		} catch (Exception e) {
			System.out.println("eXception");
			MeshNetworkManager.routingTable.remove(data.getMac());
			Receiver.somebodyLeft(data.getMac());
			Receiver.updatePeerList();
			e.printStackTrace();
		}

		return true;
	}

}
