package com.centurywar.intelligent;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ArrayAdapter;
import cn.jpush.android.api.JPushInterface;

/**
 * 自定义接收器
 * 
 * 如果不定义这个 Receiver，则： 1) 默认用户会打开主界面 2) 接收不到自定义消息
 */
public class MyReceiver extends BroadcastReceiver {
	private static final String TAG = "MyReceiver";
	private ArrayAdapter<String> mNewDevicesArrayAdapter;
	private Set<BluetoothDevice> pairedDevices;

	// Intent request codes
	private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
	private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
	private static final int REQUEST_ENABLE_BT = 3;

	// Message types sent from the BluetoothChatService Handler
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;
	private Handler mHandler;

	// Key names received from the BluetoothChatService Handler
	public static final String DEVICE_NAME = "device_name";
	public static final String TOAST = "toast";
	// Local Bluetooth adapter
	private static BluetoothAdapter mBluetoothAdapter = null;
	// Member object for the chat services
	private ConnectThread mChatService = null;

	private ConnectedThread mConnectedThread;
	private BluetoothSocket mmSocket;

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		Log.d(TAG, "onReceive - " + intent.getAction() + ", extras: "
				+ printBundle(bundle));

		if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
			String regId = bundle
					.getString(JPushInterface.EXTRA_REGISTRATION_ID);
			Log.d(TAG, "接收Registration Id : " + regId);
			// send the Registration Id to your server...
		} else if (JPushInterface.ACTION_UNREGISTER.equals(intent.getAction())) {
			String regId = bundle
					.getString(JPushInterface.EXTRA_REGISTRATION_ID);
			Log.d(TAG, "接收UnRegistration Id : " + regId);
			// send the UnRegistration Id to your server...
		} else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent
				.getAction())) {
			Log.d(TAG,
					"接收到推送下来的自定义消息: "
							+ bundle.getString(JPushInterface.EXTRA_MESSAGE));
			// processCustomMessage(context, bundle);
			String tem = "接收到推送下来的自定义消息: "
					+ bundle.getString(JPushInterface.EXTRA_MESSAGE);
			if (mConnectedThread == null) {
				initBluetooth();
			}

			ContentWrite(tem);
			try {
				mmSocket.close();
			} catch (IOException closeException) {
				closeException.printStackTrace();
			}

			mChatService = null;
			
		} else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent
				.getAction())) {
			Log.d(TAG, "接收到推送下来的通知");
			int notifactionId = bundle
					.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
			Log.d(TAG, "接收到推送下来的通知的ID: " + notifactionId);

		} else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent
				.getAction())) {
			Log.d(TAG, "用户点击打开了通知");

			// 打开自定义的Activity
			// // Intent i = new Intent(context, TestActivity.class);
			// i.putExtras(bundle);
			// i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			// context.startActivity(i);

		} else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent
				.getAction())) {
			Log.d(TAG,
					"用户收到到RICH PUSH CALLBACK: "
							+ bundle.getString(JPushInterface.EXTRA_EXTRA));
			// 在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity，
			// 打开一个网页等..

		} else {
			Log.d(TAG, "Unhandled intent - " + intent.getAction());
		}
	}

	private void initBluetooth() {
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
			// 表明此手机不支持蓝牙
			return;
		}
		// if (!mBluetoothAdapter.isEnabled()) { // 蓝牙未开启，则开启蓝牙
		// Intent enableIntent = new Intent(
		// BluetoothAdapter.ACTION_REQUEST_ENABLE);
		// startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
		// }

		// 搜索到已经配对的设备
		pairedDevices = mBluetoothAdapter.getBondedDevices();

		if (pairedDevices.size() > 0) {
			// findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
			String tem = "";
			for (BluetoothDevice device : pairedDevices) {
				tem = tem + device.getName() + " " + device.getAddress() + "\n";
				if (device.getName().equals("HC-06")) {
					mChatService = new ConnectThread(device);
					mChatService.run();
				}
			}
		} else {
		}

	}

	// 打印所有的 intent extra 数据
	private static String printBundle(Bundle bundle) {
		StringBuilder sb = new StringBuilder();
		for (String key : bundle.keySet()) {
			if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
				sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
			} else {
				sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
			}
		}
		return sb.toString();
	}

	private class ConnectThread extends Thread {
//		private final BluetoothSocket mmSocket;
		private final BluetoothDevice mmDevice;

		public ConnectThread(BluetoothDevice device) {
			// Use a temporary object that is later assigned to mmSocket,
			// because mmSocket is final
			BluetoothSocket tmp = null;
			mmDevice = device;
			Method m;
			try {
				m = device.getClass().getMethod("createRfcommSocket",
						new Class[] { int.class });
				tmp = (BluetoothSocket) m.invoke(device, Integer.valueOf(1));
			} catch (SecurityException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (NoSuchMethodException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mmSocket = tmp;
		}

		public void run() {
			// Cancel discovery because it will slow down the connection
			mBluetoothAdapter.cancelDiscovery();
			try {
				mmSocket.connect();
				mConnectedThread = new ConnectedThread(mmSocket);
				mConnectedThread.start();
			} catch (IOException connectException) {
				try {
					mmSocket.close();
				} catch (IOException closeException) {
					closeException.printStackTrace();
				}
				connectException.printStackTrace();
			}
		}

		/** Will cancel an in-progress connection, and close the socket */
		public void cancel() {
			try {
				mmSocket.close();
			} catch (IOException e) {
			}
		}
	}

	private class ConnectedThread extends Thread {
		private final BluetoothSocket mmSocket;
		private final InputStream mmInStream;
		private final OutputStream mmOutStream;

		public ConnectedThread(BluetoothSocket socket) {
			mmSocket = socket;
			InputStream tmpIn = null;
			OutputStream tmpOut = null;

			// Get the BluetoothSocket input and output streams
			try {
				tmpIn = socket.getInputStream();
				tmpOut = socket.getOutputStream();
			} catch (IOException e) {
				Log.e(TAG, "temp sockets not created", e);
			}

			mmInStream = tmpIn;
			mmOutStream = tmpOut;
		}

		public void run() {
			byte[] buffer = new byte[1024];
			int bytes;
			// while (true) {
			// try {
			// // Read from the InputStream
			// bytes = mmInStream.read(buffer);
			// // Send the obtained bytes to the UI Activity
			// mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
			// .sendToTarget();
			// } catch (IOException e) {
			// Log.e(TAG, "disconnected", e);
			// connectionLost();
			// // Start the service over to restart listening mode
			// break;
			// }
			// }
		}

		/**
		 * Write to the connected OutStream.
		 * 
		 * @param buffer
		 *            The bytes to write
		 */
		public void write(byte[] buffer) {
			try {
				mmOutStream.write(buffer);
				// Share the sent message back to the UI Activity
				// mHandler.obtainMessage(MESSAGE_WRITE, -1, -1, buffer)
				// .sendToTarget();
				this.cancel();
			} catch (IOException e) {
				Log.e(TAG, "Exception during write", e);
			}
		}

		public void cancel() {
			try {
				mmSocket.close();
			} catch (IOException e) {
				Log.e(TAG, "close() of connect socket failed", e);
			}
		}
	}

	private void connectionLost() {
		// temText.setText("连着连着中断了");
		// // Send a failure message back to the Activity
		// Message msg = mHandler.obtainMessage(BluetoothChat.MESSAGE_TOAST);
		// Bundle bundle = new Bundle();
		// bundle.putString(BluetoothChat.TOAST, "Device connection was lost");
		// msg.setData(bundle);
		// mHandler.sendMessage(msg);
		//
		// // Start the service over to restart listening mode
		// BluetoothChatService.this.start();
	}

	public void ContentWrite(String str) {
		if (mConnectedThread != null) {
			mConnectedThread.write(str.getBytes());
			mConnectedThread = null;
		}

	}
}
