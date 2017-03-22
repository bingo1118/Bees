package com.hrsst.smarthome.thread;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;
import com.hrsst.smarthome.global.AccountPersist;
import com.hrsst.smarthome.global.Constants;
import com.hrsst.smarthome.global.NpcCommon;
import com.hrsst.smarthome.pojo.Account;
import com.hrsst.smarthome.util.SharedPreferencesManager;
import com.hrsst.smarthome.util.Utils;
import com.p2p.core.network.LoginResult;
import com.p2p.core.network.NetManager;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

public class LoginService extends Service{
	private Context mContext;
	private Timer mTimer;
	private String cameraEmail;

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		mContext = this;
		if(null!=intent){
			mTimer = new Timer();
			setTimerdoAction(doAction,mTimer);
			cameraEmail = intent.getExtras().getString("cameraEmail");
			new LoginTask(cameraEmail, "hrsst123456").execute();
		}
	}
	
	class LoginTask extends AsyncTask {
		String username;
		String password;

		public LoginTask(String username, String password) {
			this.username = username;
			this.password = password;
		}

		@Override
		protected Object doInBackground(Object... params) {
			// TODO Auto-generated method stub
			return NetManager.getInstance(mContext).login(username, password);
		}

		@Override
		protected void onPostExecute(Object object) {
			// TODO Auto-generated method stub
			LoginResult result = NetManager
					.createLoginResult((JSONObject) object);
			switch (Integer.parseInt(result.error_code)) {
			case NetManager.CONNECT_CHANGE:
				new LoginTask(username, password).execute();
				return;
			case NetManager.LOGIN_SUCCESS:
				Intent ii = new Intent();
				ii.setAction("START_P2P_ACTION");
				sendBroadcast(ii);
				String userID = result.contactId;
				SharedPreferencesManager.getInstance().putData(mContext,
						SharedPreferencesManager.SP_FILE_GWELL,
						SharedPreferencesManager.KEY_RECENTNAME_EMAIL,
						userID);
				SharedPreferencesManager.getInstance().putData(mContext,
						SharedPreferencesManager.SP_FILE_GWELL,
						SharedPreferencesManager.KEY_RECENTPASS_EMAIL,
						"hrsst123456");
				SharedPreferencesManager.getInstance().putRecentLoginType(
						mContext, Constants.LoginType.EMAIL);

				String codeStr1 = String.valueOf(Long.parseLong(result.rCode1));
				String codeStr2 = String.valueOf(Long.parseLong(result.rCode2));
				Account account = AccountPersist.getInstance()
						.getActiveAccountInfo(mContext);

				if (null == account) {
					account = new Account();
				}
				account.three_number = result.contactId;
				account.phone = result.phone;
				account.email = result.email;
				account.sessionId = result.sessionId;
				account.rCode1 = codeStr1;
				account.rCode2 = codeStr2;
				account.countryCode = result.countryCode;
				AccountPersist.getInstance()
						.setActiveAccount(mContext, account);
				NpcCommon.mThreeNum = AccountPersist.getInstance()
						.getActiveAccountInfo(mContext).three_number;
				if(mTimer!=null){
					mTimer.cancel();
					mTimer=null;
				}
				Utils.sleepThread(500);
				Intent i = new Intent();
				i.setAction("REFRESH_DEV_INFO");
				sendBroadcast(i);
				stopSelf();
				break;
			}
		}
	}
	
	private int count=0;
	private void setTimerdoAction(final Handler oj,Timer t) { 
        t.schedule(new TimerTask() {  
            @Override  
            public void run() {
            	count = count+1;
            	Message message = new Message();
            	if(count>30){
            		message = oj.obtainMessage();
        			message.what = 1; 
            		oj.sendMessage(message);
            	}
            }  
        }, 1000, 1000/* 表示1000毫秒之後，每隔1000毫秒執行一次 */);  
    } 
	
	private Handler doAction = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			int messsageId = msg.what;
			switch (messsageId) {
			case 1:
				new LoginTask(cameraEmail, "hrsst123456").execute();
				break;
			default:
				break;
			}
		}
	};
}
