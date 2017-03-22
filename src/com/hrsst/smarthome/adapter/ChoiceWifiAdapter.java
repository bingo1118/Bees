package com.hrsst.smarthome.adapter;

import java.util.List;

import com.hrsst.smarthome.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ChoiceWifiAdapter extends BaseAdapter{
	private Context mContext;
	private List<String> mWifiList;
	private ViewHolder holder;

	public ChoiceWifiAdapter(List<String> mWifiList) {
		super();
		this.mWifiList = mWifiList;
	}

	public ChoiceWifiAdapter(Context mContext, List<String> mWifiList) {
		super();
		this.mContext = mContext;
		this.mWifiList = mWifiList;
	}


	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mWifiList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mWifiList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.choice_dev_item, null);
			holder = new ViewHolder();
			holder.tv = (TextView) convertView.findViewById(R.id.choice_tv);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		String wifiStr= mWifiList.get(position);
		holder.tv.setText(wifiStr);
		return convertView;
	}

	static class ViewHolder {
		public TextView tv;
	}
}
