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
	// static public String bluetoothMac = "20:13:09:30:12:77";
	static public String bluetoothMac = "";
	// public String bluetoothMac2 = "20:13:09:30:14:48";
	public int value = 0;
	public boolean useBt = true;

	public BaseControl() {
	}

	public int status() {
		return value;
	}

	protected boolean checkBluetooth() {
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter
				.getDefaultAdapter();
		return mBluetoothAdapter.isEnabled();
	}
}
