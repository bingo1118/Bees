package com.hrsst.smarthome.activity;

import com.hrsst.smarthome.R;
import com.hrsst.smarthome.global.Constants;
import com.hrsst.smarthome.util.BitmapCache;
import com.hrsst.smarthome.util.SharedPreferencesManager;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity implements OnClickListener {
	private Button loginBtn;
	private EditText userID, userPwd;
	private TextView registerUser;
	private TextView forgetPwd;
	private Context mContext;
	private String userIDStr, userPwdStr;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login1);
		mContext = this;
		init();
		regFilter();
	}

	public void init() {
		Bitmap mBitmap = BitmapCache.getInstance().getBitmap(R.drawable.denglu_beijing,mContext);
		BitmapDrawable bd = new BitmapDrawable(mContext.getResources(), mBitmap);
		ImageView denglu_image = (ImageView) findViewById(R.id.denglu_image);
		denglu_image.setImageDrawable(bd);
		String nameStr = SharedPreferencesManager.getInstance().getData(
				mContext, Constants.UserInfo.USER_ID);
		String pwdStr = SharedPreferencesManager.getInstance().getData(
				mContext, Constants.UserInfo.PWD);
		loginBtn = (Button) findViewById(R.id.btn_login);
		userID = (EditText) findViewById(R.id.user_id);
		userPwd = (EditText) findViewById(R.id.user_pwd);
		registerUser = (TextView) findViewById(R.id.tv_new_user);
		forgetPwd = (TextView) findViewById(R.id.tv_forget_pwd);
		userID.setText(nameStr);
		userID.setSelection(nameStr.length());//@@
		userPwd.setText(pwdStr);
		loginBtn.setOnClickListener(this);
		registerUser.setOnClickListener(this);
		forgetPwd.setOnClickListener(this);
	}

	public void regFilter() {
		IntentFilter filter = new IntentFilter();
		filter.addAction("PHONE_REGISTER_SUCCESS");
		filter.addAction("EMAIL_REGISTER_SUCCESS");
		mContext.registerReceiver(br, filter);
	}

	BroadcastReceiver br = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (intent.getAction().equals("PHONE_REGISTER_SUCCESS")) {
				userIDStr = intent.getExtras().getString("userID");
				userPwdStr = intent.getExtras().getString("userPwd");
				userID.setText(userIDStr);
				userPwd.setText(userPwdStr);
				login(userIDStr, userPwdStr);
			}
			
			if (intent.getAction().equals("EMAIL_REGISTER_SUCCESS")) {
				userIDStr = intent.getExtras().getString("userID");
				userPwdStr = intent.getExtras().getString("userPwd");
				userID.setText(userIDStr);
				userPwd.setText(userPwdStr);
				login(userIDStr, userPwdStr);
			}
		}
	};

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		userIDStr = userID.getText().toString().trim();
		userPwdStr = userPwd.getText().toString().trim();
		switch (view.getId()) {
		case R.id.btn_login:
			if (null != userIDStr && userIDStr.length() > 0
					&& null != userPwdStr && userPwdStr.length() > 0) {
				login(userIDStr,userPwdStr);
			} else {
				Toast.makeText(mContext, "请输入正确的用户名和密码", Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.tv_new_user:
			Intent intent = new Intent(this, ChoiceRegisterActivity.class);
			intent.putExtra("actionFlag", "Register");
			startActivity(intent);
			break;
		case R.id.tv_forget_pwd:
			Intent i = new Intent(this, ChoiceRegisterActivity.class);
			i.putExtra("actionFlag", "Reset");
			startActivity(i);
			break;
		default:
			break;
		}
	}
	
	private void login(String u,String p){
		Intent intent1 = new Intent();
		intent1.setAction("KILL_MAIN_ACTION");
		sendBroadcast(intent1);
		SharedPreferencesManager.getInstance()
		.putData(mContext,
				Constants.UserInfo.USER_ID,
				u);
		SharedPreferencesManager.getInstance()
				.putData(mContext,
						Constants.UserInfo.PWD,
						p);
		Intent i1 = new Intent(LoginActivity.this,
				SplashActivity.class);
		startActivity(i1);
		finish();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(br);
		BitmapCache.getInstance().clearCache();
	}

}
