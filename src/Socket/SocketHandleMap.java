package Socket;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;


import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.centurywar.intelligent.BaseActivity;
import com.centurywar.intelligent.BaseClass;

public class SocketHandleMap extends BaseClass{
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
			Bundle bundle = new Bundle();
			bundle.putString("jsonobj", jsonobj.toString());
			Message message = new Message();
			message.setData(bundle);
			try {
				handleMap.get(key).handler.sendMessage(message);
			} catch (Exception e) {
				System.out.println(e.toString());
			}
		}
		return true;
	}
}
