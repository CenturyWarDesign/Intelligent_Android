package com.centurywar.intelligent;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {
	protected String temstr;

	private TextView temText;
	private EditText edit_text_out;
	private Button button_send;
	private ArrayAdapter<String> mNewDevicesArrayAdapter;
	private Set<BluetoothDevice> pairedDevices;

	// Debugging
	private static final String TAG = "BluetoothChat";
	private static final boolean D = true;

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

	// Intent request codes
	private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
	private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
	private static final int REQUEST_ENABLE_BT = 3;

	// Layout Views
	private ListView mConversationView;
	private EditText mOutEditText;
	private Button mSendButton;

	// Name of the connected device
	private String mConnectedDeviceName = null;
	// Array adapter for the conversation thread
	private ArrayAdapter<String> mConversationArrayAdapter;
	// String buffer for outgoing messages
	private StringBuffer mOutStringBuffer;
	// Local Bluetooth adapter
	private BluetoothAdapter mBluetoothAdapter = null;
	// Member object for the chat services
	private ConnectThread mChatService = null;

	private ConnectedThread mConnectedThread;
	// private BroadcastReceiver mReceiver;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		temText = (TextView) findViewById(R.id.temText);
		edit_text_out = (EditText) findViewById(R.id.edit_text_out);
		button_send = (Button) findViewById(R.id.button_send);
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			// 表明此手机不支持蓝牙
			return;
		}
		if (!mBluetoothAdapter.isEnabled()) { // 蓝牙未开启，则开启蓝牙
			Intent enableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
		}

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
			temText.setText(tem);
		} else {
			temText.setText("没有找到已匹对的设备");
		}

		button_send.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
					ContentWrite(edit_text_out.getText().toString());
			}
		});
	}

	private class ConnectThread extends Thread {
		private final BluetoothSocket mmSocket;
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
		temText.setText("连着连着中断了");
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
	private void ContentWrite(String str){
		if (mConnectedThread != null) {
			mConnectedThread.write(str.getBytes());
			// temText.setText("可以发送");
			// temText.setText("可以发送");
		} else {
			temText.setText("中断了");
		}
	}
}
