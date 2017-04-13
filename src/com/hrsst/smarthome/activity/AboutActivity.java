package com.hrsst.smarthome.activity;

import com.hrsst.smarthome.R;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.widget.TextView;

public class AboutActivity extends Activity{
	private Context mContext;
	private TextView about_version;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		mContext = this;
		about_version = (TextView) findViewById(R.id.about_version);
		String version =getlocalVersion();
		about_version.setText(version);
	}
	
	private String getlocalVersion(){
 		String localversion =null;
 		try {
 			PackageInfo info = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
 			localversion = info.versionName;
 		} catch (NameNotFoundException e) {
 			e.printStackTrace();
 		}
 		return localversion;
 	}
}
