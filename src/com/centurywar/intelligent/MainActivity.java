package com.centurywar.intelligent;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class MainActivity extends BaseActivity {
	private Button btnAdd;
	private Button btnClear;
	private EditText editName;
	private EditText editPik;
	private TableLayout table;
	protected SharedPreferences gameInfo;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		btnAdd = (Button) findViewById(R.id.btnAdd);
		btnClear = (Button) findViewById(R.id.btnClear);
		editName = (EditText) findViewById(R.id.editName);
		editPik = (EditText) findViewById(R.id.editPik);
		table = (TableLayout) findViewById(R.id.layoutBtn);

		gameInfo = getSharedPreferences("gameInfo", 0);
		btnClear.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				gameInfo.edit().putString("user_setting", "").commit();
				updateword();
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
		boolean status = false;
		if (getstatus % 10 == 1) {
			status = true;
		}
		BaseControl bc = new BaseControl(pik, type);
		if (status) {
			bc.open();
		} else {
			bc.close();
		}
	}
}

