package com.hrsst.smarthome;

import com.hrsst.smarthome.activity.ApMonitorActivity;
import com.hrsst.smarthome.pojo.Contact;
import com.p2p.core.P2PHandler;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SetVideoBuzzerActivity extends Activity implements OnClickListener{
	
	public static final String PACKAGE_NAME = "com.hrsst.smarthome.global.";
	public static final String ACK_RET_SET_BUZZER = PACKAGE_NAME+"RET_SET_BUZZER";
	public static final String RET_SET_BUZZER = PACKAGE_NAME+"RET_SET_BUZZER";
	public static final String RET_GET_BUZZER = PACKAGE_NAME+"RET_GET_BUZZER";
	
	public static final String CONTROL_SETTING_PWD_ERROR = PACKAGE_NAME+"CONTROL_SETTING_PWD_ERROR";
	
	private Context mContext;
	private Contact contact;
	ImageView buzzer_img;//开关按钮
	RelativeLayout change_buzzer;
	ProgressBar progressBar;
	LinearLayout buzzer_time;//下弹窗
	RadioButton radio_one, radio_two, radio_three;
	
	int buzzer_switch;
	int cur_modify_buzzer_state;
	
	private boolean isRegFilter = false;
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_video_buzzer);
		mContext= this;
		contact = (Contact) getIntent().getSerializableExtra("contact");
		init();
		regFilter();
		P2PHandler.getInstance().getNpcSettings(contact.contactId,
				contact.contactPassword);
		P2PHandler.getInstance().getBindAlarmId(contact.contactId,
				contact.contactPassword);
	}

	private void regFilter() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACK_RET_SET_BUZZER);
		filter.addAction(RET_SET_BUZZER);
		filter.addAction(RET_GET_BUZZER);
		mContext.registerReceiver(mReceiver, filter);
		isRegFilter = true;
	}

	private void init() {
		TextView contact_name_tv = (TextView) findViewById(R.id.contact_name_tv);
		contact_name_tv.setText(contact.contactName);
		buzzer_img = (ImageView)findViewById(R.id.buzzer_img);
		change_buzzer = (RelativeLayout)findViewById(R.id.change_buzzer);
		change_buzzer.setOnClickListener(this);
		progressBar = (ProgressBar)findViewById(R.id.progressBar);
		buzzer_time = (LinearLayout)findViewById(R.id.buzzer_time);
		radio_one = (RadioButton)findViewById(R.id.radio_one);
		radio_two = (RadioButton)findViewById(R.id.radio_two);
		radio_three = (RadioButton)findViewById(R.id.radio_three);
		radio_one.setOnClickListener(this);
		radio_two.setOnClickListener(this);
		radio_three.setOnClickListener(this);
	}
	
	public void updateBuzzer(int state) {
		if (state == BUZZER_SET.BUZZER_SWITCH_ON_ONE_MINUTE) {
			buzzer_switch = BUZZER_SET.BUZZER_SWITCH_ON_ONE_MINUTE;
			buzzer_img.setBackgroundResource(R.drawable.ic_checkbox_on);
			change_buzzer.setBackgroundResource(R.drawable.tiao_bg_up);
			buzzer_time.setVisibility(RelativeLayout.VISIBLE);
			radio_one.setChecked(true);
		} else if (state == BUZZER_SET.BUZZER_SWITCH_ON_TWO_MINUTE) {
			buzzer_switch = BUZZER_SET.BUZZER_SWITCH_ON_TWO_MINUTE;
			buzzer_img.setBackgroundResource(R.drawable.ic_checkbox_on);
			change_buzzer.setBackgroundResource(R.drawable.tiao_bg_up);
			buzzer_time.setVisibility(RelativeLayout.VISIBLE);
			radio_two.setChecked(true);
		} else if (state == BUZZER_SET.BUZZER_SWITCH_ON_THREE_MINUTE) {
			buzzer_switch = BUZZER_SET.BUZZER_SWITCH_ON_THREE_MINUTE;
			buzzer_img.setBackgroundResource(R.drawable.ic_checkbox_on);
			change_buzzer.setBackgroundResource(R.drawable.tiao_bg_up);
			buzzer_time.setVisibility(RelativeLayout.VISIBLE);
			radio_three.setChecked(true);
		} else {
			buzzer_switch = BUZZER_SET.BUZZER_SWITCH_OFF;
			buzzer_img.setBackgroundResource(R.drawable.ic_checkbox_off);
			change_buzzer.setBackgroundResource(R.drawable.tiao_bg_single);
			buzzer_time.setVisibility(RelativeLayout.GONE);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.change_buzzer://切换蜂鸣器@@
			showProgress();
			if (buzzer_switch != BUZZER_SET.BUZZER_SWITCH_OFF) {
				cur_modify_buzzer_state = BUZZER_SET.BUZZER_SWITCH_OFF;
			} else {
				cur_modify_buzzer_state = BUZZER_SET.BUZZER_SWITCH_ON_ONE_MINUTE;
			}
			P2PHandler.getInstance().setBuzzer(contact.contactId,
					contact.contactPassword, cur_modify_buzzer_state);
			break;
		case R.id.radio_one:
			showProgress();
			cur_modify_buzzer_state = BUZZER_SET.BUZZER_SWITCH_ON_ONE_MINUTE;
			P2PHandler.getInstance().setBuzzer(contact.contactId,
					contact.contactPassword, cur_modify_buzzer_state);
			break;
		case R.id.radio_two:
			showProgress();
			cur_modify_buzzer_state = BUZZER_SET.BUZZER_SWITCH_ON_TWO_MINUTE;
			P2PHandler.getInstance().setBuzzer(contact.contactId,
					contact.contactPassword, cur_modify_buzzer_state);
			break;
		case R.id.radio_three:
			showProgress();
			cur_modify_buzzer_state = BUZZER_SET.BUZZER_SWITCH_ON_THREE_MINUTE;
			P2PHandler.getInstance().setBuzzer(contact.contactId,
					contact.contactPassword, cur_modify_buzzer_state);
			break;
		default:
			break;
		}
	}
	public void showProgress() {
		progressBar.setVisibility(RelativeLayout.VISIBLE);
		buzzer_img.setVisibility(RelativeLayout.GONE);
		change_buzzer.setEnabled(false);
		radio_one.setEnabled(false);
		radio_two.setEnabled(false);
		radio_three.setEnabled(false);
	}
	public void showBuzzerTime() {
		progressBar.setVisibility(RelativeLayout.GONE);
		buzzer_img.setVisibility(RelativeLayout.VISIBLE);
		change_buzzer.setEnabled(true);
		radio_one.setEnabled(true);
		radio_two.setEnabled(true);
		radio_three.setEnabled(true);
	}
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context arg0, Intent intent) {
			if (intent.getAction().equals(RET_SET_BUZZER)) {
				int result = intent.getIntExtra("result", -1);
				if (result == BUZZER_SET.SETTING_SUCCESS) {
					updateBuzzer(cur_modify_buzzer_state);
					showBuzzerTime();
					Toast.makeText(mContext,R.string.modify_success, Toast.LENGTH_SHORT).show();
				} else {
					showBuzzerTime();
//					Toast.makeText(mContext, "操作失败", Toast.LENGTH_SHORT).show();
				}
			} else if (intent.getAction().equals(RET_GET_BUZZER)) {
				int state = intent.getIntExtra("buzzerState", -1);
				updateBuzzer(state);
				showBuzzerTime();
			} else if (intent.getAction().equals(ACK_RET_SET_BUZZER)) {
				int result = intent.getIntExtra("result", -1);
				if (result == ACK_RESULT.ACK_PWD_ERROR) {
					Intent i = new Intent();
					i.setAction(CONTROL_SETTING_PWD_ERROR);
					mContext.sendBroadcast(i);
				} else if (result == ACK_RESULT.ACK_NET_ERROR) {
					Log.e("my", "net error resend:set npc settings buzzer");
					P2PHandler.getInstance().setBuzzer(contact.contactId,
							contact.contactPassword, cur_modify_buzzer_state);
				}
			} 
		}
	};
	public static class BUZZER_SET{
		public static final int SETTING_SUCCESS = 0;
		public static final int BUZZER_SWITCH_ON_ONE_MINUTE = 1;
		public static final int BUZZER_SWITCH_ON_TWO_MINUTE = 2;
		public static final int BUZZER_SWITCH_ON_THREE_MINUTE = 3;
		public static final int BUZZER_SWITCH_OFF = 0; 
	}
	public static class ACK_RESULT{
		public static final int ACK_PWD_ERROR = 9999;
		public static final int ACK_NET_ERROR = 9998;
		public static final int ACK_SUCCESS = 9997;
		public static final int ACK_INSUFFICIENT_PERMISSIONS=9996;
	}
	//@@返回上一界面
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
