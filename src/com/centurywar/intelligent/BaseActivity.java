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

	public void initJPUSH() {
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
	 * ����TAG
	 */
	// private boolean setTag(String tag) {
	//
	// // ","�����Ķ�� ת���� Set
	// String[] sArray = tag.split(",");
	// Set<String> tagSet = new LinkedHashSet<String>();
	// for (String sTagItme : sArray) {
	// if (!ExampleUtil.isValidTagAndAlias(sTagItme)) {
	// return false;
	// }
	// tagSet.add(sTagItme);
	// }
	//
	// // ����JPush API����Tag
	// JPushInterface.setAliasAndTags(getApplicationContext(), null, tagSet,
	// this);
	// return true;
	//
	// }
	//
	// /**
	// * ����Alias
	// */
	// private void setAlias(String alias) {
	// // ����JPush API����Alias
	// JPushInterface.setAliasAndTags(getApplicationContext(), alias, null,
	// this);
	// }
}
