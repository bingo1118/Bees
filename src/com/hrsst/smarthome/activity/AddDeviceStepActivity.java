package com.hrsst.smarthome.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.hrsst.smarthome.AddAirDeviceActivity;
import com.hrsst.smarthome.R;
import com.hrsst.smarthome.adapter.DeviceListAdapter;
import com.hrsst.smarthome.global.Constants;
import com.hrsst.smarthome.mygridview.lib.PullToRefreshGridView;
import com.hrsst.smarthome.mygridview.lib.PullToRefreshBase.OnRefreshListener;
import com.hrsst.smarthome.net.SocketUDP;
import com.hrsst.smarthome.order.SendServerOrder;
import com.hrsst.smarthome.order.UnPackServer;
import com.hrsst.smarthome.pojo.UserDevice;
import com.hrsst.smarthome.util.BitmapCache;
import com.hrsst.smarthome.util.SharedPreferencesManager;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class AddDeviceStepActivity extends Activity {
	private Context mContext;
	private PullToRefreshGridView mPullToRefreshGridView;
	private GridView mGridView;
	private ViewGroup contentContainer;
	private List<UserDevice> mUserDeviceList;//设备列表。。
	private SocketUDP mSocketUDP;
	private String userNum;
	private List<String> cameraList;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_step_one);
		mContext = this;
		mUserDeviceList = (List<UserDevice>) getIntent().getSerializableExtra(
				"devList");
		cameraList= (List<String>) getIntent().getSerializableExtra(
				"cameraList");
		userNum = SharedPreferencesManager.getInstance().getData(mContext,  Constants.UserInfo.USER_NUMBER);
		init();
		mSocketUDP = SocketUDP.newInstance(Constants.SeverInfo.SERVER,
				Constants.SeverInfo.PORT);
		mSocketUDP.startAcceptMessage();
		regFilter();
	}
	
	public void regFilter() {
		IntentFilter filter = new IntentFilter();
		filter.addAction("Constants.Action.unIfRegisterInyoo");
		mContext.registerReceiver(mReceiver, filter);
	}
	
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			if(arg1.getAction().equals("Constants.Action.unIfRegisterInyoo")){
				byte[] datas = arg1.getExtras().getByteArray("datasByte");
				if(datas!=null&&datas.length>0){
					String result = UnPackServer.unIfRegisterInyoo(datas);
					if(result.equals("no")){
						Intent i =new Intent(mContext,RegisterYooActivity.class);
						i.putExtra("cameraList",
								(Serializable) cameraList);
						i.putExtra("message", "no");
						i.putExtra("userId", userNum);
						startActivity(i);
						finish();
					}else if(result.equals("false")){
						
					}else{
						Intent i =new Intent(mContext,AddCameraFirstActivity.class);
						i.putExtra("cameraList",
								(Serializable) cameraList);
						startActivity(i);//如果注册过技威则跳转到添加界面。。
						finish();
					}
				}
			}
		}
		
	};

	private void init() {
		contentContainer = (ViewGroup) findViewById(R.id.rela_device);

		LayoutInflater.from(mContext).inflate(
				R.layout.pull_to_refresh_gridview, contentContainer);
		mPullToRefreshGridView = (PullToRefreshGridView) findViewById(R.id.pull_refresh_grid);
		mGridView = mPullToRefreshGridView.getRefreshableView();
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < 9; i++) {
			list.add("i" + i);
		}
		mGridView.setAdapter(new DeviceListAdapter(mContext, list));
		mPullToRefreshGridView
				.setOnDownPullRefreshListener(new OnRefreshListener() {
					@Override
					public void onRefresh() {
						mPullToRefreshGridView.onRefreshComplete();
					}
				});
		//点击事件。。
		mGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long arg3) {
				// TODO Auto-generated method stub
				switch (position) {
				case 0://智能插座
					Intent intent = new Intent(mContext,
							ActionComnfigerActivity.class);
					startActivity(intent);
					finish();
					break;
				case 1://烟感
					if (mUserDeviceList.size() > 0) {
						Intent intent1 = new Intent(mContext,
								AddFireLinkOneActivity.class);
						intent1.putExtra("devList",
								(Serializable) mUserDeviceList);
						startActivity(intent1);
						finish();
					} else {
						Toast.makeText(mContext, "请先添加智能插座", Toast.LENGTH_SHORT).show();
					}
					break;
				case 2://无线门磁
					if (mUserDeviceList.size() > 0) {
						Intent intent2 = new Intent(mContext,
								AddDoorsensorOneActivity.class);
						intent2.putExtra("devList",
								(Serializable) mUserDeviceList);
						intent2.putExtra("type", 2);
						startActivity(intent2);
						finish();
					} else {
						Toast.makeText(mContext, "请先添加智能插座", Toast.LENGTH_SHORT).show();
					}
					break;
				case 3://红外探测器
					if (mUserDeviceList.size() > 0) {
						Intent intent4 = new Intent(mContext,
								AddDoorsensorOneActivity.class);
						intent4.putExtra("devList",
								(Serializable) mUserDeviceList);
						intent4.putExtra("type", 4);
						startActivity(intent4);
						finish();
					}else{
						Toast.makeText(mContext, "请先添加智能插座", Toast.LENGTH_SHORT).show();
					}
					
					break;
				case 4://燃气探测器
					if (mUserDeviceList.size() > 0) {
						Intent intent5 = new Intent(mContext,
								AddDoorsensorOneActivity.class);
						intent5.putExtra("devList",
								(Serializable) mUserDeviceList);
						intent5.putExtra("type", 5);
						startActivity(intent5);
						finish();
					}else{
						Toast.makeText(mContext, "请先添加智能插座", Toast.LENGTH_SHORT).show();
					}
					
					break;
				case 5://水禁
					if (mUserDeviceList.size() > 0) {
						Intent intent6 = new Intent(mContext,
								AddDoorsensorOneActivity.class);
						intent6.putExtra("devList",
								(Serializable) mUserDeviceList);
						intent6.putExtra("type", 6);
						startActivity(intent6);
						finish();
					}else{
						Toast.makeText(mContext, "请先添加智能插座", Toast.LENGTH_SHORT).show();
					}
					
					break;
				case 6://万能遥控器
					if (mUserDeviceList.size() > 0) {
						Intent intent7 = new Intent(mContext,
								AddDoorsensorOneActivity.class);
						intent7.putExtra("devList",
								(Serializable) mUserDeviceList);
						intent7.putExtra("type", 7);
						startActivity(intent7);
						finish();
					}else{
						Toast.makeText(mContext, "请先添加智能插座", Toast.LENGTH_SHORT).show();
					}
					
					break;
				case 7://环境探测器
//					if (mUserDeviceList.size() > 0) {
						String userNumStr = SharedPreferencesManager.getInstance().getData(mContext, Constants.UserInfo.USER_ID);
						Intent intent8 = new Intent(mContext,AddAirDeviceActivity.class);
						startActivity(intent8);
						finish();
//					}else{
//						Toast.makeText(mContext, "请先添加智能插座", Toast.LENGTH_SHORT).show();
//					}
					
					break;
				case 8://摄像机
					byte[] orderSend =SendServerOrder.ifRegisterInyoo(userNum);//是否注册技威。。
					mSocketUDP.sendMsg(orderSend);
//					Intent monitor = new Intent();
//					monitor.setClass(mContext, CallActivity.class);
//					monitor.putExtra("callId", "3121802");
//					monitor.putExtra("contactName", "3121802");
//					monitor.putExtra("password","123");
//					monitor.putExtra("isOutCall", true);
//					monitor.putExtra("type",
//							Constants.P2P_TYPE.P2P_TYPE_MONITOR);
//					monitor.putExtra("contactType", 7);
//					mContext.startActivity(monitor);
					
//					Toast.makeText(mContext, "敬请期待...", Toast.LENGTH_SHORT).show();
					break;
				default:
					break;
				}

			}
		});
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		BitmapCache.getInstance().clearCache();
		unregisterReceiver(mReceiver);
		super.onDestroy();
		
	}

}
