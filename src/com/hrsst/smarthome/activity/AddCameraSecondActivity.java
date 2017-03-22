package com.hrsst.smarthome.activity;

import java.io.Serializable;
import java.util.List;

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

import com.hrsst.smarthome.R;
import com.hrsst.smarthome.pojo.UserDevice;
import com.hrsst.smarthome.util.BitmapCache;

public class AddCameraSecondActivity extends Activity{
	private Context mContext;
	private Button add_camera_action_two;
	private ImageView add_camera_two_image;
	private List<String> cameraList;
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_camera_second);
		mContext = this;
		cameraList = (List<String>) getIntent().getSerializableExtra(
				"cameraList");
		init();
	}
	
	
	@SuppressLint("NewApi")
	private void init() {
		// TODO Auto-generated method stub
		Bitmap mBitmap = BitmapCache.getInstance().getBitmap(R.drawable.add_camera_2,mContext);
		BitmapDrawable bd = new BitmapDrawable(mContext.getResources(), mBitmap);
		add_camera_two_image = (ImageView) findViewById(R.id.add_camera_two_image);
		add_camera_two_image.setBackground(bd);
		add_camera_action_two = (Button) findViewById(R.id.add_camera_action_two);
		add_camera_action_two.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent i = new Intent(mContext,AddCameraThirdActivity.class);
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
