package com.example.android.wifidirect;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MessageListFragment extends Fragment {
	ProgressDialog progressDialog = null;
    View mContentView = null;
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	mContentView = inflater.inflate(R.layout.message_view, null);
    	
    	mContentView.findViewById(R.id.btn_send_message).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						TextView status = (TextView) mContentView.findViewById(R.id.txt_status);
						status.setText(status.getText() + "Sendng \n");
					}
				});
    	
        return mContentView;
    }
}
