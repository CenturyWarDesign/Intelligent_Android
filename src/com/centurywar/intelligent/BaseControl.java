package com.centurywar.intelligent;

import java.net.Socket;

import HA.Socket.SocketClient;

//控制基类
public class BaseControl {
	public int pik = 0;
	public int type = 10;
    public static SocketClient socket;
	// static public String bluetoothMac = "20:13:09:30:12:77";
	static public String bluetoothMac = "";
	// public String bluetoothMac2 = "20:13:09:30:14:48";
	// 数值或者状态
	public int value = 0;
	public static Bluetooth bt = null;
	public boolean useBt=false;
	public boolean hasinitSocke=false;
	public BaseControl() {

	}
	public void initSocket(){
		if (socket == null||!socket.status()) {
			socket = new SocketClient();
			hasinitSocke=true;
		}
	}
	public boolean getSocketStatus(){
		return socket.status();
	}
	public void setPikType(String mac, int setPik, int setType) {
		pik = setPik;
		type = setType;
		if (useBt) {
			if (mac != bluetoothMac || !bt.getStatus() || bt == null) {
				bluetoothMac = mac;
				if (bt != null) {
					bt.release();
				}
				bt = new Bluetooth(bluetoothMac);
			}
		} else {
			
		}
	}

	public void open() {
		sendToDevice(type + "_" + pik + "_1_0");
	}

	public void close() {
		sendToDevice(type + "_" + pik + "_0_0");
	}

	public void sendToDevice(String message) {
		initSocket();
		if (useBt) {
			bt.ContentWrite(message);
		} else {
			if (socket != null&&socket.status()) {
				socket.sendMessage(message);
			} else {
				System.out.println("socket not init");
			}
		}
	}
	public int status() {
		return value;
	}

	public void release() {
		bt.release();
		bt = null;
	}
}
