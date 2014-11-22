package com.example.android.wifidirect;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MessageActivity extends Activity {
	public static String RECIPIENT_MAC = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.message);
		
		final Button button = (Button) findViewById(R.id.btn_send);
		final TextView status = (TextView) findViewById(R.id.txt_status);
		final EditText message = (EditText)findViewById(R.id.edit_message);
		
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	status.setText(message.getText().toString());
            }
        });
	}
}
