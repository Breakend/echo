package com.ecse414.android.echo.router.tcp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.ecse414.android.echo.router.Packet;

/**
 * Receives packets on a server socket threads and enqueues them to a receiver runner
 * 
 * @author Matthew Vertescher
 *
 */
public class TcpReciever implements Runnable {

	private ServerSocket serverSocket;
	private ConcurrentLinkedQueue<Packet> packetQueue;

	/**
	 * Constructor with the queue
	 * @param port
	 * @param queue
	 */
	public TcpReciever(int port, ConcurrentLinkedQueue<Packet> queue) {
		try {
			this.serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			System.err.println("Server socket on port " + port + " could not be created. ");
			e.printStackTrace();
		}
		this.packetQueue = queue;
	}

	/**
	 * Thread runner
	 */
	@Override
	public void run() {
		Socket socket;
		while (!Thread.currentThread().isInterrupted()) {
			try {
				socket = this.serverSocket.accept();
				InputStream in = socket.getInputStream();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();

				byte[] buf = new byte[1024];
				while (true) {
					int n = in.read(buf);
					if (n < 0)
						break;
					baos.write(buf, 0, n);
				}

				byte trimmedBytes[] = baos.toByteArray();
				Packet p = Packet.deserialize(trimmedBytes);
				p.setSenderIP(socket.getInetAddress().getHostAddress());
				this.packetQueue.add(p);
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}