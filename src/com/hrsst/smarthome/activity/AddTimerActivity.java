package com.hrsst.smarthome.activity;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map.Entry;

import com.hrsst.smarthome.R;
import com.hrsst.smarthome.adapter.WeekenAdapter;
import com.hrsst.smarthome.global.Constants;
import com.hrsst.smarthome.net.SocketUDP;
import com.hrsst.smarthome.order.SendServerOrder;
import com.hrsst.smarthome.order.UnPackServer;
import com.hrsst.smarthome.pojo.UnPackageFromServer;
import com.hrsst.smarthome.util.IntegerTo16;
import com.hrsst.smarthome.wheelutil.OnWheelScrollListener;
import com.hrsst.smarthome.wheelutil.StrericWheelAdapter;
import com.hrsst.smarthome.wheelutil.WheelView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AddTimerActivity extends Activity implements OnClickListener{
	private Context mContext;
	private RelativeLayout fire_lin;
	private LinearLayout second_lin,thire_lin;
	private RelativeLayout once_rel,everyday_rel,working_rel,diy_timer_list,diy_item,weeks_rel,diy_weenken_rel,time_list_choice;
	private ListView diy_list;
	private TextView repetition_tv,open_time,close_time,clear_close_time,clear_open_time;
	private ImageView image_view,diy_image_view,check_image,start_time_image,end_time_image,time_list_choice_image;
	private WheelView hourWheel,minuteWheel,secondWheel;
	private Button diy_cancel_btn,diy_confire_btn,confire_button;
	public static String[] hourContent = null;
	public static String[] minuteContent=null;
	public static String[] secondContent=null;
	private boolean isDpShow=false,isDiyShow=false,isTime=false;
	private Map<Integer,Integer> map;
	private byte[] by;
	private WeekenAdapter mWeekenAdapter;
	private StringBuilder sb,firstSb;
	private byte[] orderDatas,sendDatas;
	private byte id;//传过来的定时的序号
	private String mac,startTime,endTime;//传过来的设备mac地址
	int selected_Date;
	public static final int OPEN_TIME = 0;
	public static final int CLOSE_TIME = 1;
	private SocketUDP mSocketUDPClient;
	private AlertDialog dialog;
	//通信逻辑处理计时器
	private Timer mTimer;
	private boolean ifNew;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_timer);
		mContext = this;
		id = getIntent().getExtras().getByte("id");
		mac = getIntent().getExtras().getString("mac");
		endTime = getIntent().getExtras().getString("endTime");
		startTime = getIntent().getExtras().getString("startTime");
		sendDatas = getIntent().getExtras().getByteArray("orderDatas");
		if(id==0x00){
			id = sendDatas[0];
			ifNew = false;
		}else{
			ifNew = true;
		}
		initContent();
		init();
		regFilter();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.fire_lin:
			selected_Date=9;
			if(isDpShow==false){
				showDiyItem();
			}else{
				closeDiyItem();
			}
			break;
		case R.id.diy_weenken_rel:
			isDpShow=false;
			closeDiyItem();
			showDatePick();
			break;
		case R.id.second_lin:
			selected_Date = OPEN_TIME;
			showTimeList();
			break;
		case R.id.thire_lin:
			selected_Date = CLOSE_TIME;
			showTimeList();
			break;
		case R.id.image_view:
			if(isDpShow){
				closeDatePick();
			}
			break;
		case R.id.diy_image_view:
			if(isDiyShow){
				closeDiyItem();
			}
			break;
		case R.id.time_list_choice_image:
			if(isTime){
				closeTimeList();
			}
			break;
		case R.id.once_rel:
			closeDiyItem();
			for(int i=0;i<7;i++){
				by[i] = 0x00;
			}
			//getWeek();
			repetition_tv.setText(R.string.run_once);
			break;
		case R.id.everyday_rel:
			closeDiyItem();
			repetition_tv.setText(R.string.everyday);
			for(int i=0;i<7;i++){
				by[i] = 0x01;
			}
			break;
		case R.id.working_rel:
			closeDiyItem();
			repetition_tv.setText(R.string.workday);
			by[0] = 0x00;
			by[6] = 0x00;
			for(int i=1;i<6;i++){
				by[i] = 0x01;
			}
			break;
		case R.id.weeks_rel:
			closeDiyItem();
			repetition_tv.setText(R.string.weekend);
			for(int i=1;i<6;i++){
				by[i] = 0x00;
			}
			by[0] = 0x01;
			by[6] = 0x01;
			break;
		case R.id.diy_cancel_btn://自定义取消按钮
			closeDatePick();
			break;
		case R.id.diy_confire_btn://自定义确定按钮
			closeDatePick();
			if(map.size()>0){
				sb = new StringBuilder();
				List<Integer> li = new ArrayList<Integer>();
				for(Entry<Integer, Integer> str : map.entrySet()){
					int i = str.getValue();
					li.add(i);
				}
				Collections.sort(li);
				for(int j=0;j<li.size();j++){
					sb.append(Constants.WEEKEN_STRING[li.get(j)]).append(" ");
				}
				if(Constants.EVERY_DAY.equals(sb.toString())){
					repetition_tv.setText(Constants.WeekType.EVERY_DAY_TYPE);
					orderDatas[2] = 0x01;
				}else if(Constants.WORKING_DAY.equals(sb.toString())){
					repetition_tv.setText(Constants.WeekType.WORKING_DAY_TYPE);
					orderDatas[2] = 0x01;
				}else if(Constants.WEEKEN_DAY.equals(sb.toString())){
					repetition_tv.setText(Constants.WeekType.WEEKEN_DAY_TYPE);
					orderDatas[2] = 0x01;
				}else{
					repetition_tv.setText(sb.toString());
					orderDatas[2] = 0x01;
				}
			}else{
				repetition_tv.setText(Constants.WeekType.ONCE_TYPE);
				//getWeek();
				orderDatas[2] = 0x00;
			}
			for(int g=0;g<7;g++){
				orderDatas[3+g] = by[6-g];
			}
			break;
		case R.id.clear_close_time:
			orderDatas[14] = 0x00;
			orderDatas[15] = 0x00;
			orderDatas[16] = 0x00;
			orderDatas[17] = 0x00;
			clear_close_time.setVisibility(View.GONE);
			end_time_image.setVisibility(View.VISIBLE);
			close_time.setText(R.string.no_setted);
			break;
		case R.id.clear_open_time:
			orderDatas[10] = 0x00;
			orderDatas[11] = 0x00;
			orderDatas[12] = 0x00;
			orderDatas[13] = 0x00;
			clear_open_time.setVisibility(View.GONE);
			start_time_image.setVisibility(View.VISIBLE);
			open_time.setText(R.string.no_setted);
			break;
		case R.id.confire_button:
			View view = LayoutInflater.from(mContext).inflate(
					R.layout.dialog_loading, null);
			TextView title_text = (TextView) view.findViewById(R.id.title_text);
			title_text.setText(R.string.is_setting);
			AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
			dialog = builder.create();
			dialog.setCancelable(false);
			dialog.show();
			dialog.setContentView(view);
			for(int g=0;g<7;g++){
				orderDatas[3+g] = by[6-g];
			}
			byte[] orderSend = SendServerOrder.TimerOrder(mac, orderDatas);
			mSocketUDPClient.sendMsg(orderSend);
			mTimer = new Timer();
			setTimerdoAction(doAction,mTimer);
			break;
		default:
			break;
		}
	}
	
	public void regFilter() {
		IntentFilter filter = new IntentFilter();
		filter.addAction("Constants.Action.unTimerOrderPack");
		filter.addAction("Constants.Action.unServerACKPack");
		mContext.registerReceiver(mReceiver, filter);
	}
	
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			if (arg1.getAction().equals("Constants.Action.unTimerOrderPack")){
				byte[] datas = arg1.getExtras().getByteArray("datasByte");
				UnPackageFromServer mUnPackageFromServer = new UnPackServer().unTimerOrderPack(datas);
				String receiveFlag = mUnPackageFromServer.timerOrder;
				byte[] seq = mUnPackageFromServer.seq;
				if("true".equals(receiveFlag)){
					Toast.makeText(mContext, R.string.configuration_success, Toast.LENGTH_SHORT).show();
					mSocketUDPClient.sendMsg(SendServerOrder.ClientACKOrder(mac,seq));
					Intent i = new Intent(mContext,TimerListActivity.class);
					i.putExtra("dwMac", mac);
					startActivity(i);
					finish();
				}
				if("fail".equals(receiveFlag)){
					Toast.makeText(mContext, R.string.configurantion_fail, Toast.LENGTH_SHORT).show();
					mSocketUDPClient.sendMsg(SendServerOrder.ClientACKOrder(mac,seq));
				}
				if(dialog!=null&&dialog.isShowing()){
					dialog.dismiss();
				}
				mTimer.cancel();
				count=0;
			}
			
			if (arg1.getAction().equals("Constants.Action.unServerACKPack")){
				byte[] datas = arg1.getExtras().getByteArray("datasByte");
				new UnPackServer().unServerACKPack(datas);
			}
		}
	};

	private void init(){
		buttonClick();
		fire_lin = (RelativeLayout) findViewById(R.id.fire_lin);
		second_lin = (LinearLayout) findViewById(R.id.second_lin);
		thire_lin = (LinearLayout) findViewById(R.id.thire_lin);
		thire_lin.setOnClickListener(this);
		diy_timer_list = (RelativeLayout) findViewById(R.id.diy_timer_Rela);
		diy_list = (ListView) findViewById(R.id.diy_timer_list);
		image_view= (ImageView) findViewById(R.id.image_view);
		diy_image_view= (ImageView) findViewById(R.id.diy_image_view);
		start_time_image = (ImageView) findViewById(R.id.start_time_image);
		end_time_image = (ImageView) findViewById(R.id.end_time_image);
		time_list_choice_image = (ImageView) findViewById(R.id.time_list_choice_image);
		time_list_choice = (RelativeLayout) findViewById(R.id.time_list_choice);
		time_list_choice_image.setOnClickListener(this);
		image_view.setOnClickListener(this);
		diy_image_view.setOnClickListener(this);
		fire_lin.setOnClickListener(this);
		second_lin.setOnClickListener(this);
		diy_item = (RelativeLayout) findViewById(R.id.diy_item);
		diy_weenken_rel = (RelativeLayout) findViewById(R.id.diy_weenken_rel);//自定义
		diy_weenken_rel.setOnClickListener(this);
		once_rel = (RelativeLayout) findViewById(R.id.once_rel);//一次
		once_rel.setOnClickListener(this);
		everyday_rel = (RelativeLayout) findViewById(R.id.everyday_rel);//每天
		everyday_rel.setOnClickListener(this);
		working_rel = (RelativeLayout) findViewById(R.id.working_rel);//工作日
		working_rel.setOnClickListener(this);
		weeks_rel = (RelativeLayout) findViewById(R.id.weeks_rel);//周末
		weeks_rel.setOnClickListener(this);
		repetition_tv = (TextView) findViewById(R.id.repetition_tv);
		open_time = (TextView) findViewById(R.id.open_time);//开启时间
		close_time = (TextView) findViewById(R.id.close_time);//结束时间
		clear_close_time = (TextView) findViewById(R.id.clear_close_time);//清除结束时间
		clear_open_time = (TextView) findViewById(R.id.clear_open_time);//清除开启时间
		clear_close_time.setOnClickListener(this);
		clear_open_time.setOnClickListener(this);
		diy_cancel_btn = (Button) findViewById(R.id.diy_cancel_btn);
		diy_confire_btn = (Button) findViewById(R.id.diy_confire_btn);
		confire_button = (Button) findViewById(R.id.confire_button);
		confire_button.setOnClickListener(this);
		diy_cancel_btn.setOnClickListener(this);
		diy_confire_btn.setOnClickListener(this);
		
		map = new HashMap<Integer,Integer>();
		by = new byte[7];
		for(int i=0;i<7;i++){
			by[i] = 0x00;
		}
		if(ifNew==false){
			for(int i=0;i<7;i++){
				by[i] = sendDatas[9-i];
			}
			orderDatas = new byte[18];
			orderDatas[0] = id;
			orderDatas[1] = sendDatas[1];
			for(int i=0;i<16;i++){
				orderDatas[2+i] = sendDatas[2+i];
			}
			int socketOnEnable = sendDatas[10];
			int socketOffEnable = sendDatas[14];
			int count = socketOnEnable-socketOffEnable;
			switch (count) {
			case -1://0 1
				open_time.setText(R.string.no_setted);
				start_time_image.setVisibility(View.VISIBLE);
				close_time.setText(endTime);
				end_time_image.setVisibility(View.GONE);
				clear_close_time.setVisibility(View.VISIBLE);
				clear_open_time.setVisibility(View.GONE);
				break;
			case 0://1 1
				open_time.setText(startTime);
				close_time.setText(endTime);
				end_time_image.setVisibility(View.GONE);
				start_time_image.setVisibility(View.GONE);
				clear_close_time.setVisibility(View.VISIBLE);
				clear_open_time.setVisibility(View.VISIBLE);
				break;
			case 1://0 0
				open_time.setText(startTime);
				close_time.setText(R.string.no_setted);
				start_time_image.setVisibility(View.GONE);
				end_time_image.setVisibility(View.VISIBLE);
				clear_close_time.setVisibility(View.GONE);
				clear_open_time.setVisibility(View.VISIBLE);
				break;
			default:
				break;
			}
			int repeat = sendDatas[2];
			if(0==repeat){
				repetition_tv.setText(R.string.run_once);
			}else{
				List<Integer> li = new ArrayList<Integer>();
				for(int j=6;j>=0;j--){
					int g= sendDatas[3+j];
					if(1==g){
						li.add(j);
					}
				}
				firstSb = new StringBuilder();
				Collections.sort(li);
				for(int j=0;j<li.size();j++){
					firstSb.append(Constants.WEEKEN_STRING[li.get(j)]).append(" ");
				}
				if(Constants.EVERY_DAY.equals(firstSb.toString())){
					repetition_tv.setText(Constants.WeekType.EVERY_DAY_TYPE);
				}else if(Constants.WORKING_DAY.equals(firstSb.toString())){
					repetition_tv.setText(Constants.WeekType.WORKING_DAY_TYPE);
				}else if(Constants.WEEKEN_DAY.equals(firstSb.toString())){
					repetition_tv.setText(Constants.WeekType.WEEKEN_DAY_TYPE);
				}else{
					SimpleDateFormat d = new SimpleDateFormat("HH:mm:ss");
				    String dateStr = d.format(Long.valueOf(System.currentTimeMillis()));
				    long nowTime = timeToLong(dateStr);
				    long statTime = timeToLong(startTime);
				    long lastTime = timeToLong(endTime);
				    long typeTime = statTime-nowTime;
				    long typeTime1 = lastTime-nowTime;
				    if(typeTime>=0&&typeTime1>=0){
				    	repetition_tv.setText(Constants.WeekType.TODAY);
				    }else{
				    	repetition_tv.setText(Constants.WeekType.TOMORROW);
				    }
				}
			}
		}else{
			orderDatas = new byte[18];
			orderDatas[0] = id;
			orderDatas[1] = 0x01;
			for(int i=0;i<8;i++){
				orderDatas[10+i] = 0x00;
			}
		}
		diy_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,
					long arg3) {
				// TODO Auto-generated method stub
				check_image = (ImageView) view.findViewById(R.id.check_image);
				if(by[position]==0x00){
					by[position]=0x01;
					map.put(position, position);
					check_image.setImageResource(R.drawable.check_on);
				}else{
					by[position]=0x00;
					map.remove(position);
					check_image.setImageResource(R.drawable.check_off);
				}
			}
		});
		
		mSocketUDPClient = SocketUDP.newInstance(Constants.SeverInfo.SERVER
				, Constants.SeverInfo.PORT);
		mSocketUDPClient.startAcceptMessage();
	}
	
	private long timeToLong(String strTime){
		String[] my = strTime.split(":");
        int hour =Integer.parseInt(my[0]);
        int min =Integer.parseInt(my[1]);
        int sec =Integer.parseInt(my[2]);
        long totalSec =hour*3600+min*60+sec;
		return totalSec;
	}
	
	public void showDatePick() {
		isDpShow = true;
		diy_timer_list.setVisibility(RelativeLayout.VISIBLE);
		Animation anim = AnimationUtils.loadAnimation(mContext,
				R.anim.slide_in_bottom);
		diy_timer_list.startAnimation(anim);
		mWeekenAdapter = new WeekenAdapter(mContext,by,Constants.WEEKEN_STRING);
		diy_list.setAdapter(mWeekenAdapter);
	}
	
	public void closeDatePick() {
		isDpShow = false;
		diy_timer_list.setVisibility(RelativeLayout.GONE);
		Animation anim = AnimationUtils.loadAnimation(mContext,
				R.anim.slide_out_to_bottom);
		diy_timer_list.startAnimation(anim);
	}
	
	public void showDiyItem() {
		isDiyShow = true;
		diy_item.setVisibility(RelativeLayout.VISIBLE);
		Animation anim = AnimationUtils.loadAnimation(mContext,
				R.anim.slide_in_bottom);
		diy_item.startAnimation(anim);
		diy_item.setFocusableInTouchMode(true);
	}
	
	private void showTimeList(){
		isTime = true;
		time_list_choice.setVisibility(View.VISIBLE);
		Animation anim = AnimationUtils.loadAnimation(mContext,
				R.anim.slide_in_bottom);
		time_list_choice.startAnimation(anim);
	}
	
	private void closeTimeList(){
		isTime = false;
		time_list_choice.setVisibility(View.GONE);
		Animation anim = AnimationUtils.loadAnimation(mContext,
				R.anim.slide_out_to_bottom);
		time_list_choice.startAnimation(anim);
	}

	public void closeDiyItem() {
		isDiyShow = false;
		diy_item.setVisibility(RelativeLayout.GONE);
		Animation anim = AnimationUtils.loadAnimation(mContext,
				R.anim.slide_out_to_bottom);
		diy_item.startAnimation(anim);
	}
	
	private boolean wheelScrolled = false;
	OnWheelScrollListener scrolledListener = new OnWheelScrollListener() {
		public void onScrollingStarted(WheelView wheel) {
			wheelScrolled = true;
			updateSearch();
		}

		public void onScrollingFinished(WheelView wheel) {
			wheelScrolled = false;
			updateSearch();
		}
	};
	
	public void updateSearch() {
		int hour = hourWheel.getCurrentItem();
		int minute = minuteWheel.getCurrentItem();
		int second = secondWheel.getCurrentItem();
		StringBuilder sb = new StringBuilder();
		if (hour < 10) {
			sb.append("0" + hour + ":");
		} else {
			sb.append(hour + ":");
		}

		if (minute < 10) {
			sb.append("0" + minute+":");
		} else {
			sb.append(minute + ":");
		}
		
		if (second < 10) {
			sb.append("0" + second);
		} else {
			sb.append("" + second);
		}

		if (selected_Date == OPEN_TIME) {
			open_time.setText(sb.toString());
			orderDatas[10] = 0x01;
			orderDatas[11] = new IntegerTo16().algorismToHEXString(hour);
			orderDatas[12] = new IntegerTo16().algorismToHEXString(minute);
			orderDatas[13] = new IntegerTo16().algorismToHEXString(second);
			clear_open_time.setVisibility(View.VISIBLE);
			start_time_image.setVisibility(View.GONE);
		} 
		
		if(selected_Date == CLOSE_TIME){
			close_time.setText(sb.toString());
			orderDatas[14] = 0x01;
			orderDatas[15] = new IntegerTo16().algorismToHEXString(hour);
			orderDatas[16] = new IntegerTo16().algorismToHEXString(minute);
			orderDatas[17] = new IntegerTo16().algorismToHEXString(second);
			clear_close_time.setVisibility(View.VISIBLE);
			end_time_image.setVisibility(View.GONE);
		}
	}
	
	public void buttonClick()
    {	
		Calendar calendar = Calendar.getInstance();
        int curHour = calendar.get(Calendar.HOUR_OF_DAY);
        int curMinute = calendar.get(Calendar.MINUTE);
        int curSecond = calendar.get(Calendar.SECOND);

	    hourWheel = (WheelView)findViewById(R.id.hourwheel);
	    minuteWheel = (WheelView)findViewById(R.id.minutewheel);
	    secondWheel = (WheelView)findViewById(R.id.secondWheel);
        
        hourWheel.setAdapter(new StrericWheelAdapter(hourContent));
        hourWheel.setCurrentItem(curHour);
        hourWheel.setCyclic(true);
        hourWheel.addScrollingListener(scrolledListener);
        hourWheel.setInterpolator(new AnticipateOvershootInterpolator());
        
        minuteWheel.setAdapter(new StrericWheelAdapter(minuteContent));
        minuteWheel.setCurrentItem(curMinute);
        minuteWheel.setCyclic(true);
        minuteWheel.addScrollingListener(scrolledListener);
        minuteWheel.setInterpolator(new AnticipateOvershootInterpolator());

        secondWheel.setAdapter(new StrericWheelAdapter(secondContent));
        secondWheel.setCurrentItem(curSecond);
        secondWheel.setCyclic(true);
        secondWheel.addScrollingListener(scrolledListener);
        secondWheel.setInterpolator(new AnticipateOvershootInterpolator());
	}
	
	public void initContent(){
		hourContent = new String[24];
		for(int i=0;i<24;i++)
		{
			hourContent[i]= String.valueOf(i);
			if(hourContent[i].length()<2)
	        {
				hourContent[i] = "0"+hourContent[i];
	        }
		}
			
		minuteContent = new String[60];
		for(int i=0;i<60;i++)
		{
			minuteContent[i]=String.valueOf(i);
			if(minuteContent[i].length()<2)
	        {
				minuteContent[i] = "0"+minuteContent[i];
	        }
		}
		secondContent = new String[60];
		for(int i=0;i<60;i++)
		{
			secondContent[i]=String.valueOf(i);
			if(secondContent[i].length()<2)
	        {
				secondContent[i] = "0"+secondContent[i];
	        }
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode == KeyEvent.KEYCODE_BACK ){
			if(isDpShow){
				closeDatePick();
			}else if(isDiyShow){
				closeDiyItem();
			}else{
				Intent i = new Intent(mContext,TimerListActivity.class);
				i.putExtra("dwMac", mac);
				startActivity(i);
				finish();
			}
	    	return true;
	    	
	    }else{
	    	return super.onKeyDown(keyCode, event);
	    }
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(mReceiver);
		super.onDestroy();
		
	}
	
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
			switch (msg.what) {
			case 1:
				if(dialog!=null&&dialog.isShowing()){
					dialog.dismiss();
				}
				mTimer.cancel();
				count=0;
				break;
			default:
				break;
			}
		}
	};
}
