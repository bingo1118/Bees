package com.hrsst.smarthome.adapter;

import com.hrsst.smarthome.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class WeekenAdapter extends BaseAdapter{
	private Context mContext;
	private byte[] by;
	private String[] str;
	
	public WeekenAdapter(Context mContext,byte[] by,String[] str){
		this.mContext = mContext;
		this.by = by;
		this.str = str;
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
	public View getView(int pos, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		System.out.println("pos="+pos);
		ViewHolder holder = null;
		if(null==convertView){
			convertView = LayoutInflater.from(mContext).inflate(R.layout.weeken_item, null);
			holder = new ViewHolder();
			holder.image = (ImageView) convertView.findViewById(R.id.check_image);
			holder.mTextView = (TextView) convertView.findViewById(R.id.weeken_name);
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder)convertView.getTag();
		}
		if(by[pos]==0x01){
			holder.image.setImageResource(R.drawable.check_on);
		}else{
			holder.image.setImageResource(R.drawable.check_off);
		}
		holder.mTextView.setText(str[pos]);
		return convertView;
	}
	
	class ViewHolder{
		public ImageView image;
		public TextView mTextView;
	} 
}
