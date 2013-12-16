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
	private Button btnreg;
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
        
		submit = (Button) findViewById(R.id.signin_button);
		submit.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				// socketClient.sendMessageSocket("control_cup_"+userName+"_"+"7a941492a0dc743544ebc71c89370a64");
				JSONObject jsob = new JSONObject();
				try {
					jsob.put("control", ConstantControl.CHECK_USERNAME_PASSWORD);
					jsob.put("username", username.getText().toString().trim());
					jsob.put("password",
							BaseClass.MD5(password.getText().toString().trim()));
					pwd = password.getText().toString().trim();
				} catch (Exception e) {
					System.out.println("��½ʱ������쳣" + e.getMessage());
					ToastMessage("����������");
				}
				sendMessage(jsob);
				submit.setEnabled(false);
			}
		});
        
		btnreg = (Button) findViewById(R.id.btnReg);
		btnreg.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				// socketClient.sendMessageSocket("control_cup_"+userName+"_"+"7a941492a0dc743544ebc71c89370a64");
				JSONObject jsob = new JSONObject();
				try {
					jsob.put("control", ConstantControl.REG_USERNAME_PASSWORD);
					jsob.put("username", username.getText().toString().trim());
					jsob.put("password",
							BaseClass.MD5(password.getText().toString().trim()));
					pwd = password.getText().toString().trim();
				} catch (Exception e) {
					System.out.println("��½ʱ������쳣" + e.getMessage());
					ToastMessage("����������");
				}
				sendMessage(jsob);
				btnreg.setEnabled(false);
			}
		});
		setUMENGUpdate();
	}
    
    /* ���󷽷�
     * @see com.centurywar.intelligent.BaseActivity#MessageCallBack(net.sf.json.JSONObject)
     */
	public void MessageCallBack(JSONObject jsonobj) throws Exception {
		String command = jsonobj.getString("control");
		System.out.println("�յ��������ı��ģ�"+jsonobj.toString());
		if (null != command&&command.equals(ConstantControl.ECHO_CHECK_USERNAME_PASSWORD)) {
			if(jsonobj.getString("retCode").equals("0000")){
				setGameInfoStr("sec", jsonobj.getString("sec"));
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(), MainActivity.class);
				startActivity(intent);
				ToastMessage("��½�ɹ�");
				finish();
			}else{
				ToastMessage(jsonobj.getString("memo"));
			}
		
		}
//		else {
//			setGameInfoStr("password", "");
//			ToastMessage("��½ʧ��");
//		}
	}

	
	public void StatusCallBack(JSONObject jsonobj) throws Exception {
		submit.setEnabled(true);
		btnreg.setEnabled(true);
	}
	
	protected void addOneSec() {};
}
