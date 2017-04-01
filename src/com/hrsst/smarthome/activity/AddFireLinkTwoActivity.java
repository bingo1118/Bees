package com.hrsst.smarthome.activity;

import com.hrsst.smarthome.R;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class AddFireLinkTwoActivity extends Activity {
	private Context mContext;
	private Button add_fk_action_two;
	private String device;
	private EditText fk_location;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_firelink_two);
		device = getIntent().getExtras().getString("device");
		mContext = this;
		init();
	}

	@SuppressLint("NewApi")
	private void init() {
		// TODO Auto-generated method stub
		ImageView yg_2_image = (ImageView) findViewById(R.id.yg_2_image);
		Bitmap mBitmap = BitmapCache.getInstance().getBitmap(R.drawable.yg_2,mContext);
		BitmapDrawable bd = new BitmapDrawable(mContext.getResources(), mBitmap);
		yg_2_image.setBackground(bd);
		add_fk_action_two = (Button) findViewById(R.id.add_fk_action_two);
		fk_location = (EditText) findViewById(R.id.fk_location);

		add_fk_action_two.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String fk_locationStr = fk_location.getText().toString().trim();
				if (fk_locationStr != null && fk_locationStr.length() > 0) {
					Intent i = new Intent(mContext,
							AddFireLinkThreeActivity.class);
					i.putExtra("device", device);
					i.putExtra("location", fk_locationStr);
					startActivity(i);
//					finish();
				} else {
					Toast.makeText(mContext, R.string.please_input_location, 1).show();
				}
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
