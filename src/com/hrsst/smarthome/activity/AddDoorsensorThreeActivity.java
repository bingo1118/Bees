package com.hrsst.smarthome.activity;

import java.util.Timer;
import java.util.TimerTask;

import com.hrsst.smarthome.R;
import com.hrsst.smarthome.dialog.ConnectionDoorSenSorDialog;
import com.hrsst.smarthome.global.Constants;
import com.hrsst.smarthome.net.SocketUDP;
import com.hrsst.smarthome.order.SendServerOrder;
import com.hrsst.smarthome.order.UnPackServer;
import com.hrsst.smarthome.pojo.UnPackageFromServer;
import com.hrsst.smarthome.util.BitmapCache;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class AddDoorsensorThreeActivity extends Activity {

	private Context mContext;
	private Button add_doorsensor_action_three;
	private String doorsensor;
	private String doorsensor_locationStr;
	private ConnectionDoorSenSorDialog cdialog;
	private SocketUDP mSocketUDP;
	private Timer mTimer;
	private ImageView step_three_image;
	private int type;
	private byte alarmType;
	private TextView add_dev_tip;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_doorsensor_three);
		doorsensor = getIntent().getExtras().getString("doorsensor");
		doorsensor_locationStr = getIntent().getExtras().getString("location");
		type = getIntent().getExtras().getInt("type");
		mContext = this;
		init();
		regFilter();
	}

	@SuppressLint("NewApi")
	private void init() {
		// TODO Auto-generated method stub
		add_dev_tip=(TextView)findViewById(R.id.add_dev_tip);
		step_three_image = (ImageView) findViewById(R.id.step_three_image);
		switch (type) {
		case 2:
			Bitmap mBitmap = BitmapCache.getInstance().getBitmap(R.drawable.mc_3,mContext);
			BitmapDrawable bd = new BitmapDrawable(mContext.getResources(), mBitmap);
			step_three_image.setBackground(bd);
			alarmType =0x02;
			cdialog = new ConnectionDoorSenSorDialog(mContext,R.anim.connect_doorsensor_anim);
			break;
		case 4:
			Bitmap mBitmaphw_3 = BitmapCache.getInstance().getBitmap(R.drawable.hw_3,mContext);
			BitmapDrawable hw_3 = new BitmapDrawable(mContext.getResources(), mBitmaphw_3);
			step_three_image.setBackground(hw_3);
			alarmType =0x03;
			cdialog = new ConnectionDoorSenSorDialog(mContext,R.anim.connect_hw_anim);
			break;
		case 5:
			Bitmap mBitmaprq_3 = BitmapCache.getInstance().getBitmap(R.drawable.rq_3,mContext);
			BitmapDrawable rq_3 = new BitmapDrawable(mContext.getResources(), mBitmaprq_3);
			step_three_image.setBackground(rq_3);
			alarmType =0x04;
			cdialog = new ConnectionDoorSenSorDialog(mContext,R.anim.connect_rq_anim);
			break;
		case 6:
			add_dev_tip.setText(R.string.add_three_sj);
			Bitmap mBitmapsj_3 = BitmapCache.getInstance().getBitmap(R.drawable.sj_lct_3,mContext);
			BitmapDrawable sj_3 = new BitmapDrawable(mContext.getResources(), mBitmapsj_3);
			step_three_image.setBackground(sj_3);
			alarmType =0x05;
			cdialog = new ConnectionDoorSenSorDialog(mContext,R.anim.connect_doorsensor_anim);
			break;
		case 7:
			add_dev_tip.setText(R.string.add_three_ykq);
			Bitmap mBitmapykq_3 = BitmapCache.getInstance().getBitmap(R.drawable.ykq_lct_2,mContext);
			BitmapDrawable ykq_3 = new BitmapDrawable(mContext.getResources(), mBitmapykq_3);
			step_three_image.setBackground(ykq_3);
			alarmType =0x06;
			cdialog = new ConnectionDoorSenSorDialog(mContext,R.anim.connect_ykq_anim);
			break;
		default:
			break;
		}
		add_doorsensor_action_three = (Button) findViewById(R.id.add_doorsensor_action_three);
		add_doorsensor_action_three.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(cdialog!=null){
					cdialog.show();
					cdialog.startConnect();
					cdialog.setCancelable(false);
					
					byte[] locationByte = SendServerOrder
							.GetLocation(doorsensor_locationStr);
					byte[] orderSend = SendServerOrder.StudyOrder(doorsensor,
							locationByte, alarmType);
					mSocketUDP.sendMsg(orderSend);
					mTimer = new Timer();
					setTimerdoAction1(doAction1, mTimer);
				}
			}
		});
		mSocketUDP = SocketUDP.newInstance(Constants.SeverInfo.SERVER,
				Constants.SeverInfo.PORT);
		mSocketUDP.startAcceptMessage();
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (intent.getAction().equals("Constants.Action.unServerACKPack")) {
				byte[] datas = intent.getExtras().getByteArray("datasByte");
				new UnPackServer().unServerACKPack(datas);
			}
			if (intent.getAction().equals("Constants.Action.unStudyOrderPack")) {
				byte[] datas = intent.getExtras().getByteArray("datasByte");
				UnPackageFromServer	mUnPackageFromServer = new UnPackServer().unStudyOrderPack(datas);
				String studyResult = mUnPackageFromServer.order;
				byte[] seq = mUnPackageFromServer.seq;//@@
				if (studyResult.equals("fail")) {
					Toast.makeText(context,R.string.configuration_failed, Toast.LENGTH_SHORT).show();
				} else if (studyResult.equals("repetition")) {
					Toast.makeText(context, R.string.device_have_configuration, Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(context, R.string.configuration_success, Toast.LENGTH_SHORT).show();
				}
				mSocketUDP.sendMsg(SendServerOrder.ClientACKOrder(doorsensor,seq));//回复ACK@@
				if (cdialog.isShowing()) {
					cdialog.dismiss();
				}
				mTimer.cancel();
				count = 0;
//				finish();
				startActivity(new Intent(AddDoorsensorThreeActivity.this,MainActivity.class));//@@
			}
		}

	};

	public void regFilter() {
		IntentFilter filter = new IntentFilter();
		filter.addAction("Constants.Action.unServerACKPack");
		filter.addAction("Constants.Action.unStudyOrderPack");
		mContext.registerReceiver(mReceiver, filter);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(mReceiver);
		BitmapCache.getInstance().clearCache();
		super.onDestroy();
		
	}

	private int count = 0;

	private void setTimerdoAction1(final Handler oj, Timer t) {
		t.schedule(new TimerTask() {
			@Override
			public void run() {
				Message message = new Message();
				// 判断wifi硬件是否配置成功
				count = count + 1;
				if (count > 30) {// 30s结束
					message = oj.obtainMessage();
					message.what = 1;
					oj.sendMessage(message);
				}
			}
		}, 1000, 1000/* 表示1000毫秒之後，每隔1000毫秒執行一次 */);
	}

	private Handler doAction1 = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			int messsageId = msg.what;
			switch (messsageId) {
			case 1:
				mTimer.cancel();
				count = 0;
				if (cdialog.isShowing()) {
					cdialog.dismiss();
					Toast.makeText(getApplicationContext(), R.string.configuration_outtime, Toast.LENGTH_SHORT).show();//@@
				}
//				finish();
//				startActivity(new Intent(AddDoorsensorThreeActivity.this,MainActivity.class));//@@
				break;
			default:
				break;
			}
		}
	};
}
