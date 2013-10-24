package com.centurywar.intelligent;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class MainActivity extends BaseActivity {
	private Button btnAdd;
	private Button btnClear;
	private Button btnGame1;
	private EditText editName;
	private EditText editPik;
	private TableLayout table;
	private BaseControl bc;
	private LightControl bl;
	private TextView txtError;
	private TextView lightRate;
	private SeekBar lightBar;
	protected SharedPreferences gameInfo;
	private int maxlight = 255, currentlight = 0;
	private String mac = "20:13:09:30:14:48";
	private String mac2 = "20:13:09:30:12:77";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		btnAdd = (Button) findViewById(R.id.btnAdd);
		btnGame1 = (Button) findViewById(R.id.btngame1);
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
			}
		});
		btnGame1.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				gameInfo.edit().putString("user_setting", "").commit();
				bl.setPikType(mac, 3, 20);
				bl.StartLem1();
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
		bc = new BaseControl();
		bl = new LightControl();
		updateword();



		lightBar.setMax(maxlight);
		lightBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			public void onProgressChanged(SeekBar arg0, int progress,
					boolean fromUser) {
				currentlight = progress;
				lightBar.setProgress(currentlight);
				lightRate.setText(currentlight * 100 / maxlight + " %");
				bl.setPikType(mac, 3, 20);
				// bl.StartLem1();
				bl.setValue(currentlight);
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

	private void openBluetooth(){
		Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		startActivity(intent);
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
					Message message = new Message();
					message.what = 1 + pik * 10;
					handler.sendMessage(message);
				}
			});
			Button close = new Button(this);
			close.setText("close");
			open.setTag(pik);
			close.setOnClickListener(new Button.OnClickListener() {
				@Override
				public void onClick(View v) {
					Message message = new Message();
					message.what = 0 + pik * 10;
					handler.sendMessage(message);
				}
			});
			final TextView temtext = new TextView(this);
			temtext.setText(rowtext[0] + "在第" + rowtext[1] + "号");
			tr.addView(temtext);
			tr.addView(open);
			tr.addView(close);
			table.addView(tr);
		}
	}


	// 接受时间
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			setControl(msg.what);
			super.handleMessage(msg);
		}
	};

	private void setControl(int getstatus) {
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

		bc.setPikType(mac, pik, type);
		if (status) {
			bc.open();
		} else {
			bc.close();
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
			txtError.setText("未开启蓝牙");
		}
	}


	public void onPause() {
		super.onPause();
		bc.release();
	}

}

