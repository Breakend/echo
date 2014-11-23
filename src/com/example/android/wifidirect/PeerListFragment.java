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

package com.example.android.wifidirect;

import java.util.ArrayList;
import java.util.List;

import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pConfig;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * A ListFragment that displays available peers on discovery and requests the
 * parent activity to handle user interaction events
 */
public class PeerListFragment extends ListFragment {

    private List<AllEncompasingP2PClient> peers = new ArrayList<AllEncompasingP2PClient>();
    private ProgressDialog progressDialog = null;
    private View mContentView = null;      

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.setListAdapter(new PeersListAdapter(getActivity(), R.layout.row_peers, 
        		new ArrayList<AllEncompasingP2PClient>(MeshNetworkManager.routingTable.values())));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.peer_list, null);
        return mContentView;
    }


    /**
     * Initiate a connection with the peer.
     */
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
    	AllEncompasingP2PClient peer = (AllEncompasingP2PClient) getListAdapter().getItem(position);
    	MessageActivity.RECIPIENT = peer;
    	Intent intent = new Intent(this.mContentView.getContext(), MessageActivity.class);
    	startActivity(intent);
    }

    /**
     * Array adapter for ListFragment that maintains WifiP2pDevice list.
     */
    private class PeersListAdapter extends ArrayAdapter<AllEncompasingP2PClient> {

        private List<AllEncompasingP2PClient> items;

        /**
         * @param context
         * @param textViewResourceId
         * @param objects
         */
        public PeersListAdapter(Context context, int textViewResourceId,
        		 List<AllEncompasingP2PClient> objects) {
            super(context, textViewResourceId, objects);
            items = objects;

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.row_peers, null);
            }
            AllEncompasingP2PClient peer = items.get(position);
            if (peer != null) {
                TextView top = (TextView) v.findViewById(R.id.peer_name);
                TextView bottom = (TextView) v.findViewById(R.id.peer_MAC);
                if (top != null) {
                    top.setText(peer.getName());
                }
                if (bottom != null) {
                    bottom.setText(peer.getMac());
                }
            }

            return v;

        }
    }
    
    public String[] getMACKeys() {
    	return (String[])MeshNetworkManager.routingTable.keySet().toArray(); 
    }
    
    public void clearPeers() {
        peers.clear();
        ((PeersListAdapter) getListAdapter()).notifyDataSetChanged();
    }
    
    public void updatePeerList(List<AllEncompasingP2PClient> newPeers){
    	 if (progressDialog != null && progressDialog.isShowing()) {
             progressDialog.dismiss();
         }
         peers.clear();
         peers.addAll(newPeers);
         ((PeersListAdapter) getListAdapter()).notifyDataSetChanged();
    }
    /**
     * 
     */
    public void onInitiateDiscovery() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        progressDialog = ProgressDialog.show(getActivity(), "Press back to cancel", "finding peers", true,
            true, new DialogInterface.OnCancelListener() {

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
        void showDetails(AllEncompasingP2PClient peer);
        void cancelDisconnect();
        void connect(WifiP2pConfig config);
        void disconnect();
    }
    
}
