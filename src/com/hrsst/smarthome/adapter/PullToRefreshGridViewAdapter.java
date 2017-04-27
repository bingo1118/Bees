package com.hrsst.smarthome.adapter;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;



import com.hrsst.smarthome.R;
import com.hrsst.smarthome.global.Constants;
import com.hrsst.smarthome.pojo.EnvironmentInfo;
import com.hrsst.smarthome.pojo.UserDevice;
import com.hrsst.smarthome.util.BitmapCache;
import com.p2p.core.P2PHandler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class PullToRefreshGridViewAdapter extends BaseAdapter {
	private GridView mGridView;
	private Set<Integer> socketPos;
	private Set<Integer> cameraPos;
	private Context mContext;
	private List<UserDevice> list;
	private ViewHolder holder;
	private ViewHolder2 holder2 = null;
	private ViewHolder3 holder3 = null;
	private ViewHolder4 holder4 = null;
	private Map<String, Integer> m;
	
	private static final int TYPE_COUNT = 4;//item类型的总数
	private static final int TYPE_AIR = 0;//环境探测器类型
	private static final int TYPE_DEV = 1;//其他设备类型
	private static final int TYPE_VIDEO = 2;//视频设备类型
	private static final int TYPE_ADD = 3;//添加设备类型
	private int currentType;//当前item类型
	
	String cameraId;
	String cameraPwd;

	public PullToRefreshGridViewAdapter(Context mContext,List<UserDevice> list) {
		this.mContext = mContext;
		this.list = list;
//		m = new TreeMap<String, Integer>();//@@
		socketPos =new TreeSet<Integer>();
		cameraPos =new TreeSet<Integer>();
		for(int i=0;i<list.size();i++){//@@
			int type=list.get(i).getDevType();
			if(type==1){
				socketPos.add(i);
			}
			if(type==2){
				cameraPos.add(i);
//				m.put(list.get(i).getDevMac(), i);//@@
			}
		}
		m = new TreeMap<String, Integer>();
	}
	
	@Override
	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
		UserDevice mUserDevice = null;
		int type = 0;
		if(position<list.size()){
			mUserDevice=list.get(position);
			type = mUserDevice.getDevType();
		}
		if (type==3) {
			return TYPE_AIR;
		}else if(type==1){
			return TYPE_DEV;
		}else if(type==2){
			return TYPE_VIDEO;
		}else{
			return TYPE_ADD;
		}
	}

	@Override
	public int getViewTypeCount() {
		return TYPE_COUNT;
	}

	@Override
	public int getCount() {
		return list.size()+1;
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	//position=list.size+1
	@SuppressLint("NewApi")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		UserDevice mUserDevice = null;
		
		View AirView = null;
		View DevView = null;
		View VideoView = null;
		View AddView = null;
		
		
		int type = 0;
		if(position<list.size()){
			mUserDevice=list.get(position);
			type = mUserDevice.getDevType();
		}
		
		currentType = getItemViewType(position);
		if (currentType == TYPE_AIR) {
			holder2=null;
			if (convertView == null) {
				holder2 = new ViewHolder2();
				AirView = LayoutInflater.from(mContext).inflate(R.layout.air_dev, null);
				AirView.setPadding(50, 15, 0, 10);
				holder2.temperature=(TextView)AirView.findViewById(R.id.tv_temperature);//温度
				holder2.humidity=(TextView)AirView.findViewById(R.id.tv_humidity);//湿度
				holder2.pm25=(TextView)AirView.findViewById(R.id.tv_pm25);//pm2.5
				holder2.methanal=(TextView)AirView.findViewById(R.id.tv_methanal);//甲醛
				holder2.quality=(TextView)AirView.findViewById(R.id.tv_environment_quality);//空气质量
				holder2.name=(TextView)AirView.findViewById(R.id.tv_dev_name);//设备名称
				AirView.setTag(holder2);
				convertView = AirView;
			}else {
				holder2 = (ViewHolder2)convertView.getTag();
			}
			EnvironmentInfo info=mUserDevice.getEnvironment();
			holder2.temperature.setText(info.getTemperature()+"°");
			holder2.humidity.setText(info.getHumidity()+"%");
			holder2.pm25.setText(info.getPm25()+" µg/m³");
			holder2.methanal.setText(info.getMethanal()+" mg/m³");
			switch (info.getEnvironmentQuality()) {
			case 1:
				holder2.quality.setText("优");
				holder2.quality.setTextColor(0xff16bb5c);
				break;
			case 2:
				holder2.quality.setText("良");
				holder2.quality.setTextColor(0xff08b9b7);
				break;
			case 3:
				holder2.quality.setText("中");
				holder2.quality.setTextColor(0xffde9e06);
				break;
			case 4:
				holder2.quality.setText("差");
				holder2.quality.setTextColor(0xffe4150b);
				break;
			default:
				holder2.quality.setText("--");
				holder2.quality.setTextColor(0xffe4150b);
				break;
			}
			holder2.name.setText(mUserDevice.getDevName());
		}else if (currentType == TYPE_DEV){
			holder=null;
			if (convertView == null) {
				holder = new ViewHolder();
				DevView = LayoutInflater.from(mContext).inflate(R.layout.adapter_uselayout, null);
				DevView.setPadding(50, 15, 0, 10);
				holder.ifShare=(ImageView)DevView.findViewById(R.id.ifShare);
				holder.image = (ImageView) DevView.findViewById(R.id.mImageView);
				holder.defence_image = (ImageView) DevView.findViewById(R.id.defence_image);
				holder.text = (TextView) DevView.findViewById(R.id.mTextView);
				holder.open_or_close_tv = (TextView) DevView.findViewById(R.id.open_or_close_tv);
				holder.device_list_rela = (RelativeLayout) DevView.findViewById(R.id.device_list_rela);				
				DevView.setTag(holder);
				convertView=DevView;
			}else{
				holder=(ViewHolder)convertView.getTag();
			}
			if(list.size()>0&&position<list.size()){
//				UserDevice mUserDevice =list.get(position);
				//添加摄像头启用
//				int type = mUserDevice.getDevType();
				int onOrOutLine = mUserDevice.getLightOnOrOutLine();
				int openOrColse = mUserDevice.getSocketStates();
				
//					socketPos.add(position);//存储插座位置。。@@
					int defenceType = mUserDevice.getDefence();
					if(defenceType==0){
						holder.defence_image.setBackgroundResource(R.drawable.defence_on);
					}else{
						holder.defence_image.setBackgroundResource(R.drawable.defence_off);
					}
					int ifshare=mUserDevice.getIsShare();//@@
					if(ifshare==1){
						holder.ifShare.setVisibility(View.VISIBLE);
					}else{
						holder.ifShare.setVisibility(View.GONE);
					}
					
					if(openOrColse==1){
						holder.image.setImageResource(R.drawable.zhuangtai_on);
						holder.open_or_close_tv.setText(R.string.on);
					}else{
						holder.image.setImageResource(R.drawable.zhuangtai_off);
						holder.open_or_close_tv.setText(R.string.off);
					}
					if(onOrOutLine==1){
						holder.defence_image.setEnabled(true);
						holder.device_list_rela.setBackgroundResource(R.drawable.chazuo_on);
					}else{
						holder.defence_image.setEnabled(false);
						holder.device_list_rela.setBackgroundResource(R.drawable.chazuo_off);
					}
					holder.text.setText(mUserDevice.getDevName());
				//布防按钮。。
				holder.defence_image.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						UserDevice mUserDevice = list.get(position);
						int type = mUserDevice.getDevType();
						int defence = mUserDevice.getDefence();
						
							if(defence==0){
								defence=1;
							}else{
								defence=0;
							}
							Intent i = new Intent();
							i.putExtra("defencePos", position);
							i.putExtra("defenceType", defence);
							i.putExtra("devMac", list.get(position).getDevMac());
							i.setAction("DEFENCE_ACTION");
							mContext.sendBroadcast(i);
					}
				});
				
			}
		}else if(currentType == TYPE_VIDEO){
			holder3=null;
			if (convertView == null) {
				holder3 = new ViewHolder3();
				VideoView = LayoutInflater.from(mContext).inflate(R.layout.adapter_uselayout, null);
				VideoView.setPadding(50, 15, 0, 10);
				holder3.ifShare=(ImageView)VideoView.findViewById(R.id.ifShare);
				holder3.image = (ImageView) VideoView.findViewById(R.id.mImageView);
				holder3.defence_image = (ImageView) VideoView.findViewById(R.id.defence_image);
				holder3.text = (TextView) VideoView.findViewById(R.id.mTextView);
				holder3.open_or_close_tv = (TextView) VideoView.findViewById(R.id.open_or_close_tv);
				holder3.device_list_rela = (RelativeLayout) VideoView.findViewById(R.id.device_list_rela);				
				VideoView.setTag(holder3);
				convertView=VideoView;
			}else{
				holder3=(ViewHolder3)convertView.getTag();
			}
			if(list.size()>0&&position<list.size()){
//				UserDevice mUserDevice =list.get(position);
				//添加摄像头启用
//				int type = mUserDevice.getDevType();
				int onOrOutLine = mUserDevice.getLightOnOrOutLine();
				int openOrColse = mUserDevice.getSocketStates();
			
//					cameraPos.add(position);//添加摄像头位置@@
					int ifshare1=mUserDevice.getIsShare();//@@
					if(ifshare1==1){
						holder3.ifShare.setVisibility(View.VISIBLE);
					}else{
						holder3.ifShare.setVisibility(View.GONE);
					}
					holder3.defence_image.setBackgroundResource(R.drawable.defence_anxia);
					holder3.image.setVisibility(View.GONE);
					holder3.open_or_close_tv.setVisibility(View.GONE);
					cameraId = mUserDevice.getDevMac().trim();
					cameraPwd = mUserDevice.getCameraPwd().trim();
					P2PHandler.getInstance().getDefenceStates(
							cameraId, cameraPwd);//获取摄像机布防状态。。
					m.put(cameraId, position);//@@
					if(onOrOutLine==1){
						holder3.defence_image.setEnabled(true);
						holder3.device_list_rela.setBackgroundResource(R.drawable.shouye_sxt_on);
					}else{
						holder3.defence_image.setEnabled(false);
						holder3.device_list_rela.setBackgroundResource(R.drawable.shouye_sxt_off);
					}
					holder3.text.setText(mUserDevice.getDevName());
				
				//布防按钮。。
					holder3.defence_image.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						UserDevice mUserDevice = list.get(position);
						int type = mUserDevice.getDevType();
						int defence = mUserDevice.getDefence();
						
							System.out.println("defence="+defence);
							if(defence==0){//0为取消布防
								P2PHandler.getInstance().setRemoteDefence(
										mUserDevice.getDevMac().trim(),
										mUserDevice.getCameraPwd().trim(),
										Constants.P2P_SET.REMOTE_DEFENCE_SET.ALARM_SWITCH_ON);
								defence=1;
								cameraId = mUserDevice.getDevMac().trim();
								cameraPwd = mUserDevice.getCameraPwd().trim();
								View view = mGridView.getChildAt(position- mGridView.getFirstVisiblePosition());//@@
								view.findViewById(R.id.defence_image).setBackgroundResource(R.drawable.defence_on);//@@
//								holder3.defence_image.setBackgroundResource(R.drawable.defence_on);
							}else{
								P2PHandler.getInstance().setRemoteDefence(
										mUserDevice.getDevMac().trim(),
										mUserDevice.getCameraPwd().trim(),
										Constants.P2P_SET.REMOTE_DEFENCE_SET.ALARM_SWITCH_OFF);
								defence=0;
								cameraId = mUserDevice.getDevMac().trim();
								cameraPwd = mUserDevice.getCameraPwd().trim();
								View view = mGridView.getChildAt(position- mGridView.getFirstVisiblePosition());//@@
								view.findViewById(R.id.defence_image).setBackgroundResource(R.drawable.defence_off);//@@
//								holder3.defence_image.setBackgroundResource(R.drawable.defence_off);
							}
						
					}
				});
				
			}
		}else{
			holder4=null;
			if (convertView == null) {
				holder4 = new ViewHolder4();
				AddView = LayoutInflater.from(mContext).inflate(R.layout.add_device, null);
				AddView.setPadding(50, 15, 0, 10);
				holder4.device_list_rela = (RelativeLayout) AddView.findViewById(R.id.device_list_rela);				
				AddView.setTag(holder4);
				convertView=AddView;
			}else{
				holder4=(ViewHolder4)convertView.getTag();
			}
		}
		return convertView;
	}
	
	public String getCameraId(){
		if(null!=cameraId&&cameraId.length()>0){
			return cameraId;
		}else{
			return null;
		}
	}
	
	public String getCameraPwd(){
		if(null!=cameraPwd&&cameraPwd.length()>0){
			return cameraPwd;
		}else{
			return null;
		}
	}
	
	public void setGridView(GridView mGridView){
		this.mGridView = mGridView;
	}
	
    public void updateItemData(List<UserDevice> item){
    	if(null==item||item.size()<=0){
    		return;
    	}
    	for(UserDevice u:item){
    		list.set(u.getId(), u);
    		Message msg = Message.obtain();
        	msg.arg1 = u.getId();
        	msg.what=1;
        	han.sendMessage(msg);
    	}
    }
    
    public void updateCameraData(List<UserDevice> item){
    	for(UserDevice u:item){
    		list.set(u.getId(), u);
    		Message msg = Message.obtain();
        	msg.obj = u;
        	msg.what=3;
        	han.sendMessage(msg);
    	}
    }
    
    public void defence(int pos,int type){
    	Message msg = Message.obtain();
    	msg.arg1 = pos;
    	msg.arg2 = type;
    	msg.what=2;
    	han.sendMessage(msg);
    }
    
    public void cameraDefence(int pos,int type){
//    	Message msg = Message.obtain();
//    	msg.arg1 = pos;
//    	msg.arg2 = type;
//    	msg.what=4;
//    	han.sendMessage(msg);
    	updateCameraDefence(pos,type);//@@
    }
    
    private void updateDefence(int pos,int type){
    	if (mGridView == null){
            return;
        }
    	View view = mGridView.getChildAt(pos- mGridView.getFirstVisiblePosition());//@@
    	if(view==null){
    		return;
    	}
    	UserDevice mUserDevice = list.get(pos);
    	mUserDevice.setDefence(type);
    	list.set(pos, mUserDevice);
    	ImageView defence_image = (ImageView) view.findViewById(R.id.defence_image);
    	if(type==0){
    		defence_image.setBackgroundResource(R.drawable.defence_on);
		}else{
			defence_image.setBackgroundResource(R.drawable.defence_off);
		}
    }
    
    private void updateCameraDefence(int pos,int type){
    	if (mGridView == null){
            return;
        }
    	View view = mGridView.getChildAt(pos- mGridView.getFirstVisiblePosition());//@@
    	if(view==null){
    		 return;
    	}
    	UserDevice mUserDevice = list.get(pos);
    	mUserDevice.setDefence(type);
    	list.set(pos, mUserDevice);
    	ImageView defence_image = (ImageView) view.findViewById(R.id.defence_image);
    	if(type==1){
    		defence_image.setBackgroundResource(R.drawable.defence_on);
		}else{
			defence_image.setBackgroundResource(R.drawable.defence_off);
		}
    }
    
	@SuppressLint("HandlerLeak")
	private Handler han = new Handler(){
        public void handleMessage(android.os.Message msg){
        	switch (msg.what) {
			case 1:
				updateItem(msg.arg1);
				break;
			case 2:
				updateDefence(msg.arg1,msg.arg2);
				break;
			case 3:
				updateCameraStatus(msg.obj);
				break;
			case 4:
				updateCameraDefence(msg.arg1,msg.arg2);
				break;
			default:
				break;
			}
        };
    };
    
    @SuppressLint("NewApi")
	private void updateCameraStatus(Object obj){
    	if (mGridView == null){
            return;
        }
    	View view = mGridView.getChildAt(((UserDevice) obj).getId()- mGridView.getFirstVisiblePosition());//@@
    	if(view==null){
    		return;
    	}
    	ImageView defence_image = (ImageView) view.findViewById(R.id.defence_image);
    	ImageView image = (ImageView) view.findViewById(R.id.mImageView);
    	TextView open_or_close_tv = (TextView) view.findViewById(R.id.open_or_close_tv);
    	TextView text = (TextView) view.findViewById(R.id.mTextView);
    	RelativeLayout device_list_rela = (RelativeLayout) view.findViewById(R.id.device_list_rela);
    	//此处代码导致布防图标混乱@@
//    	switch (((UserDevice) obj).getDefence()) {
//		case 1:
//			defence_image.setBackgroundResource(R.drawable.defence_on);
//			break;
//		case 0:
//			defence_image.setBackgroundResource(R.drawable.defence_off);
//			break;
//		default:
//			break;
//		}
    	
		image.setVisibility(View.GONE);
		open_or_close_tv.setVisibility(View.GONE);
		if(((UserDevice) obj).getLightOnOrOutLine()==1){
			defence_image.setEnabled(true);
			device_list_rela.setBackgroundResource(R.drawable.shouye_sxt_on);
		}else{
			defence_image.setEnabled(false);
			device_list_rela.setBackgroundResource(R.drawable.shouye_sxt_off);
		}
		text.setText(((UserDevice) obj).getDevName());
    }
	
	@SuppressLint("NewApi")
	private void updateItem(int index){
		if (mGridView == null){
            return;
        }
		View view = mGridView.getChildAt(index- mGridView.getFirstVisiblePosition());//@@
		if(view==null){
			return;
		}
		ImageView im = (ImageView) view.findViewById(R.id.mImageView);
		TextView tv = (TextView) view.findViewById(R.id.mTextView);
		TextView tv_open = (TextView) view.findViewById(R.id.open_or_close_tv);
		RelativeLayout rela = (RelativeLayout) view.findViewById(R.id.device_list_rela);
		ImageView defence_image = (ImageView) view.findViewById(R.id.defence_image);
		UserDevice mUserDevice =list.get(index);
		int onOrOutLine = mUserDevice.getLightOnOrOutLine();
		int openOrColse = mUserDevice.getSocketStates();
		if(openOrColse==1){//插座开关。。
//			Bitmap mBitmap = BitmapCache.getInstance().getBitmap(R.drawable.zhuangtai_on, mContext);
//			BitmapDrawable bd = new BitmapDrawable(mContext.getResources(), mBitmap);
			im.setImageResource(R.drawable.zhuangtai_on);
			tv_open.setText(R.string.on);
		}else{
//			Bitmap mBitmap = BitmapCache.getInstance().getBitmap(R.drawable.zhuangtai_off, mContext);
//			BitmapDrawable bd = new BitmapDrawable(mContext.getResources(), mBitmap);
			im.setImageResource(R.drawable.zhuangtai_off);
			tv_open.setText(R.string.off);
		}
		if(onOrOutLine==1){//插座是否在线
//			Bitmap mBitmap = BitmapCache.getInstance().getBitmap(R.drawable.chazuo_on, mContext);
//			BitmapDrawable bd = new BitmapDrawable(mContext.getResources(), mBitmap);
			defence_image.setEnabled(true);
			rela.setBackgroundResource(R.drawable.chazuo_on);
		}else{
//			Bitmap mBitmap = BitmapCache.getInstance().getBitmap(R.drawable.chazuo_off, mContext);
//			BitmapDrawable bd = new BitmapDrawable(mContext.getResources(), mBitmap);
			defence_image.setEnabled(false);
			rela.setBackgroundResource(R.drawable.chazuo_off);
		}
		tv.setText(mUserDevice.getDevName());
    }
	
	/**
	 * 获取插座的位置号。。
	 * @return
	 */
	public Integer[] getSocketPos(){
		Integer[] arr2=socketPos.toArray(new Integer[0]);
		return arr2;
	}
	public Integer[] getCameraPos(){
		Integer[] arr1=cameraPos.toArray(new Integer[0]);
		return arr1;
	}
	
	public Map<String, Integer> getPos(){
		return m;
	}

	//插座视图
	static class ViewHolder {
		public ImageView image;
		public TextView text,open_or_close_tv;
		public RelativeLayout device_list_rela;
		public ImageView defence_image;
		public ImageView ifShare;//@@
	}
	//摄像机视图
	static class ViewHolder3 {
		public ImageView image;
		public TextView text,open_or_close_tv;
		public RelativeLayout device_list_rela;
		public ImageView defence_image;
		public ImageView ifShare;//@@
	}
	//环境探测器视图
	static class ViewHolder2 {
		public TextView temperature;//温度
		public TextView humidity;//湿度;
		public TextView pm25;//pm2.5
		public TextView methanal;//甲醛
		public TextView quality;//空气质量	
		public TextView name;//设备名称
	}
	//添加视图
		static class ViewHolder4 {
			public RelativeLayout device_list_rela;//@@
		}

}
