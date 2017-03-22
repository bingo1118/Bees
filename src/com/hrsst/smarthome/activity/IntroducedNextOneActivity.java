package com.hrsst.smarthome.activity;

import com.hrsst.smarthome.R;
import com.hrsst.smarthome.global.Constants;
import com.hrsst.smarthome.net.SocketUDP;
import com.hrsst.smarthome.order.SendServerOrder;
import com.hrsst.smarthome.order.UnPackServer;
import com.hrsst.smarthome.pojo.UnPackageFromServer;
import com.hrsst.smarthome.util.BitmapCache;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class IntroducedNextOneActivity extends Activity {
	private Context mContext;
	private TextView device_wifi_name,wifi_name;
	private Button next_action_two;
	private EditText wifi_pwd;
	private SocketUDP mSocketUDPClient;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_device_introduce);
		mContext=this;
		ImageView liuchengtu2_image = (ImageView) findViewById(R.id.liuchengtu2_image);
		Bitmap mBitmap = BitmapCache.getInstance().getBitmap(R.drawable.liuchengtu2,mContext);
		BitmapDrawable bd = new BitmapDrawable(mContext.getResources(), mBitmap);
		liuchengtu2_image.setBackground(bd);
		init();
	}

	private void init() {
		device_wifi_name = (TextView) findViewById(R.id.device_wifi_name);
		device_wifi_name.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);// 下划线
		device_wifi_name.setTextColor(getResources().getColor(R.color.wifi_change_color));
		next_action_two = (Button) findViewById(R.id.next_action_two_btn);
		wifi_name = (TextView) findViewById(R.id.wifi_name);
		wifi_pwd = (EditText) findViewById(R.id.wifi_pwd);
	
		wifi_name.setText("当前WIFI:"+getWIfi());
		wifi_name.setTextColor(getResources().getColor(R.color.wifi_change_color));
		regFilter();
		mSocketUDPClient = SocketUDP.newInstance(
				Constants.SeverInfo.SERVER, Constants.SeverInfo.PORT);
		mSocketUDPClient.startAcceptMessage();
		device_wifi_name.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
			}
		});
		next_action_two.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String wifiName = getWIfi().replaceAll("\"", "");
				String wifiPwd = wifi_pwd.getText().toString();
				if(wifiName==null||wifiPwd==null){
					Toast.makeText(mContext, "wifi名称和密码不能为空", 1).show();
					return;
				}
				if(wifiName.length()==0||wifiPwd.length()==0){
					Toast.makeText(mContext, "wifi名称和密码不能为空", 1).show();
					return;
				}
				byte[] orderSend = SendServerOrder.GetServerTimePackage();
				mSocketUDPClient.sendMsg(orderSend);
			}
		});
	}
	
	private void regFilter() {
		IntentFilter filter = new IntentFilter();
		filter.addAction("Constants.Action.unGetServerTimePackage");
		mContext.registerReceiver(mReceiver, filter);
	}
	
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			if(arg1.getAction().equals("Constants.Action.unGetServerTimePackage")){
				byte[] datas = arg1.getExtras().getByteArray("datasByte");
				UnPackageFromServer mUnPackageFromServer = UnPackServer.unGetServerTimePackage(datas);
				String serverTime = mUnPackageFromServer.serverTime;
				if(null!=serverTime&&serverTime.length()>0){
					//连接wifi
					String wifiName= getWIfi().replaceAll("\"", "");
					String wifiPwd = wifi_pwd.getText().toString().trim();
					if(wifiName==null||wifiPwd==null){
						Toast.makeText(mContext, "wifi名称和密码不能为空", 1).show();
						return;
					}
					if(wifiName.length()==0||wifiName.length()==0){
						Toast.makeText(mContext, "设备wifi名称和密码不能为空", 1).show();
						return;
					}
					Intent intent = new Intent(mContext, ConnectWifiActivity.class);
					intent.putExtra("serverTime", serverTime);
					intent.putExtra("wifiName", wifiName);
					intent.putExtra("wifiPwd", wifiPwd);
					startActivity(intent);
					finish();
				}
			}
		}
		
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
				finish();
				return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		BitmapCache.getInstance().clearCache();
		super.onDestroy();
	}
	
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		wifi_name.setText("当前WIFI:"+getWIfi());
	}
	
	private String getWIfi(){
		WifiManager wifiMgr = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiMgr.getConnectionInfo();
        String wifiId = info != null ? info.getSSID() : null;
        return wifiId;
	}
}
