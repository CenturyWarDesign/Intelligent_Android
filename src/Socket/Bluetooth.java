package Socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

import com.centurywar.intelligent.BaseActivity;
import com.centurywar.intelligent.BaseClass;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class Bluetooth extends BaseClass{
	private Set<BluetoothDevice> pairedDevices;
	private static BluetoothAdapter mBluetoothAdapter = null;
	private ConnectThread mChatService = null;
	private ConnectedThread mConnectedThread = null;
	private BluetoothSocket mmSocket = null;
	public String blueToothMac = "";

	public Bluetooth(String mac) {
		blueToothMac = mac;
		// 多线程去连接蓝牙
		new Thread() {
			public void run() {
				try {
					mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
					pairedDevices = mBluetoothAdapter.getBondedDevices();
					boolean isconnected = false;
					if (pairedDevices.size() > 0) {
						for (BluetoothDevice device : pairedDevices) {
							if (device.getAddress().equals(blueToothMac)) {
								mChatService = new ConnectThread(device);
								mChatService.run();
								isconnected = true;
								return;
							}
						}
					}
					if (!isconnected) {
						System.out.print("error bluetooth");
					}
				} catch (Exception e) {
					e.toString();
				}

			}
		}.start();
	}

	private class ConnectThread extends Thread {
		public ConnectThread(BluetoothDevice device) {
			BluetoothSocket tmp = null;
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
	}

	/**
	 * 向蓝牙间写入数据
	 * @param str
	 */
	public void ContentWrite(String str) {
		if (mConnectedThread != null) {
			mConnectedThread.write(str.getBytes());
			System.out.println("[send to bluetooth]"+str);
		} else {
			Log("booltooth connect error");
		}
	}

	public boolean getStatus() {
		if (mConnectedThread == null) {
			return false;
		}
		if (mChatService == null) {
			return false;
		}
		if (mmSocket == null) {
			return false;
		}
		return mmSocket.isConnected();
	}

	public void release() {
		mConnectedThread = null;
		try {
			mmSocket.close();
		} catch (IOException closeException) {
			closeException.printStackTrace();
		}
		mChatService = null;
	}
}
