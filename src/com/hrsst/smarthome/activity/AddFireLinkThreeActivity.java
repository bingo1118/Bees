package com.hrsst.smarthome.activity;

import java.util.Timer;
import java.util.TimerTask;

import com.hrsst.smarthome.R;
import com.hrsst.smarthome.dialog.ConnectionFKDialog;
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
import android.widget.Toast;

public class AddFireLinkThreeActivity extends Activity {
	private Context mContext;
	private Button add_fk_action_three;
	private String device;
	private String fk_locationStr;
	private ConnectionFKDialog cdialog;
	private SocketUDP mSocketUDP;
	private Timer mTimer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_firelink_three);
		device = getIntent().getExtras().getString("device");
		fk_locationStr = getIntent().getExtras().getString("location");
		mContext = this;
		init();
		regFilter();
	}

	@SuppressLint("NewApi")
	private void init() {
		// TODO Auto-generated method stub
		ImageView yg_3_image = (ImageView) findViewById(R.id.yg_3_image);
		Bitmap mBitmap = BitmapCache.getInstance().getBitmap(R.drawable.yg_3,mContext);
		BitmapDrawable bd = new BitmapDrawable(mContext.getResources(), mBitmap);
		yg_3_image.setBackground(bd);
		add_fk_action_three = (Button) findViewById(R.id.add_fk_action_three);
		add_fk_action_three.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				cdialog = new ConnectionFKDialog(mContext);//连接提示框。。
				cdialog.show();
				cdialog.startConnect();
				cdialog.setCancelable(false);
				byte type = 0x01;
				byte[] locationByte = SendServerOrder
						.GetLocation(fk_locationStr);
				byte[] orderSend = SendServerOrder.StudyOrder(device,
						locationByte, type);
				mSocketUDP.sendMsg(orderSend);
				mTimer = new Timer();
				setTimerdoAction1(doAction1, mTimer);
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
				UnPackageFromServer mUnPackageFromServer = new UnPackServer().unStudyOrderPack(datas);
				String studyResult = mUnPackageFromServer.order;
				byte[] seq = mUnPackageFromServer.seq;//@@
				if (studyResult.equals("fail")) {
					Toast.makeText(context, R.string.configuration_failed, Toast.LENGTH_SHORT).show();
				} else if (studyResult.equals("false")) {
					Toast.makeText(context, R.string.crc_configurantion_fail, Toast.LENGTH_SHORT).show();
				} else if (studyResult.equals("repetition")) {
					Toast.makeText(context, R.string.this_device_have_configurantion, Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(context, R.string.configuration_success, Toast.LENGTH_SHORT).show();
				}
				mSocketUDP.sendMsg(SendServerOrder.ClientACKOrder(device,seq));//手机回复包@@
				if (cdialog.isShowing()) {
					cdialog.dismiss();
				}
				mTimer.cancel();
				count = 0;
//				finish();
				startActivity(new Intent(AddFireLinkThreeActivity.this,MainActivity.class));//@@
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
				if (count > 35) {// 30s结束
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
				break;
			default:
				break;
			}
		}
	};
}
