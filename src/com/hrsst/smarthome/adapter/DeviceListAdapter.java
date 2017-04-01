package com.hrsst.smarthome.adapter;

import java.util.List;

import com.hrsst.smarthome.R;
import com.hrsst.smarthome.util.BitmapCache;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DeviceListAdapter extends BaseAdapter {

	private Context mContext;
	private List<String> list;
	private ViewHolder holder;

	public DeviceListAdapter(Context mContext,List<String> list) {
		this.mContext = mContext;
		this.list = list;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("NewApi")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.list_device, null);
			holder = new ViewHolder();
//			holder.image = (ImageView) convertView.findViewById(R.id.list_mImageView);
			holder.mlinearLayout = (LinearLayout) convertView.findViewById(R.id.list_device_linearlayout);
			holder.mTextView = (TextView) convertView.findViewById(R.id.list_device_textview);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		switch (position) {
		case 0:
			holder.mlinearLayout.setBackgroundResource(R.drawable.chazuo_hong_e);
			holder.mTextView.setText(R.string.devicelistadapter_gateway_socket);
		break;
		case 1:
			holder.mlinearLayout.setBackgroundResource(R.drawable.yangan_e);
			holder.mTextView.setText(R.string.devicelistadapter_smoke_detection_alarm);
			break;
		case 2:
			holder.mlinearLayout.setBackgroundResource(R.drawable.menci_e);
			holder.mTextView.setText(R.string.devicelistadapter_menci);
			break;
		case 3:
			holder.mlinearLayout.setBackgroundResource(R.drawable.hongwai_e);
			holder.mTextView.setText(R.string.devicelistadapter_hongwai);
			break;
		case 4:
			holder.mlinearLayout.setBackgroundResource(R.drawable.ranqi_e);
			holder.mTextView.setText(R.string.devicelistadapter_ranqi);
			break;
		case 8:
			holder.mlinearLayout.setBackgroundResource(R.drawable.shexiangji_e);
			holder.mTextView.setText(R.string.devicelistadapter_shexiangji);
			break;
		case 7:
			holder.mlinearLayout.setBackgroundResource(R.drawable.air_dev);
			holder.mTextView.setText(R.string.air_dev);
			break;
		case 5:
			holder.mlinearLayout.setBackgroundResource(R.drawable.water_dev);
			holder.mTextView.setText(R.string.water_dev);
			break;
		case 6:
			holder.mlinearLayout.setBackgroundResource(R.drawable.ykq);
			holder.mTextView.setText(R.string.controler_dev);
			break;
		default:
			break;
		}
		return convertView;
	}


	static class ViewHolder {
		public LinearLayout mlinearLayout;
		public TextView mTextView;
	}

	public void killBitmap(){
		BitmapCache.getInstance().clearCache();
	}
}
