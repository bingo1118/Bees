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
import android.widget.TextView;
import android.widget.Toast;

public class AddDoorsensorTwoActivity extends Activity{
	private Context mContext;
	private Button add_doorsensor_action_two;
	private String doorsensor;
	private EditText doorsensor_location;
	private ImageView step_two_image;
	private int type;
	private TextView add_dev_tip;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_doorsensor_two);
		doorsensor = getIntent().getExtras().getString("doorsensor");
		type = getIntent().getExtras().getInt("type");
		mContext = this;
		init();
	}

	@SuppressLint("NewApi")
	private void init() {
		// TODO Auto-generated method stub
		add_dev_tip=(TextView)findViewById(R.id.add_dev_tip);
		add_doorsensor_action_two = (Button) findViewById(R.id.add_doorsensor_action_two);
		doorsensor_location = (EditText) findViewById(R.id.doorsensor_location);
		step_two_image = (ImageView) findViewById(R.id.step_two_image);
		switch (type) {
		case 2:
			Bitmap mBitmap = BitmapCache.getInstance().getBitmap(R.drawable.mc_2,mContext);
			BitmapDrawable bd = new BitmapDrawable(mContext.getResources(), mBitmap);
			step_two_image.setBackground(bd);
			break;
		case 4:
			Bitmap mBitmaphw_2 = BitmapCache.getInstance().getBitmap(R.drawable.hw_2,mContext);
			BitmapDrawable hw_2 = new BitmapDrawable(mContext.getResources(), mBitmaphw_2);
			step_two_image.setBackground(hw_2);
			break;
		case 5:
			Bitmap mBitmaprq_2 = BitmapCache.getInstance().getBitmap(R.drawable.rq_2,mContext);
			BitmapDrawable rq_2 = new BitmapDrawable(mContext.getResources(), mBitmaprq_2);
			step_two_image.setBackground(rq_2);
			break;
		case 6:
			Bitmap mBitmapsj_2 = BitmapCache.getInstance().getBitmap(R.drawable.sj_lct_2,mContext);
			BitmapDrawable sj_2 = new BitmapDrawable(mContext.getResources(), mBitmapsj_2);
			step_two_image.setBackground(sj_2);
			add_dev_tip.setText(R.string.add_two);
			break;
		case 7:
			Bitmap mBitmapykq_2 = BitmapCache.getInstance().getBitmap(R.drawable.ykq_lct_1,mContext);
			BitmapDrawable ykq_2 = new BitmapDrawable(mContext.getResources(), mBitmapykq_2);
			step_two_image.setBackground(ykq_2);
			add_dev_tip.setText(R.string.add_two);
			break;
		default:
			break;
		}
		add_doorsensor_action_two.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String doorsensor_locationStr = doorsensor_location.getText().toString().trim();
				if(doorsensor_locationStr!=null&&doorsensor_locationStr.length()>0){
					Intent i = new Intent(mContext,AddDoorsensorThreeActivity.class);
					i.putExtra("doorsensor", doorsensor);
					i.putExtra("location", doorsensor_locationStr);
					i.putExtra("type", type);
					startActivity(i);
//					finish();
				}else{
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
