package com.centurywar.intelligent;

import HA.Socket.SocketClient;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Window;
import cn.jpush.android.api.JPushInterface;

public class BaseActivity extends Activity {
	protected SocketClient socketClient=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		if (socketClient == null) {
			socketClient = new SocketClient();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	public void initJPUSH() {
		JPushInterface.setDebugMode(true);
		JPushInterface.init(this);
		JPushInterface.setAlias(this, "caojunling", null);
	}

	protected boolean checkBluetooth() {
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter
				.getDefaultAdapter();
		return mBluetoothAdapter.isEnabled();
	}
}
