package com.centurywar.intelligent;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

//���ƻ���
public class LightControl extends BaseControl {
	public int nowValue = 0;
	public int putValue = 0;
	public String readyToSend = "";
	public Timer timer = null;
	private boolean lem1 = false;
	private boolean gamestart = false;
	private int i, j, k;
	public LightControl() {
		type = 20;
	}

	// ��ʼ��ʱ�亯��
	private void initTimer() {
		if (timer == null) {
			timer = new Timer();
			timer.schedule(timetask, 0, 100);
		}
	}

	// ����ʱ��
	private TimerTask timetask = new TimerTask() {
		public void run() {
			Message message = new Message();
			message.what = 1;
			handler.sendMessage(message);
		}
	};

	// ����ʱ��
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			sendBluetoothMessage();
			super.handleMessage(msg);
		}
	};

	public void sendBluetoothMessage() {
		// ��һ�ֵƹ�ģʽ
		if (gamestart = false) {
			i = 255;
			j = 0;
			k = 0;
		}
		if (lem1 && readyToSend == "") {
			gamestart = true;
			i = i - 5;
			int value = Math.abs(255 - i);
			if (i < -255) {
				i = 255;
			}
			Random an = new Random();
			int temlight = an.nextInt(3);
			if (temlight == 0) {
				pik = 3;
			} else if (temlight == 1) {
				pik = 5;
			} else {
				pik = 6;
			}

			String tem = type + "_" + pik + "_" + value + "_0,";
			bt.ContentWrite(tem);
		}
		if (readyToSend.length() > 0) {
			bt.ContentWrite(readyToSend);
			Log.v("bloothsendtem", readyToSend);
			readyToSend = "";
			gamestart = false;
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

	public void StartLem1() {
		initTimer();
		lem1 = true;
	}

	public void StopLem1() {
		lem1 = false;
	}
}
