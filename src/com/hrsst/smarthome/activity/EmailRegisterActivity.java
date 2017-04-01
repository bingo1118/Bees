package com.hrsst.smarthome.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.Volley;
import com.hrsst.smarthome.R;
import com.hrsst.smarthome.global.Constants;
import com.hrsst.smarthome.util.CheakEmail;
import com.hrsst.smarthome.util.MD5;
import com.hrsst.smarthome.volley.JsonArrayPostRequest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EmailRegisterActivity extends Activity implements OnClickListener{
	private Context mContext;
	private EditText emailNum,pwdNum,pwdNum1;
	private Button registerBtn;
	private String emailNumStr,pwdNumStr,pwdNum1Str;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_email_register);
		mContext = this;
		init();
	}
	
	private void init(){
		emailNum = (EditText) findViewById(R.id.email_register_et);
		pwdNum = (EditText) findViewById(R.id.email_pwd_et);
		pwdNum1 = (EditText) findViewById(R.id.email_rewrite_pwd_et);
		registerBtn = (Button) findViewById(R.id.email_register_btn);
		registerBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		emailNumStr = emailNum.getText().toString().trim();
		pwdNumStr = pwdNum.getText().toString().trim();
		pwdNum1Str = pwdNum1.getText().toString().trim();
		switch (view.getId()) {
		case R.id.email_register_btn:
			boolean isEmailed = CheakEmail.getInstance().cheakEmail(emailNumStr);
			if(isEmailed==true){
				if(null!=pwdNumStr&&pwdNumStr.length()>0&&pwdNumStr.equals(pwdNum1Str)){
					register(emailNumStr,pwdNumStr);
				}else{
					Toast.makeText(mContext, R.string.two_psw_diffrence, Toast.LENGTH_SHORT).show();
				}
			}else{
				Toast.makeText(mContext, R.string.email_format_error, Toast.LENGTH_SHORT).show();
			}
			break;

		default:
			break;
		}
	}
	
	private void register(final String phoneNum,final String phonePwd){
		String pwd = new MD5().getMD5ofStr(phonePwd);
		RequestQueue mQueue = Volley.newRequestQueue(mContext);
		Map<String,String> map = new HashMap<String,String>();
		map.put("userID", phoneNum);
		map.put("userPwd", pwd);
		map.put("flag", "2");
		JsonArrayPostRequest mJsonRequest = new JsonArrayPostRequest(
				Constants.USER_REGISTER_URL, 
				new Listener<JSONArray>() {

					@Override
					public void onResponse(JSONArray response) {
						if(response.length()>0){
							try {
								String obj = response.get(0).toString();
								if(null!=obj&&obj.length()>0&&obj.equals("success")){
									Intent i = new Intent(EmailRegisterActivity.this,EmailActiviteActivity.class);
									i.putExtra("userID", phoneNum);
									i.putExtra("userPwd", phonePwd);
									startActivity(i);
									finish();
								}
								if(null!=obj&&obj.length()>0&&obj.equals("false")){
									Toast.makeText(mContext, R.string.regist_fail, Toast.LENGTH_SHORT).show();
								}
								if(null!=obj&&obj.length()>0&&obj.equals("userexit")){
									Toast.makeText(mContext, R.string.user_exist, Toast.LENGTH_SHORT).show();
								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}, 
				new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						registerBtn.setEnabled(true);
					}
				}, 
				map);
		mQueue.add(mJsonRequest);
	}

}
