package com.hrsst.smarthome.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.hrsst.smarthome.R;
import com.hrsst.smarthome.global.Constants;
import com.hrsst.smarthome.net.SocketUDP;
import com.hrsst.smarthome.order.SendServerOrder;
import com.hrsst.smarthome.order.UnPackServer;
import com.hrsst.smarthome.pojo.DwTimer;
import com.hrsst.smarthome.pojo.UnPackageFromServer;
import com.hrsst.smarthome.util.IntegerTo16;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class TimerListAdapter extends BaseAdapter{
	private Context mContext;
	private List<DwTimer> mDwTimerList;
	private ViewHolder holder;
	private SocketUDP mSocketUDPClient;
	private Timer mTimer;
	private AlertDialog dialog;
	private String mac;
	
	public TimerListAdapter(Context mContext, List<DwTimer> mDwTimerList) {
		super();
		this.mContext = mContext;
		this.mDwTimerList = mDwTimerList;
		
		mSocketUDPClient = SocketUDP.newInstance(Constants.SeverInfo.SERVER
				, Constants.SeverInfo.PORT);
		mSocketUDPClient.startAcceptMessage();
	}
	
	static class ViewHolder {
		public ImageView open_close_image;
		public TextView start_time_tv;
		public TextView start_time;
		public TextView end_time;
		public TextView week_list;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mDwTimerList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return mDwTimerList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(final int pos, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.timer_list_adapter, null);
			holder = new ViewHolder();
			holder.open_close_image = (ImageView) convertView.findViewById(R.id.open_close_image);
			holder.start_time_tv = (TextView) convertView.findViewById(R.id.start_time_tv);
			holder.start_time = (TextView) convertView.findViewById(R.id.start_time);
			holder.end_time = (TextView) convertView.findViewById(R.id.end_time);
			holder.week_list = (TextView) convertView.findViewById(R.id.week_list);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		boolean imageFlag=false;
		DwTimer mDwTimer= mDwTimerList.get(pos);
		int socketOnEnable = mDwTimer.getSocketOnEnable();
		int socketOffEnable = mDwTimer.getSocketOffEnable();
		int count = socketOnEnable-socketOffEnable;
		switch (count) {
		case -1://0 1
			holder.start_time_tv.setText(R.string.close);
			holder.start_time.setText(mDwTimer.getSocketOffTime());
			holder.end_time.setText("");
			break;
		case 0://1 1
			holder.start_time_tv.setText(R.string.open_time);
			holder.start_time.setText(mDwTimer.getSocketOnTime());
			holder.end_time.setText(" - "+mDwTimer.getSocketOffTime());
			break;
		case 1://0 0
			holder.start_time_tv.setText(R.string.open);
			holder.start_time.setText(mDwTimer.getSocketOnTime());
			holder.end_time.setText("");
			break;
		default:
			break;
		}
		int sun = mDwTimer.getSun();
		int mon = mDwTimer.getMon();
		int tue = mDwTimer.getTue();
		int wed = mDwTimer.getWed();
		int thu = mDwTimer.getThu();
		int fri = mDwTimer.getFri();
		int sat = mDwTimer.getSat();
		
		List<Integer> li = new ArrayList<Integer>();
		if(1==sun){
			li.add(0);
		}
		if(1==mon){
			li.add(1);
		}
		if(1==tue){
			li.add(2);
		}
		if(1==wed){
			li.add(3);
		}
		if(1==thu){
			li.add(4);
		}
		if(1==fri){
			li.add(5);
		}
		if(1==sat){
			li.add(6);
		}
		if(li.size()>0){
			StringBuilder sb = new StringBuilder();
			for(int j=0;j<li.size();j++){
				sb.append(Constants.WEEKEN_STRING[li.get(j)]).append(" ");
			}
			if(Constants.EVERY_DAY.equals(sb.toString())){
				holder.week_list.setText(Constants.WeekType.EVERY_DAY_TYPE);
			}else if(Constants.WORKING_DAY.equals(sb.toString())){
				holder.week_list.setText(Constants.WeekType.WORKING_DAY_TYPE);
			}else if(Constants.WEEKEN_DAY.equals(sb.toString())){
				holder.week_list.setText(Constants.WeekType.WEEKEN_DAY_TYPE);
			}else{
				holder.week_list.setText(sb.toString());
			}
		}else{
			SimpleDateFormat d = new SimpleDateFormat("HH:mm:ss");
		    String dateStr = d.format(Long.valueOf(System.currentTimeMillis()));
		    long nowTime = timeToLong(dateStr);
		    long statTime = timeToLong(mDwTimer.getSocketOnTime());
		    long endTime = timeToLong(mDwTimer.getSocketOffTime());
		    long typeTime = statTime-nowTime;
		    long typeTime1 = endTime-nowTime;
		    if(statTime==0&&typeTime1>0){
		    	holder.week_list.setText(Constants.WeekType.TODAY);
		    }else if(endTime==0&&typeTime>0){
		    	holder.week_list.setText(Constants.WeekType.TODAY);
		    }else if(typeTime>=0&&typeTime1>=0){
		    	holder.week_list.setText(Constants.WeekType.TODAY);
		    }else {
		    	holder.week_list.setText(Constants.WeekType.TOMORROW);
		    }
		   
		}
		final int enable = mDwTimer.getEnable();
		int repeat = mDwTimer.getRepeat();
		if(repeat==1){
			if(1==enable){
				holder.open_close_image.setBackgroundResource(R.drawable.ic_checkbox_on);
				imageFlag = true;
			}else{
				holder.open_close_image.setBackgroundResource(R.drawable.ic_checkbox_off);
				imageFlag = false;
			}
		}else{
			if(1==enable){
				boolean enabled = actioned(mDwTimer.getSocketOnTime(),mDwTimer.getSocketOffTime(),mDwTimer.getRecordeTime());
				if(enabled){
					holder.open_close_image.setBackgroundResource(R.drawable.ic_checkbox_off);
					imageFlag = false;
				}else{
					holder.open_close_image.setBackgroundResource(R.drawable.ic_checkbox_on);
					imageFlag = true;
				}
			}else{
				holder.open_close_image.setBackgroundResource(R.drawable.ic_checkbox_off);
				imageFlag = false;
			}
		}
		
		holder.open_close_image.setOnClickListener(new OnClickListener() {
			
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				regFilter();
				View v = LayoutInflater.from(mContext).inflate(
						R.layout.dialog_loading, null);
				AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
				TextView title_text = (TextView) v.findViewById(R.id.title_text);
				title_text.setText(R.string.is_setting);
				dialog = builder.create();
				dialog.show();
				dialog.setContentView(v);
				dialog.setCancelable(false);
				DwTimer mDwTimer = mDwTimerList.get(pos);
				mac = mDwTimer.getDwMac();
				byte[] orderDatas = new byte[18];
				IntegerTo16 mIntegerTo16 = new IntegerTo16();
				orderDatas[0] = mIntegerTo16.algorismToHEXString(mDwTimer.getSequence());
				boolean ifEnabled = actioned(mDwTimer.getSocketOnTime(),mDwTimer.getSocketOffTime(),mDwTimer.getRecordeTime());
				if(1==mDwTimer.getEnable()&&ifEnabled==false){
					orderDatas[1]=0x00;
				}else if(1==mDwTimer.getEnable()&&ifEnabled==true){
					orderDatas[1]=0x01;
				}else {
					orderDatas[1]=0x01;
				}
				orderDatas[2] = mIntegerTo16.algorismToHEXString(mDwTimer.getRepeat());
				orderDatas[3] = mIntegerTo16.algorismToHEXString(mDwTimer.getSat());
				orderDatas[4] = mIntegerTo16.algorismToHEXString(mDwTimer.getFri());
				orderDatas[5] = mIntegerTo16.algorismToHEXString(mDwTimer.getThu());
				orderDatas[6] = mIntegerTo16.algorismToHEXString(mDwTimer.getWed());
				orderDatas[7] = mIntegerTo16.algorismToHEXString(mDwTimer.getTue());
				orderDatas[8] = mIntegerTo16.algorismToHEXString(mDwTimer.getMon());
				orderDatas[9] = mIntegerTo16.algorismToHEXString(mDwTimer.getSun());
				
				orderDatas[10] = mIntegerTo16.algorismToHEXString(mDwTimer.getSocketOnEnable());
				String[] onTime =mDwTimer.getSocketOnTime().split(":");
				orderDatas[11] = mIntegerTo16.algorismToHEXString(Integer.parseInt(onTime[0]));
				orderDatas[12] = mIntegerTo16.algorismToHEXString(Integer.parseInt(onTime[1]));
				orderDatas[13] = mIntegerTo16.algorismToHEXString(Integer.parseInt(onTime[2]));
				
				orderDatas[14] = mIntegerTo16.algorismToHEXString(mDwTimer.getSocketOffEnable());
				String[] offTime =mDwTimer.getSocketOffTime().split(":");
				orderDatas[15] = mIntegerTo16.algorismToHEXString(Integer.parseInt(offTime[0]));
				orderDatas[16] = mIntegerTo16.algorismToHEXString(Integer.parseInt(offTime[1]));
				orderDatas[17] = mIntegerTo16.algorismToHEXString(Integer.parseInt(offTime[2]));
				byte[] orderSend = SendServerOrder.TimerOrder(mac, orderDatas);
				mSocketUDPClient.sendMsg(orderSend);
				mTimer = new Timer();
				setTimerdoAction(doAction,mTimer);
			}
		});
		return convertView;
	}
	
	private long timeToLong(String strTime){
		String[] my = strTime.split(":");
        int hour =Integer.parseInt(my[0]);
        int min =Integer.parseInt(my[1]);
        int sec =Integer.parseInt(my[2]);
        long totalSec =hour*3600+min*60+sec;
		return totalSec;
	}
	
	public void regFilter() {
		IntentFilter filter = new IntentFilter();
		filter.addAction("Constants.Action.unServerACKPack");
		filter.addAction("Constants.Action.unTimerOrderPack");
		mContext.registerReceiver(mReceiver, filter);
	}
	
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			if (arg1.getAction().equals("Constants.Action.unServerACKPack")){
				byte[] datas = arg1.getExtras().getByteArray("datasByte");
				new UnPackServer().unServerACKPack(datas);
			}
			
			if (arg1.getAction().equals("Constants.Action.unTimerOrderPack")){
				byte[] datas = arg1.getExtras().getByteArray("datasByte");
				UnPackageFromServer mUnPackageFromServer =new UnPackServer().unTimerOrderPack(datas);
				String receiveFlag = mUnPackageFromServer.timerOrder;
				byte[] seq = mUnPackageFromServer.seq;
				if("true".equals(receiveFlag)){
					Intent i = new Intent();
					i.setAction("REFREASH_ADAPTER");
					arg0.sendBroadcast(i);
					Toast.makeText(mContext, R.string.set_success, Toast.LENGTH_SHORT).show();
					mSocketUDPClient.sendMsg(SendServerOrder.ClientACKOrder(mac,seq));
				}
				if("fail".equals(receiveFlag)){
					Toast.makeText(mContext, R.string.set_fail, Toast.LENGTH_SHORT).show();
					mSocketUDPClient.sendMsg(SendServerOrder.ClientACKOrder(mac,seq));
				}
				if(dialog!=null&&dialog.isShowing()){
					dialog.dismiss();
				}
				if(mTimer!=null){
					mTimer.cancel();
				}
				count=0;
				mContext.unregisterReceiver(mReceiver);
			}
		}
	};
	
	private int count=0;
	private void setTimerdoAction(final Handler oj,Timer t) { 
        t.schedule(new TimerTask() {  
            @Override  
            public void run() {
            	count = count+1;
            	Message message = new Message();
            	if(count>8){
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
			int messsageId = msg.what;
			switch (messsageId) {
			case 1:
				if(dialog!=null&&dialog.isShowing()){
					dialog.dismiss();
				}
				if(mTimer!=null){
					mTimer.cancel();
				}
				count=0;
				mContext.unregisterReceiver(mReceiver);
				break;
			default:
				break;
			}
		}
	};
	
	private boolean actioned(String socketOnTime,String socketOffTime,String recordeTime){	    
	    SimpleDateFormat ymd  = new SimpleDateFormat("yyyy-MM-dd");
	    long nowTime = Long.valueOf(System.currentTimeMillis());
	    String[] s = recordeTime.split(" ");
	    String re = s[1];
	    long reTime = timeToLong(re)*1000;
	    long onTime = timeToLong(socketOnTime)*1000;
	    long offTime = timeToLong(socketOffTime)*1000;
	    if(onTime!=0&&offTime!=0){
	    	long todayOn = onTime-reTime;
		    long todayOff = offTime-reTime;
		    Date d2 = null;
			try {
				d2 = ymd.parse(s[0]);
				long l = d2.getTime();
				long onTimeStr;
				long offTimeStr;
			    if(todayOn>0){
			    	 onTimeStr = l+onTime;//1463068863725 1463101383533
			    }else{
			    	 onTimeStr = l+onTime+86400000;
			    }
			    if(todayOff>0){
			    	offTimeStr = l+offTime;
			    } else{
			    	offTimeStr = l+offTime+86400000;
			    }
			    long todayOrM1;
			    long todayOrM2;
			    if(onTime>0){
			    	todayOrM1= nowTime-onTimeStr;
			    }else{
			    	todayOrM1 = nowTime;
			    }
			    if(offTime>0){
			    	todayOrM2= nowTime-offTimeStr;
			    }else{
			    	todayOrM2 = nowTime;
			    }
			     
			    if(todayOrM1>0&&todayOrM2>0){
			    	return true;//执行
			    }else {
			    	return false;
			    }
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
	    }else if(onTime==0&&offTime!=0){
	    	long todayOff = offTime-reTime;
		    Date d2 = null;
			try {
				d2 = ymd.parse(s[0]);
				long l = d2.getTime();
				long offTimeStr;
			    if(todayOff>0){
			    	offTimeStr = l+offTime;//1463068854643
			    } else{
			    	offTimeStr = l+offTime+86400000;
			    }
			    
			    long todayOrM2= nowTime-offTimeStr;
			    if(todayOrM2>0){
			    	return true;//执行
			    }else {
			    	return false;
			    }
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
	    }else if(onTime!=0&&offTime==0){
	    	long todayOn = onTime-reTime;
		    Date d2 = null;
			try {
				d2 = ymd.parse(s[0]);
				long l = d2.getTime();
				long onTimeStr;
			    if(todayOn>0){
			    	 onTimeStr = l+onTime;//1463068863725 1463101383533
			    }else{
			    	 onTimeStr = l+onTime+86400000;
			    }
			    long todayOrM1;
			    todayOrM1= nowTime-onTimeStr;
			     
			    if(todayOrM1>0){
			    	return true;//执行
			    }else {
			    	return false;
			    }
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
	    }else{
	    	return false;
	    }
	}
}
