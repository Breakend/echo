package com.example.android.wifidirect;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TcpReciever implements Runnable {
	
	private ServerSocket serverSocket;
	private ConcurrentLinkedQueue<Packet> packetQueue;
	
	public TcpReciever(int port, ConcurrentLinkedQueue<Packet> queue) {
		try {
			this.serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			System.err.println("Server socket on port " + port + " could not be created. ");
			e.printStackTrace();
		}
		this.packetQueue = queue;
	}

	@Override
	public void run() {
		Socket socket;
		while (!Thread.currentThread().isInterrupted()) {		
			try {

				socket = this.serverSocket.accept();
//				System.out.println("Got a packet?");
				InputStream in = socket.getInputStream();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();

				byte[] buf = new byte[1024];
				while(true) {
				  int n = in.read(buf);
				  if( n < 0 ) break;
				  baos.write(buf,0,n);
				}

				byte trimmedBytes[] = baos.toByteArray();
//				System.out.println("PACKET BYTES: " + new String(trimmedBytes));
				Packet p = Packet.deserialize(trimmedBytes);
				p.setSenderIP(socket.getInetAddress().getHostAddress());
//				System.out.println("IP: " +socket.getInetAddress().getHostAddress() );
//				System.err.println("Server | connection accepted, added to packet to queue");
				this.packetQueue.add(p);
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}