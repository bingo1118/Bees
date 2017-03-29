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
	private Map<String, Integer> m;
	private String cameraId;
	private String cameraPwd;

	public PullToRefreshGridViewAdapter(Context mContext,List<UserDevice> list) {
		this.mContext = mContext;
		this.list = list;
		
//		UserDevice dev=new UserDevice();
//		dev.setDevType(3);
//		this.list.add(dev);
		
		
		socketPos =new TreeSet<Integer>();
		cameraPos =new TreeSet<Integer>();
		m = new TreeMap<String, Integer>();
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
		int type = 0;
		if(position<list.size()){
			mUserDevice=list.get(position);
			type = mUserDevice.getDevType();
		}
		if (convertView == null) {
			if(type==3){
				EnvironmentInfo info=mUserDevice.getEnvironment();
				convertView = LayoutInflater.from(mContext).inflate(R.layout.air_dev, null);
				convertView.setPadding(50, 15, 0, 10);
				if(info==null){
					info=new EnvironmentInfo();
				}
				TextView temperature=(TextView)convertView.findViewById(R.id.tv_temperature);//温度
				temperature.setText(info.getTemperature()+"°");
				TextView humidity=(TextView)convertView.findViewById(R.id.tv_humidity);//湿度
				humidity.setText(info.getHumidity()+"%");
				TextView pm25=(TextView)convertView.findViewById(R.id.tv_pm25);//pm2.5
				pm25.setText(info.getPm25()+" µg/m³");
				TextView methanal=(TextView)convertView.findViewById(R.id.tv_methanal);//甲醛
				methanal.setText(info.getMethanal()+" mg/m³");
				TextView quality=(TextView)convertView.findViewById(R.id.tv_environment_quality);//空气质量
				switch (info.getEnvironmentQuality()) {
				case 1:
					quality.setText("优");
					quality.setTextColor(0xff16bb5c);
					break;
				case 2:
					quality.setText("良");
					quality.setTextColor(0xff08b9b7);
					break;
				case 3:
					quality.setText("中");
					quality.setTextColor(0xffde9e06);
					break;
				case 4:
					quality.setText("差");
					quality.setTextColor(0xffe4150b);
					break;
				default:
					quality.setText("--");
					quality.setTextColor(0xffe4150b);
					break;
				}
				
				TextView name=(TextView)convertView.findViewById(R.id.tv_dev_name);//设备名称
				name.setText(mUserDevice.getDevName());
			}else{
				convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_uselayout, null);
				convertView.setPadding(50, 15, 0, 10);
				holder = new ViewHolder();
				holder.image = (ImageView) convertView.findViewById(R.id.mImageView);
				holder.defence_image = (ImageView) convertView.findViewById(R.id.defence_image);
				holder.text = (TextView) convertView.findViewById(R.id.mTextView);
				holder.open_or_close_tv = (TextView) convertView.findViewById(R.id.open_or_close_tv);
				holder.device_list_rela = (RelativeLayout) convertView.findViewById(R.id.device_list_rela);
				convertView.setTag(holder);
			}
			
		} else {
			if(type!=3){
				holder = (ViewHolder) convertView.getTag();
			}
			
		}
		if(list.size()>0&&position<list.size()&&type!=3){
//			UserDevice mUserDevice =list.get(position);
			//添加摄像头启用
//			int type = mUserDevice.getDevType();
			int onOrOutLine = mUserDevice.getLightOnOrOutLine();
			int openOrColse = mUserDevice.getSocketStates();
			switch (type) {
			case 1:
				socketPos.add(position);//存储插座位置。。
				int defenceType = mUserDevice.getDefence();
				if(defenceType==0){
					holder.defence_image.setBackgroundResource(R.drawable.defence_on);
				}else{
					holder.defence_image.setBackgroundResource(R.drawable.defence_off);
				}
				
				if(openOrColse==1){
					holder.image.setImageResource(R.drawable.zhuangtai_on);
					holder.open_or_close_tv.setText("开");
				}else{
					holder.image.setImageResource(R.drawable.zhuangtai_off);
					holder.open_or_close_tv.setText("关");
				}
				if(onOrOutLine==1){
					holder.defence_image.setEnabled(true);
					holder.device_list_rela.setBackgroundResource(R.drawable.chazuo_on);
				}else{
					holder.defence_image.setEnabled(false);
					holder.device_list_rela.setBackgroundResource(R.drawable.chazuo_off);
				}
				holder.text.setText(mUserDevice.getDevName());
				break;
			case 2:
				cameraPos.add(position);//添加摄像头位置
				holder.defence_image.setBackgroundResource(R.drawable.defence_anxia);
				holder.image.setVisibility(View.GONE);
				holder.open_or_close_tv.setVisibility(View.GONE);
				String cameraId = mUserDevice.getDevMac().trim();
				String cameraPwd = mUserDevice.getCameraPwd().trim();
				P2PHandler.getInstance().getDefenceStates(
						cameraId, cameraPwd);
				m.put(cameraId, position);
				if(onOrOutLine==1){
					holder.defence_image.setEnabled(true);
					holder.device_list_rela.setBackgroundResource(R.drawable.shouye_sxt_on);
				}else{
					holder.defence_image.setEnabled(false);
					holder.device_list_rela.setBackgroundResource(R.drawable.shouye_sxt_off);
				}
				holder.text.setText(mUserDevice.getDevName());
				break;
			default:
				break;
			}
			
			//布防按钮。。
			holder.defence_image.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					UserDevice mUserDevice = list.get(position);
					int type = mUserDevice.getDevType();
					int defence = mUserDevice.getDefence();
					switch (type) {
					case 1:
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
						break;
					case 2:
						System.out.println("defence="+defence);
						if(defence==0){
							P2PHandler.getInstance().setRemoteDefence(
									mUserDevice.getDevMac().trim(),
									mUserDevice.getCameraPwd().trim(),
									Constants.P2P_SET.REMOTE_DEFENCE_SET.ALARM_SWITCH_ON);
							defence=1;
							cameraId = mUserDevice.getDevMac().trim();
							cameraPwd = mUserDevice.getCameraPwd().trim();
							holder.defence_image.setBackgroundResource(R.drawable.defence_on);
						}else{
							P2PHandler.getInstance().setRemoteDefence(
									mUserDevice.getDevMac().trim(),
									mUserDevice.getCameraPwd().trim(),
									Constants.P2P_SET.REMOTE_DEFENCE_SET.ALARM_SWITCH_OFF);
							defence=0;
							cameraId = mUserDevice.getDevMac().trim();
							cameraPwd = mUserDevice.getCameraPwd().trim();
							holder.defence_image.setBackgroundResource(R.drawable.defence_off);
						}
						break;
					default:
						break;
					}
					
				}
			});
			
			
		}
		if(position==list.size()){
			holder.device_list_rela.setBackgroundResource(R.drawable.add_device);
			holder.defence_image.setVisibility(View.GONE);
			holder.image.setVisibility(View.GONE);
			holder.text.setVisibility(View.GONE);
			holder.open_or_close_tv.setVisibility(View.GONE);
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
    	Message msg = Message.obtain();
    	msg.arg1 = pos;
    	msg.arg2 = type;
    	msg.what=4;
    	han.sendMessage(msg);
    }
    
    private void updateDefence(int pos,int type){
    	if (mGridView == null){
            return;
        }
    	View view = mGridView.getChildAt(pos);
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
    	View view = mGridView.getChildAt(pos);
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
    	View view = mGridView.getChildAt(((UserDevice) obj).getId());
    	if(view==null){
    		return;
    	}
    	ImageView defence_image = (ImageView) view.findViewById(R.id.defence_image);
    	ImageView image = (ImageView) view.findViewById(R.id.mImageView);
    	TextView open_or_close_tv = (TextView) view.findViewById(R.id.open_or_close_tv);
    	TextView text = (TextView) view.findViewById(R.id.mTextView);
    	RelativeLayout device_list_rela = (RelativeLayout) view.findViewById(R.id.device_list_rela);
    	switch (((UserDevice) obj).getDefence()) {
		case 1:
			defence_image.setBackgroundResource(R.drawable.defence_on);
			break;
		case 0:
			defence_image.setBackgroundResource(R.drawable.defence_off);
			break;
		default:
			break;
		}
    	
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
		View view = mGridView.getChildAt(index);
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
			tv_open.setText("开");
		}else{
//			Bitmap mBitmap = BitmapCache.getInstance().getBitmap(R.drawable.zhuangtai_off, mContext);
//			BitmapDrawable bd = new BitmapDrawable(mContext.getResources(), mBitmap);
			im.setImageResource(R.drawable.zhuangtai_off);
			tv_open.setText("关");
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

	static class ViewHolder {
		public ImageView image;
		public TextView text,open_or_close_tv;
		public RelativeLayout device_list_rela;
		public ImageView defence_image;
	}

}
