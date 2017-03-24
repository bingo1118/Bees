package com.hrsst.smarthome;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Timer;
import java.util.TimerTask;

import com.hrsst.smarthome.activity.QrCodeActivity;
import com.hrsst.smarthome.dialog.ConnectionFKDialog;
import com.hrsst.smarthome.global.Constants;
import com.hrsst.smarthome.net.HttpThread;
import com.hrsst.smarthome.util.BitmapCache;
import com.hrsst.smarthome.util.SharedPreferencesManager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class AddAirDeviceActivity extends Activity {
	private String userNumStr;
	private Context mContext;
	private ImageView image_bg;
	private EditText air_device_name;
	private EditText air_device_mac;
	private Button air_device_button,qr;
	private Timer mTimer;
	private ConnectionFKDialog cdialog;
	private HttpThread mHttpThread;
	
	private Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				Toast.makeText(mContext, "����̽�������ӳɹ�", Toast.LENGTH_SHORT).show();
				break;
			case 1:
				Toast.makeText(mContext, "�ύ����ʧ��", Toast.LENGTH_SHORT).show();
				break;
			case 2:
				Toast.makeText(mContext, "����ʧ��", Toast.LENGTH_SHORT).show();
				break;
			case 3:
				Toast.makeText(mContext, "������δ��Ӧ", Toast.LENGTH_SHORT).show();
				break;

			default:
				
				break;
			}
			if (cdialog.isShowing()) {
				cdialog.dismiss();
			}
			mTimer.cancel();
			count = 0;
//			finish();
		}
	};
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_air_device);
		mContext=this;
		
		init();
	}

	private void init() {
		userNumStr = SharedPreferencesManager.getInstance().getData(mContext, Constants.UserInfo.USER_NUMBER);
		image_bg=(ImageView)findViewById(R.id.air_device_image);
		Bitmap mBitmap = BitmapCache.getInstance().getBitmap(R.drawable.hjtcq_lct_11,mContext);
		BitmapDrawable bd = new BitmapDrawable(mContext.getResources(), mBitmap);
		image_bg.setBackground(bd);
		air_device_name=(EditText)findViewById(R.id.device_name_edit);
		air_device_mac=(EditText)findViewById(R.id.device_mac_edit);
		air_device_button=(Button)findViewById(R.id.add_air_dev_button);
		air_device_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(air_device_name.getText().toString().length()==0
						||air_device_mac.getText().toString().length()==0){
					Toast.makeText(getApplicationContext(), "��Ϣ����Ϊ��", Toast.LENGTH_SHORT).show();
				}else{
					cdialog = new ConnectionFKDialog(mContext);//������ʾ�򡣡�
					cdialog.show();
					cdialog.startConnect();
					cdialog.setCancelable(false);
					mTimer = new Timer();
					setTimerdoAction1(doAction1, mTimer);
//					String data="mac="
//								+air_device_mac.getText().toString().trim()
//								+"&userNum="+userNumStr
//								+"&devName="+air_device_name.getText().toString().trim()
//								+"&devType="+"3";
//					String url =Constants.ADDENVIRONMENTDEVICE;
					String url ="http://192.168.0.184:8080/smartHome/servlet/AddEnvironmentDevice";
					String data="mac=559b5b14&userNum=04045919&devName=����1&devType=3";
					mHttpThread=new HttpThread(url, handler,data);
					mHttpThread.start();
				}				
			}
		});
		qr=(Button)findViewById(R.id.qr);
		qr.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(AddAirDeviceActivity.this,QrCodeActivity.class);
				startActivityForResult(intent,1);				
			}
		});
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode==1){
			air_device_mac.setText(data.getExtras().getString("msg"));
		}
	}
	private int count = 0;

	private void setTimerdoAction1(final Handler oj, Timer t) {
		t.schedule(new TimerTask() {
			@Override
			public void run() {
				Message message = new Message();
				// �ж�wifiӲ���Ƿ����óɹ�
				count = count + 1;
				if (count > 35) {// 30s����
					message = oj.obtainMessage();
					message.what = 1;
					oj.sendMessage(message);
				}
			}
		}, 1000, 1000/* ��ʾ1000����֮�ᣬÿ��1000�������һ�� */);
	}

	private Handler doAction1 = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			int messsageId = msg.what;
			switch (messsageId) {
			case 1:
				mTimer.cancel();
				count = 0;
				if (cdialog.isShowing()) {
					cdialog.dismiss();
					Toast.makeText(getApplicationContext(), "���ó�ʱ", Toast.LENGTH_SHORT).show();//@@
				}
				break;
			default:
				break;
			}
		}
	};
}