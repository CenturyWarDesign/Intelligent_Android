package com.centurywar.intelligent;

import org.json.JSONObject;

import HA.Socket.SocketClient;
import HA.Socket.SocketHandleMap;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Window;
import cn.jpush.android.api.JPushInterface;

public abstract class BaseActivity extends Activity {
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
		SocketHandleMap.registerActivity(this);
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

	/**
	 * 发送报文
	 * 
	 * @param jsonobj
	 */
	protected void sendMessage(JSONObject jsonobj) {
		if (socketClient != null) {
			if (!jsonobj.has("control")) {
				System.out.println("jsonobj has not control string!");
			}
			socketClient.sendMessageSocket(jsonobj.toString());
			
		} else {
			System.out.println("Socket is Error!");
		}
	}
	
	/**
	 * 如果需要返回值的话，在这里面进行处理
	 * @param jsonobj
	 * @throws Exception 
	 */
	public abstract void MessageCallBack(JSONObject jsonobj) throws Exception;
	
	
}
