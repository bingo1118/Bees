package com.hrsst.smarthome.thread;

import com.hrsst.smarthome.global.Constants;
import com.hrsst.smarthome.net.SocketUDP;
import com.hrsst.smarthome.order.SendServerOrder;
import com.hrsst.smarthome.order.UnPackServer;
import com.hrsst.smarthome.pojo.UnPackageFromServer;
import com.hrsst.smarthome.util.SharedPreferencesManager;
import com.hrsst.smarthome.util.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

public class HeartThread {
	private Context mContext;
	private static SocketUDP mSocketUDPClient;
	private boolean isRun;
	private MainHeartThread mMainHeartThread;
	
	public HeartThread(Context mContext) {
		this.mContext = mContext;
		if(null==mSocketUDPClient){
			mSocketUDPClient = SocketUDP.newInstance(Constants.SeverInfo.SERVER
					, Constants.SeverInfo.PORT);
			mSocketUDPClient.startAcceptMessage();
		}
		regFilter();
	}
	
	class MainHeartThread extends Thread {
		@Override
		public void run() {
			String userId = SharedPreferencesManager.getInstance().getData(
					mContext,
					Constants.UserInfo.USER_NUMBER);
			isRun = true;
			while (isRun) {
				Utils.sleepThread(3 * 1000);
				String sessionId = SharedPreferencesManager.getInstance().getData(
						mContext, 
						Constants.UserInfo.SESSION_ID);
				if(null!=sessionId&&sessionId.length()>0&&null!=userId&&userId.length()>0){
					Log.v("SESSION_ID_HEART", sessionId);
					byte[] datas = SendServerOrder.HearPackage(userId, sessionId);
					mSocketUDPClient.sendMsg(datas);
					Utils.sleepThread(30 * 1000);
				}
			}
		}
	}
	
	public void regFilter() {
		IntentFilter filter = new IntentFilter();
		filter.addAction("Constants.Action.unHearPackage");
		mContext.registerReceiver(mReceiver, filter);
	}
	
	private BroadcastReceiver mReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			if(arg1.getAction().equals("Constants.Action.unHearPackage")){
				byte[] datas = arg1.getExtras().getByteArray("datasByte");
				UnPackageFromServer mUnPackageFromServer = new UnPackServer().unHeartPackage(datas);
				String result = mUnPackageFromServer.heartPackage;
				if(result.equals("offLine")){
					Intent intent1 = new Intent();
					intent1.setAction("SESSION_TIME_OUT");
					arg0.sendBroadcast(intent1);
					isRun = false;
				}
			}
		}
	};
	
	public void go() {
		if (null == mMainHeartThread || !mMainHeartThread.isAlive()) {
			mMainHeartThread = new MainHeartThread();
			mMainHeartThread.start();
		}
	}
	
	public void kill() {
		isRun = false;
		mMainHeartThread = null;
		mContext.unregisterReceiver(mReceiver);
	}
}
