package com.centurywar.intelligent;

//���ƻ���
public class BaseControl {
	public int pik = 0;
	public int type = 10;

	// static public String bluetoothMac = "20:13:09:30:12:77";
	static public String bluetoothMac = "";
	// public String bluetoothMac2 = "20:13:09:30:14:48";
	// ��ֵ����״̬
	public int value = 0;
	public Bluetooth bt = null;
	public Bluetooth bt2 = null;
	
	public BaseControl() {

	}

	public void setPikType(String mac, int setPik, int setType) {
		if (mac != bluetoothMac) {
			bluetoothMac = mac;
			if (bt != null) {
				bt.release();
			}
			bt = new Bluetooth(bluetoothMac);
		}
		pik = setPik;
		type = setType;
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
