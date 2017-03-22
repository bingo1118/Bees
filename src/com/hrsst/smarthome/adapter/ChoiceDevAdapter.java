package com.hrsst.smarthome.adapter;

import java.util.List;

import com.hrsst.smarthome.R;
import com.hrsst.smarthome.pojo.UserDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ChoiceDevAdapter extends BaseAdapter{
	private Context mContext;
	private List<UserDevice> mUserDeviceList;
	private ViewHolder holder;

	public ChoiceDevAdapter(List<UserDevice> mUserDeviceList) {
		super();
		this.mUserDeviceList = mUserDeviceList;
	}

	public ChoiceDevAdapter(Context mContext, List<UserDevice> mUserDeviceList) {
		super();
		this.mContext = mContext;
		this.mUserDeviceList = mUserDeviceList;
	}


	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mUserDeviceList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mUserDeviceList.get(position);
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
		UserDevice mUserDevice= mUserDeviceList.get(position);
		holder.tv.setText(mUserDevice.getDevName());
		return convertView;
	}

	static class ViewHolder {
		public TextView tv;
	}
}
