package com.hrsst.smarthome.activity;

import java.io.File;

import com.hrsst.smarthome.R;
import com.hrsst.smarthome.adapter.ImageBrowserAdapter;
import com.hrsst.smarthome.pojo.Contact;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.widget.GridView;

public class PrintScreenActivity extends Activity{
	private Context mContext;
	File[] files;
	GridView list;
	ImageBrowserAdapter adapter;	
	int screenWidth,screenHeight;
	private Contact mContact;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cut_pic);
		mContext = this;
		mContact=(Contact) getIntent().getSerializableExtra("contact");
		if(null==files){
			files = new File[0];
		}
		init();
	}

	private void init() {
		// TODO Auto-generated method stub
		list = (GridView)findViewById(R.id.list_grid);
		DisplayMetrics dm = new DisplayMetrics();
		adapter = new ImageBrowserAdapter(mContext);
		list.setAdapter(adapter);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK){
			Intent i = new Intent(mContext,ApMonitorActivity.class);
			i.putExtra("contact", mContact);
			startActivity(i);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
}
