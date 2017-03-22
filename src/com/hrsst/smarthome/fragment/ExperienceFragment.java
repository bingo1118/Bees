package com.hrsst.smarthome.fragment;

import com.hrsst.smarthome.R;
import com.hrsst.smarthome.global.Constants;
import com.hrsst.smarthome.util.BitmapCache;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ExperienceFragment extends Fragment implements OnClickListener{
	private View view;
	private TextView menu_ex;
	private Context mContext;
	private ImageView experience_image;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.activity_experience_fragment, null);
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		mContext = getActivity();
		Bitmap mBitmap = BitmapCache.getInstance().getBitmap(R.drawable.zhineng,mContext);
		BitmapDrawable bd = new BitmapDrawable(mContext.getResources(), mBitmap);
		experience_image = (ImageView)view.findViewById(R.id.experience_image);
		experience_image.setImageDrawable(bd);
		experience_image.setAdjustViewBounds(true);
		init();
		regFilter();
		Intent i = new Intent();
		i.setAction(Constants.Action.MAIN_ACTION);
		mContext.sendBroadcast(i);
	}
	

	private void init() {
		// TODO Auto-generated method stub
		menu_ex = (TextView) view.findViewById(R.id.menu_ex);
		menu_ex.setOnClickListener(this);
	}
	
	public void regFilter() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.Action.IF_USER_LOGIN_NO);
		filter.addAction(Constants.Action.IF_USER_LOGIN_YES);
		mContext.registerReceiver(mReceiver, filter);
	}
	
	private BroadcastReceiver mReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String result = intent.getAction();
			if(result.equals(Constants.Action.IF_USER_LOGIN_NO)){
				menu_ex.setVisibility(View.GONE);
			}
			if(result.equals(Constants.Action.IF_USER_LOGIN_YES)){
				menu_ex.setVisibility(View.VISIBLE);
			}
		}
		
	};
	
	@Override
	public void onDestroy() {
		BitmapCache.getInstance().clearCache();
		super.onDestroy();
		mContext.unregisterReceiver(mReceiver);
	}
	

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.menu_ex:
			Intent i = new Intent();
			i.setAction(Constants.Action.OPEN_SLIDE_MENU);
			mContext.sendBroadcast(i);
			break;

		default:
			break;
		}
	}
}
