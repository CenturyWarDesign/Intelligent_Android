package com.centurywar.intelligent;

//控制基类
public class BaseControl {
	public int pik = 0;
	public int type = 10;

	// static public String bluetoothMac = "20:13:09:30:12:77";
	static public String bluetoothMac = "";
	// public String bluetoothMac2 = "20:13:09:30:14:48";
	// 数值或者状态
	public int value = 0;
	public static Bluetooth bt = null;
	
	public BaseControl() {

	}

	public void setPikType(String mac, int setPik, int setType) {
		pik = setPik;
		type = setType;
		if (mac != bluetoothMac || !bt.getStatus() || bt == null) {
			bluetoothMac = mac;
			if (bt != null) {
				bt.release();
			}
			bt = new Bluetooth(bluetoothMac);
		}
	}

	public void open() {
		bt.ContentWrite(type + "_" + pik + "_1_0");
	}

	public void close() {
		bt.ContentWrite(type + "_" + pik + "_0_0");
	}

	public int status() {
		return value;
	}

	public void release() {
		bt.release();
		bt = null;
	}
}
