package com.hrsst.smarthome.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.Volley;
import com.hrsst.smarthome.R;
import com.hrsst.smarthome.adapter.TimerListAdapter;
import com.hrsst.smarthome.global.Constants;
import com.hrsst.smarthome.net.SocketUDP;
import com.hrsst.smarthome.order.SendServerOrder;
import com.hrsst.smarthome.order.UnPackServer;
import com.hrsst.smarthome.pojo.DwTimer;
import com.hrsst.smarthome.pojo.UnPackageFromServer;
import com.hrsst.smarthome.util.IntegerTo16;
import com.hrsst.smarthome.volley.JsonArrayPostRequest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class TimerListActivity extends Activity implements OnClickListener {
	private Context mContext;
	private ImageView addTimer;
	private ListView timer_list;
	private TimerListAdapter mTimerListAdapter;
	private List<DwTimer> mDwTimerList;
	private String dwMac;
	private AlertDialog dialog, loadingDialog;
	private SocketUDP mSocketUDPClient;
	private int timerId;
	private Timer mTimer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timer_list);
		dwMac = getIntent().getExtras().getString("dwMac");
		mContext = this;
		init();
		getTimerList(dwMac);
		regFilter();
	}

	private void init() {
		addTimer = (ImageView) findViewById(R.id.add_timer);
		addTimer.setOnClickListener(this);
		timer_list = (ListView) findViewById(R.id.timer_list);
		timer_list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int pos,
					long arg3) {
				// TODO Auto-generated method stub
				if (mDwTimerList.size() > 0) {
					DwTimer mDwTimer = mDwTimerList.get(pos);
					String mac = mDwTimer.getDwMac();
					IntegerTo16 mIntegerTo16 = new IntegerTo16();
					String startTime = mDwTimer.getSocketOnTime();
					String endTime = mDwTimer.getSocketOffTime();
					byte[] orderDatas = longClick(mDwTimer, mIntegerTo16
							.algorismToHEXString(mDwTimer.getEnable()),
							mIntegerTo16);
					Intent mIntent = new Intent(mContext,
							AddTimerActivity.class);
					mIntent.putExtra("orderDatas", orderDatas);
					mIntent.putExtra("startTime", startTime);
					mIntent.putExtra("endTime", endTime);
					mIntent.putExtra("id", (byte) 0x00);
					mIntent.putExtra("mac", mac);
					startActivity(mIntent);
					finish();
				}
			}
		});

		timer_list.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub
				View v = LayoutInflater.from(mContext).inflate(
						R.layout.long_click_dialog, null);
				AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
				dialog = builder.create();
				dialog.show();
				dialog.setContentView(v);
				final DwTimer mDwTimer = mDwTimerList.get(position);
				final String mac = mDwTimer.getDwMac();
				TextView cancle_delete = (TextView) v
						.findViewById(R.id.cancle_delete);
				TextView confire_delete = (TextView) v
						.findViewById(R.id.confire_delete);

				cancle_delete.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				});

				confire_delete.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						timerId = mDwTimer.getSequence();
						IntegerTo16 mIntegerTo16 = new IntegerTo16();
						byte[] datas = longClick(mDwTimer, (byte) 0x00,
								mIntegerTo16);
						byte[] orderSend = SendServerOrder.TimerOrder(mac,
								datas);
						mSocketUDPClient.sendMsg(orderSend);
						dialog.dismiss();
						View vv = LayoutInflater.from(mContext).inflate(
								R.layout.dialog_loading, null);
						AlertDialog.Builder builder = new AlertDialog.Builder(
								mContext);
						loadingDialog = builder.create();
						loadingDialog.show();
						loadingDialog.setContentView(vv);
						mTimer = new Timer();
						setTimerdoAction(doAction, mTimer);
					}
				});
				return true;
			}
		});
		mSocketUDPClient = SocketUDP.newInstance(Constants.SeverInfo.SERVER,
				Constants.SeverInfo.PORT);
		mSocketUDPClient.startAcceptMessage();
	}

	public void regFilter() {
		IntentFilter filter = new IntentFilter();
		filter.addAction("Constants.Action.unServerACKPack");
		filter.addAction("Constants.Action.unTimerOrderPack");
		filter.addAction("REFREASH_ADAPTER");
		mContext.registerReceiver(mReceiver, filter);
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			if (arg1.getAction().equals("Constants.Action.unServerACKPack")) {
				byte[] datas = arg1.getExtras().getByteArray("datasByte");
				new UnPackServer().unServerACKPack(datas);
			}

			if (arg1.getAction().equals("Constants.Action.unTimerOrderPack")) {
				byte[] datas = arg1.getExtras().getByteArray("datasByte");
				UnPackageFromServer mUnPackageFromServer =new UnPackServer().unTimerOrderPack(datas);
				String receiveFlag = mUnPackageFromServer.timerOrder;
				byte[] seq = mUnPackageFromServer.seq;
				if ("true".equals(receiveFlag)) {
					mSocketUDPClient.sendMsg(SendServerOrder.ClientACKOrder(dwMac,seq));
					deleteTimer(dwMac, timerId);
				}
				if ("fail".equals(receiveFlag)) {
					mSocketUDPClient.sendMsg(SendServerOrder.ClientACKOrder(dwMac,seq));
				}
				if (loadingDialog != null && loadingDialog.isShowing()) {
					loadingDialog.dismiss();
				}
				if (mTimer != null) {
					mTimer.cancel();
					mTimer = null;
				}
				count = 0;
			}
			if (arg1.getAction().equals("REFREASH_ADAPTER")) {
				getTimerList(dwMac);
			}
		}
	};

	private byte[] longClick(DwTimer mDwTimer, byte flagByte,
			IntegerTo16 mIntegerTo16) {
		byte[] orderDatas = new byte[18];
		orderDatas[0] = mIntegerTo16
				.algorismToHEXString(mDwTimer.getSequence());
		orderDatas[1] = flagByte;
		orderDatas[2] = mIntegerTo16.algorismToHEXString(mDwTimer.getRepeat());
		orderDatas[3] = mIntegerTo16.algorismToHEXString(mDwTimer.getSat());
		orderDatas[4] = mIntegerTo16.algorismToHEXString(mDwTimer.getFri());
		orderDatas[5] = mIntegerTo16.algorismToHEXString(mDwTimer.getThu());
		orderDatas[6] = mIntegerTo16.algorismToHEXString(mDwTimer.getWed());
		orderDatas[7] = mIntegerTo16.algorismToHEXString(mDwTimer.getTue());
		orderDatas[8] = mIntegerTo16.algorismToHEXString(mDwTimer.getMon());
		orderDatas[9] = mIntegerTo16.algorismToHEXString(mDwTimer.getSun());

		orderDatas[10] = mIntegerTo16.algorismToHEXString(mDwTimer
				.getSocketOnEnable());
		String[] onTime = mDwTimer.getSocketOnTime().split(":");
		orderDatas[11] = mIntegerTo16.algorismToHEXString(Integer
				.parseInt(onTime[0]));
		orderDatas[12] = mIntegerTo16.algorismToHEXString(Integer
				.parseInt(onTime[1]));
		orderDatas[13] = mIntegerTo16.algorismToHEXString(Integer
				.parseInt(onTime[2]));

		orderDatas[14] = mIntegerTo16.algorismToHEXString(mDwTimer
				.getSocketOffEnable());
		String[] offTime = mDwTimer.getSocketOffTime().split(":");
		orderDatas[15] = mIntegerTo16.algorismToHEXString(Integer
				.parseInt(offTime[0]));
		orderDatas[16] = mIntegerTo16.algorismToHEXString(Integer
				.parseInt(offTime[1]));
		orderDatas[17] = mIntegerTo16.algorismToHEXString(Integer
				.parseInt(offTime[2]));
		return orderDatas;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.add_timer:
			Intent intent = new Intent(mContext, AddTimerActivity.class);
			intent.putExtra("id",
					new IntegerTo16().algorismToHEXString(findId(mDwTimerList)));
			intent.putExtra("mac", dwMac);
			intent.putExtra("orderDatas", (byte[]) null);
			intent.putExtra("startTime", "");
			intent.putExtra("endTime", "");
			startActivity(intent);
			finish();
			break;

		default:
			break;
		}
	}

	private int findId(List<DwTimer> mDwTimerList) {
		if (null != mDwTimerList && mDwTimerList.size() > 0) {
			List<Integer> idList = new ArrayList<Integer>();
			for (DwTimer mDwTimer : mDwTimerList) {
				idList.add(mDwTimer.getSequence());
			}
			Collections.sort(idList);
			return idList.get(idList.size() - 1) + 1;
		} else {
			return 1;
		}
	}

	private void getTimerList(String dwMac) {
		RequestQueue mQueue = Volley.newRequestQueue(mContext);
		Map<String, String> map = new HashMap<String, String>();
		map.put("dwMac", dwMac);
		JsonArrayPostRequest mJsonRequest = new JsonArrayPostRequest(
				Constants.GET_TIMER_LIST_URL, new Listener<JSONArray>() {

					@Override
					public void onResponse(JSONArray response) {
						if (response.length() > 0) {
							mDwTimerList = new ArrayList<DwTimer>();
							for (int i = 0; i < response.length(); i++) {
								try {
									DwTimer mDwTimer = new DwTimer();
									JSONObject obj = (JSONObject) response
											.get(i);
									mDwTimer.setDwMac(obj.getString("dwMac"));
									mDwTimer.setEnable(obj.getInt("enable"));
									mDwTimer.setFri(obj.getInt("fri"));
									mDwTimer.setMon(obj.getInt("mon"));
									mDwTimer.setRepeat(obj.getInt("repeat"));
									mDwTimer.setSat(obj.getInt("sat"));
									mDwTimer.setSequence(obj.getInt("sequence"));
									mDwTimer.setSocketOffEnable(obj
											.getInt("socketOffEnable"));
									mDwTimer.setSocketOffTime(obj
											.getString("socketOffTime"));
									mDwTimer.setSocketOnEnable(obj
											.getInt("socketOnEnable"));
									mDwTimer.setSun(obj.getInt("sun"));
									mDwTimer.setThu(obj.getInt("thu"));
									mDwTimer.setTue(obj.getInt("tue"));
									mDwTimer.setWed(obj.getInt("wed"));
									mDwTimer.setSocketOnTime(obj
											.getString("socketOnTime"));
									mDwTimer.setRecordeTime(obj
											.getString("recordeTime"));
									mDwTimerList.add(mDwTimer);
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								mTimerListAdapter = new TimerListAdapter(
										mContext, mDwTimerList);
								timer_list.setAdapter(mTimerListAdapter);
								mTimerListAdapter.notifyDataSetChanged();
							}
						}
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
					}
				}, map);
		mQueue.add(mJsonRequest);
	}

	private void deleteTimer(final String dwMac, int sequence) {
		RequestQueue mQueue = Volley.newRequestQueue(mContext);
		Map<String, String> map = new HashMap<String, String>();
		map.put("dwMac", dwMac);
		map.put("sequence", sequence + "");
		JsonArrayPostRequest mJsonRequest = new JsonArrayPostRequest(
				Constants.DELETE_TIMER_URL, new Listener<JSONArray>() {
					@Override
					public void onResponse(JSONArray response) {
						if (response.length() > 0) {
							getTimerList(dwMac);
						}
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
					}
				}, map);
		mQueue.add(mJsonRequest);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(mReceiver);
		super.onDestroy();
	}

	private int count = 0;

	private void setTimerdoAction(final Handler oj, Timer t) {
		t.schedule(new TimerTask() {
			@Override
			public void run() {
				count = count + 1;
				Message message = new Message();
				if (count > 8) {
					message = oj.obtainMessage();
					message.what = 1;
					oj.sendMessage(message);
				}
			}
		}, 1000, 1000/* 表示1000毫秒之後，每隔1000毫秒執行一次 */);
	}

	private Handler doAction = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			int messsageId = msg.what;
			switch (messsageId) {
			case 1:
				if (dialog != null && dialog.isShowing()) {
					dialog.dismiss();
				}
				mTimer.cancel();
				count = 0;
				break;
			default:
				break;
			}
		}
	};
}
