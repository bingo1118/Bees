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
import android.widget.TextView;

public class SettingActivity extends Activity implements OnClickListener {
	private TextView logout, comeBack;
	private Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		mContext = this;
		Intent mIntent = new Intent();
		mIntent.setAction(Constants.Action.CLOSE_SLIDE_MENU);
		mContext.sendBroadcast(mIntent);
		init();
	}

	private void init() {
		logout = (TextView) findViewById(R.id.logout);
		comeBack = (TextView) findViewById(R.id.come_back);
		logout.setOnClickListener(this);
		comeBack.setOnClickListener(this);
		Intent i = new Intent();
		i.setAction(Constants.Action.CLOSE_SLIDE_MENU);
		mContext.sendBroadcast(i);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.logout:
			SharedPreferencesManager.getInstance().putData(mContext,
					Constants.UserInfo.PWD, "");
			Intent intent = new Intent();
			intent.setAction(Constants.Action.KILL_MAIN_ACTION);
			sendBroadcast(intent);
			Intent i = new Intent(SettingActivity.this, SplashActivity.class);
			startActivity(i);
			finish();
			break;
		case R.id.come_back:

			finish();
			break;
		default:
			break;
		}
	}
}
