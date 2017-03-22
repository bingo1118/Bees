package com.hrsst.smarthome.activity;

import java.util.List;

import com.hrsst.smarthome.R;
import com.hrsst.smarthome.adapter.ChoiceDevAdapter;
import com.hrsst.smarthome.global.Constants;
import com.hrsst.smarthome.net.SocketUDP;
import com.hrsst.smarthome.order.SendServerOrder;
import com.hrsst.smarthome.order.UnPackServer;
import com.hrsst.smarthome.pojo.UserDevice;
import com.hrsst.smarthome.util.BitmapCache;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class RelateCameraAndSocketActivity extends Activity {
	private Context mContext;
	private Button add_fk_action_one;
	private TextView device_name;
	private List<UserDevice> mUserDeviceList;
	private AlertDialog modifyDialog;
	private ChoiceDevAdapter mChoiceDevAdapter;
	private String devMac,contactId;
	private SocketUDP mSocketUDP;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_firelink_one);
		mContext = this;
		mUserDeviceList = (List<UserDevice>) getIntent().getSerializableExtra(
				"devList");
		contactId = getIntent().getExtras().getString("contactId");
		mSocketUDP = SocketUDP.newInstance(Constants.SeverInfo.SERVER,
				Constants.SeverInfo.PORT);
		mSocketUDP.startAcceptMessage();
		init();
		regFilter();
	}
	
	private void regFilter() {
		IntentFilter filter = new IntentFilter();
		filter.addAction("Constants.Action.unBinderCameraAndSocket");
		mContext.registerReceiver(mReceiver, filter);
	}
	
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent intent) {
			// TODO Auto-generated method stub
			if (intent.getAction().equals("Constants.Action.unBinderCameraAndSocket")) {
				byte[] datas = intent.getExtras().getByteArray("datasByte");
				String result = UnPackServer.unBinderCameraAndSocket(datas);
				if(result.equals("success")){
					Toast.makeText(mContext, "绑定成功", Toast.LENGTH_SHORT).show();
					finish();
				}else{
					Toast.makeText(mContext, "绑定失败", Toast.LENGTH_SHORT).show();
				}
			}
		}
		
	};

	@SuppressLint("NewApi")
	private void init() {
		// TODO Auto-generated method stub
		ImageView yg_1_image = (ImageView) findViewById(R.id.yg_1_image);
		Bitmap mBitmap = BitmapCache.getInstance().getBitmap(R.drawable.binding_camera,mContext);
		BitmapDrawable bd = new BitmapDrawable(mContext.getResources(), mBitmap);
		yg_1_image.setBackground(bd);
		add_fk_action_one = (Button) findViewById(R.id.add_fk_action_one);
		device_name = (TextView) findViewById(R.id.device_name);
		add_fk_action_one.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (devMac != null && devMac.length() > 0) {
					byte[] orderSend = SendServerOrder.binderCameraAndSocket(devMac,contactId);
					mSocketUDP.sendMsg(orderSend);
				} else {
					Toast.makeText(mContext, "请选择需要绑定的智能插座", Toast.LENGTH_SHORT).show();
				}
			}
		});
		device_name.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				View vv = LayoutInflater.from(mContext).inflate(
						R.layout.choice_dev_dialog, null);
				AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
				modifyDialog = builder.create();
				modifyDialog.show();
				modifyDialog.setContentView(vv);
				ListView dev_list = (ListView) vv.findViewById(R.id.dev_list);
				dev_list.setAdapter(mChoiceDevAdapter);
				dev_list.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int pos, long arg3) {
						// TODO Auto-generated method stub
						UserDevice mUserDevice = mUserDeviceList.get(pos);
						device_name.setText(mUserDevice.getDevName());
						devMac = mUserDevice.getDevMac();
						modifyDialog.dismiss();
					}
				});
			}
		});
		mChoiceDevAdapter = new ChoiceDevAdapter(mContext, mUserDeviceList);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(mReceiver);
		super.onDestroy();
		BitmapCache.getInstance().clearCache();
	}
}
