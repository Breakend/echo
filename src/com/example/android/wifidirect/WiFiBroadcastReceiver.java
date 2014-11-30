package com.example.android.wifidirect;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteOrder;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.NetworkInfo.DetailedState;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.Toast;

public class WiFiBroadcastReceiver extends BroadcastReceiver {

	private WifiManager wifiManager;
	private WiFiDirectActivity activity;
	private boolean isWifiConnected;
	private List<ScanResult> wifiList;
	private StringBuilder sb = new StringBuilder();

	public WiFiBroadcastReceiver(WifiManager wifiManager,
			WiFiDirectActivity activity, boolean isWifiConnected) {
		super();
		this.wifiManager = wifiManager;
		this.activity = activity;
		this.isWifiConnected = isWifiConnected;
	}

	// This method call when number of wifi connections changed
	public void onReceive(Context context, Intent intent) {

		String action = intent.getAction();
		// if (WifiManager.ERROR_AUTHENTICATING == (int) Integer
		// .getInteger(action)) {
		// Log.d(WiFiDirectActivity.TAG, "ERROR AUTHENTICATING");
		// }

		if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)) {

			String wfdSsid = null;

			sb = new StringBuilder();
			wifiList = this.wifiManager.getScanResults();
			sb.append("\n        Number Of Wifi connections :"
					+ wifiList.size() + "\n\n");

			for (int i = 0; i < wifiList.size(); i++) {

				if (wifiList.get(i).SSID.contains("DIRECT")) {
					wfdSsid = wifiList.get(i).SSID;
				}

				sb.append(new Integer(i + 1).toString() + ". ");
				sb.append((wifiList.get(i)).toString());
				sb.append("\n\n");
			}

			// Log.d(WiFiDirectActivity.TAG, sb.toString());

			// Log.d(WiFiDirectActivity.TAG, "Ssid that should be WFD : " +
			// wfdSsid);

			if (wfdSsid != null && !this.isWifiConnected) {
				//this.activity.displayConnectDialog(wfdSsid);
			} else {
				Toast.makeText(
						activity,
						"Found no WiFi direct network to connect to using wlan0",
						Toast.LENGTH_LONG).show();
			}
			// }
			// mainText.setText(sb);
		} else if(WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
			Log.d("WiFiBroadcastReceiver", "WIFI_STATE_CHANGED_ACTION");
            int iTemp = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,
                    WifiManager.WIFI_STATE_UNKNOWN);
            checkState(iTemp); 
        } else if(WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
        	Log.d("WiFiBroadcastReceiver", "NETWORK_STATE_CHANGED_ACTION");
        	NetworkInfo netInfo = (NetworkInfo)intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            DetailedState state= netInfo.getDetailedState();
            Log.d("WiFiBroadcastReceiver", "	state = " + state.name());
            changeState(state, context);
            
        } /*else if (WifiManager.SUPPLICANT_STATE_CHANGED_ACTION.equals(action)) {
        	DetailedState state=
                    ((NetworkInfo)intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO)).getDetailedState();
            changeState(state, context);
        }*/
		
	}
	
	private void changeState(DetailedState aState, Context context) {
        if (aState == DetailedState.SCANNING) {
            Log.d("WiFiBroadcastReceiver", "SCANNING");
        }
        else if (aState == DetailedState.CONNECTING) {
            Log.d("WiFiBroadcastReceiver", "CONNECTING");
        }
        else if(aState == DetailedState.OBTAINING_IPADDR) {
            Log.d("WiFiBroadcastReceiver", "OBTAINING_IPADDR");
        }
        else if (aState == DetailedState.CONNECTED) {
            Log.d("WiFiBroadcastReceiver", "CONNECTED");
            Log.d("WiFiBroadcastReceiver", "	bssid="+wifiManager.getConnectionInfo().getBSSID());
            //Log.d("WiFiBroadcastReceiver", "	ip="+Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress()));
            Log.d("WiFiBroadcastReceiver", "	ip="+this.parseIpAddress(wifiManager.getConnectionInfo().getIpAddress()));
            Log.d("WiFiBroadcastReceiver", "	ssid="+wifiManager.getConnectionInfo().getSSID());
            Log.d("WiFiBroadcastReceiver", "	dhcpGtw="+this.parseIpAddress(wifiManager.getDhcpInfo().gateway));
            Log.d("WiFiBroadcastReceiver", "	MAC="+wifiManager.getConnectionInfo().getMacAddress());
            Log.d("WiFiBroadcastReceiver", "	dhcpServer="+this.parseIpAddress(wifiManager.getDhcpInfo().serverAddress));
            Log.d("WiFiBroadcastReceiver", "	netmask="+this.parseIpAddress(wifiManager.getDhcpInfo().netmask));
            
            MeshNetworkManager.setSelf(new AllEncompasingP2PClient(wifiManager.getConnectionInfo().getMacAddress(), 
            		Configuration.GO_IP, 
            		wifiManager.getConnectionInfo().getMacAddress(), 
            		wifiManager.getConnectionInfo().getMacAddress()));

            if (!Receiver.running){
            	Receiver r = new Receiver(this.activity);
            	new Thread(r).start();
            	Sender s = new Sender();
            	new Thread(s).start();
            	}
        }
        else if (aState == DetailedState.DISCONNECTING) {
            Log.d("WiFiBroadcastReceiver", "DISCONNECTING");
        }
        else if (aState == DetailedState.DISCONNECTED) {
        	Log.d("WiFiBroadcastReceiver", "DISCONNECTED");
           // myApp myApp = (myApp)context.getApplicationContext();

            //portableHotspot = myApp.getPortableHotspot();

            //if (portableHotspot) {
             //   Methods.setWirelessHotSpot(context.getApplicationContext());
            //}

        }
        else if (aState == DetailedState.FAILED) {
        }
    }

    public void checkState(int aInt) {         
        if (aInt == WifiManager.WIFI_STATE_ENABLING) {
            Log.d("WiFiBroadcastReceiver", "WIFI_STATE_ENABLING");
        }
        else if (aInt== WifiManager.WIFI_STATE_ENABLED) {
            Log.d("WiFiBroadcastReceiver", "WIFI_STATE_ENABLED");
        }
        else if (aInt == WifiManager.WIFI_STATE_DISABLING) {
            Log.d("WiFiBroadcastReceiver", "WIFI_STATE_DISABLING");
        }
        else if (aInt == WifiManager.WIFI_STATE_DISABLED) {
            Log.d("WiFiBroadcastReceiver", "WIFI_STATE_DISABLED");
        }
    }
    
    private String parseIpAddress(int ipAddress) {

        // Convert little-endian to big-endianif needed
        if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
            ipAddress = Integer.reverseBytes(ipAddress);
        }

        byte[] ipByteArray = BigInteger.valueOf(ipAddress).toByteArray();

        String ipAddressString;
        try {
            ipAddressString = InetAddress.getByAddress(ipByteArray).getHostAddress();
        } catch (UnknownHostException ex) {
            Log.e("WIFIIP", "Unable to get host address.");
            ipAddressString = null;
        }

        return ipAddressString;
    }

}
