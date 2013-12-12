package com.centurywar.intelligent;

import org.json.JSONObject;

import com.centurywar.intelligent.control.BaseControl;

import cn.jpush.android.api.JPushInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

public class MessageActivity extends BaseActivity {

	@Override
	/*
	 * 抽象方法
	 * 
	 * @see com.centurywar.intelligent.BaseActivity#MessageCallBack(net.sf.json.
	 * JSONObject)
	 */
	public void MessageCallBack(JSONObject jsonobj) throws Exception {
		String command = jsonobj.getString("control");
		if (null != command
				&& command.equals(ConstantControl.ECHO_CHECK_USERNAME_PASSWORD)) {
			if (jsonobj.getInt("code") == ConstantCode.CODE_CONNECTET) {
				ToastMessage("连接服务器成功");
				finish();
			}
		}
	}

	public void connectError() {

	}
	protected void addOneSec() {
		
	}
}
