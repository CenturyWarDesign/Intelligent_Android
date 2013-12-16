package com.centurywar.intelligent;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;

import com.centurywar.intelligent.control.BaseControl;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;

import Socket.Bluetooth;
import Socket.SocketClient;
import Socket.SocketHandleMap;
import Socket.SocketHeart;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;

public abstract class BaseActivity extends Activity {
	public static SocketClient socketClient = null;
	protected static Bluetooth blueTooth = null;
	// protected static String mac = "20:13:09:30:14:48";
	protected SharedPreferences gameInfo;
	private  Timer timer = null;
	//发送心跳包的间隔 20秒
	public int heartSec=2000;
//	private boolean useJpush=true;
	private boolean useJpush=false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		gameInfo = getSharedPreferences("gameInfo", 0);
		if (socketClient == null) {
			socketClient = new SocketClient();
		}
		// initBlueTooth();
		// 如果是用模拟器，请把这个关闭
		MobclickAgent.setDebugMode(true);
		initJPUSH();
		// 保持屏幕常亮，仅此一句
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		
		if (timer == null) {
			timer = new Timer();
			timer.schedule(timetask, 0, 1000);
		}

	}

	protected void setUMENGUpdate() {
		UmengUpdateAgent.update(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		SocketHandleMap.registerActivity(this);
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
		
	}

	public void initBlueTooth() {
		if (blueTooth == null && BaseControl.bluetoothMac.length() > 0) {
			System.out.print("BlueToothMac:" + BaseControl.bluetoothMac);
			blueTooth = new Bluetooth(BaseControl.bluetoothMac);
		}
	}

	public void initJPUSHAlias(String username) {
		if (useJpush) {
			JPushInterface.setAlias(this, username, null);
		}
	}

	public void initJPUSH() {
		if (useJpush) {
			JPushInterface.setDebugMode(true);
			JPushInterface.init(this);
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
			if (!jsonobj.has("username")) {
				jsonobj.put("username", getGameInfoStr("username"));
			}
			if (!jsonobj.has("sec")) {
				jsonobj.put("sec", getGameInfoStr("sec"));
			}
			boolean threadBlueTooth = false;
			// 这是设置板子状态的代码，优先进行蓝牙传输
			if (jsonobj.getString("control").equals(ConstantControl.SET_STATUS)
					&&blueTooth!=null&& blueTooth.getStatus()) {
				String contrl = getSendStringFromJsonObject(jsonobj);
				if (contrl.length() > 0) {
					blueTooth.ContentWrite(contrl);
					threadBlueTooth = true;
				}
			}
			// 如果没有通过蓝牙，那就通过socket传输
			if (socketClient != null && !threadBlueTooth) {
				jsonobj.put("sec", getSec());
				socketClient.sendMessageSocket(jsonobj.toString());
			}
		} catch (Exception e) {
			System.out.print(e.toString());
		}

	}

	/**
	 * 返回JSONObject类型的指令
	 * 
	 * @param type
	 * @param pik
	 * @param value
	 * @param data
	 * @return
	 */
	protected JSONObject getJsonobject(int type, int pik, int value, int data) {
		JSONObject object = new JSONObject();
		try {
			object.put("control", ConstantControl.SET_STATUS);
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
	 * 
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
	 * 
	 * @param jsonobj
	 * @throws Exception
	 */
	public abstract void MessageCallBack(JSONObject jsonobj) throws Exception;
	
	/**
	 * 如果需要返回值的话，在这里面进行处理
	 * 
	 * @param jsonobj
	 * @throws Exception
	 */
	public abstract void StatusCallBack(JSONObject jsonobj) throws Exception;

	/**
	 * 发送Toast提示
	 * 
	 * @param message
	 */
	protected void ToastMessage(String message) {
		Toast.makeText(BaseActivity.this, message, Toast.LENGTH_SHORT).show();
	}

	// 接受事件
	public Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			try {
				Bundle bundle = msg.getData();
				JSONObject obj = new JSONObject(bundle.getString("jsonobj"));
				// 如果是服务器返回的错误信息，统一在这里进行处理一下
				if (obj.getString("control").equals(
						ConstantControl.ECHO_SERVER_MESSAGE)) {
					echoServerStatus(obj.getInt("code"));
					StatusCallBack(obj);
				} else if (obj.getString("control").equals(
						ConstantControl.ECHO_CHECK_USERNAME_PASSWORD)
						|| obj.getString("control").equals(
								ConstantControl.GET_USER_INFO)) {
					//这里初始化用户信息
					initUserInfoFromServer(obj);
				}

				MessageCallBack(obj);
			} catch (Exception e) {
				System.out.println(e.toString());
			}
			super.handleMessage(msg);
		}
	};
	
	private void echoServerStatus(int code) {
		if (code == ConstantCode.USER_MORE_THAN_ONE_ERROR) {
			ToastMessage("用户在另一个地方登录，您已经被迫下线");
			Intent intent = new Intent();
			intent.setClass(getApplicationContext(), LoginActivity.class);
			startActivity(intent);
			finish();
		} else if (code == ConstantCode.USER_ARDUINO_LOGIN) {
			System.out.println("板子登录");
			setGameInfoInt("last_arduino_login", 0);
		} else if (code == ConstantCode.USER_Mode_UPDATE_OK) {
			ToastMessage("更新模式成功");
		} else if (code == ConstantCode.USER_OR_PASSWORD_ERROR) {
			ToastMessage("用户名或密码错误");
		} else if (code == ConstantCode.USER_OR_PASSWORD_CANT_USE) {
			ToastMessage("用户名不可用");
		} else if (code == ConstantCode.USER_REG_SUCCESS) {
			ToastMessage("注册成功");
		} else if (code == ConstantCode.AUTO_GET_ARDUINO_ID_SUCCESS) {
			ToastMessage("自动获取ARDUINOID成功");
		}
	}

	/** 设置SharePerference数据，参数String */
	protected void setGameInfoStr(String key, String value) {
		gameInfo.edit().putString(key, value).commit();
	}
	
	/** 设置SharePerference数据，参数String */
	protected void setGameInfoInt(String key, int value) {
		gameInfo.edit().putInt(key, value).commit();
	}

	/** 获取SharePerference数据，参数为key */
	protected String getGameInfoStr(String key) {
		return gameInfo.getString(key, "");
	}

	/** 获取SharePerference数据，参数为key */
	protected int getGameInfoInt(String key) {
		return gameInfo.getInt(key, 0);
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
	
	// 接受时间
	Handler handlerTimer = new Handler() {
		public void handleMessage(Message msg) {
			addOneSec();
			super.handleMessage(msg);
		}
	};

	// 传递时间
	private TimerTask timetask = new TimerTask() {
		public void run() {
			Message message = new Message();
			message.what = 1;
			handlerTimer.sendMessage(message);
		}
	};



	/**
	 * 在主函数哦中的回调函数，每秒调用一次
	 */
	protected abstract void addOneSec();
	
	/**
	 * 直接发送服务器命令
	 * @param control
	 * @param obj 可以填null
	 */
	protected void sendControl(String control, JSONObject obj) {
		try {
			if (obj == null) {
				obj = new JSONObject();
			}
			obj.put("control", control);
		} catch (Exception e) {
		}
		sendMessage(obj);
	}
	
	protected void initUserInfoFromServer(JSONObject jsonobj) {
		try {
			setGameInfoInt("mode", jsonobj.getJSONObject("info").getInt("mode"));
			setGameInfoStr("username", jsonobj.getString("username"));
			if (jsonobj.getJSONObject("info").has("bluetoothmac")) {
				BaseControl.bluetoothMac = jsonobj.getJSONObject("info")
						.getString("bluetoothmac");
			}
			if (jsonobj.has("last_arduino_login")) {
				int sec = jsonobj.getInt("last_arduino_login");
				setGameInfoInt("last_arduino_login", sec);
			}
			// 登录成功初始化蓝牙
			if (BaseControl.bluetoothMac.length() > 0) {
				initBlueTooth();
			}
			gameInfo.edit()
					.putString("user_setting", jsonobj.getString("device"))
					.commit();
			initJPUSHAlias(jsonobj.getString("username"));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
