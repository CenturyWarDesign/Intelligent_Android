package com.centurywar.intelligent.control;

import java.net.Socket;


import android.bluetooth.BluetoothAdapter;
import android.os.Handler;
import android.os.Message;

import Socket.Bluetooth;
import Socket.SocketClient;

public class BaseControl {
	public int pik = 0;
	public int type = 10;
    public static SocketClient socket;
	// static public String bluetoothMac = "20:13:09:30:12:77";
	static public String bluetoothMac = "";
	// public String bluetoothMac2 = "20:13:09:30:14:48";
	public int value = 0;
	public static Bluetooth bt = null;
	public boolean useBt = true;

	public BaseControl() {
	}
	public BaseControl(String mac) {
		boolean isBluetooth = false;
		useBt = checkBluetooth();
		if (useBt) {
			if (mac != bluetoothMac || !bt.getStatus() || bt == null) {
				bluetoothMac = mac;
				if (bt != null) {
					bt.release();
				}
				bt = new Bluetooth(bluetoothMac);
				isBluetooth = true;
				handler.sendEmptyMessage(0);
			}
		}
		if (!useBt || !isBluetooth) {
			initSocket();
		}
	}
	

	public void initSocket() {
		if (useBt) {
			return;
		}
		if (socket == null || !socket.status()) {
			socket = new SocketClient();
		}
	}
	public boolean getSocketStatus(){
		return socket.status();
	}

	public void setMac(String mac) {
		boolean isBluetooth = false;
		if (useBt) {
			if (mac != bluetoothMac || !bt.getStatus() || bt == null) {
				bluetoothMac = mac;
				if (bt != null) {
					bt.release();
				}
				bt = new Bluetooth(bluetoothMac);
				isBluetooth = true;
				handler.sendEmptyMessage(0);
			}
		}
		if (!useBt || !isBluetooth) {
			initSocket();
		}
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			System.out.println("init bluetooth success..");
			if (bt.getStatus()) {
				useBt = true;
			} else {
				useBt = false;
			}
		}
	};

	public void sendToDevice(String message) {
		if (useBt) {
			bt.ContentWrite(message);
		} else {
			initSocket();
			socket.sendMessageSocket(message);
		}
	}
	public int status() {
		return value;
	}

	
	public void release() {
		if (bt != null) {
			bt.release();
			bt = null;
		}
		if (socket != null) {
			socket.closeSocket();
			socket = null;
		}
	}
	protected boolean checkBluetooth() {
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter
				.getDefaultAdapter();
		return mBluetoothAdapter.isEnabled();
	}
}
