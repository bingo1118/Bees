package com.hrsst.smarthome.adapter;

import java.util.List;

import com.hrsst.smarthome.R;
import com.hrsst.smarthome.pojo.Messages;
import com.hrsst.smarthome.util.BitmapCache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MessageAdapter extends BaseAdapter{
	private Context mContext;
	private List<Messages> messageList;
	private ViewHolder holder;
	
	public MessageAdapter(Context mContext, List<Messages> messageList) {
		super();
		this.mContext = mContext;
		this.messageList = messageList;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return messageList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return messageList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int arg0, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.activity_message_adapter, null);
			holder = new ViewHolder();
			holder.message_adapter_image = (ImageView) convertView.findViewById(R.id.message_adapter_image);
			holder.location_tv = (TextView) convertView.findViewById(R.id.location_tv);
			holder.alarm_dev_name = (TextView) convertView.findViewById(R.id.alarm_dev_name);
			holder.host_name = (TextView) convertView.findViewById(R.id.host_name);
			holder.alarm_time_tv = (TextView) convertView.findViewById(R.id.alarm_time_tv);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Messages mMessages = messageList.get(arg0);
		int type = mMessages.getDevType();
		switch (type) {
		case 1:
			Bitmap mBitmap = BitmapCache.getInstance().getBitmap(R.drawable.firelink_alarm,mContext);
			BitmapDrawable bd = new BitmapDrawable(mContext.getResources(), mBitmap);
			holder.message_adapter_image.setImageDrawable(bd);
			holder.alarm_dev_name.setText(R.string.devicelistadapter_smoke_detection_alarm);
			break;
		case 2:
			Bitmap mBitmap2 = BitmapCache.getInstance().getBitmap(R.drawable.doorsensor_alarm,mContext);
			BitmapDrawable bd2 = new BitmapDrawable(mContext.getResources(), mBitmap2);
			holder.message_adapter_image.setImageDrawable(bd2);
			holder.alarm_dev_name.setText(R.string.devicelistadapter_menci);
			break;
		case 3:
			Bitmap mBitmap3 = BitmapCache.getInstance().getBitmap(R.drawable.hw_logo_m,mContext);
			BitmapDrawable bd3 = new BitmapDrawable(mContext.getResources(), mBitmap3);
			holder.message_adapter_image.setImageDrawable(bd3);
			holder.alarm_dev_name.setText(R.string.devicelistadapter_hongwai);
			break;
		case 4:
			Bitmap mBitmap4 = BitmapCache.getInstance().getBitmap(R.drawable.rq_logo_m,mContext);
			BitmapDrawable bd4 = new BitmapDrawable(mContext.getResources(), mBitmap4);
			holder.message_adapter_image.setImageDrawable(bd4);
			holder.alarm_dev_name.setText(R.string.devicelistadapter_ranqi);
			break;
		case 5:
			Bitmap mBitmap5 = BitmapCache.getInstance().getBitmap(R.drawable.bjxx_tb_sj,mContext);
			BitmapDrawable bd5 = new BitmapDrawable(mContext.getResources(), mBitmap5);
			holder.message_adapter_image.setImageDrawable(bd5);
			holder.alarm_dev_name.setText(R.string.shuijin);
			break;//@@
		case 6:
			Bitmap mBitmap6 = BitmapCache.getInstance().getBitmap(R.drawable.bjxx_tb_ykq,mContext);
			BitmapDrawable bd6 = new BitmapDrawable(mContext.getResources(), mBitmap6);
			holder.message_adapter_image.setImageDrawable(bd6);
			holder.alarm_dev_name.setText(R.string.ykq);
			break;//@@
		default:
			break;
		}
		
		holder.location_tv.setText(mMessages.getLocation());
		holder.host_name.setText(mMessages.getDevName());
		holder.alarm_time_tv.setText(mMessages.getAlarmTime());
		return convertView;
	}

	static class ViewHolder {
		public ImageView message_adapter_image;
		public TextView location_tv;
		public TextView alarm_dev_name;
		public TextView host_name;
		public TextView alarm_time_tv;
	}
	
}
