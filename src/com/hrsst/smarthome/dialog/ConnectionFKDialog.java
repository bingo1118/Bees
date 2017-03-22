package com.hrsst.smarthome.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.hrsst.smarthome.R;
import com.hrsst.smarthome.util.BitmapCache;

public class ConnectionFKDialog extends Dialog{
	private Context mContext;
	private AnimationDrawable mAnimationDrawable;

	public ConnectionFKDialog(Context mContext) {
		super(mContext);
		// TODO Auto-generated constructor stub
		this.mContext = mContext;
	}
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 这句代码换掉dialog默认背景，否则dialog的边缘发虚透明而且很宽
        // 总之达不到想要的效果
        getWindow().setBackgroundDrawableResource(android.R.color.transparent); 
        View localView = LayoutInflater.from(mContext).inflate(R.layout.dialog_connecting_fk, null);
        Bitmap mBitmap = BitmapCache.getInstance().getBitmap(R.drawable.liuchengtu_4,mContext);
		BitmapDrawable bd = new BitmapDrawable(mContext.getResources(), mBitmap);
        RelativeLayout liuchengtu_4_rela = (RelativeLayout) localView.findViewById(R.id.liuchengtu_4_fk);
        liuchengtu_4_rela.setBackground(bd);
        ImageView im  = (ImageView) localView.findViewById(R.id.connect_fk_image);
        mAnimationDrawable = (AnimationDrawable) im.getBackground();
        setContentView(localView); 
        // 这句话起全屏的作用
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT);
	}
	
	public void startConnect(){
		mAnimationDrawable.start();
	}
	
	public void stopConnect(){
		mAnimationDrawable.stop();
	}
}
