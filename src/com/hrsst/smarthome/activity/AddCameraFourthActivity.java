package com.hrsst.smarthome.activity;

import java.io.Serializable;
import java.util.List;

import com.hrsst.smarthome.R;
import com.hrsst.smarthome.global.Constants;
import com.hrsst.smarthome.net.SocketUDP;
import com.hrsst.smarthome.order.SendServerOrder;
import com.hrsst.smarthome.order.UnPackServer;
import com.hrsst.smarthome.pojo.UnPackageFromServer;
import com.hrsst.smarthome.pojo.UserDevice;
import com.hrsst.smarthome.util.BitmapCache;
import com.hrsst.smarthome.util.SharedPreferencesManager;
import com.hrsst.smarthome.widget.NormalDialog;
import com.p2p.core.P2PHandler;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AddCameraFourthActivity extends Activity{
	private String contactId,userNum;
	private Context mContext;
	private ImageView add_camera_four_image;
	private Button add_camera_action_four;
	private EditText camera_pwd_et,camera_name_et;
	private SocketUDP mSocketUDP;
	private NormalDialog dialog_loading;
	private AlertDialog dialog;
	private List<UserDevice> mUserDeviceList;
	private List<String> cameraList;
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_camera_four);
		mContext= this;
		cameraList = (List<String>) getIntent().getSerializableExtra(
				"cameraList");
		contactId = getIntent().getExtras().getString("contactId");
		userNum = SharedPreferencesManager.getInstance().getData(mContext,  Constants.UserInfo.USER_NUMBER);
		init();
		regFilter();
	}
	
	private void regFilter() {
		IntentFilter filter = new IntentFilter();
		filter.addAction("Constants.Action.unBinderCamera");
		filter.addAction("Constants.Action.unUserUnBinderCameraAndSocket");
		mContext.registerReceiver(mReceiver, filter);
	}
	
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent intent) {
			// TODO Auto-generated method stub
			if (intent.getAction().equals("Constants.Action.unUserUnBinderCameraAndSocket")) {
				byte[] datas = intent.getExtras().getByteArray("datasByte");
				mUserDeviceList = UnPackServer.unUserUnBinderCameraAndSocket(datas);
				if(null!=mUserDeviceList&&mUserDeviceList.size()>0){
					showRelateDialog();
				}else{
					finish();
				}
			}
			
			if (intent.getAction().equals("Constants.Action.unBinderCamera")) {
				byte[] datas = intent.getExtras().getByteArray("datasByte");
				 UnPackageFromServer mUnPackageFromServer=new UnPackServer().unBinderUser(datas);
				 String binderUser = mUnPackageFromServer.binderUser;
				 if(binderUser.equals("success")){
					 String pwd = camera_pwd_et.getText().toString().trim();
					 P2PHandler.getInstance().setRemoteDefence(contactId,pwd,
								Constants.P2P_SET.REMOTE_DEFENCE_SET.ALARM_SWITCH_ON);
					 Toast.makeText(mContext, R.string.addcamerafourthactivity_save_success, Toast.LENGTH_SHORT).show();
					 if(null!=dialog_loading){
						dialog_loading.dismiss();
						dialog_loading = null;
					 }
					 if(null!=cameraList){
						 byte[] orderSend = SendServerOrder.userUnBinderCameraAndSocket(userNum,1);
						 mSocketUDP.sendMsg(orderSend);
					 }else{
						 finish();
					 }
				 }else{
					 Toast.makeText(mContext,R.string.addcamerafourthactivity_save_fail, Toast.LENGTH_SHORT).show();
					 if(null!=dialog_loading){
						dialog_loading.dismiss();
						dialog_loading = null;
					 }
				 }
			}
		}
	};
	//是否把摄像机绑定插座
	private void showRelateDialog(){
		View v = LayoutInflater.from(mContext).inflate(
				R.layout.relate_dialog, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		dialog = builder.create();
		dialog.show();
		dialog.setContentView(v);
		dialog.setCancelable(false);
		RelativeLayout cancle_delete = (RelativeLayout) v.findViewById(R.id.relate_cancle_delete);
		RelativeLayout confire_delete = (RelativeLayout) v.findViewById(R.id.relate_confire_delete);
		TextView dialog_info = (TextView) v.findViewById(R.id.relate_dialog_info);
		TextView relate_confire_delete_tv = (TextView) v.findViewById(R.id.relate_confire_delete_tv);
		TextView relate_cancle_delete_tv = (TextView) v.findViewById(R.id.relate_cancle_delete_tv);
		dialog_info.setText(R.string.addcamerafourthactivity_need_camera_unband_socket);
		relate_confire_delete_tv.setText(R.string.addcamerafourthactivity_yes);
		relate_cancle_delete_tv.setText(R.string.addcamerafourthactivity_no);
		cancle_delete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				finish();
			}
		});
		confire_delete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(mContext, RelateCameraAndSocketActivity.class);
				intent.putExtra("devList",
						(Serializable) mUserDeviceList);
				intent.putExtra("contactId", contactId);
				startActivity(intent);
				finish();
			}
		});
	}
	
	private void showDialog(){
		if(null==dialog_loading){
			dialog_loading = new NormalDialog(mContext,
					mContext.getResources().getString(R.string.saving),
					"","","");
			dialog_loading.setStyle(NormalDialog.DIALOG_STYLE_LOADING);
		}
		dialog_loading.showDialog();
	}

	@SuppressLint("NewApi")
	private void init() {
		// TODO Auto-generated method stub
		add_camera_action_four = (Button) findViewById(R.id.add_camera_action_four);
		add_camera_four_image = (ImageView) findViewById(R.id.add_camera_four_image);
		Bitmap mBitmap = BitmapCache.getInstance().getBitmap(R.drawable.add_camera_4,mContext);
		BitmapDrawable bd = new BitmapDrawable(mContext.getResources(), mBitmap);
		add_camera_four_image.setBackground(bd);
		camera_pwd_et = (EditText) findViewById(R.id.camera_pwd_et);
		camera_name_et = (EditText) findViewById(R.id.camera_name_et);
		camera_name_et.setText(contactId);
		camera_name_et.setSelection(contactId.length());//@@
		
		add_camera_action_four.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String cameraPwd = camera_pwd_et.getText().toString().trim();
				String cameraName = camera_name_et.getText().toString().trim();
				if(null!=cameraList){
					boolean containCamera = cameraList.contains(contactId);
					if(containCamera){
						Toast.makeText(mContext, R.string.addcamerafourthactivity_camera_exist, Toast.LENGTH_SHORT).show();
						finish();
					}else{
						if(null!=cameraPwd&&cameraPwd.length()>0&&null!=cameraName&&cameraName.length()>0){
							UserDevice mUserDevice = new UserDevice();
							mUserDevice.setCameraPwd(cameraPwd);
							mUserDevice.setDevMac(contactId);
							mUserDevice.setUserNum(userNum.trim());
							mUserDevice.setDevName(cameraName);
							mUserDevice.setDevType(2);
							byte[] orderSend = SendServerOrder.BinderCamera(mUserDevice);
							mSocketUDP.sendMsg(orderSend);
							showDialog();
						}else if(null==cameraName||cameraName.length()==0){
							Toast.makeText(mContext, R.string.addcamerafourthactivity_input_device_name, Toast.LENGTH_SHORT).show();
						}else if(null==cameraPwd||cameraPwd.length()==0){
							Toast.makeText(mContext, R.string.addcamerafourthactivity_input_device
									+ R.string.addcamerafourthactivity_initial_psw, Toast.LENGTH_SHORT).show();
						}
					}
				}else{
					if(null!=cameraPwd&&cameraPwd.length()>0&&null!=cameraName&&cameraName.length()>0){
						UserDevice mUserDevice = new UserDevice();
						mUserDevice.setCameraPwd(cameraPwd);
						mUserDevice.setDevMac(contactId);
						mUserDevice.setUserNum(userNum.trim());
						mUserDevice.setDevName(cameraName);
						mUserDevice.setDevType(2);
						byte[] orderSend = SendServerOrder.BinderCamera(mUserDevice);
						mSocketUDP.sendMsg(orderSend);
						showDialog();
					}else if(null==cameraName||cameraName.length()==0){
						Toast.makeText(mContext, R.string.addcamerafourthactivity_input_device_name, Toast.LENGTH_SHORT).show();
					}else if(null==cameraPwd||cameraPwd.length()==0){
						Toast.makeText(mContext, R.string.addcamerafourthactivity_input_device
								+ R.string.addcamerafourthactivity_initial_psw, Toast.LENGTH_SHORT).show();
					}
				}
			}
		});
		mSocketUDP = SocketUDP.newInstance(Constants.SeverInfo.SERVER,
				Constants.SeverInfo.PORT);
		mSocketUDP.startAcceptMessage();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(mReceiver);
		super.onDestroy();
	}
	
}
