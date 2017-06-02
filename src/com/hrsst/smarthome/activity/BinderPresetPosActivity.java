package com.hrsst.smarthome.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.hrsst.smarthome.R;
import com.hrsst.smarthome.global.Constants;
import com.hrsst.smarthome.net.SocketUDP;
import com.hrsst.smarthome.order.SendServerOrder;
import com.hrsst.smarthome.order.UnPackServer;
import com.hrsst.smarthome.pojo.BindingPreset;
import com.hrsst.smarthome.pojo.Contact;
import com.hrsst.smarthome.pojo.UnPackageFromServer;
import com.hrsst.smarthome.widget.NormalDialog;
import com.p2p.core.P2PHandler;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class BinderPresetPosActivity extends Activity implements OnClickListener{
	private Context mContext;
	private RelativeLayout defence_im0,defence_im1,defence_im2,defence_im3,defence_im4,
			defence_im5,defence_im6,defence_im7;
	private Contact contact;
	private boolean isRegFilter = false;
	private int[] status;
	private AlertDialog dialog;
	private int current_type;
	private NormalDialog dialog_loading;
	private int item;
	private String alarmType=null,yuzhiweiNumber=null;
	private SocketUDP mSocketUDP;
	private List<String> list;
	private List<BindingPreset> listBindingPreset;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_binder_preset_pos);
		mContext= this;
		contact = (Contact) getIntent().getSerializableExtra("contact");
		init();
		regFilter();
		mSocketUDP = SocketUDP.newInstance(Constants.SeverInfo.SERVER,
				Constants.SeverInfo.PORT);
		mSocketUDP.startAcceptMessage();
		getDefenceStatus();
	}

	private void init() {
		// TODO Auto-generated method stub
		TextView contact_name_tv = (TextView) findViewById(R.id.contact_name_tv);
		contact_name_tv.setText(contact.contactName);
		//8个绑定设备位置
		defence_im0 = (RelativeLayout) findViewById(R.id.defence_im0);
		defence_im1 = (RelativeLayout) findViewById(R.id.defence_im1);
		defence_im2 = (RelativeLayout) findViewById(R.id.defence_im2);
		defence_im3 = (RelativeLayout) findViewById(R.id.defence_im3);
		defence_im4 = (RelativeLayout) findViewById(R.id.defence_im4);
		defence_im5 = (RelativeLayout) findViewById(R.id.defence_im5);
		defence_im6 = (RelativeLayout) findViewById(R.id.defence_im6);
		defence_im7 = (RelativeLayout) findViewById(R.id.defence_im7);

		defence_im0.setOnClickListener(this);
		defence_im1.setOnClickListener(this);
		defence_im2.setOnClickListener(this);
		defence_im3.setOnClickListener(this);
		defence_im4.setOnClickListener(this);
		defence_im5.setOnClickListener(this);
		defence_im6.setOnClickListener(this);
		defence_im7.setOnClickListener(this);
	}
	
	public void regFilter(){
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.P2P.RET_SET_DEFENCE_AREA);
		filter.addAction(Constants.P2P.MESG_TYPE_RET_ALARM_TYPE_MOTOR_PRESET_POS);
		filter.addAction(Constants.P2P.RET_GET_DEFENCE_AREA);
		filter.addAction("STUDYCODE_ACTION");
		filter.addAction("YZWCODE_ACTION");
		filter.addAction("Constants.Action.BinderPreset");
		filter.addAction("Constants.Action.GetBinderPreset");
		mContext.registerReceiver(mReceiver, filter);
		isRegFilter = true;
	}
	
	private BroadcastReceiver mReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context arg0, Intent intent) {
			// TODO Auto-generated method stub
			if("Constants.Action.BinderPreset".equals(intent.getAction())){
				//绑定预置位
				byte[] datas = intent.getExtras().getByteArray("datasByte");
				String result = UnPackServer.unBinderPreset(datas);
				if(null!=result&&result.length()>0&&result.equals("success")){
					if(null!=dialog_loading){
						dialog_loading.dismiss();
						dialog_loading = null;
					}
					getDefenceStatus();
					Toast.makeText(mContext, R.string.bind_success, Toast.LENGTH_SHORT).show();
					alarmType=null;
					yuzhiweiNumber=null;
				}else{
					P2PHandler.getInstance().setDefenceAreaState(contact.contactId, 
							contact.contactPassword, 
							1, 
							item,
							Constants.P2P_SET.DEFENCE_AREA_SET.DEFENCE_AREA_TYPE_CLEAR);
					Toast.makeText(mContext, R.string.bind_fail, Toast.LENGTH_SHORT).show();
					alarmType=null;
					yuzhiweiNumber=null;
				}
			}
			if("Constants.Action.GetBinderPreset".equals(intent.getAction())){
				//获取绑定预置位
				byte[] datas = intent.getExtras().getByteArray("datasByte");
				UnPackageFromServer mUnPackageFromServer = UnPackServer.unGetBinderPreset(datas);
				Map<String,Integer> map = mUnPackageFromServer.map;
				//listBindingPreset
				
				if(null!=list&&list.size()>0){
					for(String s:list){
						int presetType = map.get(s);
						int pos = Integer.parseInt(s.substring(s.length()-1))-1;
						BindingPreset mBindingPreset = listBindingPreset.get(pos);
						mBindingPreset.presetType = presetType;
						listBindingPreset.set(pos, mBindingPreset);
					}
					initRela();
				}
			}
			
			if("STUDYCODE_ACTION".equals(intent.getAction())){
				alarmType = intent.getExtras().getString("alarmType");
			}
			if("YZWCODE_ACTION".equals(intent.getAction())){
				yuzhiweiNumber = intent.getExtras().getString("yuzhiweiNum");
			}
			
			if(intent.getAction().equals(Constants.P2P.RET_GET_DEFENCE_AREA)){
				@SuppressWarnings("unchecked")
				ArrayList<int[]> data = (ArrayList<int[]>) intent.getSerializableExtra("data");
				initData(data);
			}
			
			if(intent.getAction().equals(Constants.P2P.RET_SET_DEFENCE_AREA)){
				int result = intent.getIntExtra("result", -1);
				//学习成功
				if(result==Constants.P2P_SET.DEFENCE_AREA_SET.SETTING_SUCCESS){
					if(current_type == Constants.P2P_SET.DEFENCE_AREA_SET.DEFENCE_AREA_TYPE_CLEAR){
						if(null!=dialog){
							dialog.dismiss();
							dialog = null;
							Toast.makeText(mContext, R.string.clear_success, Toast.LENGTH_SHORT).show();
						}
						getDefenceStatus();
					}else{
						binderPreset(1,item,Integer.parseInt(yuzhiweiNumber));
					}
				}else{
					if(null!=dialog_loading){
						dialog_loading.dismiss();
						dialog_loading = null;
					}
					Toast.makeText(mContext,R.string.bind_fail, Toast.LENGTH_SHORT).show();
					alarmType=null;
					yuzhiweiNumber=null;
				}
			}
			
			if(intent.getAction().equals(Constants.P2P.MESG_TYPE_RET_ALARM_TYPE_MOTOR_PRESET_POS)){
				byte[] result = intent.getByteArrayExtra("result");
				if(0==(result[1]&0xff)){
					//发送服务器段绑定
					if(null!=alarmType&&alarmType.length()>0){
						int presetType = 0;
						switch (alarmType) {
							case "烟感探测器":
								presetType = 1;
								break;
							case "Smoke Detection":
								presetType = 1;
								break;
							case "门磁探测器":
								presetType = 2;
							case "Doorsensor":
								presetType = 2;
								break;
							case "燃气探测器":
								presetType = 4;
								break;
							case "Gas Detection":
								presetType = 4;
								break;
							case "红外探测器":
								presetType = 3;
								break;
							case "Infrared Detection":
								presetType = 3;
								break;
							case "水禁":
								presetType = 5;
								break;
							case "Water Level Detector":
								presetType = 5;
								break;
							case "万能遥控器":
								presetType = 6;
								break;
							case "Controler":
								presetType = 6;
								break;
							default:
								break;
						}
						String presetId = contact.contactId+"0"+(item+1);
						byte[] orderSend =SendServerOrder.binderPreset(presetId, presetType);
						mSocketUDP.sendMsg(orderSend);
					}
					
				}else{
					if(null!=dialog_loading){
						dialog_loading.dismiss();
						dialog_loading = null;
					}
					P2PHandler.getInstance().setDefenceAreaState(contact.contactId, 
							contact.contactPassword, 
							1, 
							item,
							Constants.P2P_SET.DEFENCE_AREA_SET.DEFENCE_AREA_TYPE_CLEAR);
					Toast.makeText(mContext, R.string.bind_fail, Toast.LENGTH_SHORT).show();
					alarmType=null;
					yuzhiweiNumber=null;
				}
			}
		}
		
	};
	/**
	 * 给8个预置位设置背景。。
	 */
	private void initRela(){
		for(BindingPreset mBindingPreset : listBindingPreset){
			int id = mBindingPreset.id;
			int presetType = mBindingPreset.presetType;
			switch (id) {
				case 0:
					switch (presetType) {
						case 1:
							defence_im0.setBackgroundResource(R.drawable.delete_defence_image_selector1);
							break;
						case 2:
							defence_im0.setBackgroundResource(R.drawable.delete_defence_image_selector2);						
							break;
						case 3:
							defence_im0.setBackgroundResource(R.drawable.delete_defence_image_selector3);
							break;
						case 4:
							defence_im0.setBackgroundResource(R.drawable.delete_defence_image_selector4);
							break;
						case 5:
							defence_im0.setBackgroundResource(R.drawable.delete_defence_image_selector5);
							break;
						case 6:
							defence_im0.setBackgroundResource(R.drawable.delete_defence_image_selector6);
							break;
						default:
							defence_im0.setBackgroundResource(R.drawable.add_defence_image_selector1);
							break;
					}
					break;
				case 1:
					switch (presetType) {
						case 1:
							defence_im1.setBackgroundResource(R.drawable.delete_defence_image_selector1);
							break;
						case 2:
							defence_im1.setBackgroundResource(R.drawable.delete_defence_image_selector2);						
							break;
						case 3:
							defence_im1.setBackgroundResource(R.drawable.delete_defence_image_selector3);
							break;
						case 4:
							defence_im1.setBackgroundResource(R.drawable.delete_defence_image_selector4);
							break;
						case 5:
							defence_im1.setBackgroundResource(R.drawable.delete_defence_image_selector5);
							break;
						case 6:
							defence_im1.setBackgroundResource(R.drawable.delete_defence_image_selector6);
							break;
						default:
							defence_im1.setBackgroundResource(R.drawable.add_defence_image_selector1);
							break;
					}					
					break;
				case 2:
					switch (presetType) {
						case 1:
							defence_im2.setBackgroundResource(R.drawable.delete_defence_image_selector1);
							break;
						case 2:
							defence_im2.setBackgroundResource(R.drawable.delete_defence_image_selector2);						
							break;
						case 3:
							defence_im2.setBackgroundResource(R.drawable.delete_defence_image_selector3);
							break;
						case 4:
							defence_im2.setBackgroundResource(R.drawable.delete_defence_image_selector4);
							break;
						case 5:
							defence_im2.setBackgroundResource(R.drawable.delete_defence_image_selector5);
							break;
						case 6:
							defence_im2.setBackgroundResource(R.drawable.delete_defence_image_selector6);
							break;
						default:
							defence_im2.setBackgroundResource(R.drawable.add_defence_image_selector1);
							break;
					}
					break;
				case 3:
					switch (presetType) {
						case 1:
							defence_im3.setBackgroundResource(R.drawable.delete_defence_image_selector1);
							break;
						case 2:
							defence_im3.setBackgroundResource(R.drawable.delete_defence_image_selector2);						
							break;
						case 3:
							defence_im3.setBackgroundResource(R.drawable.delete_defence_image_selector3);
							break;
						case 4:
							defence_im3.setBackgroundResource(R.drawable.delete_defence_image_selector4);
							break;
						case 5:
							defence_im3.setBackgroundResource(R.drawable.delete_defence_image_selector5);
							break;
						case 6:
							defence_im3.setBackgroundResource(R.drawable.delete_defence_image_selector6);
							break;
						default:
							defence_im3.setBackgroundResource(R.drawable.add_defence_image_selector1);
							break;
					}
					break;
				case 4:
					switch (presetType) {
						case 1:
							defence_im4.setBackgroundResource(R.drawable.delete_defence_image_selector1);
							break;
						case 2:
							defence_im4.setBackgroundResource(R.drawable.delete_defence_image_selector2);						
							break;
						case 3:
							defence_im4.setBackgroundResource(R.drawable.delete_defence_image_selector3);
							break;
						case 4:
							defence_im4.setBackgroundResource(R.drawable.delete_defence_image_selector4);
							break;
						case 5:
							defence_im4.setBackgroundResource(R.drawable.delete_defence_image_selector5);
							break;
						case 6:
							defence_im4.setBackgroundResource(R.drawable.delete_defence_image_selector6);
							break;

						default:
							defence_im4.setBackgroundResource(R.drawable.add_defence_image_selector1);
							break;
					}
					break;
				case 5:
					switch (presetType) {
						case 1:
							defence_im5.setBackgroundResource(R.drawable.delete_defence_image_selector1);
							break;
						case 2:
							defence_im5.setBackgroundResource(R.drawable.delete_defence_image_selector2);						
							break;
						case 3:
							defence_im5.setBackgroundResource(R.drawable.delete_defence_image_selector3);
							break;
						case 4:
							defence_im5.setBackgroundResource(R.drawable.delete_defence_image_selector4);
							break;
						case 5:
							defence_im5.setBackgroundResource(R.drawable.delete_defence_image_selector5);
							break;
						case 6:
							defence_im5.setBackgroundResource(R.drawable.delete_defence_image_selector6);
							break;

						default:
							defence_im5.setBackgroundResource(R.drawable.add_defence_image_selector1);
							break;
					}
					break;
				case 6:
					switch (presetType) {
						case 1:
							defence_im6.setBackgroundResource(R.drawable.delete_defence_image_selector1);
							break;
						case 2:
							defence_im6.setBackgroundResource(R.drawable.delete_defence_image_selector2);						
							break;
						case 3:
							defence_im6.setBackgroundResource(R.drawable.delete_defence_image_selector3);
							break;
						case 4:
							defence_im6.setBackgroundResource(R.drawable.delete_defence_image_selector4);
							break;
						case 5:
							defence_im6.setBackgroundResource(R.drawable.delete_defence_image_selector5);
							break;
						case 6:
							defence_im6.setBackgroundResource(R.drawable.delete_defence_image_selector6);
							break;

						default:
							defence_im6.setBackgroundResource(R.drawable.add_defence_image_selector1);
							break;
						
					}
					break;
				case 7:
					switch (presetType) {
						case 1:
							defence_im7.setBackgroundResource(R.drawable.delete_defence_image_selector1);
							break;
						case 2:
							defence_im7.setBackgroundResource(R.drawable.delete_defence_image_selector2);						
							break;
						case 3:
							defence_im7.setBackgroundResource(R.drawable.delete_defence_image_selector3);
							break;
						case 4:
							defence_im7.setBackgroundResource(R.drawable.delete_defence_image_selector4);
							break;
						case 5:
							defence_im7.setBackgroundResource(R.drawable.delete_defence_image_selector5);
							break;
						case 6:
							defence_im7.setBackgroundResource(R.drawable.delete_defence_image_selector6);
							break;

						default:
							defence_im7.setBackgroundResource(R.drawable.add_defence_image_selector1);
							break;
					}
					break;
				default:
					break;
			}
		}
	}
	
	private void initData(ArrayList<int[]> data){
		status = data.get(1);
		list = new ArrayList<String>();
		String id = contact.contactId;
		listBindingPreset = new ArrayList<BindingPreset>();
		for(int j=0;j<status.length;j++){
			String presetId = id+"0"+(j+1);
			if(status[j]==0){
				//获取绑定预置位
				list.add(presetId);
			}
			BindingPreset mBindingPreset = new BindingPreset();
			mBindingPreset.id=j;
			mBindingPreset.presetId = presetId;
			listBindingPreset.add(mBindingPreset);
		}
		if(null!=list&&list.size()>0){
			byte[] orderSend =SendServerOrder.getBinderPreset(list);
			mSocketUDP.sendMsg(orderSend);
		}else{
			initRela();
		}
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.defence_im0:
			if(null!=status&&status.length>0&&status[0]==0){
				clearDefence(0);
			}else{
				item= 0;
				configDev(item);
			}
			break;
		case R.id.defence_im1:
			if(null!=status&&status.length>0&&status[1]==0){
				clearDefence(1);
			}else{
				item= 1;
				configDev(item);
			}
			break;
		case R.id.defence_im2:
			if(null!=status&&status.length>0&&status[2]==0){
				clearDefence(2);
			}else{
				item= 2;
				configDev(item);
			}
			break;
		case R.id.defence_im3:
			if(null!=status&&status.length>0&&status[3]==0){
				clearDefence(3);
			}else{
				item= 3;
				configDev(item);
			}
			break;
		case R.id.defence_im4:
			if(null!=status&&status.length>0&&status[4]==0){
				clearDefence(4);
			}else{
				item= 4;
				configDev(item);
			}
			break;
		case R.id.defence_im5:
			if(null!=status&&status.length>0&&status[5]==0){
				clearDefence(5);
			}else{
				item= 5;
				configDev(item);
			}
			break;
		case R.id.defence_im6:
			if(null!=status&&status.length>0&&status[6]==0){
				clearDefence(6);
			}else{
				item= 6;
				configDev(item);
			}
			break;
		case R.id.defence_im7:
			if(null!=status&&status.length>0&&status[7]==0){
				clearDefence(7);
			}else{
				item= 7;
				configDev(item);
			}
			break;
		default:
			break;
		}
	}
	/**
	 * 选择提示框显示并操作。。
	 * @param item
	 */
	private void configDev(final int item){
		final NormalDialog dialog = new NormalDialog(
				mContext,
				mContext.getResources().getString(R.string.learing_code),
				mContext.getResources().getString(R.string.learing_code_prompt),
				mContext.getResources().getString(R.string.bind),
				mContext.getResources().getString(R.string.cancel)
				);
		dialog.setOnButtonOkListener(new NormalDialog.OnButtonOkListener() {
			
			@Override
			public void onClick() {
				// TODO Auto-generated method stub
				if(alarmType==null||yuzhiweiNumber==null){
					Toast.makeText(mContext,R.string.please_choose_chuanganqi_and_preset_position, Toast.LENGTH_SHORT).show();
					return;
				}
				if(null==dialog_loading){
					dialog_loading = new NormalDialog(mContext,
							mContext.getResources().getString(R.string.studying),
							"","","");
					dialog_loading.setStyle(NormalDialog.DIALOG_STYLE_LOADING);
				}
				dialog_loading.showDialog();
				study(1,item);
				dialog.cancel();
			}
		});
		dialog.setOnButtonCancelListener(new NormalDialog.OnButtonCancelListener() {
			
			@Override
			public void onClick() {
				// TODO Auto-generated method stub
				alarmType=null;
				yuzhiweiNumber=null;
			}
		});
		dialog.showSelectDialog(1);
	}
	
	private void study(int group,int item){
		current_type = Constants.P2P_SET.DEFENCE_AREA_SET.DEFENCE_AREA_TYPE_LEARN;
		P2PHandler.getInstance().setDefenceAreaState(contact.contactId, 
				contact.contactPassword, 
				group, item ,
				Constants.P2P_SET.DEFENCE_AREA_SET.DEFENCE_AREA_TYPE_LEARN);		
	}
	
	private void binderPreset(int areaID,int channelID,int yuzhiweiNum){
		byte bPresetNum = (byte) (yuzhiweiNum-1);
		byte[] datas = new byte[7];
		datas[0] = 90;
		datas[1] = 0;
		datas[2] = 1;
		datas[3] = 0;
		datas[4] = (byte) (areaID-1);
		datas[5] = (byte) channelID;
		datas[6] = bPresetNum;
		P2PHandler.getInstance().sMesgSetAlarmPresetMotorPos(contact.contactId,contact.contactPassword,datas);
	}
	
	private void getDefenceStatus(){
		P2PHandler.getInstance().getDefenceArea(contact.contactId, contact.contactPassword);
	}
	/**
	 * 删除布放设备。。
	 * @param item
	 */
	private void clearDefence(final int item){
		View v = LayoutInflater.from(mContext).inflate(
				R.layout.relate_dialog, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		dialog = builder.create();
		dialog.show();
		dialog.setContentView(v);
		RelativeLayout cancle_delete = (RelativeLayout) v.findViewById(R.id.relate_cancle_delete);
		RelativeLayout confire_delete = (RelativeLayout) v.findViewById(R.id.relate_confire_delete);
		cancle_delete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		confire_delete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				current_type = Constants.P2P_SET.DEFENCE_AREA_SET.DEFENCE_AREA_TYPE_CLEAR;
				P2PHandler.getInstance().setDefenceAreaState(contact.contactId, 
						contact.contactPassword, 
						1, 
						item,
						Constants.P2P_SET.DEFENCE_AREA_SET.DEFENCE_AREA_TYPE_CLEAR);
			}
		});		
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (isRegFilter == true) {
			mContext.unregisterReceiver(mReceiver);
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK){
			Intent i = new Intent(mContext,ApMonitorActivity.class);
			i.putExtra("contact",contact);
			startActivity(i);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
}
