package com.hrsst.smarthome.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.hrsst.smarthome.R;
import com.hrsst.smarthome.pojo.UserDevice;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

public class CameraListAdapter extends BaseAdapter{
	Context context;
	public  List<UserDevice> list=new ArrayList<UserDevice>();
	
	HashMap<String, Boolean> states = new HashMap<String, Boolean>();
	
	public CameraListAdapter() {
		super();
	}
	
	public CameraListAdapter(Context context, List<UserDevice> list) {
		super();
		this.context = context;
		this.list = list;
	}

	class ViewHolder{
		public RadioButton mRadioButton;
		public TextView mTextView;
		
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final ViewHolder holder;
		if(null==convertView){
			convertView = LayoutInflater.from(context).inflate(R.layout.list_alarm_type_item, null);
			holder = new ViewHolder();
			holder.mRadioButton=(RadioButton)convertView.findViewById(R.id.alarm_type_radio);
			holder.mTextView=(TextView)convertView.findViewById(R.id.alarm_type_tv);
			convertView.setTag(holder);
			
		}else{
			holder = (ViewHolder)convertView.getTag();  
		}
		final UserDevice mUserDevice = list.get(position);
		String str = mUserDevice.getDevName();
		holder.mTextView.setText(str);
		holder.mRadioButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				 for (String key : states.keySet()) {
			          states.put(key, false);
			     }
		        states.put(String.valueOf(position), holder.mRadioButton.isChecked());
		        CameraListAdapter.this.notifyDataSetChanged();
		        String devMac = mUserDevice.getDevMac();
		        Intent intent = new Intent();  
		        intent.setAction("GET_CAMERA_MAC_ACTION");
		        intent.putExtra("devMac", devMac);
		        context.sendBroadcast(intent);
			}
		});
		
		boolean res = false;
	    if (states.get(String.valueOf(position)) == null
	        || states.get(String.valueOf(position)) == false) {
	      res = false;
	      states.put(String.valueOf(position), false);
	    } else{
	      res = true;
	    }
	    holder.mRadioButton.setChecked(res);
		return convertView;
	}

}

