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

public class LoginActivity extends BaseActivity {
	private EditText username;
	private EditText password;
	private Button submit;
	private String pwd;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.login);
        username	= (EditText)findViewById(R.id.username_edit);
        password	= (EditText)findViewById(R.id.password_edit);
        if(null!=getUsername()){
        	username.setText(getUsername());
        }
        if(null!=getGameInfoStr("password")){
        	password.setText(getGameInfoStr("password"));
        }
        submit		= (Button)findViewById(R.id.signin_button);
        submit.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				// socketClient.sendMessageSocket("control_cup_"+userName+"_"+"7a941492a0dc743544ebc71c89370a64");
				JSONObject jsob = new JSONObject();
				try {
					jsob.put("control", "cup");
					jsob.put("username", username.getText().toString().trim());
					jsob.put("password", BaseClass.MD5(password.getText().toString().trim()));
					pwd = password.getText().toString().trim();
				} catch (Exception e) {
					System.out.println("登陆时候出现异常"+e.getMessage());
					ToastMessage("请重新输入");
				}
				sendMessage(jsob);
			}
		});
    }
    
    /* 抽象方法
     * @see com.centurywar.intelligent.BaseActivity#MessageCallBack(net.sf.json.JSONObject)
     */
	public void MessageCallBack(JSONObject jsonobj) throws Exception {
		String command = jsonobj.getString("control");
		Log.v("liuchunlong", "登陆，收到的返回报文为："+jsonobj.toString());
		System.out.println("[liuchunlong] 收到服务器的报文："+jsonobj.toString());
		if (null != command&&command.equals(ConstantControl.ECHO_CHECK_USERNAME_PASSWORD)) {
			if(jsonobj.getString("retCode").equals("0000")){
				setGameInfoStr("sec", jsonobj.getString("sec"));
				setGameInfoStr("username", jsonobj.getString("username"));
				setGameInfoStr("password", pwd);
				BaseControl.bluetoothMac=jsonobj.getJSONObject("info").getString("bluetoothmac");
				//登录成功初始化蓝牙
				if (BaseControl.bluetoothMac.length() > 0) {
					initBlueTooth();
				}
				JPushInterface.setAlias(this, jsonobj.getString("username"), null);
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(), MainActivity.class);
				startActivity(intent);
				ToastMessage("登陆成功");
				finish();
			}else{
				ToastMessage(jsonobj.getString("memo"));
			}
		}
//		else {
//			setGameInfoStr("password", "");
//			ToastMessage("登陆失败");
//		}
	}
}
