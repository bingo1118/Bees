package com.hrsst.smarthome.activity;

import java.io.InputStream;
import java.util.List;

import com.hrsst.smarthome.R;
import com.hrsst.smarthome.adapter.CameraListAdapter;
import com.hrsst.smarthome.global.Constants;
import com.hrsst.smarthome.net.SocketUDP;
import com.hrsst.smarthome.order.SendServerOrder;
import com.hrsst.smarthome.order.UnPackServer;
import com.hrsst.smarthome.pojo.UnPackageFromServer;
import com.hrsst.smarthome.pojo.UserDevice;
import com.hrsst.smarthome.util.BitmapCache;
import com.hrsst.smarthome.util.IntegerTo16;
import com.hrsst.smarthome.util.SharedPreferencesManager;
import com.hrsst.smarthome.widget.NormalDialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class DeviceInfoActivity extends Activity implements OnClickListener {
	private Context mContext;
	private ImageView openOrClose, mTimer, more_control,
			more_control_image_view,relate_image;
	private ImageView socket_mImageView;
	private SocketUDP mSocketUDPClient;
	private AlertDialog  modifyDialog;
	private String mac, devName,cameraMac;
	private int ocState;
	private byte[] states;
	private TextView mTextView,relate_tv,relate_tv_name;
	private RelativeLayout socket_info_bg, more_control_rela, cancel_tv_name,
			modify_dev_name, fk_tv_name, door_tv_name, dev_share,infrared_tv_name,gas_tv_name,
			sj_tv_name,ykq_tv_name;//@@
	private String fromUserNum;
	private String modifyName;
	private String devStatus,relateResult;
	private List<UserDevice> mUserDeviceList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device_info);
		mac = getIntent().getExtras().getString("mac");
		ocState = getIntent().getExtras().getInt("ocState");
		devName = getIntent().getExtras().getString("devName");
		mContext = this;
		init();
		fromUserNum = SharedPreferencesManager.getInstance().getData(mContext,
				Constants.UserInfo.USER_NUMBER);
		mSocketUDPClient = SocketUDP.newInstance(Constants.SeverInfo.SERVER,
				Constants.SeverInfo.PORT);
		mSocketUDPClient.startAcceptMessage();
		regFilter();
		byte[] orderSend =SendServerOrder.findBinderCameraAndSocket(mac);
		mSocketUDPClient.sendMsg(orderSend);
	}

	@SuppressLint("NewApi")
	private void init() {
		openOrClose = (ImageView) findViewById(R.id.open_or_close);
		mTimer = (ImageView) findViewById(R.id.mTimer);
		relate_image = (ImageView) findViewById(R.id.relate_image);
		socket_info_bg = (RelativeLayout) findViewById(R.id.socket_info_bg);
		mTextView = (TextView) findViewById(R.id.mTextView);
		relate_tv= (TextView) findViewById(R.id.relate_tv);
		relate_tv_name= (TextView) findViewById(R.id.relate_tv_name);
		more_control = (ImageView) findViewById(R.id.more_control);
		more_control_rela = (RelativeLayout) findViewById(R.id.more_control_rela);
		more_control_image_view = (ImageView) findViewById(R.id.more_control_image_view);
		cancel_tv_name = (RelativeLayout) findViewById(R.id.cancel_tv_name);
		door_tv_name = (RelativeLayout) findViewById(R.id.door_tv_name);
		sj_tv_name=(RelativeLayout)findViewById(R.id.sj_tv_name);//@@
		ykq_tv_name=(RelativeLayout)findViewById(R.id.ykq_tv_name);//@@
		modify_dev_name = (RelativeLayout) findViewById(R.id.modify_dev_name);
		fk_tv_name = (RelativeLayout) findViewById(R.id.fk_tv_name);
		dev_share = (RelativeLayout) findViewById(R.id.dev_share);
		infrared_tv_name = (RelativeLayout) findViewById(R.id.infrared_tv_name);
		infrared_tv_name.setOnClickListener(this);
		gas_tv_name = (RelativeLayout) findViewById(R.id.gas_tv_name);
		gas_tv_name.setOnClickListener(this);
		socket_mImageView = (ImageView) findViewById(R.id.socket_mImageView);
		socket_mImageView.setOnClickListener(this);

		relate_image.setOnClickListener(this);
		openOrClose.setOnClickListener(this);
		mTimer.setOnClickListener(this);
		more_control.setOnClickListener(this);
		more_control_image_view.setOnClickListener(this);
		cancel_tv_name.setOnClickListener(this);
		fk_tv_name.setOnClickListener(this);
		door_tv_name.setOnClickListener(this);
		modify_dev_name.setOnClickListener(this);
		dev_share.setOnClickListener(this);
		sj_tv_name.setOnClickListener(this);
		ykq_tv_name.setOnClickListener(this);
		if (ocState == 1) {
			Bitmap mBitmap = BitmapCache.getInstance().getBitmap(R.drawable.bj_on,mContext);
			BitmapDrawable bd = new BitmapDrawable(mContext.getResources(), mBitmap);
			socket_info_bg.setBackground(bd);
			socket_mImageView.setBackgroundResource(R.drawable.dev_button);
			openOrClose.setImageResource(R.drawable.switch_selector_on);
			devStatus="开";
			mTextView.setText(devName+" "+devStatus);
			ocState = 0;
		} else {
			Bitmap mBitmap = readBitMap(mContext,R.drawable.bj_off);
			BitmapDrawable bd = new BitmapDrawable(mContext.getResources(), mBitmap);
			socket_info_bg.setBackground(bd);
			socket_mImageView.setBackgroundResource(R.drawable.dev_button_off);
			openOrClose.setImageResource(R.drawable.switch_selector);
			ocState = 1;
			devStatus="关";
			mTextView.setText(devName+" "+devStatus);
		}
		states = new byte[8];

		for (int i = 1; i < 8; i++) {
			states[i] = 0x00;
		}
		
	}
	
	 public static Bitmap readBitMap(Context context, int resId){
		 BitmapFactory.Options opt = new BitmapFactory.Options();
		 opt.inPreferredConfig = Bitmap.Config.RGB_565;
		 opt.inPurgeable = true;
		 opt.inInputShareable = true;
		 //获取资源图片
		 InputStream is = context.getResources().openRawResource(resId);
		 return BitmapFactory.decodeStream(is,null,opt);
	 }

	public void regFilter() {
		IntentFilter filter = new IntentFilter();
		filter.addAction("Constants.Action.unOpenOrCloseOrderPack");
		filter.addAction("Constants.Action.unServerACKPack");
		filter.addAction("Constants.Action.unActionCamera");
		filter.addAction("Constants.Action.unBinderCameraAndSocket");
		filter.addAction("Constants.Action.unFindBinderCameraAndSocket");
		filter.addAction("Constants.Action.unUserUnBinderCameraAndSocket");
		filter.addAction("Constants.Action.unBinderCameraAndSocketPk");
		filter.addAction("GET_CAMERA_MAC_ACTION");
		mContext.registerReceiver(mReceiver, filter);
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@SuppressLint("NewApi")
		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			if (arg1.getAction().equals("Constants.Action.unBinderCameraAndSocketPk")) {
				byte[] datas = arg1.getExtras().getByteArray("datasByte");
				String result = UnPackServer.unBinderCameraAndSocketPk(datas);
				if("success".equals(result)){
					relateResult=null;
					Toast.makeText(mContext, "解除成功", Toast.LENGTH_SHORT).show();
					relate_tv.setText("绑定");
				}else{
					Toast.makeText(mContext, "解除失败", Toast.LENGTH_SHORT).show();
				}
			}
			
			if (arg1.getAction().equals("Constants.Action.unUserUnBinderCameraAndSocket")) {
				byte[] datas = arg1.getExtras().getByteArray("datasByte");
				mUserDeviceList = UnPackServer.unUserUnBinderCameraAndSocket(datas);
				if(null!=mUserDeviceList&&mUserDeviceList.size()>0){
					showCameraList(mUserDeviceList);
				}else{
					Toast.makeText(mContext, "请先添加摄像头", Toast.LENGTH_SHORT).show();
				}
			}
			
			if (arg1.getAction().equals("Constants.Action.unBinderCameraAndSocket")) {
				byte[] datas = arg1.getExtras().getByteArray("datasByte");
				String result = UnPackServer.unBinderCameraAndSocket(datas);
				if("success".equals(result)){
					Toast.makeText(mContext, "绑定成功", Toast.LENGTH_SHORT).show();
					//更新ui
					byte[] orderSend =SendServerOrder.findBinderCameraAndSocket(mac);
					mSocketUDPClient.sendMsg(orderSend);
					relate_tv.setText("解绑");
				}else{
					Toast.makeText(mContext, "绑定失败", Toast.LENGTH_SHORT).show();
				}
				cameraMac=null;
				if(null!=dialog_loading){
					dialog_loading.dismiss();
				}
			}
			
			if (arg1.getAction().equals("GET_CAMERA_MAC_ACTION")) {
				cameraMac = arg1.getExtras().getString("devMac");
			}
			
			//获取插座是否关联摄像头
			if(arg1.getAction().equals("Constants.Action.unFindBinderCameraAndSocket")){
				byte[] datas = arg1.getExtras().getByteArray("datasByte");
				UnPackageFromServer mUnPackageFromServer = UnPackServer.unFindBinderCameraAndSocket(datas);
				//更新ui
				if(null!=mUnPackageFromServer){
					relateResult = mUnPackageFromServer.result;
					if("yes".equals(relateResult)){
						relate_image.setImageResource(R.drawable.relate_selector_on);
						String devName = mUnPackageFromServer.devName;
						relate_tv_name.setText("已与摄像头："+devName+"绑定");
						relate_tv.setText("解绑");
					}else{
						relate_image.setImageResource(R.drawable.relate_selector);
						relate_tv_name.setText("");
						relate_tv.setText("绑定");
					}
				}
			}
			
			if (arg1.getAction().equals(
					"Constants.Action.unOpenOrCloseOrderPack")) {
				byte[] datas = arg1.getExtras().getByteArray("datasByte");
				UnPackageFromServer mUnPackageFromServer = new UnPackServer().unOpenOrCloseOrderPack(datas);
				String receiveFlag = mUnPackageFromServer.devStates;
				if ("close".equals(receiveFlag)) {
					Bitmap mBitmap = BitmapCache.getInstance().getBitmap(R.drawable.bj_on,mContext);
					BitmapDrawable bd = new BitmapDrawable(mContext.getResources(), mBitmap);
					socket_info_bg.setBackground(bd);
					socket_mImageView.setBackgroundResource(R.drawable.dev_button);
					openOrClose.setImageResource(R.drawable.switch_selector_on);
					ocState = 0;
					devStatus = "开";
					mTextView.setText(devName+" "+devStatus);
				}
				if ("open".equals(receiveFlag)) {
					Bitmap mBitmap = BitmapCache.getInstance().getBitmap(R.drawable.bj_off,mContext);
					BitmapDrawable bd = new BitmapDrawable(mContext.getResources(), mBitmap);
					socket_info_bg.setBackground(bd);
					socket_mImageView.setBackgroundResource(R.drawable.dev_button_off);
					openOrClose.setImageResource(R.drawable.switch_selector);
					ocState = 1;
					devStatus = "关";
					mTextView.setText(devName+" "+devStatus);
				}
			}

			if (arg1.getAction().equals("Constants.Action.unServerACKPack")) {
				byte[] datas = arg1.getExtras().getByteArray("datasByte");
				new UnPackServer().unServerACKPack(datas);
			}
			if (arg1.getAction().equals("Constants.Action.unActionCamera")) {
				byte[] datas = arg1.getExtras().getByteArray("datasByte");
				UnPackageFromServer mUnPackageFromServer = UnPackServer.unActionCamera(datas);
				if(mUnPackageFromServer!=null){
					String binderResult = mUnPackageFromServer.binderResult;
					switch (binderResult) {
					case "success":
						devName = modifyName;
						mTextView.setText(devName+" "+devStatus);
						Toast.makeText(mContext, "修改成功", 1).show();
						break;
					case "failed":
						Toast.makeText(mContext, "修改失败", 1).show();
						break;
					default:
						break;
					}
				}
			}
			
		}
	};

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.relate_image:
			if(null!=relateResult&&"yes".equals(relateResult)){
				byte[] orderSend = SendServerOrder.unBinderCameraAndSocket(mac);
				mSocketUDPClient.sendMsg(orderSend);
			}else if(null!=relateResult&&"no".equals(relateResult)){
				byte[] orderSend = SendServerOrder.userUnBinderCameraAndSocket(fromUserNum,2);
				mSocketUDPClient.sendMsg(orderSend);
			}
			break;
		
		case R.id.socket_mImageView:
			//socket_mImageView.setBackgroundResource(R.drawable.dev_button);
			openOrColsed();
			break;
		case R.id.mTimer:
			Intent intent = new Intent(mContext, TimerListActivity.class);
			intent.putExtra("dwMac", mac);
			startActivity(intent);
			break;
		case R.id.open_or_close:
			openOrColsed();
			break;
		case R.id.more_control:
			more_control_rela.setVisibility(View.VISIBLE);
			Animation anim1 = AnimationUtils.loadAnimation(mContext,
					R.anim.slide_in_bottom);
			more_control_rela.startAnimation(anim1);
			break;
		case R.id.more_control_image_view:
			more_control_rela.setVisibility(View.GONE);
			Animation anim = AnimationUtils.loadAnimation(mContext,
					R.anim.slide_out_to_bottom);
			more_control_rela.startAnimation(anim);
			break;
		case R.id.cancel_tv_name:
			more_control_rela.setVisibility(View.GONE);
			Animation anim2 = AnimationUtils.loadAnimation(mContext,
					R.anim.slide_out_to_bottom);
			more_control_rela.startAnimation(anim2);
			break;
		case R.id.modify_dev_name:// 修改插座名称
			View vv = LayoutInflater.from(mContext).inflate(
					R.layout.modify_dev_name_dialog, null);
			TextView cancle_modify = (TextView) vv
					.findViewById(R.id.cancle_modify);
			TextView confire_modify = (TextView) vv
					.findViewById(R.id.confire_modify);
			final EditText modify_named = (EditText) vv
					.findViewById(R.id.modify_named);

			AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
			modifyDialog = builder.create();
			modifyDialog.show();
			modifyDialog.setContentView(vv);
			more_control_rela.setVisibility(View.GONE);
			modifyDialog.getWindow().clearFlags(
					WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
			Animation anim3 = AnimationUtils.loadAnimation(mContext,
					R.anim.slide_out_to_bottom);
			more_control_rela.startAnimation(anim3);
			cancle_modify.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					modifyDialog.dismiss();
				}
			});
			confire_modify.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					modifyName = modify_named.getText().toString()
							.trim();
					UserDevice mUserDevice = new UserDevice();
					mUserDevice.setUserNum(fromUserNum);
					mUserDevice.setCameraPwd("");
					mUserDevice.setDevName(modifyName);
					mUserDevice.setDevMac(mac);
					modifyDialog.dismiss();
					if(modifyName.length()>15){
						Toast.makeText(mContext, "输入名称过长，请重新输入", Toast.LENGTH_SHORT).show();//@@
					}else{
						byte[] orderSend =SendServerOrder.ModifyDev(mUserDevice,(byte)0x02);
						mSocketUDPClient.sendMsg(orderSend);
					}
					
				}
			});
			modify_named.setFocusable(true);
			modify_named.setText(devName);
			modify_named.setSelection(devName.length());//@@
			break;
		case R.id.fk_tv_name:// 烟感列表
			more_control_rela.setVisibility(View.GONE);
			Intent i = new Intent(mContext, AlarmTypeListActivity.class);
			i.putExtra("type", 1);
			i.putExtra("mac", mac);
			startActivity(i);
			break;
		case R.id.door_tv_name:// 门磁列表
			more_control_rela.setVisibility(View.GONE);
			Intent i1 = new Intent(mContext, AlarmTypeListActivity.class);
			i1.putExtra("type", 2);
			i1.putExtra("mac", mac);
			startActivity(i1);
			break;
		case R.id.gas_tv_name:// 燃气列表
			more_control_rela.setVisibility(View.GONE);
			Intent gas = new Intent(mContext, AlarmTypeListActivity.class);
			gas.putExtra("type", 4);
			gas.putExtra("mac", mac);
			startActivity(gas);
			break;
		case R.id.infrared_tv_name:// 红外列表
			more_control_rela.setVisibility(View.GONE);
			Intent infrared = new Intent(mContext, AlarmTypeListActivity.class);
			infrared.putExtra("type", 3);
			infrared.putExtra("mac", mac);
			startActivity(infrared);
			break;
		case R.id.sj_tv_name:// 水禁列表
			more_control_rela.setVisibility(View.GONE);
			Intent sj = new Intent(mContext, AlarmTypeListActivity.class);
			sj.putExtra("type", 5);
			sj.putExtra("mac", mac);
			startActivity(sj);
			break;
		case R.id.ykq_tv_name:// 遥控器列表
			more_control_rela.setVisibility(View.GONE);
			Intent ykq = new Intent(mContext, AlarmTypeListActivity.class);
			ykq.putExtra("type", 6);
			ykq.putExtra("mac", mac);
			startActivity(ykq);
			break;
		case R.id.dev_share:// 设备共享
			View shareView = LayoutInflater.from(mContext).inflate(
					R.layout.modify_dev_name_dialog, null);
			TextView shareView_cancle_modify = (TextView) shareView
					.findViewById(R.id.cancle_modify);
			TextView shareView_confire_modify = (TextView) shareView
					.findViewById(R.id.confire_modify);
			final EditText shareView_modify_named = (EditText) shareView
					.findViewById(R.id.modify_named);
			shareView_modify_named.setHint("请输入对方的账号");
			AlertDialog.Builder shareView_builder = new AlertDialog.Builder(
					mContext);
			modifyDialog = shareView_builder.create();
			modifyDialog.show();
			modifyDialog.setContentView(shareView);
			more_control_rela.setVisibility(View.GONE);
			modifyDialog.getWindow().clearFlags(
					WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
			Animation anim4 = AnimationUtils.loadAnimation(mContext,
					R.anim.slide_out_to_bottom);
			more_control_rela.startAnimation(anim4);
			shareView_cancle_modify.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					modifyDialog.dismiss();
				}
			});
			shareView_confire_modify.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					modifyDialog.dismiss();
					String toUserNum = shareView_modify_named.getText()
							.toString().trim();
					if (toUserNum != null && toUserNum.length() > 0&&!fromUserNum.equals(toUserNum)) {
						mSocketUDPClient.sendMsg(SendServerOrder.ShareDev(mac,
								fromUserNum, toUserNum));
					} else {
						Toast.makeText(mContext, "输入的账号有误，请输入对方的正确账号", 1).show();
					}
				}
			});
			shareView_modify_named.setFocusable(true);
			break;
		default:
			break;
		}
	}
	
	private AlertDialog dialog;
	private void showCameraList(List<UserDevice> list) {
		// TODO Auto-generated method stub
		View v = LayoutInflater.from(mContext).inflate(
				R.layout.dialog_camera_list, null);
		ListView listView = (ListView) v.findViewById(R.id.camera_list);
		TextView relate_camera_text = (TextView) v.findViewById(R.id.relate_camera_text);
		TextView cancel_relate_text = (TextView) v.findViewById(R.id.cancel_relate_text);
		CameraListAdapter mCameraListAdapter = new CameraListAdapter(mContext, list);
		listView.setAdapter(mCameraListAdapter);
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		dialog = builder.create();
		dialog.show();
		dialog.setContentView(v);
		dialog.setCancelable(false);
		cancel_relate_text.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				cameraMac=null;
			}
		});
		relate_camera_text.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(null!=cameraMac&&cameraMac.length()>0){
					//关联摄像头通信
					byte[] orderSend =SendServerOrder.binderCameraAndSocket(mac,cameraMac);
					mSocketUDPClient.sendMsg(orderSend);
					dialog.dismiss();
					showDialog();
				}else{
					Toast.makeText(mContext, "请选择需要关联的摄像头", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
	
	private NormalDialog dialog_loading;
	private void showDialog(){
		if(null==dialog_loading){
			dialog_loading = new NormalDialog(mContext,
					mContext.getResources().getString(R.string.saving),
					"","","");
			dialog_loading.setStyle(NormalDialog.DIALOG_STYLE_LOADING);
		}
		dialog_loading.showDialog();
	}

	private void openOrColsed() {
		states[0] = new IntegerTo16().algorismToHEXString(ocState);
		mSocketUDPClient.sendMsg(SendServerOrder.OpenOrCloseOrder(mac, states));
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(mReceiver);
		super.onDestroy();
	}

}
