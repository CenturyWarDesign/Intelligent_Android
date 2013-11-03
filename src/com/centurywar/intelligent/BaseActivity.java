package com.centurywar.intelligent;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
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
		super.onResume();
		// initJPUSH();
	}

	private void initJPUSH() {
		JPushInterface.setDebugMode(true);
		JPushInterface.init(this);
		JPushInterface.setAlias(this, "caojunling", null);
	}

	protected boolean checkBluetooth() {
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter
				.getDefaultAdapter();
		return mBluetoothAdapter.isEnabled();
	}

	/**
	 * 设置TAG
	 */
	// private boolean setTag(String tag) {
	//
	// // ","隔开的多个 转换成 Set
	// String[] sArray = tag.split(",");
	// Set<String> tagSet = new LinkedHashSet<String>();
	// for (String sTagItme : sArray) {
	// if (!ExampleUtil.isValidTagAndAlias(sTagItme)) {
	// return false;
	// }
	// tagSet.add(sTagItme);
	// }
	//
	// // 调用JPush API设置Tag
	// JPushInterface.setAliasAndTags(getApplicationContext(), null, tagSet,
	// this);
	// return true;
	//
	// }
	//
	// /**
	// * 设置Alias
	// */
	// private void setAlias(String alias) {
	// // 调用JPush API设置Alias
	// JPushInterface.setAliasAndTags(getApplicationContext(), alias, null,
	// this);
	// }
}
