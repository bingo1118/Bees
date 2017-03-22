package com.hrsst.smarthome.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.Volley;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.baoyz.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
import com.hrsst.smarthome.R;
import com.hrsst.smarthome.adapter.SystemMsgAdapter;
import com.hrsst.smarthome.global.Constants;
import com.hrsst.smarthome.net.SocketUDP;
import com.hrsst.smarthome.order.SendServerOrder;
import com.hrsst.smarthome.order.UnPackServer;
import com.hrsst.smarthome.pojo.ShareMessages;
import com.hrsst.smarthome.util.SharedPreferencesManager;
import com.hrsst.smarthome.volley.JsonArrayPostRequest;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.TypedValue;

public class SystemMessageActivity extends Activity{
	private Context mContext;
	private SwipeMenuListView system_msg_list;
	private String toUserNum;
	private List<ShareMessages> li;
	private SystemMsgAdapter sysAdapter;
	private SocketUDP mSocketUDPClient;
	private int messageId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_system_message);
		mContext = this;
		toUserNum = getIntent().getExtras().getString("toUserNum");
		init();
		regFilter();
	}
	private void init() {
		// TODO Auto-generated method stub
		system_msg_list = (SwipeMenuListView) findViewById(R.id.system_msg_list);
		getShareMsgs(-1,toUserNum);
		mSocketUDPClient = SocketUDP.newInstance(Constants.SeverInfo.SERVER
				, Constants.SeverInfo.PORT);
		mSocketUDPClient.startAcceptMessage();
		swipe();
	}
	
	private void swipe(){
		// step 1. create a MenuCreator
		SwipeMenuCreator creator = new SwipeMenuCreator() {

			@Override
			public void create(SwipeMenu menu) {
				// create "delete" item
				SwipeMenuItem deleteItem = new SwipeMenuItem(
						getApplicationContext());
				// set item background
				deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
						0x3F, 0x25)));
				// set item width
				deleteItem.setWidth(dp2px(90));
				// set a icon
				deleteItem.setIcon(R.drawable.ic_delete);
				// add to menu
				menu.addMenuItem(deleteItem);
			}
		};
		// set creator
		system_msg_list.setMenuCreator(creator);
		// step 2. listener item click event
		system_msg_list.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public void onMenuItemClick(int position, SwipeMenu menu, int index) {
				ShareMessages item = li.get(position);
				switch (index) {
				case 0:
					// delete
					getShareMsgs(item.getId(),toUserNum);
					break;
				}
			}
		});
	}
	
	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getResources().getDisplayMetrics());
	}
	
	public void regFilter() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.Action.DELETE_OR_FIND_SYSTEM_MSG);
		filter.addAction(Constants.Action.AGREE_SYSTEM_MSG);
		filter.addAction("Constants.Action.unServerACKPack");
		filter.addAction("Constants.Action.unIfRegisterInyoo");
		mContext.registerReceiver(mReceiver, filter);
	}
	
	private BroadcastReceiver mReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			String userNum = SharedPreferencesManager.getInstance().getData(mContext,  Constants.UserInfo.USER_NUMBER);
			if(arg1.getAction().equals("Constants.Action.unIfRegisterInyoo")){
				byte[] datas = arg1.getExtras().getByteArray("datasByte");
				if(datas!=null&&datas.length>0){
					String result = UnPackServer.unIfRegisterInyoo(datas);
					if(result.equals("no")){
						Intent i =new Intent(mContext,RegisterYooActivity.class);
						i.putExtra("userId", userNum);
						i.putExtra("message", "yes");
						startActivity(i);
					}else{
						mSocketUDPClient.sendMsg(SendServerOrder.AgreeShare(messageId+""));
					}
				}
			}
			if(arg1.getAction().equals(Constants.Action.DELETE_OR_FIND_SYSTEM_MSG)){
				int id = arg1.getExtras().getInt("id");
				getShareMsgs(id,toUserNum);
			}
			if(arg1.getAction().equals(Constants.Action.AGREE_SYSTEM_MSG)){
				messageId = arg1.getExtras().getInt("id");
				String devMac = arg1.getExtras().getString("mac");
				if(null!=devMac){
					if(devMac.length()==12){
						mSocketUDPClient.sendMsg(SendServerOrder.AgreeShare(messageId+""));
					}else{
						byte[] orderSend =SendServerOrder.ifRegisterInyoo(userNum);
						mSocketUDPClient.sendMsg(orderSend);
					}
				}
			}
			if(arg1.getAction().equals("Constants.Action.unServerACKPack")){
				getShareMsgs(-1,toUserNum);
			}
		}
	};
	
	private void getShareMsgs(int id,String toUserNum){
		RequestQueue mQueue = Volley.newRequestQueue(mContext);
		Map<String,String> map = new HashMap<String,String>();
		map.put("id", id+"");
		map.put("toUserNum", toUserNum);
		JsonArrayPostRequest mJsonRequest = new JsonArrayPostRequest(
				Constants.GET_SYSTEM_MSG_URL,
				new Listener<JSONArray>() {
					@Override
					public void onResponse(JSONArray response) {
						li = new ArrayList<ShareMessages>();
						if(response.length()>0){
							for(int i=0;i<response.length();i++){
								try {
									JSONObject obj = (JSONObject) response.get(i);
									ShareMessages mShareMessages = new ShareMessages();
									mShareMessages.setDwMac(obj.getString("dwMac"));
									mShareMessages.setFromUserNum(obj.getString("fromUserNum"));
									mShareMessages.setId(obj.getInt("id"));
									mShareMessages.setIsRead(obj.getInt("isRead"));
									mShareMessages.setTime(obj.getString("time"));
									mShareMessages.setToUserNum(obj.getString("toUserNum"));
									li.add(mShareMessages);
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
						sysAdapter = new SystemMsgAdapter(mContext, li);
						system_msg_list.setAdapter(sysAdapter);
						sysAdapter.notifyDataSetChanged();//189 31 26
					}
				}, 
				new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
					}
				}, 
				map);
		mQueue.add(mJsonRequest);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(mReceiver);
		super.onDestroy();
		
	}
}
