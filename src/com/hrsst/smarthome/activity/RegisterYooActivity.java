package com.hrsst.smarthome.activity;


import java.io.Serializable;
import java.util.List;

import org.json.JSONObject;

import com.hrsst.smarthome.R;
import com.hrsst.smarthome.global.AccountPersist;
import com.hrsst.smarthome.global.Constants;
import com.hrsst.smarthome.global.NpcCommon;
import com.hrsst.smarthome.net.SocketUDP;
import com.hrsst.smarthome.order.SendServerOrder;
import com.hrsst.smarthome.order.UnPackServer;
import com.hrsst.smarthome.pojo.Account;
import com.hrsst.smarthome.pojo.UserDevice;
import com.hrsst.smarthome.util.BitmapCache;
import com.hrsst.smarthome.util.SharedPreferencesManager;
import com.hrsst.smarthome.util.Utils;
import com.hrsst.smarthome.widget.NormalDialog;
import com.p2p.core.network.LoginResult;
import com.p2p.core.network.NetManager;
import com.p2p.core.network.RegisterResult;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class RegisterYooActivity extends Activity {
	private Context mContext;
	private EditText register_yoo_tv;
	private Button register_yoo_btn;
	private SocketUDP mSocketUDPClient;
	private String userId,message;
	private String emailStr;
	private ImageView binder_camera_user_image;
	private NormalDialog dialog_loading;
	private List<String> cameraList;
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_yoo);
		mContext= this;
		cameraList = (List<String>) getIntent().getSerializableExtra(
				"cameraList");
		userId = getIntent().getExtras().getString("userId");
		message = getIntent().getExtras().getString("message");
		init();
		regFilter();
		mSocketUDPClient = SocketUDP.newInstance(Constants.SeverInfo.SERVER
				, Constants.SeverInfo.PORT);
		mSocketUDPClient.startAcceptMessage();
	}
	
	public void regFilter() {
		IntentFilter filter = new IntentFilter();
		filter.addAction("Constants.Action.ifBinderInyoo");
		mContext.registerReceiver(mReceiver, filter);
	}
	
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			if(arg1.getAction().equals("Constants.Action.ifBinderInyoo")){
				byte[] datas = arg1.getExtras().getByteArray("datasByte");
				if(datas!=null&&datas.length>0){
					new UnPackServer();
					String result = UnPackServer.ifBinderInyoo(datas);
					switch (result) {
					case "success":
						new LoginTask(emailStr, "hrsst123456").execute();//注册成功后登陆
						break;
					case "failed":
						if(null!=dialog_loading){
							dialog_loading.dismiss();
							dialog_loading = null;
						}
						Toast.makeText(mContext, "失败", Toast.LENGTH_SHORT).show();
						break;
					case "false":
						if(null!=dialog_loading){
							dialog_loading.dismiss();
							dialog_loading = null;
						}
						Toast.makeText(mContext, "CRC错误", Toast.LENGTH_SHORT).show();
						break;
					default:
						break;
					}
				}
			}
		}
		
	};

	@SuppressLint("NewApi")
	private void init() {
		// TODO Auto-generated method stub
		binder_camera_user_image = (ImageView) findViewById(R.id.binder_camera_user_image);
		Bitmap mBitmap = BitmapCache.getInstance().getBitmap(R.drawable.binder_youxiang,mContext);
		BitmapDrawable bd = new BitmapDrawable(mContext.getResources(), mBitmap);
		binder_camera_user_image.setBackground(bd);
		register_yoo_tv = (EditText) findViewById(R.id.register_yoo_tv);
		register_yoo_btn = (Button) findViewById(R.id.register_yoo_btn);
		register_yoo_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				emailStr = register_yoo_tv.getText().toString().trim();
				new RegisterTask("1",emailStr,null,"","hrsst123456","hrsst123456","","1").execute();
				showDialog();
			}
		});
	}
	
	private void showDialog(){
		if(null==dialog_loading){
			dialog_loading = new NormalDialog(mContext,
					mContext.getResources().getString(R.string.bindering),
					"","","");
			dialog_loading.setStyle(NormalDialog.DIALOG_STYLE_LOADING);
		}
		dialog_loading.showDialog();
	}
	
	class RegisterTask extends AsyncTask{
		String VersionFlag;
		String Email;
		String CountryCode;
		String PhoneNO;
		String Pwd;
		String RePwd;
		String VerifyCode;
		String IgnoreSafeWarning;
		public RegisterTask(
				String VersionFlag,
				String Email,
				String CountryCode,
				String PhoneNO,
				String Pwd,
				String RePwd,
				String VerifyCode,
				String IgnoreSafeWarning){
			this.VersionFlag = VersionFlag;
			this.Email = Email;
			this.CountryCode = CountryCode;
			this.PhoneNO = PhoneNO;
			this.Pwd = Pwd;
			this.RePwd = RePwd;
			this.VerifyCode = VerifyCode;
			this.IgnoreSafeWarning = IgnoreSafeWarning;
		}
		@Override
		protected Object doInBackground(Object... params) {
			// TODO Auto-generated method stub
			Utils.sleepThread(1000);
			return NetManager.getInstance(mContext).register(
					VersionFlag,
					Email,
					CountryCode,
					PhoneNO,
					Pwd,
					RePwd,
					VerifyCode,
					IgnoreSafeWarning);
		}
		
		@Override
		protected void onPostExecute(Object object) {
			// TODO Auto-generated method stub
			RegisterResult result = NetManager.createRegisterResult((JSONObject)object);
			switch(Integer.parseInt(result.error_code)){
			case NetManager.SESSION_ID_ERROR:
				
				break;
			case NetManager.CONNECT_CHANGE:
				new RegisterTask(VersionFlag,Email,CountryCode,PhoneNO,Pwd,RePwd,VerifyCode,IgnoreSafeWarning).execute();
				return;
			case NetManager.REGISTER_SUCCESS:
				String userID = "0"+String.valueOf(Integer.parseInt(result.contactId)& 0x7fffffff);
				byte[] orderSend =SendServerOrder.ifBinderInyoo(userId, userID, emailStr);
				mSocketUDPClient.sendMsg(orderSend);
				break;
			case NetManager.REGISTER_EMAIL_USED:
				Toast.makeText(mContext, "该邮箱已被绑定", Toast.LENGTH_SHORT).show();
				if(null!=dialog_loading){
					dialog_loading.dismiss();
					dialog_loading = null;
				}
				break;
			case NetManager.REGISTER_EMAIL_FORMAT_ERROR:
				Toast.makeText(mContext, "邮箱格式不对", Toast.LENGTH_SHORT).show();
				if(null!=dialog_loading){
					dialog_loading.dismiss();
					dialog_loading = null;
				}
				break;
			case NetManager.REGISTER_PASSWORD_NO_MATCH:

				break;	

			default:
				break;
			}
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
			Utils.sleepThread(1000);
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
				}else{
					userId = account.three_number;
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
				Toast.makeText(mContext, "绑定成功", Toast.LENGTH_SHORT).show();
				//登陆成功后跳转到添加摄像头步骤
				Intent ii = new Intent();
				ii.setAction("START_P2P_ACTION");
				sendBroadcast(ii);
				if(null!=dialog_loading){
					dialog_loading.dismiss();
					dialog_loading = null;
				}
				if(message.equals("yes")){
					finish();
				}else{
					Intent i =new Intent(mContext,AddCameraFirstActivity.class);
					i.putExtra("cameraList",
							(Serializable) cameraList);
					startActivity(i);
					finish();
				}
				break;
			}
		}

	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(mReceiver);
		super.onDestroy();
		
	}
}
