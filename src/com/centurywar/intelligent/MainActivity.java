package com.centurywar.intelligent;


import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.centurywar.intelligent.control.BaseControl;



import android.content.SharedPreferences;
import android.os.Bundle;
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
	private Button btnGetTem;
	private EditText editName;
	private TextView txtSocket;
	private EditText editPik;
	private TableLayout table;
	private TextView txtError;
	private TextView lightRate;
	private SeekBar lightBar;
	protected SharedPreferences gameInfo;
	private int maxlight = 255, currentlight = 0;
	private BaseControl tembc;
	private List<Integer> statusChange=new ArrayList<Integer>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		btnAdd = (Button) findViewById(R.id.btnAdd);
		btnClear = (Button) findViewById(R.id.btnClear);
		editName = (EditText) findViewById(R.id.editName);
		editPik = (EditText) findViewById(R.id.editPik);
		table = (TableLayout) findViewById(R.id.layoutBtn);
		txtError = (TextView) findViewById(R.id.txtError);
		lightRate = (TextView) findViewById(R.id.lightRate);
		gameInfo = getSharedPreferences("gameInfo", 0);
		lightBar = (SeekBar) findViewById(R.id.lightBar);
		
		
		btnClear.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				gameInfo.edit().putString("user_setting", "").commit();
				updateword();
				updateDeviceToServer();
			}
		});
		
		

		btnAdd.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				//
				String strtem = gameInfo.getString("user_setting", "");
				JSONArray jsa;
				try {
					if (strtem.length() > 0) {
						jsa = new JSONArray(strtem);
					} else {
						jsa = new JSONArray();
					}
					JSONObject obj = new JSONObject();
					obj.put("name", editName.getText().toString());
					obj.put("pik", editPik.getText().toString());
					obj.put("value", 0);
					jsa.put(obj);
					System.out.println(jsa.toString());
					gameInfo.edit().putString("user_setting", jsa.toString())
							.commit();
				} catch (Exception e) {

				}

				editName.setText("");
				editPik.setText("");
				try {
					updateword();
					updateDeviceToServer();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});

		try {
			updateword();
		} catch (Exception e) {
			e.printStackTrace();
		}
		lightBar.setMax(60);
		lightBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			public void onProgressChanged(SeekBar arg0, int progress,
					boolean fromUser) {
				currentlight = progress;
				lightBar.setProgress(currentlight);
				lightRate.setText(currentlight + "");

				// bl.setPikType(mac, 3, 20);
				// bl.setValue(currentlight);
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
	}

	
	public void MessageCallBack(JSONObject jsonobj) throws Exception {
		String command = jsonobj.getString("control");
		Log.i("liuchunlong", "MainActivity收到的返回报文为：" + jsonobj.toString());
		if (command.equals(ConstantControl.ECHO_CHECK_USERNAME_PASSWORD)) {
			// 验证用户名密码
		} else if (command.equals(ConstantControl.ECHO_GET_USER_TEMPERATURE)) {
			// 取得温度
			JSONObject tem = (JSONObject) jsonobj.getJSONArray("data").get(0);
			btnGetTem.setText("当前温度：" + tem.get("values").toString());
		} else if (command.equals(ConstantControl.ECHO_SET_STATUS)) {
			String str = jsonobj.getString("command");
			String[] temcommand = str.split("_");
			// r_10_3_1_0
			int pik = Integer.parseInt(temcommand[2]);
			if (statusChange.contains(pik)) {
				statusChange.remove(statusChange.indexOf(pik));
			}
			int val = Integer.parseInt(temcommand[3]);
			String strtem = gameInfo.getString("user_setting", "");
			try {
				JSONArray jsa = new JSONArray(strtem);
				for (int i = 0; i < jsa.length(); i++) {
					JSONObject obj = jsa.getJSONObject(i);
					if (obj.getInt("pik") == pik) {
						obj.put("value", val);
					}
				}
				gameInfo.edit().putString("user_setting", jsa.toString())
						.commit();
			} catch (Exception e) {
				e.printStackTrace();
			}
			updateword();

		}

	}
	
	/**
	 * 把本地配置同步到网上
	 */
	private void updateDeviceToServer() {
		JSONObject jsob = new JSONObject();
		try {
			jsob.put("control", ConstantControl.UPDAT_DEVICE_TO_SERVER);
			JSONArray jsa = new JSONArray(
					gameInfo.getString("user_setting", ""));
			jsob.put("device", jsa);
		} catch (Exception e) {
		}
		sendMessage(jsob);
	}

	private void updateword()  {
		try{
		JSONArray  jsa = new JSONArray( gameInfo.getString("user_setting", ""));
		table.removeAllViews();
		for (int n = 0; n < jsa.length(); n++) {
			JSONObject obj = jsa.getJSONObject(n);
			TableRow tr = new TableRow(this);
			final int pik = obj.getInt("pik");
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

				String closeOrOpen = obj.getInt("value") == 0 ? "关闭" : "打开";
				
				if (statusChange.contains(obj.getInt("pik"))) {
					closeOrOpen = "loading...";
					open.setEnabled(false);
					close.setEnabled(false);
				}

				temtext.setText(String.format("%s [%d] %s",
						obj.getString("name"), obj.getInt("pik"), closeOrOpen));
				tr.addView(temtext);
				tr.addView(open);
				tr.addView(close);
				table.addView(tr);
			}
		}
		catch(Exception e){
			System.out.println(e.toString());
		}
	}



	private void setControl(int getstatus) {
		int pik = getstatus / 10;
		int type = 10;
		boolean status = false;
		if (getstatus % 10 == 1) {
			status = true;
		}

		int delay=currentlight;
		if (status) {
			sendMessage(getJsonobject(10, pik, 1, 0));
		} else {
			sendMessage(getJsonobject(10, pik, 0, 0));
		}
		setButtonLoading(pik);
	}

	/**
	 * 把按键状态设置为loading
	 * @param pik
	 */
	public void setButtonLoading(int pik){
		statusChange.add(pik);
		updateword();
	}
	
	public void onResume() {
		super.onResume();
		if (checkBluetooth()) {
			txtError.setVisibility(View.GONE);
		} else {
			txtError.setVisibility(View.VISIBLE);
			txtError.setText("未打开蓝牙");
		}
	}

	public void onPause() {
		super.onPause();
		finish();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
	}


	@Override
	protected void onStop() {
		super.onStop();
		// do never forget to unregister a registered receiver
	}
	
	

}


//	