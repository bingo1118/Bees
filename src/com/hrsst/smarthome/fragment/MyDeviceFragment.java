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
import java.util.HashMap;
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

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.hrsst.smarthome.AirDevInfoActivity;
import com.hrsst.smarthome.R;
import com.hrsst.smarthome.activity.AddDeviceStepActivity;
import com.hrsst.smarthome.activity.ApMonitorActivity;
import com.hrsst.smarthome.activity.DeviceInfoActivity;
import com.hrsst.smarthome.activity.IntroducedNextOneActivity;
import com.hrsst.smarthome.adapter.ChoiceWifiAdapter;
import com.hrsst.smarthome.adapter.PullToRefreshGridViewAdapter;
import com.hrsst.smarthome.adapter.SystemMsgAdapter;
import com.hrsst.smarthome.global.Constants;
import com.hrsst.smarthome.mygridview.lib.PullToRefreshBase.OnRefreshListener;
import com.hrsst.smarthome.mygridview.lib.PullToRefreshGridView;
import com.hrsst.smarthome.net.SocketUDP;
import com.hrsst.smarthome.order.SendServerOrder;
import com.hrsst.smarthome.order.UnPackServer;
import com.hrsst.smarthome.pojo.Contact;
import com.hrsst.smarthome.pojo.DeviceStates;
import com.hrsst.smarthome.pojo.EnvironmentInfo;
import com.hrsst.smarthome.pojo.ShareMessages;
import com.hrsst.smarthome.pojo.UnPackageFromServer;
import com.hrsst.smarthome.pojo.UserDevice;
import com.hrsst.smarthome.thread.MainThread;
import com.hrsst.smarthome.util.BitmapCache;
import com.hrsst.smarthome.util.SharedPreferencesManager;
import com.hrsst.smarthome.volley.JsonArrayPostRequest;
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
	private List<UserDevice> mSmartSocketList;//@@智能插座列表
	
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
			//获取插座是否关联摄像头
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
					//添加摄像头启用
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
			
			//删除设备回复包
			if(arg1.getAction().equals("Constants.Action.unActionCamera")){
				byte[] datas = arg1.getExtras().getByteArray("datasByte");
				UnPackageFromServer mUnPackageFromServer = UnPackServer.unActionCamera(datas);
				if(mUnPackageFromServer!=null){
					String binderResult = mUnPackageFromServer.binderResult;
					switch (binderResult) {
					case "success":
						Toast.makeText(mContext, "删除成功", Toast.LENGTH_SHORT).show();
						getUserDev();
						break;
					case "failed":
						Toast.makeText(mContext, "删除失败", Toast.LENGTH_SHORT).show();
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
			//获取用户设备回复包
			if(arg1.getAction().equals("Constants.Action.unGetUserDev")){
				byte[] datas = arg1.getExtras().getByteArray("datasByte");
				UnPackageFromServer mUnPackageFromServer = UnPackServer.unGetUserDev(datas);
				if(mUnPackageFromServer!=null){
					mUserDeviceList = mUnPackageFromServer.userDeviceList;
					macList = mUnPackageFromServer.macList;//插座mac列表
					cameraList = mUnPackageFromServer.cameraList;//摄像头mac列表
					mPullToRefreshGridViewAdapter = new PullToRefreshGridViewAdapter(mContext,mUserDeviceList);
					mPullToRefreshGridViewAdapter.setGridView(mGridView);
					mGridView.setAdapter(mPullToRefreshGridViewAdapter);
					mPullToRefreshGridView.onRefreshComplete();
					String[] contactIds = new String[cameraList.size()];//摄像头mac数组
					for(int i=0;i<cameraList.size();i++){
						contactIds[i] = cameraList.get(i);
					}
					byte[] orderSend =SendServerOrder.GetDeviceStatesList(macList);//获取设备状态使用0x02
					MainThread.setByte(orderSend,0,contactIds);//发送获取设备状态命令
					MainThread.refreash();
					
					mSmartSocketList = new ArrayList<UserDevice>();//智能插座列表。。
					for(UserDevice u:mUserDeviceList){
						if(u.getDevType()==1){
							mSmartSocketList.add(u);//添加插座类型。。
						}//@@
					}
				}else{
					mUserDeviceList = new ArrayList<UserDevice>();
					mPullToRefreshGridViewAdapter = new PullToRefreshGridViewAdapter(mContext,mUserDeviceList);
					mGridView.setAdapter(mPullToRefreshGridViewAdapter);
					mPullToRefreshGridView.onRefreshComplete();
				}
			}
			
			//接收布防或者撤防命令
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
					new_dev_num_tv.setText("    最新消息：发现 "+wifiList.size()+""+"  个新设备      ");
				}else{
					new_dev_rela.setVisibility(View.GONE);
				}
			}
		}
	};
	
//	private void getUserDev(){
//		byte[] orderSend =SendServerOrder.GetUserDev(userNum);
//		mSocketUDPClient.sendMsg(orderSend);
//	}

	/**
	 * 获取设备列表（Volley）@@
	 */
	private void getUserDev(){//获取设备列表@@
//		String url=Constants.HTTPGETDEV+userNum;
//		String url="http://192.168.0.23:8080/smartHome/servlet/GetDeviceStateAction?userNum="+userNum;
		String url="http://119.29.224.28:51091/smartHome/servlet/GetDeviceStateAction?userNum="+userNum;
		RequestQueue mQueue = Volley.newRequestQueue(mContext);
		JsonObjectRequest mJsonRequest = new JsonObjectRequest(Method.GET,
				url, 
				null, 
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject jsonObject) {
						int errorCode;
						try {
							errorCode = jsonObject.getInt("errorCode");
							if(errorCode==0){
								JSONArray array=jsonObject.getJSONArray("deviceState");
								mUserDeviceList=new ArrayList<UserDevice>();
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
									mUserDeviceList.add(userDevice);
								}
								refreshGridview();
							}else{
								Toast.makeText(getActivity(), "获取设备失败", Toast.LENGTH_SHORT).show();
							}
						} catch (JSONException e) {
							// TODO 自动生成的 catch 块
							e.printStackTrace();
							Toast.makeText(getActivity(), "获取设备错误", Toast.LENGTH_SHORT).show();
						}
						
						
					}
				}, 
				new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Toast.makeText(getActivity(), "获取设备错误", Toast.LENGTH_SHORT).show();
					}
				});
		mQueue.add(mJsonRequest);
	}
	
	private void init(){
		Bitmap mBitmap = BitmapCache.getInstance().getBitmap(R.drawable.yetoutu, mContext);
		BitmapDrawable bd = new BitmapDrawable(mContext.getResources(), mBitmap);
		ImageView main_image = (ImageView) view.findViewById(R.id.main_image);
		main_image.setBackground(bd);
		menu = (TextView) view.findViewById(R.id.menu);
		add_device = (TextView) view.findViewById(R.id.add_device);
		new_dev_num_tv = (MarqueeTextView) view.findViewById(R.id.new_dev_num_tv);
		//单行显示 
		new_dev_num_tv.setSingleLine(true);
		//显示格式，跑马灯
		new_dev_num_tv.setEllipsize(TruncateAt.MARQUEE);
		//跑马灯重复次数，无线次
		new_dev_num_tv.setMarqueeRepeatLimit(-1);
		new_dev_num_tv.setGravity(Gravity.CENTER_HORIZONTAL);
		//可touch聚焦
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
		//点击事件。。
		mGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {
				// TODO Auto-generated method stub
				mSmartSocketList = new ArrayList<UserDevice>();//智能插座列表。。
				for(UserDevice u:mUserDeviceList){
					if(u.getDevType()==1){
						mSmartSocketList.add(u);//添加插座类型。。
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
							Toast.makeText(mContext, "设备离线", Toast.LENGTH_SHORT).show();
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
							Toast.makeText(mContext, "设备离线", Toast.LENGTH_SHORT).show();
						}
						break;
					case 3:
							Intent monitor = new Intent();
							monitor.setClass(mContext, AirDevInfoActivity.class);
							monitor.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							monitor.putExtra("info",itemUserDevice);
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
			//添加摄像头启用
			Integer[] socketPos = mPullToRefreshGridViewAdapter.getSocketPos();//获取插座的列表中位置。。
			if(null!=socketPos&&socketPos.length>0){
				for(int i=0;i<listM.size();i++){
					Map<String,DeviceStates> m = listM.get(i);
					UserDevice mUserDevice = mUserDeviceList.get(socketPos[i]);
					DeviceStates mDeviceStates = m.get(mUserDevice.getDevMac());
					if(null!=mDeviceStates){
						//添加摄像头启用
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
        }, 1000, 1000/* 表示1000毫秒之後，每隔1000毫秒執行一次 */);  
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
	
	/**
	 * 刷新GridView@@
	 */
	private void refreshGridview() {
		mPullToRefreshGridViewAdapter = new PullToRefreshGridViewAdapter(mContext,mUserDeviceList);
		mPullToRefreshGridViewAdapter.setGridView(mGridView);
		mGridView.setAdapter(mPullToRefreshGridViewAdapter);
		mPullToRefreshGridView.onRefreshComplete();
		mSmartSocketList = new ArrayList<UserDevice>();//智能插座列表。。
		cameraList=new ArrayList<String>();//摄像机mac地址表@@
		for(UserDevice u:mUserDeviceList){
			if(u.getDevType()==1){
				mSmartSocketList.add(u);//添加插座类型。。
			}
			if(u.getDevType()==2){
				cameraList.add(u.getDevMac());
			}//@@
	    }
		
		//获取摄像头状态
		String[] contactIds = new String[cameraList.size()];//摄像头mac数组
		for(int i=0;i<cameraList.size();i++){
			contactIds[i] = cameraList.get(i);
		}
		MainThread.setByte(0,contactIds);//发送获取设备状态命令
		MainThread.refreash();
	}


}
