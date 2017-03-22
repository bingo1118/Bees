package com.hrsst.smarthome.thread;

import com.hrsst.smarthome.activity.ForwardDownActivity;
import com.hrsst.smarthome.util.UpdateManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationClickReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Intent newIntent = new Intent(context, ForwardDownActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		newIntent.putExtra("state", UpdateManager.HANDLE_MSG_DOWN_FAULT);
		context.startActivity(newIntent);
	}
	
}
