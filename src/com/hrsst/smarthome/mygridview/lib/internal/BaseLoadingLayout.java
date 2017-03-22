package com.hrsst.smarthome.mygridview.lib.internal;

import android.content.Context;
import android.widget.FrameLayout;

public class BaseLoadingLayout extends FrameLayout {

	static final int DEFAULT_ROTATION_ANIMATION_DURATION = 150;

	public BaseLoadingLayout(Context context, final int mode, String releaseLabel,String releaseLabel1, String pullLabel, String pullLabel1, String refreshingLabel)  {
		super(context);
		/*ViewGroup header = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.pull_to_refresh_header_special, this);*/
	}

	public void reset() {
		
	}

	public void releaseToRefresh() {
	}

	public void setPullLabel(String pullLabel) {
	}

	public void refreshing() {
	}

	public void setRefreshingLabel(String refreshingLabel) {
	}

	public void setReleaseLabel(String releaseLabel) {
	}

	public void pullToRefresh() {
	}

	public void setTextColor(int color) {
	}

}
