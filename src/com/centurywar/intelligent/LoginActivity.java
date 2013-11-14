package com.centurywar.intelligent;
import net.sf.json.JSONObject;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends BaseActivity {
	private EditText username;
	private EditText password;
	private Button submit;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.login);
        username	= (EditText)findViewById(R.id.username_edit);
        password	= (EditText)findViewById(R.id.password_edit);
        submit		= (Button)findViewById(R.id.signin_button);
        submit.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				String userName = username.getText().toString();
				String pwd	= password.getText().toString();
//				socketClient.sendMessageSocket("control_cup_"+userName+"_"+"7a941492a0dc743544ebc71c89370a64");
				JSONObject jsob = new JSONObject();
				jsob.put("control", "cpd");
				jsob.put("username", "wanbin");
				jsob.put("password", "7a941492a0dc743544ebc71c89370a64");
				sendMessage(jsob);
			}
		});
    }
}
