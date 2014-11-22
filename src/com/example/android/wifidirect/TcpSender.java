package com.example.android.wifidirect;


import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class TcpSender {
	
	Socket tcpSocket = null;
	
	public boolean sendPacket(String ip, int port, byte[] data) {
		
		try {
			
			 System.out.println(" executeCammand");
		        Runtime runtime = Runtime.getRuntime();
		        try
		        {
		            Process  mIpAddrProcess = runtime.exec("/system/bin/ping -c 1 192.168.49.144");
		            int mExitValue = mIpAddrProcess.waitFor();
		            System.out.println(" mExitValue "+mExitValue);
		        }
		        catch (InterruptedException ignore)
		        {
		            ignore.printStackTrace();
		            System.out.println(" Exception:"+ignore);
		        } 
		        catch (IOException e) 
		        {
		            e.printStackTrace();
		            System.out.println(" Exception:"+e);
		        }
			
			
			
			InetAddress serverAddr = InetAddress.getByName(ip);
			System.out.println("IP TO SEND TO: " + ip);
			System.out.println("IP TO 2 2easfaf SEND TO: " + serverAddr.toString());
			tcpSocket = new Socket();
			System.out.println("socket created");
			tcpSocket.bind(null);
			System.out.println("socket bound null");
			tcpSocket.connect(new InetSocketAddress(serverAddr, port), 5000);
			System.out.println("Socket connected");

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		OutputStream os = null;
		
		try {
			os = tcpSocket.getOutputStream();
			System.out.println("stream created");

			os.write(data);
			System.out.println("wrote data");

			os.close();
			System.out.println("flush ");

			tcpSocket.close();
			System.out.println("closed socket ");

		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("Sent packet");
		return true;
	}
	
}
