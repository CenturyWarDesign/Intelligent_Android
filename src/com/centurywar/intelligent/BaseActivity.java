package com.centurywar.intelligent;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Window;
import cn.jpush.android.api.JPushInterface;

public class BaseActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

	}

	@Override
	protected void onResume() {
		initJPUSH();
		super.onResume();
	}

	private void initJPUSH() {
		JPushInterface.setDebugMode(true);
		JPushInterface.init(this);
	}

}
