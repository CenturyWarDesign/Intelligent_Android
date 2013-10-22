package com.centurywar.intelligent;

import java.util.Timer;
import java.util.TimerTask;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

//控制基类
public class LightControl extends BaseControl {
	public int nowValue = 0;
	public int putValue = 0;
	public String readyToSend = "";
	public Timer timer = null;
	public LightControl() {
		type = 20;
	}

	// 初始化时间函数
	private void initTimer() {
		if (timer == null) {
			timer = new Timer();
			timer.schedule(timetask, 0, 50);
		}
	}

	// 传递时间
	private TimerTask timetask = new TimerTask() {
		public void run() {
			Message message = new Message();
			message.what = 1;
			handler.sendMessage(message);
		}
	};

	// 接受时间
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			sendBluetoothMessage();
			super.handleMessage(msg);
		}
	};

	public void sendBluetoothMessage() {
		if (readyToSend.length() > 0) {
			bt.ContentWrite(readyToSend);
			Log.v("bloothsendtem", readyToSend);
			readyToSend = "";
		}
	}
	public void setValue(int value) {
		nowValue = value;
			putValue = value;
			readyToSend = type + "_" + pik + "_" + value + "_0,";
			initTimer();
	}

	public int getValue() {
		return nowValue;
	}
}
