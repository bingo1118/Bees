package com.hrsst.smarthome.activity;

import java.io.File;

import com.hrsst.smarthome.global.Constants;
import com.hrsst.smarthome.util.UpdateManager;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

public class ForwardDownActivity extends Activity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		int state = this.getIntent().getIntExtra("state", -1);
		System.out.println("state====f======"+state);
    	switch(state){
		case UpdateManager.HANDLE_MSG_DOWN_SUCCESS:
			Intent intent = new Intent(Intent.ACTION_VIEW);
			File file = new File(Environment.getExternalStorageDirectory()+"/"+Constants.Update.SAVE_PATH+"/"+Constants.Update.FILE_NAME);
			if(!file.exists()){
				return;
			}
			intent.setDataAndType(Uri.fromFile(file), Constants.Update.INSTALL_APK);
			this.startActivity(intent);
			break;
		case UpdateManager.HANDLE_MSG_DOWNING:
			UpdateManager.getInstance().cancelDown();
			break;
		case UpdateManager.HANDLE_MSG_DOWN_FAULT:
			
			break;
		}
		finish();
	}
}
