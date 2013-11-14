package com.centurywar.intelligent.control;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

import com.centurywar.intelligent.BaseActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;

public class Bluetooth extends BaseActivity {
	private Set<BluetoothDevice> pairedDevices;
	private static BluetoothAdapter mBluetoothAdapter = null;
	private ConnectThread mChatService = null;
	private ConnectedThread mConnectedThread = null;
	private BluetoothSocket mmSocket = null;

	public Bluetooth(String mac) {
		
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		pairedDevices = mBluetoothAdapter.getBondedDevices();
		if (pairedDevices.size() > 0) {
			for (BluetoothDevice device : pairedDevices) {
				if (device.getAddress().equals(mac)) {
					mChatService = new ConnectThread(device);
					mChatService.run();
					return;
				}
			}
		}

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

	private class ConnectedThread extends Thread {
		private final BluetoothSocket mmSocket;
		private final InputStream mmInStream;
		private final OutputStream mmOutStream;

		public ConnectedThread(BluetoothSocket socket) {
			mmSocket = socket;
			InputStream tmpIn = null;
			OutputStream tmpOut = null;
			try {
				tmpIn = socket.getInputStream();
				tmpOut = socket.getOutputStream();
			} catch (IOException e) {
			}

			mmInStream = tmpIn;
			mmOutStream = tmpOut;
		}

//		private BufferedReader getReader(BluetoothSocket socket)
//				throws IOException {
//			InputStream socketIn = socket.getInputStream();
//			return new BufferedReader(new InputStreamReader(socketIn));
//		}

		public void run() {
//			try {
//				BufferedReader br = getReader(mmSocket);
//				String msg = null;
//				while ((msg = br.readLine()) != null) {
//					System.out.println(" write:" + msg);
//				}
//			} catch (IOException e) {
//				System.out.println("�Ͽ�������");
//				e.printStackTrace();
//			} finally {
//				try {
//					if (mmSocket != null) {
//						mmSocket.close();
//					}
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
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
			} catch (IOException e) {
			}
		}

		public void cancel() {
			try {
				mmSocket.close();
			} catch (IOException e) {
			}
		}
	}

	public void ContentWrite(String str) {
		if (mConnectedThread != null) {
			mConnectedThread.write(str.getBytes());
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
