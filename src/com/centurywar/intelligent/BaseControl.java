package com.centurywar.intelligent;

//控制基类
public class BaseControl {
	public int pik = 0;
	public int type = 10;
	public String bluetoothName = "HC-06";
	// 数值或者状态
	public int value = 0;

	public BaseControl(int setPik, int setType) {
		pik = setPik;
		type = setType;
	}

	public void open() {
		Bluetooth bt = new Bluetooth(bluetoothName);
		bt.ContentWrite(type + "_" + pik + "_1_0");
		bt.release();
	}

	public void close() {
		Bluetooth bt = new Bluetooth(bluetoothName);
		bt.ContentWrite(type + "_" + pik + "_0_0");
		bt.release();
	}

	public int status() {
		return value;
	}
}
