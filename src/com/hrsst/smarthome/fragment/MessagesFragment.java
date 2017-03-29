package com.hrsst.smarthome.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.Volley;
import com.hrsst.smarthome.R;
import com.hrsst.smarthome.adapter.MessageAdapter;
import com.hrsst.smarthome.global.Constants;
import com.hrsst.smarthome.net.SocketUDP;
import com.hrsst.smarthome.order.SendServerOrder;
import com.hrsst.smarthome.order.UnPackServer;
import com.hrsst.smarthome.pojo.Messages;
import com.hrsst.smarthome.pojo.UnPackageFromServer;
import com.hrsst.smarthome.util.SharedPreferencesManager;
import com.hrsst.smarthome.volley.JsonArrayPostRequest;
import com.lib.pullToRefresh.PullToRefreshBase;
import com.lib.pullToRefresh.PullToRefreshBase.Mode;
import com.lib.pullToRefresh.PullToRefreshBase.OnPullEventListener;
import com.lib.pullToRefresh.PullToRefreshBase.State;
import com.lib.pullToRefresh.PullToRefreshListView;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.TextView;

public class MessagesFragment extends Fragment implements OnClickListener{
	private TextView menu_me;
	private View view;
	private Context mContext;
	private PullToRefreshListView pullToRefreshListView;
	private MessageAdapter mMessageAdapter;
	private List<Messages> li;
	private AlertDialog dialog;
	private TextView clear_alarm_message;
	private SocketUDP mSocketUDP;
	private String userNumStr;
	private ProgressBar progressBar;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.activity_messages_fragment, null);
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		mContext = getActivity();
		init();
		regFilter();
		mSocketUDP = SocketUDP.newInstance(Constants.SeverInfo.SERVER,
				Constants.SeverInfo.PORT);
		mSocketUDP.startAcceptMessage();
	}
	
	public void regFilter() {
		IntentFilter filter = new IntentFilter();
		filter.addAction("Constants.Action.unClearAlarmMessage");
		mContext.registerReceiver(mReceiver, filter);
	}
	
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			if(arg1.getAction().equals("Constants.Action.unClearAlarmMessage")){
				byte[] datas = arg1.getExtras().getByteArray("datasByte");
				if(null!=datas&&datas.length>0){
					UnPackageFromServer mUnPackageFromServer = new UnPackServer().unClearAlarmMessage(datas);
					String result = mUnPackageFromServer.clearAlarmMsg;
					if(result.equals("failed")){
						Toast.makeText(mContext, "清空失败", Toast.LENGTH_SHORT).show();
					}else if(result.equals("success")){
						getMessages(userNumStr,-1);
						Toast.makeText(mContext, "清空成功", Toast.LENGTH_SHORT).show();
					}else{
						Toast.makeText(mContext, "CRC错误", Toast.LENGTH_SHORT).show();
					}
				}
			}
		}
	};

	private void init() {
		// TODO Auto-generated method stub
		progressBar=(ProgressBar)view.findViewById(R.id.progressBar);
		clear_alarm_message = (TextView) view.findViewById(R.id.clear_alarm_message);
		clear_alarm_message.setOnClickListener(this);
		menu_me = (TextView) view.findViewById(R.id.menu_me);
		menu_me.setOnClickListener(this);
		pullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.pullToRefreshListView);
		userNumStr = SharedPreferencesManager.getInstance().getData(mContext, Constants.UserInfo.USER_NUMBER);
		getMessages(userNumStr,-1);
		pullToRefreshListView.setOnPullEventListener(new OnPullEventListener<ListView>() {

			@Override
			public void onPullEvent(PullToRefreshBase<ListView> refreshView,
					State state, Mode direction) {
					getMessages(userNumStr,-1);
				
			}
		});//下拉刷新@@
		//长按监听。。
		pullToRefreshListView.getRefreshableView().setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				final Messages m = (Messages) arg0.getAdapter().getItem(arg2);
				View v = LayoutInflater.from(mContext).inflate(
						R.layout.long_click_dialog, null);
				AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
				dialog = builder.create();
				dialog.show();
				dialog.setContentView(v);
				TextView cancle_delete = (TextView) v.findViewById(R.id.cancle_delete);
				TextView confire_delete = (TextView) v.findViewById(R.id.confire_delete);
				
				cancle_delete.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				});
				confire_delete.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						dialog.dismiss();
						getMessages(userNumStr,m.getId());
					}
				});
				return true;
			}
		});
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.menu_me:
			Intent i = new Intent();
			i.setAction(Constants.Action.OPEN_SLIDE_MENU);
			mContext.sendBroadcast(i);
			break;
		case R.id.clear_alarm_message:
			if(null!=li&&li.size()>0){
				View v = LayoutInflater.from(mContext).inflate(
						R.layout.long_click_dialog, null);
				AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
				dialog = builder.create();
				dialog.show();
				dialog.setContentView(v);
				TextView dialog_info = (TextView) v.findViewById(R.id.dialog_info);
				TextView cancle_delete = (TextView) v.findViewById(R.id.cancle_delete);
				TextView confire_delete = (TextView) v.findViewById(R.id.confire_delete);
				dialog_info.setText("是否清空所有数据");
				confire_delete.setText("清空");
				cancle_delete.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				});
				confire_delete.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						dialog.dismiss();
						byte[] orderSend = SendServerOrder.ClearAlarmMessage(userNumStr);
						mSocketUDP.sendMsg(orderSend);
					}
				});
			}
			break;
		default:
			break;
		}
	}
	
	private void getMessages(String userNum,int id){
		progressBar.setVisibility(View.VISIBLE);
		RequestQueue mQueue = Volley.newRequestQueue(mContext);
		Map<String,String> map = new HashMap<String,String>();
		map.put("id", id+"");
		map.put("userNum", userNum);
		JsonArrayPostRequest mJsonRequest = new JsonArrayPostRequest(
				Constants.FIND_OR_DELETE_MESSAGES_URL, 
				new Listener<JSONArray>() {
					@Override
					public void onResponse(JSONArray response) {
						li = new ArrayList<Messages>();
						if(response.length()>0){
							for(int i=0;i<response.length();i++){
								try {
									JSONObject obj = (JSONObject) response.get(i);
									Messages mMessages = new Messages();
									mMessages.setAlarmTime(obj.getString("time433"));
									mMessages.setDevMac(obj.getString("dwMac"));
									mMessages.setAlarmDev(obj.getString("mac433"));
									mMessages.setDevName(obj.getString("dwName"));
									mMessages.setDevType(obj.getInt("type433"));
									mMessages.setLocation(obj.getString("position"));
									mMessages.setUserNum(obj.getString("userNum"));
									mMessages.setId(obj.getInt("id"));
									li.add(mMessages);
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
						mMessageAdapter = new MessageAdapter(mContext,li);
						pullToRefreshListView.setAdapter(mMessageAdapter);
						pullToRefreshListView.onRefreshComplete();
						progressBar.setVisibility(View.GONE);
					}
				}, 
				new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						progressBar.setVisibility(View.GONE);
						Toast.makeText(mContext, "获取数据失败", Toast.LENGTH_SHORT).show();
					}
				}, 
				map);
		mQueue.add(mJsonRequest);
		
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		mContext.unregisterReceiver(mReceiver);
		super.onDestroy();
		
	}
}
