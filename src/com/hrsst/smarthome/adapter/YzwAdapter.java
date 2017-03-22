package com.hrsst.smarthome.adapter;

import java.util.HashMap;

import com.hrsst.smarthome.R;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;


public class YzwAdapter extends BaseAdapter{
	Context context;
	String[] str;
	HashMap<String, Boolean> states = new HashMap<String, Boolean>();
	
	public YzwAdapter() {
		super();
	}
	
	public YzwAdapter(Context context, String[] str) {
		super();
		this.context = context;
		this.str = str;
	}

	class ViewHolder{
		public RadioButton mRadioButton;
		public TextView mTextView;
		
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return str.length;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return str[arg0];
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
		holder.mTextView.setText(str[position]);
		holder.mRadioButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				 for (String key : states.keySet()) {
			          states.put(key, false);
			     }
		        states.put(String.valueOf(position), holder.mRadioButton.isChecked());
		        YzwAdapter.this.notifyDataSetChanged();
		        String yuzhiweiNum = holder.mTextView.getText().toString().trim();
		        Intent intent = new Intent();  
		        intent.setAction("YZWCODE_ACTION");
		        intent.putExtra("yuzhiweiNum", yuzhiweiNum); 
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

