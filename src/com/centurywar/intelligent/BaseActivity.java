package com.centurywar.intelligent;


import org.json.JSONObject;


import Socket.Bluetooth;
import Socket.SocketClient;
import Socket.SocketHandleMap;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;

public abstract class BaseActivity extends Activity {
	protected SocketClient socketClient = null;
	protected static Bluetooth blueTooth = null;
	protected static String mac = "20:13:09:30:14:48";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		if (socketClient == null) {
			socketClient = new SocketClient();
		}
		initBlueTooth();
		initJPUSH();
	}

	@Override
	protected void onResume() {
		super.onResume();
		SocketHandleMap.registerActivity(this);
	}

	public void initBlueTooth() {
		if (blueTooth == null) {
			blueTooth = new Bluetooth(mac);
		}
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

	
	/**
	 * 发送Toast提示
	 * @param message
	 */
	protected void ToastMessage(String message) {
		Toast.makeText(BaseActivity.this, message, Toast.LENGTH_SHORT).show();
	}
	
	// 接受时间
	public Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			try {
				Bundle bundle = msg.getData();
				MessageCallBack( new JSONObject(bundle.getString("jsonobj")));
			} catch (Exception e) {
				System.out.println(e.toString());
			}
			super.handleMessage(msg);
		}
	};
	
}
