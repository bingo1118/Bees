package com.hrsst.smarthome;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.hrsst.smarthome.R.id;
import com.hrsst.smarthome.pojo.EnvironmentInfo;
import com.hrsst.smarthome.pojo.UserDevice;
import com.hrsst.smarthome.view.ViewPagerIndicator;
import com.hrsst.smarthome.view.VpSimpleFragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AirDevInfoActivity extends FragmentActivity{
	
	private TextView tv_environmentQuality;
	private TextView tv_methanal;
	private TextView tv_pm25;
	private TextView tv_humidity;
	private TextView tv_temperature;
	private TextView tv_position;
	private UserDevice device;
	private LinearLayout air_info_linearLayout;
	private LinearLayout btn_methanal,btn_pm25,btn_temperature,btn_humidity;
	
	private ViewPager viewpager;
	private ViewPagerIndicator indicator;
	private List<String> title_list=Arrays.asList("pm25","mathanal","temperature","humidity");
	private List<VpSimpleFragment> mContent=new ArrayList<VpSimpleFragment>();
	private FragmentPagerAdapter adapter;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_air_dev_info);
		
		device=(UserDevice) getIntent().getExtras().getSerializable("info");
		init();
		initViews();
		initData();
		
		viewpager.setAdapter(adapter);
		viewpager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				// TODO 自动生成的方法存根
				
			}
			
			@Override
			public void onPageScrolled(int position, float arg1, int arg2) {
				indicator.scrollBy(position, arg1);
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO 自动生成的方法存根
				
			}
		});
	}
	
	private void initData() {
		for(String title:title_list){
			VpSimpleFragment fragment=VpSimpleFragment.newInstance(title,device);
			mContent.add(fragment);
		}
		adapter=new FragmentPagerAdapter(getSupportFragmentManager()) {
			
			@Override
			public int getCount() {
				// TODO 自动生成的方法存根
				return mContent.size();
			}
			
			@Override
			public Fragment getItem(int arg0) {
				// TODO 自动生成的方法存根
				return mContent.get(arg0);
			}
		};
	}

	private void initViews() {
		viewpager=(ViewPager)findViewById(R.id.viewpager);
		indicator=(ViewPagerIndicator)findViewById(R.id.indicator);
		indicator.setmViewPager(viewpager);
		indicator.setItemClickEvent();
	}


	private void init() {
		EnvironmentInfo environmentInfo=device.getEnvironment();
		air_info_linearLayout=(LinearLayout)findViewById(R.id.airinfo_linearLayout);
		tv_environmentQuality=(TextView)findViewById(R.id.tv_info_environmentQuality);
		tv_methanal=(TextView)findViewById(R.id.tv_info_methanal);
		tv_pm25=(TextView)findViewById(R.id.tv_info_pm25);
		tv_humidity=(TextView)findViewById(R.id.tv_info_humidity);
		tv_temperature=(TextView)findViewById(R.id.tv_info_temperature);
		tv_position=(TextView)findViewById(R.id.tv_info_devname);
		String quality;
		switch (environmentInfo.getEnvironmentQuality()) {
		case 1:
			quality="优";
			air_info_linearLayout.setBackground(getResources().getDrawable(R.drawable.bj_you));
			break;
		case 2:
			quality="良";
			air_info_linearLayout.setBackground(getResources().getDrawable(R.drawable.bj_liang));
			break;
		case 3:
			quality="中";
			air_info_linearLayout.setBackground(getResources().getDrawable(R.drawable.bj_zhong));
			break;
		case 4:
			quality="差";
			air_info_linearLayout.setBackground(getResources().getDrawable(R.drawable.bj_cha));
			tv_environmentQuality.setTextColor(Color.parseColor("#ff0700"));
			break;
			

		default:
			quality="-";
		}
		tv_environmentQuality.setText(quality);
		tv_methanal.setText(environmentInfo.getMethanal());
		tv_pm25.setText(environmentInfo.getPm25());
		tv_humidity.setText(environmentInfo.getHumidity());
		tv_temperature.setText(environmentInfo.getTemperature()+"°");
		tv_position.setText(device.getDevName());
		
		btn_methanal=(LinearLayout)findViewById(R.id.btn_methanal);
//		btn_methanal.setOnClickListener(this);
		btn_pm25=(LinearLayout)findViewById(R.id.btn_pm25);
//		btn_pm25.setOnClickListener(this);
		btn_temperature=(LinearLayout)findViewById(R.id.btn_temperature);
//		btn_temperature.setOnClickListener(this);
		btn_humidity=(LinearLayout)findViewById(R.id.btn_humidity);
//		btn_humidity.setOnClickListener(this);
	}



}
