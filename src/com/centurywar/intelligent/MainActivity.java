package com.centurywar.intelligent;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import cn.jpush.android.api.JPushInterface;

public class MainActivity extends Activity {
	protected String temstr;

	private TextView temText;
	private EditText edit_text_out;
	private Button button_send;




	// Layout Views
	private ListView mConversationView;
	private EditText mOutEditText;
	private Button mSendButton;

	// Name of the connected device
	private String mConnectedDeviceName = null;
	// Array adapter for the conversation thread
	private ArrayAdapter<String> mConversationArrayAdapter;
	// String buffer for outgoing messages
	private StringBuffer mOutStringBuffer;

	// private BroadcastReceiver mReceiver;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		temText = (TextView) findViewById(R.id.temText);
		edit_text_out = (EditText) findViewById(R.id.edit_text_out);
		button_send = (Button) findViewById(R.id.button_send);

		button_send.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				initJPUSH();
				// ContentWrite(edit_text_out.getText().toString());
			}
		});
	}

	@Override
	protected void onResume() {
		initJPUSH();
		super.onResume();
	}

	private void initJPUSH() {
		JPushInterface.setDebugMode(true);
		JPushInterface.init(this);
	}



}

