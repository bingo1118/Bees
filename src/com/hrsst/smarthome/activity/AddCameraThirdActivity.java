package com.hrsst.smarthome.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.hrsst.smarthome.R;
import com.hrsst.smarthome.pojo.UserDevice;
import com.hrsst.smarthome.util.BitmapCache;
import com.hrsst.smarthome.util.Utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AddCameraThirdActivity extends Activity implements OnClickListener{
	private Context mContext;
	private TextView camera_wifi_name;
	private ImageView wifi_list_image_view;
	private Button next_add_camera_second_btn;
	private RelativeLayout wifi_list_rela;
	private EditText wifi_pwd;
	private String ssid;
	private int mLocalIp;
	boolean bool1, bool2, bool3, bool4;
	private byte mAuthMode;
	private byte AuthModeOpen = 0;
	private byte AuthModeWPA = 3;
	private byte AuthModeWPA1PSKWPA2PSK = 9;
	private byte AuthModeWPA1WPA2 = 8;
	private byte AuthModeWPA2 = 6;
	private byte AuthModeWPA2PSK = 7;
	private byte AuthModeWPAPSK = 4;
	private boolean isWifiOpen = false;
	boolean isRegFilter = false;
	private int type;
	private ImageView add_camera_three_image;
	private List<String> cameraList;
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_camera_third);
		mContext = this;
		cameraList = (List<String>) getIntent().getSerializableExtra(
				"cameraList");
		init();
	}

	@SuppressLint("NewApi")
	private void init() {
		// TODO Auto-generated method stub
		Bitmap mBitmap = BitmapCache.getInstance().getBitmap(R.drawable.add_camera_3,mContext);
		BitmapDrawable bd = new BitmapDrawable(mContext.getResources(), mBitmap);
		add_camera_three_image = (ImageView) findViewById(R.id.add_camera_three_image);
		add_camera_three_image.setBackground(bd);
		
		camera_wifi_name = (TextView) findViewById(R.id.camera_wifi_name);
		camera_wifi_name.setOnClickListener(this);
		wifi_list_image_view = (ImageView) findViewById(R.id.wifi_list_image_view);
		wifi_list_image_view.setOnClickListener(this);
		next_add_camera_second_btn = (Button) findViewById(R.id.next_add_camera_second_btn);
		next_add_camera_second_btn.setOnClickListener(this);
		camera_wifi_name.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);// ÏÂ»®Ïß
		camera_wifi_name.setTextColor(getResources().getColor(
				R.color.wifi_change_color));
		wifi_list_rela = (RelativeLayout) findViewById(R.id.wifi_list_rela);
		wifi_list_image_view = (ImageView) findViewById(R.id.wifi_list_image_view);
		wifi_pwd = (EditText) findViewById(R.id.camera_wifi_pwd);
		currentWifi();
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.wifi_list_image_view:
			wifi_list_rela.setVisibility(View.GONE);
			wifi_list_image_view.setVisibility(View.GONE);
			Animation anim = AnimationUtils.loadAnimation(mContext,
					R.anim.slide_out_to_top);
			wifi_list_rela.startAnimation(anim);
			break;
		case R.id.next_add_camera_second_btn:
			InputMethodManager manager = (InputMethodManager) getSystemService(mContext.INPUT_METHOD_SERVICE);
			if (manager != null) {
				manager.hideSoftInputFromWindow(wifi_pwd.getWindowToken(), 0);
			}
			String wifiPwd = wifi_pwd.getText().toString();
			if (ssid == null || ssid.equals("")) {
				Toast.makeText(mContext, R.string.addcamerathirdactivity_phone_connect_wifi_first, Toast.LENGTH_SHORT).show();
				return;
			}
			if (ssid.equals("<unknown ssid>")) {
				Toast.makeText(mContext, R.string.addcamerathirdactivity_phone_connect_wifi_first, Toast.LENGTH_SHORT).show();
				return;
			}
			if (!isWifiOpen) {
				if (null == wifiPwd || wifiPwd.length() <= 0
						&& (type == 1 || type == 2)) {
					Toast.makeText(mContext, R.string.addcamerathirdactivity_input_wifi_psw, Toast.LENGTH_SHORT).show();
					return;
				}
			}

			Intent device_network = new Intent(mContext, AddWaitActicity.class);
			device_network.putExtra("cameraList",
					(Serializable) cameraList);
			device_network.putExtra("ssidname", ssid);
			device_network.putExtra("wifiPwd", wifiPwd);
			device_network.putExtra("type", mAuthMode);
			device_network.putExtra("LocalIp", mLocalIp);
			device_network.putExtra("isNeedSendWifi", true);
			startActivity(device_network);
			finish();
			break;
		default:
			break;
		}
	}
	
	private void currentWifi() {
		WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		if (!manager.isWifiEnabled())
			return;
		WifiInfo info = manager.getConnectionInfo();
		ssid = info.getSSID();
		mLocalIp = info.getIpAddress();
		Log.e("ssid", ssid);
		List<ScanResult> datas = new ArrayList<ScanResult>();
		if (!manager.isWifiEnabled())
			return;
		manager.startScan();
		datas = manager.getScanResults();
		if (ssid == null) {
			return;
		}
		if (ssid.equals("")) {
			return;
		}
		int a = ssid.charAt(0);
		if (a == 34) {
			ssid = ssid.substring(1, ssid.length() - 1);
		}
		if (!ssid.equals("<unknown ssid>") && !ssid.equals("0x")) {
			camera_wifi_name.setText(ssid);
		}
		for (int i = 0; i < datas.size(); i++) {
			ScanResult result = datas.get(i);
			if (!result.SSID.equals(ssid)) {
				continue;
			}
			if (Utils.isWifiOpen(result)) {
				type = 0;
				isWifiOpen = true;
			} else {
				type = 1;
				isWifiOpen = false;
			}
			bool1 = result.capabilities.contains("WPA-PSK");
			bool2 = result.capabilities.contains("WPA2-PSK");
			bool3 = result.capabilities.contains("WPA-EAP");
			bool4 = result.capabilities.contains("WPA2-EAP");
			if (result.capabilities.contains("WEP")) {
				this.mAuthMode = this.AuthModeOpen;
			}
			if ((bool1) && (bool2)) {
				mAuthMode = AuthModeWPA1PSKWPA2PSK;
			} else if (bool2) {
				this.mAuthMode = this.AuthModeWPA2PSK;
			} else if (bool1) {
				this.mAuthMode = this.AuthModeWPAPSK;
			} else if ((bool3) && (bool4)) {
				this.mAuthMode = this.AuthModeWPA1WPA2;
			} else if (bool4) {
				this.mAuthMode = this.AuthModeWPA2;
			} else {
				if (!bool3)
					break;
				this.mAuthMode = this.AuthModeWPA;
			}

		}

	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		BitmapCache.getInstance().clearCache();
	}
	
}
