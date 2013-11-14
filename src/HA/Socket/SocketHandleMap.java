package HA.Socket;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import com.centurywar.intelligent.BaseActivity;

public class SocketHandleMap {
	public static Map<String, BaseActivity> handleMap = new HashMap<String, BaseActivity>();

	public static boolean registerActivity(BaseActivity baseActivity) {
		String name = baseActivity.getClass().getName();
		if (handleMap.containsKey(name)) {
			handleMap.remove(name);
		}
		handleMap.put(name, baseActivity);
		System.out.println(String.format("[add %s to handleMap]", name));
		return true;
	}

	public static boolean unRegisterActivity(String name) {
		handleMap.remove(name);
		return true;
	}

	/**
	 * 把返回的数据分发到相应的类里面 
	 * @param jsonobj
	 * @return
	 */
	public static boolean sendToActivity(JSONObject jsonobj) {
		for (String key : handleMap.keySet()) {
			handleMap.get(key).MessageCallBack(jsonobj);
		}
		return true;
	}
}
