package com.example.android.wifidirect;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

@SuppressLint("ValidFragment")
public class PromptPasswordFragment extends DialogFragment {
	
	private WiFiDirectActivity activty;
	private View mContentView;
	private String ssid;
	
	public PromptPasswordFragment() {

	}
	
	public PromptPasswordFragment(WiFiDirectActivity activty, String ssid) {
		this.activty = activty;
		this.ssid = ssid;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    // Get the layout inflater
	    LayoutInflater inflater = getActivity().getLayoutInflater();

	    mContentView = inflater.inflate(R.layout.prompt_password, null);
	    ((TextView)mContentView.findViewById(R.id.ssid)).setText("Enter password for (" + this.ssid + ")");
	    
	    // Inflate and set the layout for the dialog
	    // Pass null as the parent view because its going in the dialog layout
	    builder.setView(mContentView)
	    // Add action buttons
	           .setPositiveButton(R.string.label_connect, new DialogInterface.OnClickListener() {
	        	   
	               @Override
	               public void onClick(DialogInterface dialog, int id) {
	            	   String ssid = PromptPasswordFragment.this.ssid;
	            	   String password = ((TextView)PromptPasswordFragment.this.mContentView.findViewById(R.id.password))
	            			   .getText().toString();

	            	   PromptPasswordFragment.this.activty.connectToAccessPoint(ssid, password);

	               }
	           })
	           .setNegativeButton(R.string.label_cancel, new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int id) {
	            	   PromptPasswordFragment.this.getDialog().cancel();
	               }
	           });      
	    return builder.create();
	}
}
