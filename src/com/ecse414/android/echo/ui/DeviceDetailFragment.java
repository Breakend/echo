package com.ecse414.android.echo.ui;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ecse414.android.echo.WiFiDirectActivity;
import com.ecse414.android.echo.router.AllEncompasingP2PClient;
import com.ecse414.android.echo.router.MeshNetworkManager;
import com.ecse414.android.echo.router.Packet;
import com.ecse414.android.echo.router.Sender;
import com.ecse414.android.echo.ui.DeviceListFragment.DeviceActionListener;
import com.ecse414.android.echo.wifi.WiFiDirectBroadcastReceiver;
import com.ecse414.android.echo.R;

/**
 * A fragment that manages a particular peer and allows interaction with device
 * i.e. setting up network connection and transferring data.
 * 
 * NOTE: much of this was taken from the Android example on P2P networking
 */
public class DeviceDetailFragment extends Fragment implements ConnectionInfoListener {

	private static View mContentView = null;
	private WifiP2pDevice device;
	ProgressDialog progressDialog = null;

	/**
	 * Update who is in the chat from the routing table
	 */
	public static void updateGroupChatMembersMessage() {
		TextView view = (TextView) mContentView.findViewById(R.id.device_address);
		if (view != null) {
			String s = "Currently in the network chatting: \n";
			for (AllEncompasingP2PClient c : MeshNetworkManager.routingTable.values()) {
				s += c.getMac() + "\n";
			}
			view.setText(s);
		}
	}

	/**
	 * Once the activity is created make sure to call the super constructor
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	/**
	 * Handle the view setup and callbacks
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		mContentView = inflater.inflate(R.layout.device_detail, null);
		mContentView.findViewById(R.id.btn_connect).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				WifiP2pConfig config = new WifiP2pConfig();
				config.deviceAddress = device.deviceAddress;
				config.wps.setup = WpsInfo.PBC;
				if (progressDialog != null && progressDialog.isShowing()) {
					progressDialog.dismiss();
				}
				progressDialog = ProgressDialog.show(getActivity(), "Press back to cancel", "Connecting to :"
						+ device.deviceAddress, true, true);
				((DeviceActionListener) getActivity()).connect(config);

			}
		});

		mContentView.findViewById(R.id.btn_disconnect).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				((DeviceActionListener) getActivity()).disconnect();
			}
		});

		return mContentView;
	}

	/**
	 * This is mostly for debugging
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		// User has picked an image. Transfer it to group owner i.e peer using
		// FileTransferService.
		Uri uri = data.getData();
		TextView statusText = (TextView) mContentView.findViewById(R.id.status_text);
		statusText.setText("Sending: " + uri);
		Log.d(WiFiDirectActivity.TAG, "Intent----------- " + uri);
	}

	/**
	 * If you aren't the group owner and a connection has been established, send a hello packet to set up the connection
	 */
	@Override
	public void onConnectionInfoAvailable(final WifiP2pInfo info) {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
		this.getView().setVisibility(View.VISIBLE);

		if (!info.isGroupOwner) {
			Sender.queuePacket(new Packet(Packet.TYPE.HELLO, new byte[0], null, WiFiDirectBroadcastReceiver.MAC));
		}

		// hide the connect button
		mContentView.findViewById(R.id.btn_connect).setVisibility(View.GONE);
	}

	/**
	 * Updates the UI with device data
	 * 
	 * @param device
	 *            the device to be displayed
	 */
	public void showDetails(WifiP2pDevice device) {
		this.device = device;
		this.getView().setVisibility(View.VISIBLE);
		TextView view = (TextView) mContentView.findViewById(R.id.device_address);
		String s = "Currently in the network chatting: \n";
		for (AllEncompasingP2PClient c : MeshNetworkManager.routingTable.values()) {
			s += c.getMac() + "\n";
		}
		view.setText(s);
	}

	/**
	 * Clears the UI fields after a disconnect or direct mode disable operation.
	 */
	public void resetViews() {
		mContentView.findViewById(R.id.btn_connect).setVisibility(View.VISIBLE);
		TextView view = (TextView) mContentView.findViewById(R.id.device_address);
		view.setText(R.string.empty);
		view = (TextView) mContentView.findViewById(R.id.device_info);
		view.setText(R.string.empty);
		view = (TextView) mContentView.findViewById(R.id.group_owner);
		view.setText(R.string.empty);
		view = (TextView) mContentView.findViewById(R.id.status_text);
		view.setText(R.string.empty);
		this.getView().setVisibility(View.GONE);
	}

}
