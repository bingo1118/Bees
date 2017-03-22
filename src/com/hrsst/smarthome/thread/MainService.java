package com.hrsst.smarthome.thread;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public class MainService extends Service{
	private Context context;
	private MainThread mMainThread;
	private HeartThread mHearThread;
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		context=this;
		regFilter();
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		mMainThread = new MainThread(getApplication());
		mMainThread.go();
		mHearThread = new HeartThread(getApplication());
		mHearThread.go();
	}
	
	public void regFilter() {
		IntentFilter filter = new IntentFilter();
		filter.addAction("KILL_THREAD");
		context.registerReceiver(mReceiver, filter);
	}
	
	private BroadcastReceiver mReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			if(arg1.getAction().equals("KILL_THREAD")){
				if(null!=mMainThread){
					mMainThread.kill();
				}
				if(null!=mHearThread){
					mHearThread.kill();
				}
			}
		}
	};
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(mReceiver);
		super.onDestroy();
		
	}

}
