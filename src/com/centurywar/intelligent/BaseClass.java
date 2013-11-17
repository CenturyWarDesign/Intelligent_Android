package com.centurywar.intelligent;

import android.util.Log;
import android.widget.Toast;

public class BaseClass {
	public static boolean isDebugger=true;
	protected void Log(String str){
		Log.v(getClass().getName(), str);
	}
	

}
