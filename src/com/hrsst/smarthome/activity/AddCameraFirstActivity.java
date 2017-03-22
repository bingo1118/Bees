package com.hrsst.smarthome.activity;

import java.io.Serializable;
import java.util.List;

import com.hrsst.smarthome.R;
import com.hrsst.smarthome.pojo.UserDevice;
import com.hrsst.smarthome.util.BitmapCache;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class AddCameraFirstActivity extends Activity{
	private Context mContext;
	private Button next_add_camera_first_btn;
	private ImageView add_camera_one_image;
	private List<String> cameraList;
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_camera_first);
		mContext = this;
		cameraList = (List<String>) getIntent().getSerializableExtra(
				"cameraList");
		init();
	}
	
	
	@SuppressLint("NewApi")
	private void init() {
		// TODO Auto-generated method stub
		Bitmap mBitmap = BitmapCache.getInstance().getBitmap(R.drawable.add_camera_1,mContext);
		BitmapDrawable bd = new BitmapDrawable(mContext.getResources(), mBitmap);
		add_camera_one_image = (ImageView) findViewById(R.id.add_camera_one_image);
		add_camera_one_image.setBackground(bd);
		next_add_camera_first_btn = (Button) findViewById(R.id.add_camera_action_one);
		next_add_camera_first_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent i = new Intent(mContext,AddCameraSecondActivity.class);
				i.putExtra("cameraList",
						(Serializable) cameraList);
				startActivity(i);
				finish();
			}
		});
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		BitmapCache.getInstance().clearCache();
	}
}
