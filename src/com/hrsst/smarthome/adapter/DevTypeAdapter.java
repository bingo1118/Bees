package com.hrsst.smarthome.adapter;

import java.util.List;

import com.hrsst.smarthome.R;
import com.hrsst.smarthome.pojo.AlarmType;
import com.hrsst.smarthome.util.BitmapCache;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DevTypeAdapter extends BaseAdapter{
	private List<AlarmType> listAlarmType;
	private Context mContext;
	private ViewHolder holder;
	private int type;
	
	public DevTypeAdapter(List<AlarmType> listAlarmType, Context mContext,int type) {
		super();
		this.listAlarmType = listAlarmType;
		this.mContext = mContext;
		this.type = type;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listAlarmType.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return listAlarmType.get(arg0);
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
			convertView = LayoutInflater.from(mContext).inflate(R.layout.activity_dev_type_adapter, null);
			holder = new ViewHolder();
			holder.dev_type_iamge_logo = (ImageView) convertView.findViewById(R.id.dev_type_iamge_logo);
			holder.dev_type_location = (TextView) convertView.findViewById(R.id.dev_type_location);
			holder.dev_type_name = (TextView) convertView.findViewById(R.id.dev_type_name);
			holder.dev_type_record_time = (TextView) convertView.findViewById(R.id.dev_type_record_time);
			holder.modify_alarm_image = (ImageView) convertView.findViewById(R.id.modify_alarm_image);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		switch (type) {
		case 1:
			Bitmap mBitmap = BitmapCache.getInstance().getBitmap(R.drawable.sclb_yg,mContext);
			BitmapDrawable bd = new BitmapDrawable(mContext.getResources(), mBitmap);
			holder.dev_type_iamge_logo.setImageDrawable(bd);
			holder.dev_type_name.setText(R.string.devicelistadapter_smoke_detection_alarm);
			break;
		case 2:
			Bitmap mBitmap2 = BitmapCache.getInstance().getBitmap(R.drawable.sclb_mc,mContext);
			BitmapDrawable bd2 = new BitmapDrawable(mContext.getResources(), mBitmap2);
			holder.dev_type_iamge_logo.setImageDrawable(bd2);
			holder.dev_type_name.setText(R.string.devicelistadapter_menci);
			break;
		case 3:
			Bitmap mBitmap3 = BitmapCache.getInstance().getBitmap(R.drawable.hw_logo,mContext);
			BitmapDrawable bd3 = new BitmapDrawable(mContext.getResources(), mBitmap3);
			holder.dev_type_iamge_logo.setImageDrawable(bd3);
			holder.dev_type_name.setText(R.string.devicelistadapter_hongwai);
			break;
		case 4:
			Bitmap mBitmap4 = BitmapCache.getInstance().getBitmap(R.drawable.rq_logo,mContext);
			BitmapDrawable bd4 = new BitmapDrawable(mContext.getResources(), mBitmap4);
			holder.dev_type_iamge_logo.setImageDrawable(bd4);
			holder.dev_type_name.setText(R.string.devicelistadapter_ranqi);
			break;
		case 5:
			Bitmap mBitmap5 = BitmapCache.getInstance().getBitmap(R.drawable.sclb_sj_dev,mContext);
			BitmapDrawable bd5 = new BitmapDrawable(mContext.getResources(), mBitmap5);
			holder.dev_type_iamge_logo.setImageDrawable(bd5);
			holder.dev_type_name.setText(R.string.shuijin);
			break;//@@
		case 6:
			Bitmap mBitmap6 = BitmapCache.getInstance().getBitmap(R.drawable.sclb_ykq,mContext);
			BitmapDrawable bd6 = new BitmapDrawable(mContext.getResources(), mBitmap6);
			holder.dev_type_iamge_logo.setImageDrawable(bd6);
			holder.dev_type_name.setText(R.string.ykq);
			break;//@@
		default:
			break;
		}
		
		final AlarmType mAlarmType = listAlarmType.get(arg0);
		holder.dev_type_location.setText(mAlarmType.getLocation());
		holder.dev_type_record_time.setText(mAlarmType.getRecordTime());
		holder.modify_alarm_image.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent i = new Intent();
				i.putExtra("exLcation", mAlarmType.getLocation());
				i.putExtra("alarmMac", mAlarmType.getDevMac());
				i.setAction("MODIFY_ALARM_DEV_LOCATION_NAME");
				mContext.sendBroadcast(i);
			}
		});
		return convertView;
	}
	
	static class ViewHolder {
		public ImageView dev_type_iamge_logo,modify_alarm_image;
		public TextView dev_type_record_time;
		public TextView dev_type_location;
		public TextView dev_type_name;
	}
	
	
}
