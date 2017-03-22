package com.hrsst.smarthome.adapter;

import java.util.List;

import com.hrsst.smarthome.R;
import com.hrsst.smarthome.global.Constants;
import com.hrsst.smarthome.pojo.ShareMessages;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class SystemMsgAdapter extends BaseAdapter{
	private Context mContext;
	private List<ShareMessages> mShareMessagesList;
	private ViewHolder holder;

	public SystemMsgAdapter(Context mContext,
			List<ShareMessages> mShareMessagesList) {
		super();
		this.mContext = mContext;
		this.mShareMessagesList = mShareMessagesList;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mShareMessagesList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mShareMessagesList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.system_list_adapter, null);
			holder = new ViewHolder();
			holder.msg_content = (TextView) convertView.findViewById(R.id.msg_content);
			holder.system_time_tv = (TextView) convertView.findViewById(R.id.system_time_tv);
			holder.msg_agree = (Button) convertView.findViewById(R.id.msg_agree);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final ShareMessages mShareMessages = mShareMessagesList.get(position);
		holder.msg_content.setText(mShareMessages.getFromUserNum());
		holder.system_time_tv.setText(mShareMessages.getTime());
		int isRead = mShareMessages.getIsRead();
		if(isRead==0){
			holder.msg_agree.setEnabled(true);
		}else{
			holder.msg_agree.setEnabled(false);
			holder.msg_agree.setBackgroundResource(R.drawable.agreed);
		}
		holder.msg_agree.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				int id = mShareMessages.getId();
				String devMac = mShareMessages.getDwMac();
				Intent i = new Intent();
				i.putExtra("id", id);
				i.putExtra("mac", devMac);
				i.setAction(Constants.Action.AGREE_SYSTEM_MSG);
				mContext.sendBroadcast(i);
			}
		});
		return convertView;
	}

	static class ViewHolder {
		public TextView msg_content;
		public TextView system_time_tv;
		public Button msg_agree;
	}
}
