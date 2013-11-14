package com.centurywar.intelligent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import net.sf.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

import HA.Socket.SocketClient;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import at.abraxas.amarino.Amarino;

public class MainActivity extends BaseActivity {
	private Button btnAdd;
	private Button btnClear;
	private Button btnGame1;
	private Button btnSocket;
	private Button btnSocketSend;
	private EditText editName;
	private TextView txtSocket;
	private EditText editPik;
	private TableLayout table;
	private TextView txtError;
	private TextView lightRate;
	private SeekBar lightBar;
	protected SharedPreferences gameInfo;
	private int maxlight = 255, currentlight = 0;
	private String mac = "20:13:09:30:14:48";
	private String mac2 = "20:13:09:30:12:77";
	private BaseControl tembc;
	private boolean isInit=false;
	private ArduinoReceiver arduinoReceiver = new ArduinoReceiver();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		btnAdd = (Button) findViewById(R.id.btnAdd);
		btnGame1 = (Button) findViewById(R.id.btngame1);
		btnClear = (Button) findViewById(R.id.btnClear);
		btnSocket = (Button) findViewById(R.id.btnSocket);
		btnSocketSend = (Button) findViewById(R.id.btnSocketSend);
		editName = (EditText) findViewById(R.id.editName);
		editPik = (EditText) findViewById(R.id.editPik);
		table = (TableLayout) findViewById(R.id.layoutBtn);
		txtError = (TextView) findViewById(R.id.txtError);
		txtSocket = (TextView) findViewById(R.id.txtSocket);
		lightRate = (TextView) findViewById(R.id.lightRate);
		gameInfo = getSharedPreferences("gameInfo", 0);
		lightBar = (SeekBar) findViewById(R.id.lightBar);
		
		
		btnClear.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				gameInfo.edit().putString("user_setting", "").commit();
				updateword();
			}
		});
		btnGame1.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				System.out.println("ad");
				Amarino.sendDataToArduino(getApplicationContext(),mac,'A', "ad");
				//gameInfo.edit().putString("user_setting", "").commit();
//				bl.setMac(mac);
//				bl.StartLem1();
			}
		});
		
		btnSocket.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
//				tembc = new BaseControl(mac);
//				isInit=true;
				socketClient.sendMessageSocket("send to server");
				JSONObject jsob = new JSONObject();
				jsob.put("control", "cpd");
				jsob.put("username", "wanbin");
				jsob.put("password", "wanbin");
				sendMessage(jsob);
//				Amarino.connect(getApplicationContext(), mac);
			}
		});
		
		btnSocketSend.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				//tembc.release();
				Amarino.disconnect(getApplicationContext(), mac);
				System.out.println("disconnection");
//				tembc.sendToDevice("10_1_1_0");
			}
		});

		btnAdd.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				String strtem = gameInfo.getString("user_setting", "");
				if (strtem.length() > 0) {
					strtem = strtem + "," + editName.getText().toString() + "_"
							+ editPik.getText().toString();
				} else {
					strtem = editName.getText().toString() + "_"
							+ editPik.getText().toString();
				}
				strtem = strtem.trim();
				gameInfo.edit().putString("user_setting", strtem).commit();
				editName.setText("");
				editPik.setText("");
				updateword();
				
			}
		});

		updateword();
		lightBar.setMax(60);
		lightBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			public void onProgressChanged(SeekBar arg0, int progress,
					boolean fromUser) {
				currentlight = progress;
				lightBar.setProgress(currentlight);
				lightRate.setText(currentlight+ "");
				
//				bl.setPikType(mac, 3, 20);
//				bl.setValue(currentlight);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}
		});
		initJPUSH();
	}

	private void updateword() {
		String[] userSetting = gameInfo.getString("user_setting", "")
				.split(",");
		table.removeAllViews();
		for (int n = 0; n < userSetting.length; n++) {
			String[] rowtext = userSetting[n].split("_");
			if (rowtext.length != 2) {
				return;
			}
			TableRow tr = new TableRow(this);
			final int pik = Integer.parseInt(rowtext[1]);
			Button open = new Button(this);
			open.setText("open");
			open.setTag(pik);
			open.setOnClickListener(new Button.OnClickListener() {
				@Override
				public void onClick(View v) {
					setControl(1 + pik * 10);
				}
			});
			Button close = new Button(this);
			close.setText("close");
			open.setTag(pik);
			close.setOnClickListener(new Button.OnClickListener() {
				@Override
				public void onClick(View v) {
					setControl(0 + pik * 10);
				}
			});
			final TextView temtext = new TextView(this);
			temtext.setText(rowtext[0] + "" + rowtext[1] + "");
			tr.addView(temtext);
			tr.addView(open);
			tr.addView(close);
			table.addView(tr);
		}
	}



	private void setControl(int getstatus) {
		if (!isInit) {
			Toast.makeText(MainActivity.this, "未打开蓝牙", Toast.LENGTH_SHORT)
					.show();
			return;
		}
		int pik = getstatus / 10;
		int type = 10;
		// Random rd = new Random();
		// int i = rd.nextInt() % 2;

		// if (i == 1) {
		// }
		boolean status = false;
		if (getstatus % 10 == 1) {
			status = true;
		}

		int delay=currentlight;
		if (status) {
			tembc.sendToDevice("10_" + pik + "_1_"+delay);
		} else {
			tembc.sendToDevice("10_" + pik + "_0_"+delay);
		}
	}

	public void onResume() {
		super.onResume();
		if (checkBluetooth()) {
			txtError.setVisibility(View.GONE);
			// btnBlue.setVisibility(View.GONE);
		} else {
			txtError.setVisibility(View.VISIBLE);
			// btnBlue.setVisibility(View.GONE);
			txtError.setText("未打开蓝牙");
		}

//		tembc.initSocket();
		//initBt();
//		tembc.initSocket();
	}

	public void onPause() {
//		tembc.release();
//		tembc=null;
		super.onPause();
		finish();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		// in order to receive broadcasted intents we need to register our receiver
		registerReceiver(arduinoReceiver, new IntentFilter("amarino.RESPONSE"));
		
//		Intent setCollection = new Intent("amarino.SET_COLLECTION");
//		setCollection.putExtra("COLLECTION_NAME", "SensorGraph");
//		sendBroadcast(setCollection);
		// tell Amarino to connect
//		sendBroadcast(new Intent("amarino.CONNECT"));
		
	}


	@Override
	protected void onStop() {
		super.onStop();
		// tell Amarino to disconnect
//		sendBroadcast(new Intent("amarino.DISCONNECT"));
		// do never forget to unregister a registered receiver
		unregisterReceiver(arduinoReceiver);
	}
	

	

}


//	