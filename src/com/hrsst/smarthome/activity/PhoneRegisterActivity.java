package com.hrsst.smarthome.activity;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.Volley;
import com.hrsst.smarthome.R;
import com.hrsst.smarthome.global.Constants;
import com.hrsst.smarthome.global.MyApp;
import com.hrsst.smarthome.util.MD5;
import com.hrsst.smarthome.volley.JsonArrayPostRequest;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PhoneRegisterActivity extends Activity implements OnClickListener {
	private Context mContext;
	private EditText phoneNum, pwdNum, pwdNum1, codeNum;
	private Button codeBtn, registerBtn;
	private String phoneNumStr, pwdNumStr, pwdNum1Str, codeNumStr;
	private Timer mTimer;
	private int count = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_phone_register);
		mContext = this;
		init();
		regFilter();
	}

	private void init() {
		phoneNum = (EditText) findViewById(R.id.phone_register_et);
		pwdNum = (EditText) findViewById(R.id.pwd_et);
		pwdNum1 = (EditText) findViewById(R.id.rewrite_pwd_et);
		codeNum = (EditText) findViewById(R.id.code_et);
		codeBtn = (Button) findViewById(R.id.get_code_btn);
		registerBtn = (Button) findViewById(R.id.phone_register_btn);
		codeBtn.setOnClickListener(this);
		registerBtn.setOnClickListener(this);
		SMSSDK.initSDK(this, "10c126154a372",
				"063a00c954d0b2b7cb25b8108a070401");
		EventHandler mEventHandler = new EventHandler() {
			@Override
			public void afterEvent(int event, int result, Object data) {
				// TODO Auto-generated method stub
				super.afterEvent(event, result, data);
				if (result == SMSSDK.RESULT_COMPLETE) {
					// 回调完成
					if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
						// 提交验证码成功
						Intent i = new Intent();
						i.setAction("VERIFY_SUCCESS");
						mContext.sendBroadcast(i);
					} else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
						Intent i = new Intent();
						i.setAction("CODE_ALREADY_SEND_ACTION");
						mContext.sendBroadcast(i);
						// 获取验证码成功
					} else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
						// 返回支持发送验证码的国家列表
					}
				}else if(result==SMSSDK.RESULT_ERROR){
					Intent i = new Intent();
					i.setAction("VERIFY_FALSE");
					mContext.sendBroadcast(i);
				} else {
					((Throwable) data).printStackTrace();
				}
			}
		};

		SMSSDK.registerEventHandler(mEventHandler);
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		phoneNumStr = phoneNum.getText().toString().trim();
		pwdNumStr = pwdNum.getText().toString().trim();
		pwdNum1Str = pwdNum1.getText().toString().trim();
		switch (view.getId()) {
		case R.id.get_code_btn:
			if (phoneNumStr.length() != 11) {
				Toast.makeText(mContext, R.string.the_phone_format_error, 1).show();
			} else {
				if (null != pwdNumStr && pwdNumStr.length() > 0
						&& pwdNumStr.equals(pwdNum1Str)) {
					ifExit(phoneNumStr);
				} else {
					Toast.makeText(mContext,R.string.two_psw_diffrence, 1).show();
				}
			}
			break;
		case R.id.phone_register_btn:
			codeNumStr = codeNum.getText().toString().trim();
			if (null != codeNumStr && codeNumStr.length() == 4) {
				SMSSDK.submitVerificationCode("86", phoneNumStr, codeNumStr);
				registerBtn.setEnabled(false);
			} else {
				Toast.makeText(mContext, R.string.verification_code_error, 1).show();
			}
			break;
		default:
			break;
		}
	}

	public void regFilter() {
		IntentFilter filter = new IntentFilter();
		filter.addAction("CODE_ALREADY_SEND_ACTION");
		filter.addAction("VERIFY_SUCCESS");
		filter.addAction("VERIFY_FALSE");
		mContext.registerReceiver(br, filter);
	}

	// 13428282520 7050
	BroadcastReceiver br = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (intent.getAction().equals("CODE_ALREADY_SEND_ACTION")) {
				Toast.makeText(mContext,R.string.verification_code_have_send, 1).show();
				phoneNum.setEnabled(false);
				pwdNum.setEnabled(false);
				pwdNum1.setEnabled(false);
				mTimer = new Timer();
				setTimerdoAction(doAction, mTimer);
			}
			if (intent.getAction().equals("VERIFY_SUCCESS")) {
				Toast.makeText(mContext, R.string.verification_code_pass, 1).show();
				register(phoneNumStr, pwdNumStr);
			}
			if (intent.getAction().equals("VERIFY_FALSE")) {
				Toast.makeText(mContext, R.string.verification_code_error, 1).show();
				phoneNum.setEnabled(true);
				pwdNum.setEnabled(true);
				pwdNum1.setEnabled(true);
				codeBtn.setEnabled(true);
				registerBtn.setEnabled(true);
			}
		}

	};

	private void ifExit(final String phoneNum) {
		RequestQueue mQueue = Volley.newRequestQueue(mContext);
		Map<String, String> map = new HashMap<String, String>();
		map.put("userID", phoneNum);
		map.put("flag", "1");
		JsonArrayPostRequest mJsonRequest = new JsonArrayPostRequest(
				Constants.USER_IFEXIT_URL, new Listener<JSONArray>() {

					@Override
					public void onResponse(JSONArray response) {
						if (response.length() > 0) {
							try {
								String obj = response.get(0).toString();
								if (null != obj && obj.length() > 0
										&& obj.equals("no")) {
									SMSSDK.getVerificationCode("86", phoneNum);
									codeBtn.setEnabled(false);
								}
								if (null != obj && obj.length() > 0
										&& obj.equals("yes")) {
									Toast.makeText(mContext, R.string.phone_exist, 1)
											.show();
								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
					}
				}, map);
		mQueue.add(mJsonRequest);
	}

	private void register(final String phoneNum, final String phonePwd) {
		String pwd = new MD5().getMD5ofStr(phonePwd);
		RequestQueue mQueue = Volley.newRequestQueue(mContext);
		Map<String, String> map = new HashMap<String, String>();
		map.put("userID", phoneNum);
		map.put("userPwd", pwd);
		map.put("flag", "1");
		JsonArrayPostRequest mJsonRequest = new JsonArrayPostRequest(
				Constants.USER_REGISTER_URL, new Listener<JSONArray>() {

					@Override
					public void onResponse(JSONArray response) {
						if (response.length() > 0) {
							try {
								String obj = response.get(0).toString();
								if (null != obj && obj.length() > 0
										&& obj.equals("success")) {
									mTimer.cancel();
									count = 0;
									Toast.makeText(mContext,R.string.regist_success, 1).show();
									registerBtn.setEnabled(true);
									Intent i = new Intent();
									i.setAction("PHONE_REGISTER_SUCCESS");
									i.putExtra("userID", phoneNum);
									i.putExtra("userPwd", phonePwd);
									MyApp.app.sendBroadcast(i);
									finish();
								}
								if (null != obj && obj.length() > 0
										&& obj.equals("false")) {
									Toast.makeText(mContext, R.string.regist_fail, 1)
											.show();
									mTimer.cancel();
									count = 0;
									registerBtn.setEnabled(true);
								}
							} catch (JSONException e) {
								//PushManager.getInstance().bindAlias
								// TODO Auto-generated catch block
								e.printStackTrace();
								registerBtn.setEnabled(true);
							}
						}
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						mTimer.cancel();
						count = 0;
						registerBtn.setEnabled(true);
					}
				}, map);
		mQueue.add(mJsonRequest);
	}

	private void setTimerdoAction(final Handler oj, Timer t) {
		t.schedule(new TimerTask() {
			@Override
			public void run() {
				Message message = new Message();
				message = oj.obtainMessage();
				oj.sendMessage(message);
				count = count + 1;
			}
		}, 1000, 1000);
	}

	private Handler doAction = new Handler() {
		public void handleMessage(Message msg) {
			codeBtn.setText(count + "" + "(s)");
			if (count == 60) {
				mTimer.cancel();
				count = 0;
				phoneNum.setEnabled(true);
				pwdNum.setEnabled(true);
				pwdNum1.setEnabled(true);
				codeBtn.setEnabled(true);
				registerBtn.setEnabled(true);
				codeBtn.setText(R.string.get_verification_code);
			}
		};
	};

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		SMSSDK.unregisterAllEventHandler();
		super.onDestroy();
		unregisterReceiver(br);
	}

}
