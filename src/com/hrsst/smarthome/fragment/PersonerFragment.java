package com.hrsst.smarthome.fragment;

import com.hrsst.smarthome.R;
import com.hrsst.smarthome.activity.LoginActivity;
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

public class PersonerFragment extends Fragment implements OnClickListener{
	private View view;
	private Context mContext;
	private TextView user_id,menu_pe;
	private ImageView per_imageView;
	
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.activity_personer_fragment, null);
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		mContext = getActivity();
		init();
		regFilter();
		Intent i = new Intent();
		i.setAction(Constants.Action.MAIN_ACTION);
		mContext.sendBroadcast(i);
	}
	
	private void init(){
		user_id = (TextView) view.findViewById(R.id.user_id);
		user_id.setOnClickListener(this);
		menu_pe = (TextView) view.findViewById(R.id.menu_pe);
		menu_pe.setOnClickListener(this);
		Bitmap mBitmap = BitmapCache.getInstance().getBitmap(R.drawable.faxian,mContext);
		BitmapDrawable bd = new BitmapDrawable(mContext.getResources(), mBitmap);
		per_imageView = (ImageView) view.findViewById(R.id.per_imageView);
		per_imageView.setImageDrawable(bd);
		per_imageView.setAdjustViewBounds(true);
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
				user_id.setVisibility(View.VISIBLE);
				menu_pe.setVisibility(View.GONE);
			}
			if(result.equals(Constants.Action.IF_USER_LOGIN_YES)){
				user_id.setVisibility(View.GONE);
				menu_pe.setVisibility(View.VISIBLE);
			}
		}
		
	};

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.user_id:
			Intent intent = new Intent(mContext,LoginActivity.class);
			startActivity(intent);
			break;
		case R.id.menu_pe:
			Intent i = new Intent();
			i.setAction(Constants.Action.OPEN_SLIDE_MENU);
			mContext.sendBroadcast(i);
			break;
		default:
			break;
		}
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		BitmapCache.getInstance().clearCache();
		super.onDestroy();
		mContext.unregisterReceiver(mReceiver);
	}
}
