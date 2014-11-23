package com.example.android.wifidirect;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MessageActivity extends Activity {
	public static AllEncompasingP2PClient RECIPIENT = null;
	
	private static TextView messageView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.message);
		
		messageView = (TextView) findViewById(R.id.message_view);
		
		final Button button = (Button) findViewById(R.id.btn_send);
		//final TextView status = (TextView) findViewById(R.id.txt_status);
		final EditText message = (EditText)findViewById(R.id.edit_message);
		//status.setText(RECIPIENT.getMac());
		
		this.setTitle("Group Chat");
		
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	//status.setText(message.getText().toString());
            	addMessage("This phone",message.getText().toString());
            	message.setText("");
            }
        });
	}
	
	public static void addMessage(String from, String text) {
		//messageView.setText(messageView.getText() + from + " says " + text + "\n");
		messageView.append(from + " says " + text + "\n");
		final int scrollAmount = messageView.getLayout().getLineTop(messageView.getLineCount()) - messageView.getHeight();
	    // if there is no need to scroll, scrollAmount will be <=0
	    if (scrollAmount > 0)
	    	messageView.scrollTo(0, scrollAmount);
	    else
	    	messageView.scrollTo(0, 0);
	}
	
}
