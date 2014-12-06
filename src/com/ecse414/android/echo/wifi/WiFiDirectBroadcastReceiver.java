/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ecse414.android.echo.wifi;

import com.ecse414.android.echo.WiFiDirectActivity;
import com.ecse414.android.echo.config.Configuration;
import com.ecse414.android.echo.router.AllEncompasingP2PClient;
import com.ecse414.android.echo.router.MeshNetworkManager;
import com.ecse414.android.echo.router.Receiver;
import com.ecse414.android.echo.router.Sender;
import com.ecse414.android.echo.ui.DeviceDetailFragment;
import com.ecse414.android.echo.ui.DeviceListFragment;
import com.ecse414.android.echo.R;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.util.Log;

/**
 * A BroadcastReceiver that notifies of important wifi p2p events.
 */
public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {

	private WifiP2pManager manager;
	private Channel channel;
	private WiFiDirectActivity activity;

	public static String MAC;

	/**
	 * @param manager
	 *            WifiP2pManager system service
	 * @param channel
	 *            Wifi p2p channel
	 * @param activity
	 *            activity associated with the receiver
	 */
	public WiFiDirectBroadcastReceiver(WifiP2pManager manager, Channel channel, WiFiDirectActivity activity) {
		super();
		this.manager = manager;
		this.channel = channel;
		this.activity = activity;
	}

	/**
	 * State transitions based on connection and state information, callback based on P2P library
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();

		if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {

			// UI update to indicate wifi p2p status.
			int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);

			if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
				// Wifi Direct mode is enabled
				activity.setIsWifiP2pEnabled(true);

				manager.createGroup(channel, new ActionListener() {

					@Override
					public void onSuccess() {
						Log.d(WiFiDirectActivity.TAG, "P2P Group created");
					}

					@Override
					public void onFailure(int reason) {
						Log.d(WiFiDirectActivity.TAG, "P2P Group failed");
					}
				});
			} else {
				activity.setIsWifiP2pEnabled(false);
				activity.resetData();

			}

			Log.d(WiFiDirectActivity.TAG, "P2PACTION : WIFI_P2P_STATE_CHANGED_ACTION state = " + state);
		} else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {

			// request available peers from the wifi p2p manager. This is an
			// asynchronous call and the calling activity is notified with a
			// callback on PeerListListener.onPeersAvailable()
			if (manager != null) {
				manager.requestPeers(channel,
						(PeerListListener) activity.getFragmentManager().findFragmentById(R.id.frag_list));
			}
			Log.d(WiFiDirectActivity.TAG, "P2PACTION : WIFI_P2P_PEERS_CHANGED_ACTION");
		} else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {

			if (manager == null) {
				return;
			}

			NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

			if (networkInfo.isConnected()) {
				// we are connected with the other device, request connection
				// info to find group owner IP
				DeviceDetailFragment fragment = (DeviceDetailFragment) activity.getFragmentManager().findFragmentById(
						R.id.frag_detail);
				manager.requestConnectionInfo(channel, fragment);
			} else {
				// It's a disconnect
				Log.d(WiFiDirectActivity.TAG, "P2PACTION : WIFI_P2P_CONNECTION_CHANGED_ACTION -- DISCONNECT");
				activity.resetData();
			}
		} else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
			DeviceListFragment fragment = (DeviceListFragment) activity.getFragmentManager().findFragmentById(
					R.id.frag_list);
			fragment.updateThisDevice((WifiP2pDevice) intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE));

			MAC = ((WifiP2pDevice) intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE)).deviceAddress;

			//Set yourself on connection
			MeshNetworkManager.setSelf(new AllEncompasingP2PClient(((WifiP2pDevice) intent
					.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE)).deviceAddress, Configuration.GO_IP,
					((WifiP2pDevice) intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE)).deviceName,
					((WifiP2pDevice) intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE)).deviceAddress));

			//Launch receiver and sender once connected to someone
			if (!Receiver.running) {
				Receiver r = new Receiver(this.activity);
				new Thread(r).start();
				Sender s = new Sender();
				new Thread(s).start();
			}

			manager.requestGroupInfo(channel, new WifiP2pManager.GroupInfoListener() {
				@Override
				public void onGroupInfoAvailable(WifiP2pGroup group) {
					if (group != null) {
						// clients require these
						String ssid = group.getNetworkName();
						String passphrase = group.getPassphrase();

						Log.d(WiFiDirectActivity.TAG, "GROUP INFO AVALABLE");
						Log.d(WiFiDirectActivity.TAG, " SSID : " + ssid + "\n Passphrase : " + passphrase);

					}
				}
			});
		}
	}
}
