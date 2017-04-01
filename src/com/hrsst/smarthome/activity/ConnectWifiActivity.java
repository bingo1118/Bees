package com.hrsst.smarthome.activity;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.hrsst.smarthome.R;
import com.hrsst.smarthome.dialog.ConnectionDialog;
import com.hrsst.smarthome.global.Constants;
import com.hrsst.smarthome.net.SocketTCP;
import com.hrsst.smarthome.net.SocketUDP;
import com.hrsst.smarthome.order.SendServerOrder;
import com.hrsst.smarthome.order.SendWifiOrder;
import com.hrsst.smarthome.order.UnPackServer;
import com.hrsst.smarthome.pojo.UnPackageFromServer;
import com.hrsst.smarthome.util.ASCIIToString;
import com.hrsst.smarthome.util.BitmapCache;
import com.hrsst.smarthome.util.ByteToString;
import com.hrsst.smarthome.util.CRC16;
import com.hrsst.smarthome.util.SharedPreferencesManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ConnectWifiActivity extends Activity implements OnClickListener {
	private Context mContext;
	private Button action_one;
	private TextView wifi_name;
	private String wifiName, wifiPwd;
	private Timer mTimer, mTimer1;
	private SocketTCP mSocketTCPClient;
	private ConnectionDialog cdialog;
	private SocketUDP mSocketUDPClient;
	private String wifiMac = null;
	private String userNum;
	private byte[] seq;
	private int seqInt=0;
	private int sendSeq=0,netID;
	private String serverTime;
//	private Long finishTime;
	private String devHeartTime;
	private WifiManager wifiManager = null;
	private String ssidStr;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_start);
		mContext = this;
		serverTime = getIntent().getExtras().getString("serverTime");
		wifiName = getIntent().getExtras().getString("wifiName");
		wifiPwd = getIntent().getExtras().getString("wifiPwd");
		init();
		regFilter();
	}

	@SuppressLint("NewApi")
	private void init() {
		// TODO Auto-generated method stub
		ImageView liuchengtu3_image = (ImageView) findViewById(R.id.liuchengtu3_image);
		Bitmap mBitmap = BitmapCache.getInstance().getBitmap(R.drawable.liuchengtu_3,mContext);
		BitmapDrawable bd = new BitmapDrawable(mContext.getResources(), mBitmap);
		liuchengtu3_image.setBackground(bd);
		action_one = (Button) findViewById(R.id.action_one);
		action_one.setOnClickListener(this);
		wifi_name = (TextView) findViewById(R.id.wifi_name);
		wifi_name.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);// 下划线
		wifi_name.setTextColor(getResources().getColor(
				R.color.wifi_change_color));
		wifi_name.setOnClickListener(this);
		connectUDP();
	}

	private void connectUDP() {
		mSocketUDPClient = SocketUDP.newInstance(
				Constants.SeverInfo.SERVER, Constants.SeverInfo.PORT);
		mSocketUDPClient.startAcceptMessage();
	}

	public void regFilter() {
		IntentFilter filter = new IntentFilter();
		filter.addAction("Constants.Action.unACKPack");
		filter.addAction("Constants.Action.unWifiNamePack");
		filter.addAction("Constants.Action.unServerACKPack");
		filter.addAction("Constants.Action.unBinderUser");
		filter.addAction("Constants.Action.unGetDevHeartTimePackage");
		mContext.registerReceiver(mReceiver, filter);
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
//			if(arg1.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)){
//				ConnectivityManager connectivityManager=(ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
//		        NetworkInfo  wifiNetInfo=connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
//		        if(!wifiNetInfo.isConnected()){
//		        	Log.i("wifi", "wifi disconnect");
//		        	connectTCP();
//		        }
//			}
			
			if(arg1.getAction().equals("Constants.Action.unGetDevHeartTimePackage")){
				byte[] datas = arg1.getExtras().getByteArray("datasByte");
				
				UnPackageFromServer mUnPackageFromServer = UnPackServer.unGetDevHeartTimePackage(datas);
				devHeartTime = mUnPackageFromServer.devHeartTime;
				try {
					long heartLong = Long.parseLong(devHeartTime);
					long serverLong = Long.parseLong(serverTime);
					
					long mistiming = heartLong-serverLong;
					if(mistiming>0){
						//排插已上线，发送绑定排插包
						byte[] orderSend = SendServerOrder.BinderUser(userNum, wifiMac);
						mSocketUDPClient.sendMsg(orderSend);
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			
			if (arg1.getAction().equals("Constants.Action.unACKPack")) {
				byte[] datas = arg1.getExtras().getByteArray("datasByte");
				boolean crc = unPack(datas);
				if(crc){
					switch (seqInt) {
					case 1:

						break;
					case 2:
						count = 0;
						byte[] seqByte = new byte[2];
						seqByte[0] = 0x00;
						seqByte[1] = 0x03;
						mSocketTCPClient.sendMsg(SendWifiOrder.secondOrder(
								wifiPwd, (byte) 0x03,seqByte));
						sendSeq=3;
						break;
					case 3:
						count = 0;
						mSocketTCPClient.sendMsg(SendWifiOrder.WorkMode());
						sendSeq=4;
						break;
					case 4:
						if(null!=mTimer){
							mTimer.cancel();
							mTimer=null;
						}
						count = 0;
						sendSeq=0;
						userNum = SharedPreferencesManager
								.getInstance().getData(mContext,
										Constants.UserInfo.USER_NUMBER);
						if(mTimer1==null){
							mTimer1 = new Timer();
						}
						removeWifi2();
						setTimerdoAction1(doAction1, mTimer1);
						break;
					default:
						break;
					}
				}
			}
			if (arg1.getAction().equals("Constants.Action.unWifiNamePack")) {
				Log.i("count", "unWifiNamePack");
				byte[] datas = arg1.getExtras().getByteArray("datasByte");
				count = 0;
				byte[] seqByte = new byte[2];
				seqByte[0] = 0x00;
				seqByte[1] = 0x02;
				mSocketTCPClient.sendMsg(SendWifiOrder.ACKOrder(seq));
				try {
					new Thread().sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				mSocketTCPClient.sendMsg(SendWifiOrder.secondOrder(wifiName, (byte) 0x02,seqByte));
				sendSeq=2;
			}

			if (arg1.getAction().equals("Constants.Action.unServerACKPack")) {
				byte[] datas = arg1.getExtras().getByteArray("datasByte");
				new UnPackServer().unServerACKPack(datas);
			}

			if (arg1.getAction().equals("Constants.Action.unBinderUser")) {
				byte[] datas = arg1.getExtras().getByteArray("datasByte");
				if(null!=datas&&datas.length>0){
					UnPackageFromServer mUnPackageFromServer = new UnPackServer().unBinderUser(datas);
					String result = mUnPackageFromServer.binderUser;
					if(result.equals("failed")){
						//Toast.makeText(mContext, "配置失败", Toast.LENGTH_SHORT).show();
					}else if(result.equals("success")){
						if(mTimer1!=null){
							mTimer1.cancel();
							mTimer1=null;
						}
						Toast.makeText(mContext, R.string.configuration_success, Toast.LENGTH_SHORT).show();
						udpSize = 0;
//						finish();
						startActivity(new Intent(ConnectWifiActivity.this,MainActivity.class));
					}
				}
			}
		}
	};

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.wifi_name:
			startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
			break;
		case R.id.action_one:
			// connectTCP();
			String devWifiName = wifi_name.getText().toString().trim();
			if (mSocketTCPClient!=null&&devWifiName.startsWith("\""+"HRSST_")) {
				//开始配置设备
				mSocketTCPClient.sendMsg(SendWifiOrder.firstOrder());
				sendSeq=1;
				mTimer = new Timer();
				setTimerdoAction(doAction, mTimer);
				cdialog = new ConnectionDialog(mContext);
				cdialog.show();
				cdialog.startConnect();
				cdialog.setCancelable(false);
			}else{
				Toast.makeText(mContext, R.string.please_choose_right_devide_wifi, Toast.LENGTH_SHORT).show();
			}
			break;
		default:
			break;
		}
	}

	private int count = 0;
	private void setTimerdoAction(final Handler oj, Timer t) {
		t.schedule(new TimerTask() {
			@Override
			public void run() {
				
				Message message = new Message();
				Log.i("count", sendSeq+"s");
				Log.i("count", seqInt+"i");
				if(count%4==0&&seqInt!=sendSeq){
					message = oj.obtainMessage();
					message.what = 1;
					oj.sendMessage(message);
				}
				if(count>20){
					message = oj.obtainMessage();
					message.what = 5;
					oj.sendMessage(message);
				}
				count = count + 1;
			}
		}, 1000, 1000/* 表示1000毫秒之後，每隔1000毫秒執行一次 */);
	}

	private Handler doAction = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			int messsageId = msg.what;
			Log.i("seqInt=", sendSeq+"");
			if(messsageId==1){
				switch (sendSeq) {
				case 1:
					mSocketTCPClient.sendMsg(SendWifiOrder.firstOrder());
					break;
				case 2:
					byte[] seqByte = new byte[2];
					seqByte[0] = 0x00;
					seqByte[1] = 0x02;
					mSocketTCPClient.sendMsg(SendWifiOrder.ACKOrder(seq));
					mSocketTCPClient.sendMsg(SendWifiOrder.secondOrder(wifiName, (byte) 0x02,seqByte));
					break;
				case 3:
					byte[] seqByte3 = new byte[2];
					seqByte3[0] = 0x00;
					seqByte3[1] = 0x03;
					mSocketTCPClient.sendMsg(SendWifiOrder.secondOrder(wifiPwd, (byte) 0x03,seqByte3));
					break;
				case 4:
					mSocketTCPClient.sendMsg(SendWifiOrder.WorkMode());
					break;
				default:
					break;
				}
			}else{
				count = 0;
				if(mTimer!=null){
					mTimer.cancel();
					mTimer=null;
				}
				Toast.makeText(mContext, R.string.configuration_fail, Toast.LENGTH_SHORT)
				.show();
				removeWifi();
				//finish();
			}
		}
	};

	private int udpSize = 0;

	private void setTimerdoAction1(final Handler oj, Timer t) {
		t.schedule(new TimerTask() {
			@Override
			public void run() {
				Message message = new Message();
				if(udpSize==0){
					message = oj.obtainMessage();
					message.what = 1;
					oj.sendMessage(message);
				}
				if (udpSize % 3 == 0) {
					message = oj.obtainMessage();
					message.what = 1;
					oj.sendMessage(message);
				}
				if (udpSize > 60) {// 30s结束
					message = oj.obtainMessage();
					message.what = 2;
					oj.sendMessage(message);
				}
				udpSize = udpSize + 1;
			}
		}, 1000, 1000/* 表示1000毫秒之後，每隔1000毫秒執行一次 */);
	}

	private Handler doAction1 = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			int messsageId = msg.what;
			switch (messsageId) {
			case 1:
				byte[] orderSend = SendServerOrder.GetDevHeartTimePackage(wifiMac);
				mSocketUDPClient.sendMsg(orderSend);
				Log.i("seqInt", "send...");
				break;
			case 2:
				udpSize = 0;
				if(mTimer1!=null){
					mTimer1.cancel();
					mTimer1=null;
				}
				Toast.makeText(mContext, R.string.configuration_outtime, 1).show();
				removeWifi();
				//finish();
				break;
			default:
				break;
			}
		}
	};

	// 解包
	private boolean unPack(byte[] pk) {
		int dataLen = pk[7];
		seq = new byte[2];
		byte[] crc = new byte[5 + dataLen];
		for (int i = 0; i < crc.length; i++) {
			crc[i] = pk[3 + i];
		}
		String str = String.format("%04x", CRC16.calcCrc16(crc));
		byte[] bs = new byte[2];
		bs[0] = pk[8 + dataLen];
		bs[1] = pk[9 + dataLen];
		String s = new ByteToString().encodeHexStr(bs);
		if (s.equals(str)) {
			if (null == wifiMac || wifiMac.length() <= 0) {
				byte[] bss = new byte[12];
				for (int j = 0; j < 12; j++) {
					bss[j] = pk[8 + j];
				}
				// 获取wifi的mac
				wifiMac = ASCIIToString.hexStr2Str(new ByteToString()
						.encodeHexStr(bss));
			}
			seq[0] = pk[5];
			seq[1] = pk[6];
			seqInt =  (pk[5]+ pk[6])&0xff;
			Log.i("seqInt", seqInt+"");
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if(mTimer1!=null){
			mTimer1.cancel();
			mTimer1=null;
		}
		super.onDestroy();
		if (mSocketTCPClient != null) {
			mSocketTCPClient.clearClient();
		}
		unregisterReceiver(mReceiver);
		if(netID!=0&&wifiManager!=null){
			wifiManager.removeNetwork(netID);
		}
		BitmapCache.getInstance().clearCache();
	}

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
	
	private void removeWifi(){
		int netID = 0;
		mSocketTCPClient.clearClient();
		WifiManager wifiManager = (WifiManager) mContext
				.getSystemService(Service.WIFI_SERVICE);
		WifiInfo info = wifiManager.getConnectionInfo();
		String ssidStr = info.getSSID();
		List<WifiConfiguration> existingConfigs = wifiManager
				.getConfiguredNetworks();
		for (WifiConfiguration existingConfig : existingConfigs) {
			if (existingConfig.SSID.toString().equals(ssidStr)) {
				netID = existingConfig.networkId;
			}
		}
		if(ssidStr.startsWith("\""+"HRSST_")&&netID!=0&&ssidStr.length()==20){
			wifiManager.removeNetwork(netID);
		}
		
		finish();
	}
	
	private void removeWifi2(){
		mSocketTCPClient.clearClient();
		int netID = 0;
		WifiManager wifiManager = (WifiManager) mContext
				.getSystemService(Service.WIFI_SERVICE);
		WifiInfo info = wifiManager.getConnectionInfo();
		String ssidStr = info.getSSID();
		List<WifiConfiguration> existingConfigs = wifiManager
				.getConfiguredNetworks();
		for (WifiConfiguration existingConfig : existingConfigs) {
			if (existingConfig.SSID.toString().equals(ssidStr)) {
				netID = existingConfig.networkId;
			}
		}
		if(ssidStr.startsWith("\""+"HRSST_")&&netID!=0&&ssidStr.length()==20){
			wifiManager.removeNetwork(netID);
		}
		
	}
	
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		wifi_name.setText(getWIfi());
		mSocketTCPClient = SocketTCP.newInstance(Constants.WifiInfo.WIFI_IP,
				Constants.WifiInfo.WIFI_PORT, mContext);
		mSocketTCPClient.connectServer();
	}
	
	private String getWIfi(){
		WifiManager wifiMgr = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        int wifiState = wifiMgr.getWifiState();
        WifiInfo info = wifiMgr.getConnectionInfo();
        String wifiId = info != null ? info.getSSID() : null;
        return wifiId;
	}

}
