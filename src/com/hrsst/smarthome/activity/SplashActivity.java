package com.hrsst.smarthome.activity;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hrsst.smarthome.R;
import com.hrsst.smarthome.global.Constants;
import com.hrsst.smarthome.global.MyApp;
import com.hrsst.smarthome.global.SetTagService;
import com.hrsst.smarthome.net.SocketUDP;
import com.hrsst.smarthome.order.SendServerOrder;
import com.hrsst.smarthome.order.UnPackServer;
import com.hrsst.smarthome.pojo.UnPackageFromServer;
import com.hrsst.smarthome.pojo.User;
import com.hrsst.smarthome.thread.LoginService;
import com.hrsst.smarthome.util.CheakEmail;
import com.hrsst.smarthome.util.MD5;
import com.hrsst.smarthome.util.NetworkUtil;
import com.hrsst.smarthome.util.SharedPreferencesManager;
import com.hrsst.smarthome.volley.JsonArrayPostRequest;
import com.igexin.sdk.PushManager;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class SplashActivity extends Activity{
	private Context mContext;
	private String userId;
	private String pwd;
	Handler handler;
	private static SocketUDP mSocketUDPClient;
	private Timer mTimer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		mContext = this;
		userId = SharedPreferencesManager.getInstance().getData(mContext, Constants.UserInfo.USER_ID);
		pwd = SharedPreferencesManager.getInstance().getData(mContext, Constants.UserInfo.PWD);
		boolean connected = NetworkUtil.isConnected(mContext);
		regFilter();
		if(connected){
			handler = new Handler(){
				@Override
				public void handleMessage(Message msg) {
					// TODO Auto-generated method stub
					super.handleMessage(msg);
					autoLogin(userId,pwd);
//					LoginHttp(userId,pwd);
				}
			};
			Message msg = new Message();
			msg.what = 0x11;
			handler.sendMessageDelayed(msg, 1000);
			if(null==mSocketUDPClient){
				mSocketUDPClient = SocketUDP.newInstance(Constants.SeverInfo.SERVER
						, Constants.SeverInfo.PORT);
				mSocketUDPClient.startAcceptMessage();
			}
		}else{
			Toast.makeText(mContext, R.string.please_connect_network, Toast.LENGTH_SHORT).show();
			Intent i2 = new Intent(SplashActivity.this,LoginActivity.class);
			i2.putExtra("ifLogin", "no");
			startActivity(i2);
			finish();
		}
		
	}
	
	public void regFilter() {
		IntentFilter filter = new IntentFilter();
		filter.addAction("Constants.Action.unLoginUser");
		mContext.registerReceiver(mReceiver, filter);
	}
	
	private BroadcastReceiver mReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			if (arg1.getAction().equals("Constants.Action.unLoginUser")) {
				byte[] datas = arg1.getExtras().getByteArray("datasByte");
				UnPackageFromServer mUnPackageFromServer = new UnPackServer().unLoginUser(datas);
				User mUser = mUnPackageFromServer.mUser;
				int result = mUser.getIfLogin();
				switch (result) {
				case 1:
					String s = SharedPreferencesManager.getInstance().getData(
							mContext,
							Constants.UserInfo.USER_NUMBER);
					SharedPreferencesManager
							.getInstance()
							.putData(
							mContext,
							Constants.UserInfo.USER_NUMBER,
							mUser.getUserNum());
					SharedPreferencesManager
							.getInstance()
							.putData(mContext,Constants.UserInfo.SESSION_ID,mUser.getSessionId());
					String setTag = SharedPreferencesManager.getInstance().getData(mContext, Constants.UserInfo.SETTAG);
					if(setTag==null){
						setTag="false";
					}
					
					if (!s.equals(mUser.getUserNum())||!setTag.equals("true")) {
						PushManager.getInstance().initialize(getApplicationContext());
						boolean resultB = PushManager.getInstance().bindAlias(
								SplashActivity.this, mUser.getUserNum());
						if(resultB==false){
							Intent setTagIntent = new Intent(mContext,SetTagService.class);
							setTagIntent.putExtra("UserNum", mUser.getUserNum());
			        		startService(setTagIntent);
						}else{
							SharedPreferencesManager
							.getInstance()
							.putData(mContext,Constants.UserInfo.SETTAG,"true");
						}
					}
					String cid = SharedPreferencesManager.getInstance().getData(mContext,
				                SharedPreferencesManager.SP_FILE_GWELL,
				                SharedPreferencesManager.CID);
					if(cid!=null&&cid.length()>0){
						register(mUser.getUserNum(),cid);
					}
					String cameraUser = mUser.getCameraUser();
					String cameraEmail = mUser.getCameraEmail();
					Log.i("login","server...");
					if(null!=cameraUser&&cameraUser.length()>0&&null!=cameraEmail&&cameraEmail.length()>0){
						Intent inService = new Intent(mContext,LoginService.class);
						inService.putExtra("cameraEmail", cameraEmail);
						inService.setAction("com.hrsst.smarthome.thread.LoginService");
						mContext.startService(inService);
					}

					if(mTimer!=null){
						mTimer.cancel();
						mTimer=null;
					}
					count=0;
					Intent i = new Intent(SplashActivity.this,MainActivity.class);
					i.putExtra("ifLogin", "yes");
					i.putExtra("YooLogin", "no");
					startActivity(i);
					finish();

					break;
				case 2:
					if(mTimer!=null){
						mTimer.cancel();
						mTimer=null;
					}
					count=0;
					Toast.makeText(mContext, R.string.please_input_right_user_name_and_psw, Toast.LENGTH_SHORT).show();
					Intent i2 = new Intent(SplashActivity.this,LoginActivity.class);
					i2.putExtra("ifLogin", "no");
					startActivity(i2);
					finish();
					break;
				case 3:
					
					break;
				default:
					break;
				}
				
			}
		}
	};
	
	private void autoLogin(String userID,String password){
		if(null!=userID&&userID.length()>0&&null!=password&&password.length()>0){
			mTimer = new Timer();//@@
			setTimerdoAction(doAction,mTimer);//@@
			boolean result = CheakEmail.getInstance().cheakEmail(userID);
			if (true == result) {
				byte type=0x02;
//				login(userID, password, type);
				LoginHttp(userID,password,type);//@@
			}
			if(userID.startsWith("0")){
				byte type=0x01;
//				login(userID, password, type);
				LoginHttp(userID,password,type);//@@
			}
			if(userID.startsWith("1")&&userID.length()==11){
				byte type=0x03;
//				login(userID, password, type);
				LoginHttp(userID,password,type);//@@
			}
		}else{
			Intent i = new Intent(SplashActivity.this,MainActivity.class);
			i.putExtra("ifLogin", "no");
			startActivity(i);
			finish();
		}
	}
	
	private void login(String phoneNum, String phonePwd, byte type) {
		mTimer = new Timer();
		setTimerdoAction(doAction,mTimer);
		String pwd = new MD5().getMD5ofStr(phonePwd);
		byte[] datas = SendServerOrder.LoginUser(phoneNum, pwd, type);
		mSocketUDPClient.sendMsg(datas);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(mReceiver);	
		super.onDestroy();
	}

	private int count=0;
	private void setTimerdoAction(final Handler oj,Timer t) { 
        t.schedule(new TimerTask() {  
            @Override  
            public void run() {
            	count = count+1;
            	Message message = new Message();
            	if(count>10){
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
				if(mTimer!=null){
					mTimer.cancel();
					mTimer=null;
				}
				count=0;
				Intent i2 = new Intent(SplashActivity.this,LoginActivity.class);
				i2.putExtra("ifLogin", "no");
				startActivity(i2);
				Toast.makeText(mContext, R.string.login_timeout, Toast.LENGTH_SHORT).show();
				finish();
				break;
			default:
				break;
			}
		}
	};
	
	private void register(final String alias, final String cid) {
		RequestQueue mQueue = Volley.newRequestQueue(mContext);
		Map<String, String> map = new HashMap<String, String>();
		map.put("alias", alias);
		map.put("cid", cid);
		map.put("projectName", "bees");
		JsonArrayPostRequest mJsonRequest = new JsonArrayPostRequest(
				Constants.BIND_ALIAS_URL, new Listener<JSONArray>() {

					@Override
					public void onResponse(JSONArray response) {
						
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
					}
				}, map);
		mQueue.add(mJsonRequest);
	}
	
	//登陆http方式@@
	private void LoginHttp(String username,String psw, byte type) {
		String cid = SharedPreferencesManager.getInstance().getData(mContext,
                SharedPreferencesManager.SP_FILE_GWELL,
                SharedPreferencesManager.CID);
		String url=Constants.LOGINHTTP
						+"?userNum="+username
						+"&pwd="+new MD5().getMD5ofStr(psw)
						+"&type="+type
						+"&geTuiClientId="+cid
						+"&token=&appType=1";
		RequestQueue mQueue = Volley.newRequestQueue(mContext);
		StringRequest mJsonRequest = new StringRequest(
				url, 
				new Listener<String>() {
					@Override
					public void onResponse(String response) {
						try {
							JSONObject object=new JSONObject(response);
							int result = object.getInt("loginResult");
							switch (result) {
							case 3:
								String s = SharedPreferencesManager.getInstance().getData(
										mContext,
										Constants.UserInfo.USER_NUMBER);
								SharedPreferencesManager
										.getInstance()
										.putData(
										mContext,
										Constants.UserInfo.USER_NUMBER,
										object.getString("userNum"));//存储用户id
								SharedPreferencesManager
								.getInstance()
								.putData(
								mContext,
								"userPhone",
								object.getString("userPhone"));//存储用户手机号
								SharedPreferencesManager
								.getInstance()
								.putData(
								mContext,
								"userEmail",
								object.getString("userEmail"));//存储用户邮箱
								SharedPreferencesManager
										.getInstance()
										.putData(mContext,Constants.UserInfo.SESSION_ID,object.getString("nowTime"));
								String setTag = SharedPreferencesManager.getInstance().getData(mContext, Constants.UserInfo.SETTAG);
								if(setTag==null){
									setTag="false";
								}
								
								if (!s.equals(object.getString("userNum"))||!setTag.equals("true")) {
									PushManager.getInstance().initialize(getApplicationContext());
									boolean resultB = PushManager.getInstance().bindAlias(
											SplashActivity.this, object.getString("userNum"));
									if(resultB==false){
										Intent setTagIntent = new Intent(mContext,SetTagService.class);
										setTagIntent.putExtra("UserNum", object.getString("userNum"));
						        		startService(setTagIntent);
									}else{
										SharedPreferencesManager
										.getInstance()
										.putData(mContext,Constants.UserInfo.SETTAG,"true");
									}
								}
								String cid = SharedPreferencesManager.getInstance().getData(mContext,
							                SharedPreferencesManager.SP_FILE_GWELL,
							                SharedPreferencesManager.CID);
								if(cid!=null&&cid.length()>0){
									register(object.getString("userNum"),cid);
								}
								String cameraUser = object.getString("cameraUser");
								String cameraEmail = object.getString("cameraEmail");
								Log.i("login","server...");
								if(null!=cameraUser&&cameraUser.length()>0&&null!=cameraEmail&&cameraEmail.length()>0){
									Intent inService = new Intent(mContext,LoginService.class);
									inService.putExtra("cameraEmail", cameraEmail);
									inService.setAction("com.hrsst.smarthome.thread.LoginService");
									mContext.startService(inService);
								}

								if(mTimer!=null){
									mTimer.cancel();
									mTimer=null;
								}
								count=0;
								Intent i = new Intent(SplashActivity.this,MainActivity.class);
								i.putExtra("ifLogin", "yes");
								i.putExtra("YooLogin", "no");
								startActivity(i);
								finish();

								break;
							case 2:
								if(mTimer!=null){
									mTimer.cancel();
									mTimer=null;
								}
								count=0;
								Toast.makeText(mContext, R.string.please_input_right_user_name_and_psw, Toast.LENGTH_SHORT).show();
								Intent i2 = new Intent(SplashActivity.this,LoginActivity.class);
								i2.putExtra("ifLogin", "no");
								startActivity(i2);
								finish();
								break;
							case 1:
								if(mTimer!=null){//@@
									mTimer.cancel();
									mTimer=null;
								}
								count=0;//@@
								Toast.makeText(mContext, R.string.user_on_exist, Toast.LENGTH_SHORT).show();
								Intent i3 = new Intent(SplashActivity.this,LoginActivity.class);//@@
								i3.putExtra("ifLogin", "no");//@@
								startActivity(i3);//@@
								finish();//@@
								break;
							default:
								break;
							}
						} catch (JSONException e) {
							// TODO 自动生成的 catch 块
							e.printStackTrace();
						}
						
						
					}

					
				}, 
				new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Toast.makeText(mContext, R.string.error, Toast.LENGTH_SHORT).show();
					}
				});
		mQueue.add(mJsonRequest);
		
	}
}
