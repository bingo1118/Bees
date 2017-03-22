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
import com.hrsst.smarthome.util.MD5;
import com.hrsst.smarthome.util.SharedPreferencesManager;
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

public class PhoneFindPwdActivity extends Activity implements OnClickListener {
	private EditText phoneEt;
	private EditText phonePwd;
	private EditText phonePwd1;
	private EditText code;
	private Button getCode;
	private Button find;
	private String phoneNumStr, pwdNumStr, pwdNum1Str, codeNumStr;
	private Context mContext;
	private Timer mTimer;
	private int count = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_phone_find);
		mContext = this;
		init();
		regFilter();
	}

	private void init() {
		phoneEt = (EditText) findViewById(R.id.phone_find_et);
		phonePwd = (EditText) findViewById(R.id.find_pwd_et);
		phonePwd1 = (EditText) findViewById(R.id.find_rewrite_pwd_et);
		code = (EditText) findViewById(R.id.find_code_et);
		getCode = (Button) findViewById(R.id.find_get_code_btn);
		find = (Button) findViewById(R.id.phone_find_btn);
		getCode.setOnClickListener(this);
		find.setOnClickListener(this);
		SMSSDK.initSDK(this, "10c126154a372",
				"063a00c954d0b2b7cb25b8108a070401");
		EventHandler mEventHandler = new EventHandler() {
			@Override
			public void afterEvent(int event, int result, Object data) {
				// TODO Auto-generated method stub
				super.afterEvent(event, result, data);
				System.out.println("result=" + result);
				System.out.println("event=" + event);
				System.out.println("data=" + data.toString());
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
				} else if(result==SMSSDK.RESULT_ERROR){
					Intent i = new Intent();
					i.setAction("VERIFY_FALSE");
					mContext.sendBroadcast(i);
				}
			}
		};

		SMSSDK.registerEventHandler(mEventHandler);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		phoneNumStr = phoneEt.getText().toString().trim();
		pwdNumStr = phonePwd.getText().toString().trim();
		pwdNum1Str = phonePwd1.getText().toString().trim();
		switch (v.getId()) {
		case R.id.find_get_code_btn:
			if (phoneNumStr.length() != 11) {
				Toast.makeText(mContext, "手机号格式不对", Toast.LENGTH_SHORT).show();
			} else {
				if (null != pwdNumStr && pwdNumStr.length() > 0
						&& pwdNumStr.equals(pwdNum1Str)) {
					ifExit(phoneNumStr);
				} else {
					Toast.makeText(mContext, "两次的密码不一样，请重新输入", Toast.LENGTH_SHORT).show();
				}
			}
			break;
		case R.id.phone_find_btn:
			codeNumStr = code.getText().toString().trim();
			if (null != codeNumStr && codeNumStr.length() == 4) {
				SMSSDK.submitVerificationCode("86", phoneNumStr, codeNumStr);
				find.setEnabled(false);
			} else {
				Toast.makeText(mContext, "验证码不正确，请重新输入", Toast.LENGTH_SHORT).show();
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

	BroadcastReceiver br = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (intent.getAction().equals("CODE_ALREADY_SEND_ACTION")) {
				Toast.makeText(mContext, "验证码已发送", Toast.LENGTH_SHORT).show();
				phoneEt.setEnabled(false);
				phonePwd.setEnabled(false);
				phonePwd1.setEnabled(false);
				mTimer = new Timer();
				setTimerdoAction(doAction, mTimer);
			}
			if (intent.getAction().equals("VERIFY_SUCCESS")) {
				Toast.makeText(mContext, "验证码已通过", Toast.LENGTH_SHORT).show();
				find(phoneNumStr, pwdNumStr);
			}
			if (intent.getAction().equals("VERIFY_FALSE")) {
				Toast.makeText(mContext, "验证码错误，请输入正确的验证码", Toast.LENGTH_SHORT).show();
				phoneEt.setEnabled(true);
				phonePwd.setEnabled(true);
				phonePwd1.setEnabled(true);
				getCode.setEnabled(true);
				find.setEnabled(true);
			}
		}
	};

	private void find(final String phoneNumStr, final String pwdNumStr) {
		// TODO Auto-generated method stub
		String pwd = new MD5().getMD5ofStr(pwdNumStr);
		RequestQueue mQueue = Volley.newRequestQueue(mContext);
		Map<String, String> map = new HashMap<String, String>();
		map.put("userID", phoneNumStr);
		map.put("userPwd", pwd);
		map.put("flag", "1");
		JsonArrayPostRequest mJsonRequest = new JsonArrayPostRequest(
				Constants.UPDATE_USER_PWD_URL, new Listener<JSONArray>() {

					@Override
					public void onResponse(JSONArray response) {
						if (response.length() > 0) {
							try {
								String obj = response.get(0).toString();
								if (null != obj && obj.length() > 0
										&& obj.equals("success")) {
									mTimer.cancel();
									count = 0;
									Toast.makeText(mContext, "密码更改成功", Toast.LENGTH_SHORT)
											.show();
									find.setEnabled(true);
									Intent intent = new Intent();
									intent.setAction(Constants.Action.KILL_MAIN_ACTION);
									sendBroadcast(intent);
									SharedPreferencesManager.getInstance()
											.putData(mContext,
													Constants.UserInfo.USER_ID,
													phoneNumStr);
									SharedPreferencesManager.getInstance()
											.putData(mContext,
													Constants.UserInfo.PWD,
													pwdNumStr);
									Intent i = new Intent(
											PhoneFindPwdActivity.this,
											SplashActivity.class);
									startActivity(i);
									finish();
								}
								if (null != obj && obj.length() > 0
										&& obj.equals("false")) {
									Toast.makeText(mContext, "找回失败，请重新找回密码", Toast.LENGTH_SHORT)
											.show();
									mTimer.cancel();
									count = 0;
									
								}
								if (null != obj && obj.length() > 0
										&& obj.equals("userenotxit")) {
									Toast.makeText(mContext, "用户不存在", Toast.LENGTH_SHORT).show();
								}
								find.setEnabled(true);
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								find.setEnabled(true);
							}
						}
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						mTimer.cancel();
						count = 0;
						find.setEnabled(true);
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
			getCode.setText(count + "" + "(s)");
			if (count == 60) {
				mTimer.cancel();
				count = 0;
				phoneEt.setEnabled(true);
				phonePwd.setEnabled(true);
				phonePwd1.setEnabled(true);
				getCode.setEnabled(true);
				find.setEnabled(true);
				getCode.setText("获取验证码");
			}
		};
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
									Toast.makeText(mContext, "该手机号不存在", Toast.LENGTH_SHORT)
											.show();
								}
								if (null != obj && obj.length() > 0
										&& obj.equals("yes")) {
									SMSSDK.getVerificationCode("86", phoneNum);
									getCode.setEnabled(false);
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

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		SMSSDK.unregisterAllEventHandler();
		super.onDestroy();
		unregisterReceiver(br);
	}
}
