package com.hrsst.smarthome.activity;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import com.hrsst.smarthome.R;
import com.hrsst.smarthome.fragment.ExperienceFragment;
import com.hrsst.smarthome.fragment.MessagesFragment;
import com.hrsst.smarthome.fragment.MyDeviceFragment;
import com.hrsst.smarthome.fragment.PersonerFragment;
import com.hrsst.smarthome.global.Constants;
import com.hrsst.smarthome.global.MyApp;
import com.hrsst.smarthome.global.PushInitService;
import com.hrsst.smarthome.global.SetTagService;
import com.hrsst.smarthome.net.P2PListener;
import com.hrsst.smarthome.net.SettingListener;
import com.hrsst.smarthome.thread.MainService;
import com.hrsst.smarthome.thread.MainThread;
import com.hrsst.smarthome.util.SharedPreferencesManager;
import com.hrsst.smarthome.util.UpdateManager;
import com.igexin.sdk.PushManager;
import com.p2p.core.P2PHandler;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.FrameLayout.LayoutParams;

public class MainActivity extends Activity implements OnClickListener{
	private Context mContext;
	
	private int currFrag = 0;
	private String ifLogin;
	private LinearLayout tab_component;
	private MyDeviceFragment mMyDeviceFragment;
	private ExperienceFragment mExperienceFragment;
	private PersonerFragment mPersonerFragment;
	private MessagesFragment mMessagesFragment;
	private String[] fragTags = new String[] { "mMyDeviceFragment", "mExperienceFragment",
			"mPersonerFragment", "mMessagesFragment"};
	private TextView tv_contact, tv_message, tv_image, tv_more,mailAdr,user_num,menu_one,menu_two,menu_five,menu_four;
	private AlertDialog dialog_update;
	private DrawerLayout drawerLayout;
	private RelativeLayout leftLayout;
	private FrameLayout fragment_layout;
	private RelativeLayout menu_title;
	private LinearLayout setting_lin;
	private Intent inService;
	private Intent service;
	private String yooLogin;
	private Intent setTagIntent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_fragment);
		ifLogin = getIntent().getExtras().getString("ifLogin");
		mContext = this;
		
		init();
		regFilter();
		if(null!=ifLogin&&ifLogin.length()>0&&ifLogin.equals("yes")){
			yooLogin = getIntent().getExtras().getString("YooLogin");
			if (null == mMyDeviceFragment) {
				mMyDeviceFragment = new MyDeviceFragment();
			}
			tab_component.setBackgroundResource(R.drawable.daohang1);
			replaceFragment(R.id.fragContainer, mMyDeviceFragment, fragTags[0]);

			inService = new Intent(mContext,MainService.class);
			inService.setAction("com.hrsst.smarthome.thread.MAINSERVICE");
			mContext.startService(inService);

			if(null!=yooLogin&&yooLogin.length()>0&&yooLogin.equals("yes")){
				//PushManager.getInstance().initialize(this.getApplicationContext());
				setTagIntent = new Intent(mContext,PushInitService.class);
        		startService(setTagIntent);
				P2PHandler.getInstance().p2pInit(this,
						new P2PListener(),
						new SettingListener());
				connect();
			}
		}else{
			drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
			if (null == mPersonerFragment) {
				mPersonerFragment = new PersonerFragment();
			}
			tab_component.setBackgroundResource(R.drawable.daohang3);
			replaceFragment(R.id.fragContainer, mPersonerFragment, fragTags[2]);
		}

	}
	
	private void connect() {
		service = new Intent(mContext,com.hrsst.smarthome.global.MainService.class);
		startService(service);
	}
	
	public void regFilter() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.Action.MAIN_ACTION);
		filter.addAction(Constants.Action.KILL_MAIN_ACTION);
		filter.addAction(Constants.Action.CLOSE_SLIDE_MENU);
		filter.addAction(Constants.Action.OPEN_SLIDE_MENU);
		filter.addAction("Constants.Action.ACTION_UPDATE");
		filter.addAction("Constants.Action.ACTION_UPDATE_NO");
		filter.addAction("SESSION_TIME_OUT");
		filter.addAction("KILL_MAIN_ACTION");
		filter.addAction("START_P2P_ACTION");
		filter.addAction("STOP_PUSH_INIT_SERVICE");
		mContext.registerReceiver(mReceiver, filter);
	}
	
	private BroadcastReceiver mReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if(intent.getAction().equals("STOP_PUSH_INIT_SERVICE")){
				if(setTagIntent!=null){
					stopService(setTagIntent);
					setTagIntent=null;
				}
			}
			if(intent.getAction().equals(Constants.Action.MAIN_ACTION)){
				if(null!=ifLogin&&ifLogin.length()>0&&ifLogin.equals("no")){
					Intent i = new Intent();
					i.setAction(Constants.Action.IF_USER_LOGIN_NO);
					sendBroadcast(i);
				}
			}
			
			if(intent.getAction().equals("START_P2P_ACTION")){
				PushManager.getInstance().initialize(getApplicationContext());
				P2PHandler.getInstance().p2pInit(mContext, new P2PListener(),
						new SettingListener());
				connect();
				System.out.println("START_P2P_ACTION...");
			}
			
			if(intent.getAction().equals(Constants.Action.OPEN_SLIDE_MENU)){
				openLeftDrawLayout(leftLayout);
			}
			
			if(intent.getAction().equals(Constants.Action.CLOSE_SLIDE_MENU)){
				drawerLayout.closeDrawer(leftLayout);
			}
			
			if(intent.getAction().equals("SESSION_TIME_OUT")){
				PushManager.getInstance().stopService(mContext);
				Toast.makeText(mContext, "会话已过期", Toast.LENGTH_SHORT).show();
				Intent i = new Intent(mContext,LoginActivity.class);
				startActivity(i);
				finish();
			}
			
			if(intent.getAction().equals(Constants.Action.KILL_MAIN_ACTION)){
				PushManager.getInstance().stopService(mContext);
				finish();
			}
			
			if(intent.getAction().equals("KILL_MAIN_ACTION")){
				finish();
			}
			
			if(intent.getAction().equals("Constants.Action.ACTION_UPDATE_NO")){
				View view = LayoutInflater.from(mContext).inflate(
						R.layout.dialog_update, null);
				TextView title = (TextView) view.findViewById(R.id.title_text);
				WebView content = (WebView) view
						.findViewById(R.id.content_text);
				TextView button1 = (TextView) view
						.findViewById(R.id.button1_text);
				TextView button2 = (TextView) view
						.findViewById(R.id.button2_text);
				ImageView minddle_image = (ImageView) view
						.findViewById(R.id.minddle_image);
				RelativeLayout cancel_rela_dialog = (RelativeLayout) view
						.findViewById(R.id.cancel_rela_dialog);
				title.setText("更新消息");
				content.setBackgroundColor(getResources().getColor(R.color.update_message)); // 设置背景色
				content.getBackground().setAlpha(255); // 设置填充透明度 范围：0-255
				String data = intent.getStringExtra("message");
				content.loadDataWithBaseURL(null, data, "text/html", "utf-8",
						null);
				minddle_image.setVisibility(View.GONE);
				cancel_rela_dialog.setVisibility(View.GONE);
				button2.setText("确定");
				button2.setTextColor(Color.BLACK);
				AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
				dialog_update = builder.create();
				dialog_update.show();
				dialog_update.setContentView(view);
				button2.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						if (null != dialog_update) {
							dialog_update.cancel();
						}
					}
				});
			}
			//Constants.Action.ACTION_UPDATE
			if(intent.getAction().equals("Constants.Action.ACTION_UPDATE")){
				if (null != dialog_update && dialog_update.isShowing()) {
					Log.e("my", "isShowing");
					return;
				}
				View view = LayoutInflater.from(mContext).inflate(
						R.layout.dialog_update, null);
				TextView title = (TextView) view.findViewById(R.id.title_text);
				WebView content = (WebView) view
						.findViewById(R.id.content_text);
				TextView button1 = (TextView) view
						.findViewById(R.id.button1_text);
				TextView button2 = (TextView) view
						.findViewById(R.id.button2_text);

				title.setText("更新");
				content.setBackgroundColor(Color.WHITE); // 设置背景色
				content.getBackground().setAlpha(100); // 设置填充透明度 范围：0-255
				String data = intent.getStringExtra("message");
				final String downloadPath = intent.getStringExtra("url");
				content.loadDataWithBaseURL(null, data, "text/html", "utf-8",
						null);
				button1.setText("立即更新");
				button2.setText("下次再说");
				button1.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (null != dialog_update) {
							dialog_update.dismiss();
							dialog_update = null;
						}
						if (UpdateManager.getInstance().getIsDowning()) {
							return;
						}
						MyApp.app.showDownNotification(
								UpdateManager.HANDLE_MSG_DOWNING, 0);
						new Thread() {
							public void run() {
								System.out.println("Thread==========");
								UpdateManager.getInstance().downloadApk(
										handler, Constants.Update.SAVE_PATH,
										Constants.Update.FILE_NAME,downloadPath);
							}
						}.start();
					}
				});
				button2.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (null != dialog_update) {
							dialog_update.cancel();
						}
					}
				});

				AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
				dialog_update = builder.create();
				dialog_update.show();
				dialog_update.setContentView(view);
				FrameLayout.LayoutParams layout = (LayoutParams) view
						.getLayoutParams();
				layout.width = (int) mContext.getResources().getDimension(
						R.dimen.update_dialog_width);
				view.setLayoutParams(layout);
				dialog_update.setCanceledOnTouchOutside(false);
				Window window = dialog_update.getWindow();
				window.setWindowAnimations(R.style.dialog_normal);
			}
		}
		
	};
	
	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.tv_contact:
			if(null!=ifLogin&&ifLogin.length()>0&&ifLogin.equals("yes")){
				currFrag = 0;
				if (null == mMyDeviceFragment) {
					mMyDeviceFragment = new MyDeviceFragment();
				}
				tab_component.setBackgroundResource(R.drawable.daohang1);
				replaceFragment(R.id.fragContainer, mMyDeviceFragment, fragTags[0]);
			}else{
				Intent intent = new Intent(mContext,LoginActivity.class);
				startActivity(intent);
				
			}
			break;
		case R.id.tv_message:
			//if(null!=ifLogin&&ifLogin.length()>0&&ifLogin.equals("yes")){
				currFrag = 1;
				if (null == mExperienceFragment) {
					mExperienceFragment = new ExperienceFragment();
				}
				tab_component.setBackgroundResource(R.drawable.daohang2);
				replaceFragment(R.id.fragContainer, mExperienceFragment, fragTags[1]);
//			}else{
//				Intent intent = new Intent(mContext,LoginActivity.class);
//				startActivity(intent);
//			}
			break;
		case R.id.tv_image:
			currFrag = 2;
			if (null == mPersonerFragment) {
				mPersonerFragment = new PersonerFragment();
			}
			tab_component.setBackgroundResource(R.drawable.daohang3);
			replaceFragment(R.id.fragContainer, mPersonerFragment, fragTags[2]);
			
			break;
		case R.id.tv_more:
			if(null!=ifLogin&&ifLogin.length()>0&&ifLogin.equals("yes")){
				currFrag = 3;
				if (null == mMessagesFragment) {
					mMessagesFragment = new MessagesFragment();
				}
				tab_component.setBackgroundResource(R.drawable.daohang4);
				replaceFragment(R.id.fragContainer, mMessagesFragment, fragTags[3]);
			}else{
				Intent intent = new Intent(mContext,LoginActivity.class);
				startActivity(intent);
				
			}
			break;
		case R.id.menu_four:
			if(drawerLayout.isDrawerOpen(leftLayout)){
				drawerLayout.closeDrawer(leftLayout);
			}
			Intent intent = new Intent(mContext,AboutActivity.class);
			mContext.startActivity(intent);
			break;
		case R.id.menu_one://消息
			if(drawerLayout.isDrawerOpen(leftLayout)){
				drawerLayout.closeDrawer(leftLayout);
			}
			String toUserNum = SharedPreferencesManager.getInstance().getData(mContext, Constants.UserInfo.USER_NUMBER);
			Intent in = new Intent(mContext,SystemMessageActivity.class);
			in.putExtra("toUserNum", toUserNum);
			mContext.startActivity(in);
			break;
		case R.id.menu_two://版本更新
			if(drawerLayout.isDrawerOpen(leftLayout)){
				drawerLayout.closeDrawer(leftLayout);
			}
			new MyTast().execute();
			
			break;
		case R.id.menu_five://注销
			SharedPreferencesManager.getInstance().putData(mContext, Constants.UserInfo.PWD, "");
			Intent intent1 = new Intent();
			intent1.setAction(Constants.Action.KILL_MAIN_ACTION);
			sendBroadcast(intent1);
			Intent i = new Intent(MainActivity.this,SplashActivity.class);
			startActivity(i);
			finish();
			break;
		default:
			break;
		}
		
	}
	
	private void init(){
		tv_contact = (TextView) findViewById(R.id.tv_contact);
		tv_message = (TextView) findViewById(R.id.tv_message);
		tv_image = (TextView) findViewById(R.id.tv_image);
		tv_more = (TextView) findViewById(R.id.tv_more);
		mailAdr = (TextView) findViewById(R.id.mailAdr);
		user_num = (TextView) findViewById(R.id.user_num);
		menu_one = (TextView) findViewById(R.id.menu_one);
		menu_two = (TextView) findViewById(R.id.menu_two);
		menu_five = (TextView) findViewById(R.id.menu_five);
		menu_four = (TextView) findViewById(R.id.menu_four);
		tab_component = (LinearLayout) findViewById(R.id.tab_component);
		setting_lin = (LinearLayout)findViewById(R.id.setting_lin);
		tv_contact.setOnClickListener(this);
		tv_message.setOnClickListener(this);
		tv_image.setOnClickListener(this);
		tv_more.setOnClickListener(this);
		menu_five.setOnClickListener(this);
		setting_lin.setOnClickListener(this);
		menu_one.setOnClickListener(this);
		menu_two.setOnClickListener(this);
		menu_four.setOnClickListener(this);
		
		drawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout);
		drawerLayout.setScrimColor(Color.argb(80, 80, 80, 80));
		leftLayout=(RelativeLayout) findViewById(R.id.left);
		fragment_layout = (FrameLayout) findViewById(R.id.fragment_layout);
		menu_title  = (RelativeLayout) findViewById(R.id.menu_title);
		menu_title.setOnClickListener(this);
		
		String mailAdrStr = SharedPreferencesManager.getInstance().getData(mContext, Constants.UserInfo.USER_NUMBER);
		String userNumStr = SharedPreferencesManager.getInstance().getData(mContext, Constants.UserInfo.USER_ID);
		mailAdr.setText(mailAdrStr);
		user_num.setText(userNumStr);
	}
	
	public void replaceFragment(int container, Fragment fragment, String tag) {
		try {
			FragmentManager manager = getFragmentManager();
			FragmentTransaction transaction = manager.beginTransaction();
			transaction.replace(R.id.fragContainer, fragment, tag);
			transaction.addToBackStack(tag);
			transaction.commit();
			manager.executePendingTransactions();
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("my", "replaceFrag error--main");
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if(null!=ifLogin&&ifLogin.length()>0&&ifLogin.equals("yes")){
			Intent i = new Intent();
			i.setAction("KILL_THREAD");
			sendBroadcast(i);
		}
		unregisterReceiver(mReceiver);
		super.onDestroy();
	}
	
	private void openLeftDrawLayout(View v){
		drawerLayout.openDrawer(v);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode == KeyEvent.KEYCODE_BACK ){
			if(drawerLayout.isDrawerOpen(leftLayout)){
				drawerLayout.closeDrawer(leftLayout);
			}else{
				exitBy2Click();
			}
	    	return true;
	    }else{
	    	return super.onKeyDown(keyCode, event);
	    }
	}
	
	/** 
	 * 双击退出函数 
	 */  
	private static Boolean isExit = false;  
	  
	private void exitBy2Click() {  
	    Timer tExit = null;  
	    if (isExit == false) {  
	        isExit = true; // 准备退出  
	        Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();  
	        tExit = new Timer();  
	        tExit.schedule(new TimerTask() {  
	            @Override  
	            public void run() {  
	                isExit = false; // 取消退出  
	            }  
	        }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务  
	  
	    } else {  
	    	moveTaskToBack(false);
	    }  
	} 
	
	Handler handler = new Handler() {
		long last_time;

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			int value = msg.arg1;
			
			switch (msg.what) {
			case UpdateManager.HANDLE_MSG_DOWNING:
				if ((System.currentTimeMillis() - last_time) > 1000) {
					MyApp.app.showDownNotification(
							UpdateManager.HANDLE_MSG_DOWNING, value);
					last_time = System.currentTimeMillis();
				}
				break;
			case UpdateManager.HANDLE_MSG_DOWN_SUCCESS:
				MyApp.app.hideDownNotification();
				Intent intent = new Intent(Intent.ACTION_VIEW);
				File file = new File(Environment.getExternalStorageDirectory()
						+ "/" + Constants.Update.SAVE_PATH + "/"
						+ Constants.Update.FILE_NAME);
				if (!file.exists()) {
					return;
				}
				intent.setDataAndType(Uri.fromFile(file),
						Constants.Update.INSTALL_APK);
				mContext.startActivity(intent);
				break;
			case UpdateManager.HANDLE_MSG_DOWN_FAULT:
//				MyApp.app.showDownNotification(
//						UpdateManager.HANDLE_MSG_DOWN_FAULT, value);
				break;
			}
		}
	};
	
	class MyTast extends AsyncTask<String, Integer, String>{

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub\
			long ll = -1;
			new MainThread(mContext).checkUpdate(ll);
			return null;
		}
		
	}
}
