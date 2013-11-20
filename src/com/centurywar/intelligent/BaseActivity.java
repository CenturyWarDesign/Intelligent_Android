package com.centurywar.intelligent;


import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.json.JSONObject;



import Socket.Bluetooth;
import Socket.SocketClient;
import Socket.SocketHandleMap;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.SharedPreferences;
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
	protected SharedPreferences gameInfo;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		gameInfo = getSharedPreferences("gameInfo", 0);
		if (socketClient == null) {
			socketClient = new SocketClient();
		}
		initBlueTooth();
		//如果是用模拟器，请把这个关闭
		//initJPUSH();
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
		try{
			JPushInterface.setDebugMode(true);
			JPushInterface.init(this);
			JPushInterface.setAlias(this, "caojunling", null);
		}
		catch(Exception e){
			System.out.println(e.toString());
		}
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
		if (!jsonobj.has("control")) {
			System.out.println("jsonobj has not control string!");
			return;
		}
		try {
			boolean threadBlueTooth = false;
			// 这是设置板子状态的代码，优先进行蓝牙传输
			if (jsonobj.getString("control").equals(ConstantControl.SET_STATUS)
					&& blueTooth.getStatus()) {
				String contrl = getSendStringFromJsonObject(jsonobj);
				if (contrl.length() > 0) {
					blueTooth.ContentWrite(contrl);
					threadBlueTooth = true;
				}
			}
			// 如果没有通过蓝牙，那就通过socket传输
			if (socketClient != null && !threadBlueTooth) {
				socketClient.sendMessageSocket(jsonobj.toString());
			}
		} catch (Exception e) {
			System.out.print(e.toString());
		}

	}
	
	/**
	 * 返回JSONObject类型的指令
	 * @param type
	 * @param pik
	 * @param value
	 * @param data
	 * @return
	 */
	protected JSONObject getJsonobject(int type, int pik, int value, int data) {
		JSONObject object = new JSONObject();
		try {
			object.put("control", ConstantControl.CONTROL_DEVICE);
			object.put("type", type);
			object.put("pik", pik);
			object.put("value", value);
			object.put("data", data);
			object.put("username", getGameInfoStr("username"));
			object.put("sec", getGameInfoStr("username"));
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return object;
	}
	
	/**
	 * 把JSONObject 转化为指令
	 * @param obj
	 * @return
	 */
	protected String getSendStringFromJsonObject(JSONObject obj) {
		if (!obj.has("type") || !obj.has("pik") || !obj.has("value")
				|| !obj.has("data")) {
			return "";
		} else {
			try {
				return String.format("%d_%d_%d_%d", obj.getInt("type"),
						obj.getInt("pik"), obj.getInt("value"),
						obj.getInt("data"));
			} catch (Exception e) {
				System.out.print(e.toString());
				return "";
			}
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
	
	/** 设置SharePerference数据，参数String */
	protected void setGameInfoStr(String key,String value) {
		gameInfo.edit().putString(key, value).commit();
	}
	
	/** 获取SharePerference数据，参数为key */
	protected String getGameInfoStr(String key) {
		return gameInfo.getString(key, "");
	}
	
	/** 获取SharePerference数据，参数为sec序列码 */
	protected String getSec() {
		return getGameInfoStr("sec");
	}
	
	/** 获取SharePerference数据，参数为username */
	protected String getUsername() {
		return getGameInfoStr("username");
	}

	public static String MD52(String val) throws NoSuchAlgorithmException {
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		md5.update(val.getBytes());
		byte[] m = md5.digest();// 加密
		return getString(m);
	}

	private static String getString(byte[] b) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			sb.append(b[i]);
		}
		return sb.toString();
	}
	
	
}
