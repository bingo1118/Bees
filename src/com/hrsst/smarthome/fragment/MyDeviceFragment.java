package com.hrsst.smarthome.fragment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hrsst.smarthome.AirDevInfoActivity;
import com.hrsst.smarthome.R;
import com.hrsst.smarthome.activity.AddDeviceStepActivity;
import com.hrsst.smarthome.activity.ApMonitorActivity;
import com.hrsst.smarthome.activity.DeviceInfoActivity;
import com.hrsst.smarthome.activity.IntroducedNextOneActivity;
import com.hrsst.smarthome.adapter.ChoiceWifiAdapter;
import com.hrsst.smarthome.adapter.PullToRefreshGridViewAdapter;
import com.hrsst.smarthome.global.Constants;
import com.hrsst.smarthome.mygridview.lib.PullToRefreshBase.OnRefreshListener;
import com.hrsst.smarthome.mygridview.lib.PullToRefreshGridView;
import com.hrsst.smarthome.net.SocketUDP;
import com.hrsst.smarthome.order.SendServerOrder;
import com.hrsst.smarthome.order.UnPackServer;
import com.hrsst.smarthome.pojo.Contact;
import com.hrsst.smarthome.pojo.DeviceStates;
import com.hrsst.smarthome.pojo.EnvironmentInfo;
import com.hrsst.smarthome.pojo.UnPackageFromServer;
import com.hrsst.smarthome.pojo.UserDevice;
import com.hrsst.smarthome.thread.MainThread;
import com.hrsst.smarthome.util.BitmapCache;
import com.hrsst.smarthome.util.SharedPreferencesManager;
import com.hrsst.smarthome.widget.MarqueeTextView;
import com.p2p.core.P2PHandler;

public class MyDeviceFragment extends Fragment implements OnClickListener{
	private View view;
	private TextView menu;
	private TextView add_device;
	private MarqueeTextView new_dev_num_tv;
	private Context mContext;
	private PullToRefreshGridView mPullToRefreshGridView;
	private GridView mGridView;
	private ViewGroup contentContainer;
	private SocketUDP mSocketUDPClient;
	private Timer mTimer;
	private List<UserDevice> mUserDeviceList;
	
	private List<String> macList;
	private List<String> cameraList;
	private String userNum;
	private PullToRefreshGridViewAdapter mPullToRefreshGridViewAdapter;
	private AlertDialog dialog,loadingDialog;
	private RelativeLayout new_dev_rela;
	private ArrayList<String> wifiList;
	private AlertDialog modifyDialog;
	private ChoiceWifiAdapter mChoiceDevAdapter;
	private int defencePos;
	private UserDevice mUserDevice;
	private List<UserDevice> mSmartSocketList;//@@���ܲ����б�
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.activity_mydevice_fragment, null);
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		mContext = getActivity();
		userNum = SharedPreferencesManager.getInstance().getData(mContext,  Constants.UserInfo.USER_NUMBER);
		mSocketUDPClient = SocketUDP.newInstance(Constants.SeverInfo.SERVER
				, Constants.SeverInfo.PORT);
		mSocketUDPClient.startAcceptMessage();
		init();
	}
	
	private void regFilter1(){
		IntentFilter filter1 = new IntentFilter();
		filter1.addAction("Constants.Action.unFindBinderCameraAndSocket");
		filter1.addAction("Constants.Action.unBinderCameraAndSocketPk");
		mContext.registerReceiver(mReceiver1, filter1);
	}
	
	private BroadcastReceiver mReceiver1 = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			//��ȡ�����Ƿ��������ͷ
			if(arg1.getAction().equals("Constants.Action.unFindBinderCameraAndSocket")){
				byte[] datas = arg1.getExtras().getByteArray("datasByte");
				UnPackageFromServer mUnPackageFromServer = UnPackServer.unFindBinderCameraAndSocket(datas);
				if(null!=mUnPackageFromServer){
					String relateResult = mUnPackageFromServer.result;
					if("yes".equals(relateResult)){
						String mac = mUnPackageFromServer.devMac;
						byte[] orderSend = SendServerOrder.unBinderCameraAndSocket(mac);
						mSocketUDPClient.sendMsg(orderSend);
					}else{
						mContext.unregisterReceiver(mReceiver1);
						byte[] orderSend =SendServerOrder.ModifyDev(mUserDevice,(byte)0x01);
						mSocketUDPClient.sendMsg(orderSend);
					}
				}
			}
			
			if (arg1.getAction().equals("Constants.Action.unBinderCameraAndSocketPk")) {
				byte[] datas = arg1.getExtras().getByteArray("datasByte");
				String result = UnPackServer.unBinderCameraAndSocketPk(datas);
				if("success".equals(result)){
					byte[] orderSend =SendServerOrder.ModifyDev(mUserDevice,(byte)0x01);
					mSocketUDPClient.sendMsg(orderSend);
				}else{
					if(null!=loadingDialog){
						loadingDialog.dismiss();
						loadingDialog=null;
					}
				}
				mContext.unregisterReceiver(mReceiver1);
			}
		}
		
	};

	private void regFilter() {
		IntentFilter filter = new IntentFilter();
		filter.addAction("Constants.Action.DATA_CHANGE");
		filter.addAction("Constants.Action.unGetDeviceStatesListPack");
		filter.addAction("Constants.Action.unServerACKPack");
		filter.addAction("FIND_NEW_DEVICE_NUMBER");
		filter.addAction("DEFENCE_ACTION");
		filter.addAction("Constants.Action.unDefence");
		filter.addAction("Constants.Action.unGetUserDev");
		filter.addAction("Constants.Action.unActionCamera");
		filter.addAction(Constants.Action.GET_FRIENDS_STATE);
		filter.addAction(Constants.P2P.RET_GET_REMOTE_DEFENCE);
		filter.addAction(Constants.P2P.RET_SET_REMOTE_DEFENCE);
		filter.addAction("REFRESH_DEV_INFO");
		mContext.registerReceiver(mReceiver, filter);
	}
	
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@SuppressWarnings("unchecked")
		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			if(arg1.getAction().equals("REFRESH_DEV_INFO")){
				getUserDev();
			}
			
			if (arg1.getAction().equals(Constants.P2P.RET_SET_REMOTE_DEFENCE)) {
				int state = arg1.getIntExtra("state", -1);
				if(state==0||state==1){
					String contactId = mPullToRefreshGridViewAdapter.getCameraId();
					String contactPwd = mPullToRefreshGridViewAdapter.getCameraPwd();
					if(null!=contactId&&contactId.length()>0&&null!=contactPwd&&contactPwd.length()>0){
						P2PHandler.getInstance().getDefenceStates(
								contactId, contactPwd);
					}
				}
			}
			
			if (arg1.getAction().equals(
					Constants.P2P.RET_GET_REMOTE_DEFENCE)) {
				int state = arg1.getIntExtra("state", -1);
				String contactId = arg1.getStringExtra("contactId");
				System.out.println("state="+state);
				if((state==0||state==1)&&null!=contactId&&contactId.length()>0){
					Map<String, Integer> map = mPullToRefreshGridViewAdapter.getPos();
					if(null!=map&&map.size()>0){
						String indexStr = map.get(contactId)+"";
						if(null!=indexStr&&indexStr.length()>0){
							int index = map.get(contactId);
							mPullToRefreshGridViewAdapter.setGridView(mGridView);
							mPullToRefreshGridViewAdapter.cameraDefence(index, state);
						}
					}
					
				}
			}
			
			if(arg1.getAction().equals(Constants.Action.GET_FRIENDS_STATE)){
				Map<String,Integer> li = (Map<String,Integer>) arg1.getSerializableExtra("contactList");
				if(null!=mUserDeviceList&&mUserDeviceList.size()>0&&null!=li&&li.size()>0){
					Log.v("li", li.size()+"");
					//��������ͷ����
					Integer[] cameraPos = mPullToRefreshGridViewAdapter.getCameraPos();
					List<UserDevice> mUserDeviceAdapterList = new ArrayList<UserDevice>();
					if(null!=cameraPos&&cameraPos.length>0){
						for(int i=0;i<li.size();i++){
							UserDevice mUserDevice = mUserDeviceList.get(cameraPos[i]);
							mUserDevice.setId(cameraPos[i]);
							mUserDevice.setLightOnOrOutLine(li.get(mUserDevice.getDevMac()));
							mUserDeviceAdapterList.add(mUserDevice);
						}
					}
					mPullToRefreshGridViewAdapter.setGridView(mGridView);
					mPullToRefreshGridViewAdapter.updateCameraData(mUserDeviceAdapterList);
				}
				
			}
			
			//ɾ���豸�ظ���
			if(arg1.getAction().equals("Constants.Action.unActionCamera")){
				byte[] datas = arg1.getExtras().getByteArray("datasByte");
				UnPackageFromServer mUnPackageFromServer = UnPackServer.unActionCamera(datas);
				if(mUnPackageFromServer!=null){
					String binderResult = mUnPackageFromServer.binderResult;
					switch (binderResult) {
					case "success":
						Toast.makeText(mContext, "ɾ���ɹ�", Toast.LENGTH_SHORT).show();
						getUserDev();
						break;
					case "failed":
						Toast.makeText(mContext, "ɾ��ʧ��", Toast.LENGTH_SHORT).show();
						break;
					default:
						break;
					}
					if(null!=loadingDialog&&loadingDialog.isShowing()){
						loadingDialog.dismiss();
						loadingDialog=null;
					}
				}
			}
			//��ȡ�û��豸�ظ���
			if(arg1.getAction().equals("Constants.Action.unGetUserDev")){
				byte[] datas = arg1.getExtras().getByteArray("datasByte");
				UnPackageFromServer mUnPackageFromServer = UnPackServer.unGetUserDev(datas);
				if(mUnPackageFromServer!=null){
					mUserDeviceList = mUnPackageFromServer.userDeviceList;
					macList = mUnPackageFromServer.macList;//����mac�б�
					cameraList = mUnPackageFromServer.cameraList;//����ͷmac�б�
					mPullToRefreshGridViewAdapter = new PullToRefreshGridViewAdapter(mContext,mUserDeviceList);
					mPullToRefreshGridViewAdapter.setGridView(mGridView);
					mGridView.setAdapter(mPullToRefreshGridViewAdapter);
					mPullToRefreshGridView.onRefreshComplete();
					String[] contactIds = new String[cameraList.size()];//����ͷmac����
					for(int i=0;i<cameraList.size();i++){
						contactIds[i] = cameraList.get(i);
					}
					byte[] orderSend =SendServerOrder.GetDeviceStatesList(macList);//��ȡ�豸״̬ʹ��0x02
					MainThread.setByte(orderSend,0,contactIds);//���ͻ�ȡ�豸״̬����
					MainThread.refreash();
					
					mSmartSocketList = new ArrayList<UserDevice>();//���ܲ����б�����
					for(UserDevice u:mUserDeviceList){
						if(u.getDevType()==1){
							mSmartSocketList.add(u);//���Ӳ������͡���
						}//@@
					}
				}else{
					mUserDeviceList = new ArrayList<UserDevice>();
					mPullToRefreshGridViewAdapter = new PullToRefreshGridViewAdapter(mContext,mUserDeviceList);
					mGridView.setAdapter(mPullToRefreshGridViewAdapter);
					mPullToRefreshGridView.onRefreshComplete();
				}
			}
			
			//���ղ������߳�������
			if(arg1.getAction().equals("DEFENCE_ACTION")){
				defencePos = arg1.getExtras().getInt("defencePos");
				int defenceType = arg1.getExtras().getInt("defenceType");
				String devMac = arg1.getExtras().getString("devMac");
				byte[] orderSend =SendServerOrder.Defence(userNum, devMac, (byte)defenceType);
				mSocketUDPClient.sendMsg(orderSend);
			}
			if(arg1.getAction().equals("Constants.Action.unDefence")){
				byte[] datas = arg1.getExtras().getByteArray("datasByte");
				UnPackageFromServer mUnPackageFromServer = UnPackServer.unDefence(datas);
				int result = mUnPackageFromServer.defence;
				mPullToRefreshGridViewAdapter.setGridView(mGridView);
				mPullToRefreshGridViewAdapter.defence(defencePos,result);
			}
			
			if (arg1.getAction().equals("Constants.Action.DATA_CHANGE")){
				byte[] datas = arg1.getExtras().getByteArray("datas");
				UnPackageFromServer mUnPackageFromServer = new UnPackServer().unGetDeviceStatesListPack(datas);
				List<Map<String,DeviceStates>> listMap = mUnPackageFromServer.deviceStatesList;
				um(listMap);
			}

			if (arg1.getAction().equals("Constants.Action.unGetDeviceStatesListPack")){
				byte[] datas = arg1.getExtras().getByteArray("datasByte");
				if(datas!=null&&datas.length>0){
					UnPackageFromServer mUnPackageFromServer = new UnPackServer().unGetDeviceStatesListPack(datas);
					List<Map<String,DeviceStates>> listMap = mUnPackageFromServer.deviceStatesList;
					if(listMap!=null&&listMap.size()>0){
						um(listMap);
					}
				}
			}
			
			if (arg1.getAction().equals("Constants.Action.unServerACKPack")){
				byte[] datas = arg1.getExtras().getByteArray("datasByte");
				new UnPackServer().unServerACKPack(datas);
			}
			
			if(arg1.getAction().equals("FIND_NEW_DEVICE_NUMBER")){
				wifiList = arg1.getExtras().getStringArrayList("count");
				if(wifiList.size()>0){
					new_dev_rela.setVisibility(View.GONE);
					new_dev_num_tv.setText("    ������Ϣ������ "+wifiList.size()+""+"  �����豸      ");
				}else{
					new_dev_rela.setVisibility(View.GONE);
				}
			}
		}
	};
	
	private void getUserDev(){
		byte[] orderSend =SendServerOrder.GetUserDev(userNum);
		mSocketUDPClient.sendMsg(orderSend);
	}
//	Handler handler=new Handler(){
//		@Override
//		public void handleMessage(Message msg) {
//			super.handleMessage(msg);
//			if(msg.what==0){
//				mPullToRefreshGridViewAdapter = new PullToRefreshGridViewAdapter(mContext,mUserDeviceList);
//				mPullToRefreshGridViewAdapter.setGridView(mGridView);
//				mGridView.setAdapter(mPullToRefreshGridViewAdapter);
//				mPullToRefreshGridView.onRefreshComplete();
//				mSmartSocketList = new ArrayList<UserDevice>();//���ܲ����б�����
//				cameraList=new ArrayList<String>();//�����mac��ַ��@@
//				for(UserDevice u:mUserDeviceList){
//					if(u.getDevType()==1){
//						mSmartSocketList.add(u);//���Ӳ������͡���
//					}
//					if(u.getDevType()==2){
//						cameraList.add(u.getDevMac());
//					}//@@
//			    }
//				
//				//��ȡ����ͷ״̬
//				String[] contactIds = new String[cameraList.size()];//����ͷmac����
//				for(int i=0;i<cameraList.size();i++){
//					contactIds[i] = cameraList.get(i);
//				}
//				MainThread.setByte(0,contactIds);//���ͻ�ȡ�豸״̬����
//				MainThread.refreash();
//				
//			}
//			if(msg.what!=0){
//				mUserDeviceList = new ArrayList<UserDevice>();
//				mPullToRefreshGridViewAdapter = new PullToRefreshGridViewAdapter(mContext,mUserDeviceList);
//				mGridView.setAdapter(mPullToRefreshGridViewAdapter);
//				mPullToRefreshGridView.onRefreshComplete();
//				Toast.makeText(mContext, "��ȡ�豸ʧ��", Toast.LENGTH_SHORT).show();
//				
//		}
//		}
//	};
//	private void getUserDev(){//��ȡ�豸�б�@@
//		new HttpThreadGetDev(userNum, handler).start();
////		mPullToRefreshGridViewAdapter = new PullToRefreshGridViewAdapter(mContext,mUserDeviceList);
////		mPullToRefreshGridViewAdapter.setGridView(mGridView);
////		mGridView.setAdapter(mPullToRefreshGridViewAdapter);
////		mPullToRefreshGridView.onRefreshComplete();
//	}
	
	private void init(){
		Bitmap mBitmap = BitmapCache.getInstance().getBitmap(R.drawable.yetoutu, mContext);
		BitmapDrawable bd = new BitmapDrawable(mContext.getResources(), mBitmap);
		ImageView main_image = (ImageView) view.findViewById(R.id.main_image);
		main_image.setBackground(bd);
		menu = (TextView) view.findViewById(R.id.menu);
		add_device = (TextView) view.findViewById(R.id.add_device);
		new_dev_num_tv = (MarqueeTextView) view.findViewById(R.id.new_dev_num_tv);
		//������ʾ 
		new_dev_num_tv.setSingleLine(true);
		//��ʾ��ʽ��������
		new_dev_num_tv.setEllipsize(TruncateAt.MARQUEE);
		//�������ظ����������ߴ�
		new_dev_num_tv.setMarqueeRepeatLimit(-1);
		new_dev_num_tv.setGravity(Gravity.CENTER_HORIZONTAL);
		//��touch�۽�
		new_dev_num_tv.setFocusableInTouchMode(true);
		new_dev_rela = (RelativeLayout) view.findViewById(R.id.new_dev_rela);
		new_dev_rela.setOnClickListener(this);
		menu.setOnClickListener(this);
		add_device.setOnClickListener(this);
		contentContainer = (ViewGroup) view.findViewById(R.id.contentContainer);
		LayoutInflater.from(mContext).inflate(R.layout.pull_to_refresh_gridview, contentContainer);
		mPullToRefreshGridView = (PullToRefreshGridView) view.findViewById(R.id.pull_refresh_grid);
		mGridView = mPullToRefreshGridView.getRefreshableView();
		mPullToRefreshGridView.setOnDownPullRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				new MainThread(mContext).getWifiSSID();
				getUserDev();
				if(null==mTimer){
					mTimer = new Timer();
				}
				setTimerdoAction(doAction,mTimer);
			}
		});
		//����¼�����
		mGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {
				// TODO Auto-generated method stub
				mSmartSocketList = new ArrayList<UserDevice>();//���ܲ����б�����
				for(UserDevice u:mUserDeviceList){
					if(u.getDevType()==1){
						mSmartSocketList.add(u);//���Ӳ������͡���
					}
				}
				if(pos==mUserDeviceList.size()){
					Intent intent = new Intent(mContext,AddDeviceStepActivity.class);
					intent.putExtra("devList", (Serializable)mSmartSocketList);
					intent.putExtra("cameraList", (Serializable)cameraList);
					startActivity(intent);
				}else{
					UserDevice itemUserDevice = (UserDevice) arg0.getItemAtPosition(pos);
					int type = itemUserDevice.getDevType();
					int onOrOutLine = itemUserDevice.getLightOnOrOutLine();
					switch (type) {
					case 1:
						if(onOrOutLine==1){
							
							Intent intent = new Intent(mContext,DeviceInfoActivity.class);
							intent.putExtra("mac", itemUserDevice.getDevMac());
							intent.putExtra("devName", itemUserDevice.getDevName());
							intent.putExtra("ocState", itemUserDevice.getSocketStates());
							startActivity(intent);
						}else{
							Toast.makeText(mContext, "�豸����", Toast.LENGTH_SHORT).show();
						}
						break;
					case 2:
						if(onOrOutLine==1){
							Contact mContact = new Contact();
							mContact.contactType=0;
							mContact.contactId=itemUserDevice.getDevMac();
							mContact.contactPassword = itemUserDevice.getCameraPwd();
							mContact.contactName = itemUserDevice.getDevName();
							mContact.apModeState = 1;
							
							Intent monitor = new Intent();
							monitor.setClass(mContext, ApMonitorActivity.class);
							monitor.putExtra("contact", mContact);
							monitor.putExtra("connectType",Constants.ConnectType.P2PCONNECT);
							monitor.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							mContext.startActivity(monitor);
						}else{
							Toast.makeText(mContext, "�豸����", Toast.LENGTH_SHORT).show();
						}
						break;
					case 3:
							Intent monitor = new Intent();
							monitor.setClass(mContext, AirDevInfoActivity.class);
							monitor.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							mContext.startActivity(monitor);
						
						break;
					default:
						break;
					}
				}
			}
		});
		mGridView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub
				int count = mUserDeviceList.size();
				if(position<count){
					View v = LayoutInflater.from(mContext).inflate(
							R.layout.long_click_dialog, null);
					AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
					dialog = builder.create();
					dialog.show();
					dialog.setContentView(v);
					
					final UserDevice mU = mUserDeviceList.get(position);
					TextView cancle_delete = (TextView) v.findViewById(R.id.cancle_delete);
					TextView confire_delete = (TextView) v.findViewById(R.id.confire_delete);
					cancle_delete.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							if(null!=dialog){
								dialog.dismiss();
								dialog=null;
							}
						}
					});
					confire_delete.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							regFilter1();
							String devMac = mU.getDevMac();
							int s = mU.getIsShare();
							mUserDevice = new UserDevice();
							mUserDevice.setUserNum(userNum);
							mUserDevice.setCameraPwd("");
							mUserDevice.setDevName("");
							mUserDevice.setDevMac(devMac);
							if(null!=dialog){
								dialog.dismiss();
								dialog=null;
							}
							View vv = LayoutInflater.from(mContext).inflate(
									R.layout.dialog_loading, null);
							AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
							loadingDialog = builder.create();
							loadingDialog.show();
							loadingDialog.setContentView(vv);
							if(s==1){
								byte[] orderSend =SendServerOrder.ModifyDev(mUserDevice,(byte)0x01);
								mSocketUDPClient.sendMsg(orderSend);
							}else if(s==0){
								
								byte[] orderSend =SendServerOrder.findBinderCameraAndSocket(devMac);
								mSocketUDPClient.sendMsg(orderSend);
							}
							
						}
					});
					
				}
				return true;
			}
		});
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.menu:
			Intent i = new Intent();
			i.setAction(Constants.Action.OPEN_SLIDE_MENU);
			mContext.sendBroadcast(i);
			break;
		case R.id.add_device:
			Intent intent = new Intent(mContext,AddDeviceStepActivity.class);
			intent.putExtra("devList", (Serializable)mSmartSocketList);
			startActivity(intent);
			break;
		case R.id.new_dev_rela:
			if(null!=wifiList&&wifiList.size()>0){
				View vv = LayoutInflater.from(mContext).inflate(
						R.layout.choice_wifi_dialog, null);
				ListView dev_list = (ListView) vv.findViewById(R.id.wifi_dev_list);
				mChoiceDevAdapter = new ChoiceWifiAdapter(mContext, wifiList);
				dev_list.setAdapter(mChoiceDevAdapter);
				dev_list.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int pos, long arg3) {
						// TODO Auto-generated method stub
						modifyDialog.dismiss();
						String wifiStr = wifiList.get(pos);
						Intent i = new Intent(mContext,IntroducedNextOneActivity.class);
						i.putExtra("wifiName", wifiStr);
						startActivity(i);
					}
				});
				AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
				modifyDialog = builder.create();
				modifyDialog.show();
				modifyDialog.setContentView(vv);
			}
			break;
		default:
			break;
		}
	}

	private void um(List<Map<String,DeviceStates>> listM){
		List<UserDevice> mUserDeviceAdapterList = new ArrayList<UserDevice>();
		if(null!=mUserDeviceList&&mUserDeviceList.size()>0){
			//��������ͷ����
			Integer[] socketPos = mPullToRefreshGridViewAdapter.getSocketPos();//��ȡ�������б���λ�á���
			if(null!=socketPos&&socketPos.length>0){
				for(int i=0;i<listM.size();i++){
					Map<String,DeviceStates> m = listM.get(i);
					UserDevice mUserDevice = mUserDeviceList.get(socketPos[i]);
					DeviceStates mDeviceStates = m.get(mUserDevice.getDevMac());
					if(null!=mDeviceStates){
						//��������ͷ����
						mUserDevice.setId(socketPos[i]);
						mUserDevice.setLightStates(mDeviceStates.getLightStates());
						mUserDevice.setSocketStates(mDeviceStates.getSocketStates());
						mUserDevice.setLightOnOrOutLine(mDeviceStates.getLightOnOrOutLine());
						mUserDeviceAdapterList.add(mUserDevice);
					}
				}
			}
		}
		mPullToRefreshGridViewAdapter.setGridView(mGridView);
		mPullToRefreshGridViewAdapter.updateItemData(mUserDeviceAdapterList);
		if(mTimer!=null){
			mTimer.cancel();
			mTimer=null;
		}
		count=0;
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		BitmapCache.getInstance().clearCache();
	}
	
	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		mContext.unregisterReceiver(mReceiver);
		MainThread.setOpenThread(false);
		if(mTimer!=null){
			mTimer.cancel();
			mTimer=null;
		}
		count=0;
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		regFilter();
		MainThread.setOpenThread(true);
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		//getDevice(userNum,"");
		getUserDev();
	}

	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
	
	
	private int count=0;
	private void setTimerdoAction(final Handler oj,Timer t) { 
        t.schedule(new TimerTask() {  
            @Override  
            public void run() {
            	count = count+1;
            	Message message = new Message();
            	if(count>5){
            		message = oj.obtainMessage();
        			message.what = 1; 
            		oj.sendMessage(message);
            	}
            }  
        }, 1000, 1000/* ��ʾ1000����֮�ᣬÿ��1000�������һ�� */);  
    } 
	
	private Handler doAction = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				if(null!=mPullToRefreshGridView){
					mPullToRefreshGridView.onRefreshComplete();
				}
				if(mTimer!=null){
					mTimer.cancel();
					mTimer=null;
				}
				count=0;
				break;
			default:
				break;
			}
		}
	};
	
	
	
	
	
	//@@
	class HttpThreadGetDev extends Thread {
		
		private String userNum;
		private Handler handler;
		
		String str;
		public HttpThreadGetDev(String userNum, Handler handler) {
			super();
			this.userNum = userNum;
			this.handler = handler;
		}
		
		@Override
		public void run() {
			super.run();
			try {
//				String url=Constants.HTTPGETDEV+userNum;
				String url="http://192.168.0.184:8080/smartHome/servlet/GetDeviceStateAction?userNum=04045919";
				URL httpUrl=new URL(url);
				HttpURLConnection connection=(HttpURLConnection)httpUrl.openConnection();
				connection.setReadTimeout(5000);
				connection.setRequestMethod("GET");
				final StringBuffer sb=new StringBuffer();
				InputStream a=connection.getInputStream();
				BufferedReader reader=new BufferedReader(new InputStreamReader(a,"gbk"));
				while((str=reader.readLine())!=null){
					sb.append(str);
				}
				String devinfolist=sb.toString();
//				String devinfolist="{\"error\":\"��ȡ�豸��Ϣ�ɹ�\",\"errorCode\":0,\"deviceState\":[{\"cameraPwd\":\"\",\"netState\":1,\"outlet\":0,\"environment\":{\"c_environmentQuality\":0,\"c_humidity\":\"\",\"c_temperature\":\"\",\"c_pm25\":\"\",\"c_methanal\":\"\",\"c_co2\":\"\"},\"defence\":0,\"devType\":1,\"devName\":\"�����豸1797\",\"mac\":\"18fe34de06af\",\"isShare\":0,\"light\":0},{\"cameraPwd\":\"\",\"netState\":0,\"outlet\":0,\"environment\":{\"c_environmentQuality\":2,\"c_humidity\":\"0.018\",\"c_temperature\":\"22.6\",\"c_pm25\":\"70\",\"c_methanal\":\"68\",\"c_co2\":\"930\"},\"defence\":1,\"devType\":3,\"devName\":\"zhewei\",\"mac\":\"559b5b14\",\"isShare\":0,\"light\":0}]}";
				JSONObject jsonObject=new JSONObject(devinfolist);
				int errorCode=jsonObject.getInt("errorCode");
				if(errorCode==0){
					final List<UserDevice> userdevlist=new ArrayList<UserDevice>();
					JSONArray array=jsonObject.getJSONArray("deviceState");
					for(int i=0;i<array.length();i++){
						JSONObject jsonObjectdev=array.getJSONObject(i);
						UserDevice userDevice=new UserDevice();
						userDevice.setCameraPwd(jsonObjectdev.getString("cameraPwd"));
						userDevice.setId(i);
						userDevice.setUserNum(userNum);
						userDevice.setLightOnOrOutLine(jsonObjectdev.getInt("netState"));
						userDevice.setSocketStates(jsonObjectdev.getInt("outlet"));
						userDevice.setDefence(jsonObjectdev.getInt("defence"));
						userDevice.setDevType(jsonObjectdev.getInt("devType"));
						userDevice.setDevName(jsonObjectdev.getString("devName"));
						userDevice.setDevMac(jsonObjectdev.getString("mac"));
						userDevice.setIsShare(jsonObjectdev.getInt("isShare"));
						userDevice.setLightStates(jsonObjectdev.getInt("light"));
						EnvironmentInfo environmentInfo=new EnvironmentInfo();
						JSONObject envInfo=jsonObjectdev.getJSONObject("environment");
						environmentInfo.setEnvironmentQuality(envInfo.getInt("c_environmentQuality"));
						environmentInfo.setHumidity(envInfo.getString("c_humidity"));
						environmentInfo.setMethanal(envInfo.getString("c_methanal"));
						environmentInfo.setTemperature(envInfo.getString("c_temperature"));
						environmentInfo.setPm25(envInfo.getString("c_pm25"));
						environmentInfo.setCo2(envInfo.getString("c_co2"));
						userDevice.setEnvironment(environmentInfo);
						userdevlist.add(userDevice);
					}
					mUserDeviceList=userdevlist;
//					handler.post(new Runnable() {
//						
//						@Override
//						public void run() {
//							list=userdevlist;
//						}
//					});
					Message msg=new Message();
					msg.what=0;
					handler.sendMessage(msg);
				}else{
					Message msg=new Message();
					msg.what=errorCode;
					handler.sendMessage(msg);
				}
			} 
			catch (MalformedURLException e) {
				e.printStackTrace();
				Message msg=new Message();
				msg.what=2;
				handler.sendMessage(msg);
			} catch (IOException e) {
				e.printStackTrace();
				Message msg=new Message();
				msg.what=3;
				handler.sendMessage(msg);
			} 
			catch (JSONException e) {
				e.printStackTrace();
				Message msg=new Message();
				msg.what=2;
				handler.sendMessage(msg);
			}
		}

	}


}