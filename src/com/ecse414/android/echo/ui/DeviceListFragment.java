package com.ecse414.android.echo.ui;

import java.util.ArrayList;
import java.util.List;

import com.ecse414.android.echo.WiFiDirectActivity;
import com.ecse414.android.echo.R;

import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * A ListFragment that displays available peers on discovery and requests the
 * parent activity to handle user interaction events
 * 
 * NOTE: much of this was taken from the example in the Android P2P networking library
 */
public class DeviceListFragment extends ListFragment implements PeerListListener {

	/**
	 * A list of Wi-Fi Direct enabled ppers
	 */
	private List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
	ProgressDialog progressDialog = null;
	View mContentView = null;
	private WifiP2pDevice device;

	/**
	 * Once the activity is created make sure that an adapter is fit to the fragment to update on finding new peers
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		this.setListAdapter(new WiFiPeerListAdapter(getActivity(), R.layout.row_devices, peers));
	}

	/**
	 * Inflate the devices list view 
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mContentView = inflater.inflate(R.layout.device_list, null);
		return mContentView;
	}

	/**
	 * @return this device
	 */
	public WifiP2pDevice getDevice() {
		return device;
	}

	/**
	 * For a given device see if it's connected to a group, pending a connection, etc.
	 * @param deviceStatus
	 * @return
	 */
	private static String getDeviceStatus(int deviceStatus) {
		Log.d(WiFiDirectActivity.TAG, "Peer status :" + deviceStatus);
		switch (deviceStatus) {
		case WifiP2pDevice.AVAILABLE:
			return "Available";
		case WifiP2pDevice.INVITED:
			return "Invited";
		case WifiP2pDevice.CONNECTED:
			return "Connected";
		case WifiP2pDevice.FAILED:
			return "Failed";
		case WifiP2pDevice.UNAVAILABLE:
			return "Unavailable";
		default:
			return "Unknown";

		}
	}

	/**
	 * Initiate a connection with the peer.
	 */
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		WifiP2pDevice device = (WifiP2pDevice) getListAdapter().getItem(position);
		((DeviceActionListener) getActivity()).showDetails(device);
	}

	/**
	 * Array adapter for ListFragment that maintains WifiP2pDevice list.
	 */
	private class WiFiPeerListAdapter extends ArrayAdapter<WifiP2pDevice> {

		private List<WifiP2pDevice> items;

		/**
		 * @param context
		 * @param textViewResourceId
		 * @param objects
		 */
		public WiFiPeerListAdapter(Context context, int textViewResourceId, List<WifiP2pDevice> objects) {
			super(context, textViewResourceId, objects);
			items = objects;

		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.row_devices, null);
			}
			WifiP2pDevice device = items.get(position);
			if (device != null) {
				TextView top = (TextView) v.findViewById(R.id.device_name);
				TextView bottom = (TextView) v.findViewById(R.id.device_details);
				if (top != null) {
					top.setText(device.deviceName);
				}
				if (bottom != null) {
					bottom.setText(getDeviceStatus(device.status));
				}
			}

			return v;

		}
	}

	/**
	 * Update UI for this device.
	 * 
	 * @param device
	 *            WifiP2pDevice object
	 */
	public void updateThisDevice(WifiP2pDevice device) {
		this.device = device;
		TextView view = (TextView) mContentView.findViewById(R.id.my_name);
		view.setText(device.deviceName);
		view = (TextView) mContentView.findViewById(R.id.my_status);
		view.setText(getDeviceStatus(device.status));
	}

	/**
	 * Callback for async peer searching
	 */
	@Override
	public void onPeersAvailable(WifiP2pDeviceList peerList) {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
		peers.clear();
		peers.addAll(peerList.getDeviceList());
		((WiFiPeerListAdapter) getListAdapter()).notifyDataSetChanged();
		if (peers.size() == 0) {
			Log.d(WiFiDirectActivity.TAG, "No devices found");
			return;
		}

	}

	/**
	 * Remove the peers
	 */
	public void clearPeers() {
		peers.clear();
		((WiFiPeerListAdapter) getListAdapter()).notifyDataSetChanged();
	}

	/**
	 * Callback to bring up searching modal
	 */
	public void onInitiateDiscovery() {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
		progressDialog = ProgressDialog.show(getActivity(), "Press back to cancel", "finding peers", true, true,
				new DialogInterface.OnCancelListener() {

					@Override
					public void onCancel(DialogInterface dialog) {

					}
				});
	}

	/**
	 * An interface-callback for the activity to listen to fragment interaction
	 * events.
	 */
	public interface DeviceActionListener {

		void showDetails(WifiP2pDevice device);
		void cancelDisconnect();
		void connect(WifiP2pConfig config);
		void disconnect();
	}

}
