package com.hrsst.smarthome.global;

import java.util.Timer;
import java.util.TimerTask;

import com.hrsst.smarthome.activity.SplashActivity;
import com.hrsst.smarthome.util.SharedPreferencesManager;
import com.igexin.sdk.PushManager;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

public class SetTagService extends Service{

	private Timer mTimer;
	private Context mContext;
	private String userNum;
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		userNum = intent.getExtras().getString("UserNum");
		if(userNum!=null){
			mTimer = new Timer();
			setTimerdoAction(doActionHandler,mTimer,2);
		}
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		mContext = getApplicationContext();
		
	}
	
	private void setTimerdoAction(final Handler oj,Timer t,final int typeInt) { 
        t.schedule(new TimerTask() {  
            @Override  
            public void run() {
	    		Message message = new Message();	
				message = oj.obtainMessage();
				message.what = typeInt; 
	    		oj.sendMessage(message);
            }  
        }, 1000, 5000/*表示1000毫秒之後，每隔1000毫秒執行一次 */);  
    } 
    
    private Handler doActionHandler = new Handler() {
		
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 2:
				boolean resultB = PushManager.getInstance().bindAlias(
						mContext, userNum);
				if(resultB){
					SharedPreferencesManager
					.getInstance()
					.putData(mContext,Constants.UserInfo.SETTAG,"true");
					mTimer.cancel();
					stopSelf();
				}
				break;
			default:
				break;
			}
		}
	};

}
