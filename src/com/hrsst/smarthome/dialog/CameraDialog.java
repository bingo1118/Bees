package com.hrsst.smarthome.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class CameraDialog extends Dialog{
	private Context mContext;

	
	public CameraDialog(Context context,int resource,int action) {
		super(context);
		// TODO Auto-generated constructor stub
		this.mContext = mContext;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 这句代码换掉dialog默认背景，否则dialog的边缘发虚透明而且很宽
        // 总之达不到想要的效果
        getWindow().setBackgroundDrawableResource(android.R.color.transparent); 
        View localView = LayoutInflater.from(mContext).inflate(null, null);
        
        setContentView(localView); 
        // 这句话起全屏的作用
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT);
	}
	
}
