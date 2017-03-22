package com.hrsst.smarthome.activity;

import com.hrsst.smarthome.R;
import com.hrsst.smarthome.global.Constants;
import com.hrsst.smarthome.util.SharedPreferencesManager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class EmailActiviteActivity extends Activity{
	private Button activityBtn;
	private String userId,userPwd;
	private Context mContext;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_email_activate);
		mContext = this;
		userId = getIntent().getExtras().getString("userID");
		userPwd = getIntent().getExtras().getString("userPwd");
		activityBtn = (Button) findViewById(R.id.email_activite_btn);
		activityBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				SharedPreferencesManager.getInstance().putData(mContext, Constants.UserInfo.USER_ID, userId);
				SharedPreferencesManager.getInstance().putData(mContext, Constants.UserInfo.PWD, userPwd);
				Intent intent = new Intent();
				intent.setAction("EMAIL_REGISTER_SUCCESS");
				intent.putExtra("userID", userId);
				intent.putExtra("userPwd", userPwd);
				sendBroadcast(intent);
				finish();
			}
		});
	}
}
